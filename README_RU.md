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
- Swagger/OpenAPI-документация для HTTP API commerce-сервисов.
- Actuator health checks и Prometheus-метрики в каждом Spring-сервисе.
- Prometheus и Grafana в Docker с заранее настроенным дашбордом Smart Home.
- PostgreSQL со схемой на каждый бизнес-сервис при запуске через Docker.
- Тесты для бизнес-логики, API-контрактов, fallback-поведения и observability endpoint'ов.

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
| `prometheus` | `9090` | Сбор и запрос метрик |
| `grafana` | `3000` | Дашборды метрик |

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
| [http://localhost:9090/targets](http://localhost:9090/targets) | Targets в Prometheus |
| [http://localhost:3000](http://localhost:3000) | Grafana, логин `admin` / `admin` |

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
- fallback-поведение Feign-клиента при недоступном складе;
- доступность Actuator health и Prometheus endpoint'ов во всех Spring-сервисах.

## API-документация

Swagger UI доступен для commerce-сервисов после запуска стека:

| Сервис | Swagger UI | OpenAPI JSON |
| --- | --- | --- |
| `shopping-store` | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) |
| `shopping-cart` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) |
| `warehouse` | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) |

## Наблюдаемость

Каждый Spring-сервис отдаёт Actuator health и Prometheus-метрики. Docker Compose использует health endpoint для проверки состояния контейнеров, а Prometheus собирает метрики с Prometheus endpoint.

