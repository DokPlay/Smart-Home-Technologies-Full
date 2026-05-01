[English version](README.md)

# Smart Home Technologies - Commerce Microservices

Smart Home Technologies Commerce - это микросервисное приложение на Spring Boot и Spring Cloud для интернет-магазина устройств умного дома. Проект включает витрину товаров, корзину пользователя и склад, а также общие API-контракты, service discovery, внешнюю конфигурацию и локальный запуск через Docker.

Проект сделан как масштабируемая backend-основа для продажи устройств Smart Home Technologies. Каждая бизнес-возможность вынесена в отдельный сервис со своей моделью данных и отдельной схемой базы данных.

## Возможности

- Витрина товаров с фильтрацией по категории, просмотром карточки товара, мягким удалением и состоянием доступного количества.
- Корзина пользователя по имени пользователя: добавление, удаление, изменение количества и деактивация корзины.
- Склад: регистрация товара, пополнение остатков, проверка доступности товаров и получение адреса склада.
- Общие DTO и API-контракты в отдельном модуле `interaction-api`.
- Service Discovery через Eureka.
- Внешняя конфигурация через Spring Cloud Config.
- REST-взаимодействие между сервисами через Feign.
- Circuit Breaker для вызовов `shopping-cart -> warehouse` с понятным ответом `503`, если склад недоступен.
- PostgreSQL со схемой на каждый бизнес-сервис при запуске через Docker.
- Тесты для бизнес-логики, API-контрактов и fallback-поведения.

## Архитектура

```text
smart-home-technologies-full
├── eureka-server
├── config-server
│   └── config-repo
└── commerce
    ├── interaction-api
    ├── shopping-store
    ├── shopping-cart
    └── warehouse
```

### Сервисы

| Сервис | Порт в Docker | Назначение |
| --- | ---: | --- |
| `eureka-server` | `8761` | Service Discovery |
| `config-server` | `8888` | Централизованная конфигурация |
| `shopping-store` | `8081` | Витрина товаров |
| `shopping-cart` | `8082` | Корзина пользователя |
| `warehouse` | `8083` | Остатки, проверка бронирования, адрес склада |
| `postgres` | `5432` | Общий PostgreSQL-сервер с отдельными схемами |

## Требования

- Docker Desktop или Docker Engine с Docker Compose.
- Java 17+ и Maven 3.9+ для локального запуска без Docker.

## Быстрый запуск через Docker

Из корня репозитория:

```bash
docker compose up --build -d
```

Проверить, что контейнеры запущены:

```bash
docker compose ps
```

Открыть Eureka:

[http://localhost:8761](http://localhost:8761)

Ожидаемые зарегистрированные приложения:

- `SHOPPING-STORE`
- `SHOPPING-CART`
- `WAREHOUSE`

Полезные локальные адреса:

| URL | Описание |
| --- | --- |
| [http://localhost:8761](http://localhost:8761) | Панель Eureka |
| [http://localhost:8888/shopping-cart/default](http://localhost:8888/shopping-cart/default) | Пример ответа Config Server |
| [http://localhost:8081/api/v1/shopping-store](http://localhost:8081/api/v1/shopping-store) | API витрины товаров |
| [http://localhost:8082/api/v1/shopping-cart](http://localhost:8082/api/v1/shopping-cart) | API корзины |
| [http://localhost:8083/api/v1/warehouse/address](http://localhost:8083/api/v1/warehouse/address) | API адреса склада |

Остановить приложение:

```bash
docker compose down
```

Остановить приложение и удалить данные базы:

```bash
docker compose down -v
```

## Сборка и тесты локально

Полная проверка Maven:

```bash
mvn clean verify
```

Тестами покрыты:

- контракт и бизнес-логика витрины товаров;
- контракт и бизнес-правила корзины;
- складская логика и расчёт параметров бронирования;
- fallback-поведение Feign-клиента при недоступном складе.

## Запуск сервисов без Docker

Сначала запустите инфраструктуру:

```bash
mvn -pl eureka-server spring-boot:run
mvn -pl config-server spring-boot:run
```

Затем запустите commerce-сервисы:

```bash
mvn -pl commerce/warehouse spring-boot:run
mvn -pl commerce/shopping-store spring-boot:run
mvn -pl commerce/shopping-cart spring-boot:run
```

При запуске без Docker commerce-сервисы берут настройки из `config-server/config-repo` и по умолчанию используют in-memory H2, если не переданы переменные окружения для datasource.

## Обзор API

### Shopping Store

Base path: `/api/v1/shopping-store`

- `GET /api/v1/shopping-store?category=SENSORS`
- `GET /api/v1/shopping-store/{productId}`
- `PUT /api/v1/shopping-store`
- `POST /api/v1/shopping-store`
- `POST /api/v1/shopping-store/removeProductFromStore`
- `POST /api/v1/shopping-store/quantityState`

### Shopping Cart

Base path: `/api/v1/shopping-cart`

- `GET /api/v1/shopping-cart?username=alice`
- `PUT /api/v1/shopping-cart?username=alice`
- `DELETE /api/v1/shopping-cart?username=alice`
- `POST /api/v1/shopping-cart/remove?username=alice`
- `POST /api/v1/shopping-cart/change-quantity?username=alice`

### Warehouse

Base path: `/api/v1/warehouse`

- `PUT /api/v1/warehouse`
- `POST /api/v1/warehouse/add`
- `POST /api/v1/warehouse/check`
- `GET /api/v1/warehouse/address`

## Конфигурация

Файлы конфигурации находятся здесь:

```text
config-server/config-repo
```

Docker использует переменные окружения из `docker-compose.yml`, чтобы подключить сервисы к PostgreSQL, Eureka и Config Server. Для бизнес-сервисов используются отдельные схемы PostgreSQL:

- `shopping_store`
- `shopping_cart`
- `warehouse`

## Поведение Circuit Breaker

`shopping-cart` обращается к `warehouse` через Feign-клиент. Если сервис склада недоступен, Circuit Breaker fallback быстро возвращает понятный ответ:

```json
{
  "error": "WAREHOUSE_UNAVAILABLE",
  "message": "Warehouse service is temporarily unavailable. Please try again later."
}
```

HTTP-статус: `503 Service Unavailable`.