| Сервис | Health | Prometheus metrics |
| --- | --- | --- |
| `eureka-server` | [http://localhost:8761/actuator/health](http://localhost:8761/actuator/health) | [http://localhost:8761/actuator/prometheus](http://localhost:8761/actuator/prometheus) |
| `config-server` | [http://localhost:8888/actuator/health](http://localhost:8888/actuator/health) | [http://localhost:8888/actuator/prometheus](http://localhost:8888/actuator/prometheus) |
| `shopping-store` | [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health) | [http://localhost:8081/actuator/prometheus](http://localhost:8081/actuator/prometheus) |
| `shopping-cart` | [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health) | [http://localhost:8082/actuator/prometheus](http://localhost:8082/actuator/prometheus) |
| `warehouse` | [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health) | [http://localhost:8083/actuator/prometheus](http://localhost:8083/actuator/prometheus) |

Prometheus доступен по адресу [http://localhost:9090](http://localhost:9090). На странице [http://localhost:9090/targets](http://localhost:9090/targets) можно проверить, что все service targets находятся в состоянии `UP`.

Grafana доступна по адресу [http://localhost:3000](http://localhost:3000), локальный логин и пароль: `admin` / `admin`. В локальном Docker-профиле запрос смены пароля при первом входе отключён, поэтому дашборд `Smart Home Technologies Overview` открывается сразу после авторизации. Datasource Prometheus создаётся автоматически.

Если стандартные порты уже заняты другим локальным стеком, их можно переопределить перед запуском. Самый удобный локальный вариант - создать файл `.env` в корне репозитория:

```env
PROMETHEUS_HOST_PORT=9091
GRAFANA_HOST_PORT=3002
```

После этого стек запускается обычной командой:

```bash
docker compose up --build -d
```

В примере выше Grafana будет доступна по адресу [http://localhost:3002](http://localhost:3002).

Также порты можно передать прямо в команде запуска:

```bash
PROMETHEUS_HOST_PORT=9091 GRAFANA_HOST_PORT=3002 docker compose up --build -d
```

В Windows PowerShell:

```powershell
$env:PROMETHEUS_HOST_PORT = "9091"
$env:GRAFANA_HOST_PORT = "3002"
docker compose up --build -d
```

## Подключение сайта, хостинга, CMS или CRM

Этот репозиторий - backend-часть интернет-магазина. Публичный сайт, админ-панель, CMS или CRM должны подключаться к нему через REST API. В production наружу лучше отдавать только публичную API-точку через HTTPS, а PostgreSQL, Eureka, Config Server, Prometheus и внутренние порты сервисов оставлять закрытыми.

> [!TIP]
> Нажмите на стрелочку ниже, чтобы раскрыть информацию по каждому пункту.

<details>
<summary><strong>🟦 Сайт или frontend-приложение</strong> <sub>нажмите, чтобы раскрыть</sub></summary>

Backend можно подключить к любому frontend: React, Next.js, Vue, статическому сайту, мобильному приложению или теме CMS.

Рекомендуемая настройка frontend:

```env
SHOP_API_BASE_URL=https://api.example.com
```

Для браузерных фреймворков используйте формат публичных переменных окружения, который нужен конкретному фреймворку, например:

```env
NEXT_PUBLIC_SHOP_API_BASE_URL=https://api.example.com
VITE_SHOP_API_BASE_URL=https://api.example.com
```

Пример запроса списка товаров:

```js
const apiBaseUrl = import.meta.env.VITE_SHOP_API_BASE_URL;

const response = await fetch(`${apiBaseUrl}/api/v1/shopping-store?category=SENSORS`);
const products = await response.json();
```

Пример добавления товара в корзину:

```js
const apiBaseUrl = import.meta.env.VITE_SHOP_API_BASE_URL;

await fetch(`${apiBaseUrl}/api/v1/shopping-cart?username=alice`, {
  method: "PUT",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify({
    productId: "00000000-0000-0000-0000-000000000001",
    quantity: 1
  })
});
```

Для локальной разработки без reverse proxy можно обращаться напрямую к сервисам:

| Сервис | Локальный URL |
| --- | --- |
| Витрина товаров | `http://localhost:8081` |
| Корзина | `http://localhost:8082` |
| Склад | `http://localhost:8083` |

Для production-сайта удобнее использовать один публичный API-домен, например `https://api.example.com`, а внутри reverse proxy будет направлять каждый путь `/api/v1/...` в нужный сервис.

</details>

<details>
<summary><strong>🟩 Хостинг с Docker Compose</strong> <sub>нажмите, чтобы раскрыть</sub></summary>

Этот вариант подходит для VPS, выделенного сервера или платформы, где можно запускать Docker Compose.

1. Установите Docker и Docker Compose на сервер.
2. Скопируйте или склонируйте репозиторий на сервер.
3. Создайте файл `.env` в корне репозитория для портов и секретов конкретного сервера.
4. Запустите стек:

```bash
docker compose up --build -d
```

5. Проверьте контейнеры:

```bash
docker compose ps
```

6. Проверьте health endpoints:

```bash
curl http://localhost:8761/actuator/health
curl http://localhost:8888/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

В production не стоит открывать внутреннюю инфраструктуру напрямую в интернет. Наружу выводите домен для commerce API, а админские и мониторинговые endpoint'ы защищайте.

</details>

<details>
<summary><strong>🟪 Reverse proxy для одного публичного API-домена</strong> <sub>нажмите, чтобы раскрыть</sub></summary>

Reverse proxy нужен, чтобы сайт работал с одним стабильным API URL, а микросервисы оставались разделёнными внутри сервера.

Пример публичного API URL:

```text
https://api.example.com
```

Пример маршрутизации через Nginx:

```nginx
server {
    listen 443 ssl;
    server_name api.example.com;

    location /api/v1/shopping-store {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api/v1/shopping-cart {
        proxy_pass http://127.0.0.1:8082;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api/v1/warehouse {
        proxy_pass http://127.0.0.1:8083;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Swagger, Actuator, Prometheus и Grafana открывайте наружу только осознанно. Для публичного окружения лучше закрыть их VPN, basic auth, allowlist по IP или приватной сетью.

</details>

<details>
<summary><strong>🟨 Интеграция с CMS или CRM</strong> <sub>нажмите, чтобы раскрыть</sub></summary>

CMS или CRM подключаются как внешний REST-клиент. Они должны обращаться к тому же публичному API-домену, который использует сайт.

Типовая интеграция с CMS:

- вывод списка товаров через `GET /api/v1/shopping-store`;
- вывод карточки товара через `GET /api/v1/shopping-store/{productId}`;
- работа с корзиной через серверные actions, plugin или код темы;
- административные методы управления товарами только в защищённой админ-зоне.

Типовая интеграция с CRM:

- синхронизация каталога товаров из `shopping-store`;
- отправка данных корзины или заявок с сайта в CRM через небольшой integration-service;
- после добавления order, payment и delivery сервисов - синхронизация заказов и статусов заказов.

В текущем спринте реализованы витрина, корзина и склад. Полную CRM-синхронизацию заказов, оплат и доставок логично добавлять после появления соответствующих сервисов.

</details>

<details>
<summary><strong>🟥 Production checklist перед публикацией API</strong> <sub>нажмите, чтобы раскрыть</sub></summary>

- Поставить HTTPS перед публичным трафиком.
- Добавить authentication и authorization перед публикацией админских endpoint'ов.
- Настроить CORS для реального домена сайта, если браузерные запросы идут с другого домена.
- Хранить секреты в environment variables или secrets хостинга, а не в Git.
- Не открывать наружу PostgreSQL, Eureka, Config Server, Prometheus и прямые порты сервисов.
- Использовать database backups и persistent volumes.
- Следить за `/actuator/health`, Prometheus targets и Grafana dashboards.
- Добавить rate limiting на gateway или reverse proxy для публичных API.
- Использовать отдельные настройки для development, staging и production.

</details>

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
