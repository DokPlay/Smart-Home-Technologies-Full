[English version](README.md)

# Smart Home Technologies - Commerce Microservices

Smart Home Technologies Commerce - это микросервисное приложение на Spring Boot и Spring Cloud для интернет-магазина устройств умного дома. Проект включает витрину товаров, корзину пользователя, склад, заказы, оплату, доставку и API Gateway, а также общие API-контракты, service discovery, внешнюю конфигурацию и локальный запуск через Docker.

Проект сделан как масштабируемая backend-основа для продажи устройств Smart Home Technologies. Каждая бизнес-возможность вынесена в отдельный сервис со своей моделью данных и отдельной схемой базы данных.

## Возможности

- Витрина товаров с фильтрацией по категории, просмотром карточки товара, мягким удалением и состоянием доступного количества.
- Корзина пользователя по имени пользователя: добавление, удаление, изменение количества и деактивация корзины.
- Склад: регистрация товара, пополнение остатков, проверка доступности товаров, сборка заказа, передача в доставку, возвраты и получение адреса склада.
- Управление заказами от оформления по корзине до сборки, оплаты, доставки, завершения, ошибок и возврата.
- Адаптер оплаты с расчётом стоимости товаров через `shopping-store`, НДС 10%, сохранением статусов `PENDING`/`SUCCESS`/`FAILED` и обратными вызовами в `order`.
- Адаптер доставки с планированием доставки, расчётом стоимости, статусами получения/успеха/ошибки и обратными вызовами в `order` и `warehouse`.
- API Gateway с маршрутами для `order`, `payment` и `delivery` через Eureka Load Balancer.
- Общие DTO и API-контракты в отдельном модуле `interaction-api`.
- Service Discovery через Eureka.
- Внешняя конфигурация через Spring Cloud Config.
- REST-взаимодействие между сервисами через Feign.
- Circuit Breaker и Feign fallback для критичных межсервисных вызовов с понятным ответом `503`, если зависимый сервис недоступен.
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
    ├── warehouse
    ├── order
    ├── payment
    └── delivery
api-gateway
```

### Сервисы

| Сервис | Порт в Docker | Назначение |
| --- | ---: | --- |
| `eureka-server` | `8761` | Service Discovery |
| `config-server` | `8888` | Централизованная конфигурация |
| `api-gateway` | `8080` | Единая точка входа для order, payment и delivery |
| `shopping-store` | `8081` | Витрина товаров |
| `shopping-cart` | `8082` | Корзина пользователя |
| `warehouse` | `8083` | Остатки, проверка бронирования, адрес склада |
| `order` | `8084` | Жизненный цикл заказа |
| `payment` | `8085` | Расчёт и состояние оплаты |
| `delivery` | `8086` (`8086-8087` при масштабировании) | Планирование и состояние доставки |
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

Чтобы проверить Load Balancer для доставки, запустите два экземпляра `delivery`:

```bash
docker compose up --build -d --scale delivery=2
```

При двух экземплярах Docker публикует доставку на `8086` и `8087`, а Gateway продолжает маршрутизировать запросы через Eureka Load Balancer.

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
- `ORDER`
- `PAYMENT`
- `DELIVERY`
- `API-GATEWAY`

Полезные локальные адреса:

| URL | Описание |
| --- | --- |
| [http://localhost:8761](http://localhost:8761) | Панель Eureka |
| [http://localhost:8888/shopping-cart/default](http://localhost:8888/shopping-cart/default) | Пример ответа Config Server |
| [http://localhost:8080/delivery/cost](http://localhost:8080/delivery/cost) | Пример маршрута Gateway для доставки |
| [http://localhost:8081/api/v1/shopping-store](http://localhost:8081/api/v1/shopping-store) | API витрины товаров |
| [http://localhost:8082/api/v1/shopping-cart](http://localhost:8082/api/v1/shopping-cart) | API корзины |
| [http://localhost:8083/api/v1/warehouse/address](http://localhost:8083/api/v1/warehouse/address) | API адреса склада |
| [http://localhost:8084/api/v1/order](http://localhost:8084/api/v1/order) | API заказов |
| [http://localhost:8085/api/v1/payment/productCost](http://localhost:8085/api/v1/payment/productCost) | API оплаты |
| [http://localhost:8086/api/v1/delivery/cost](http://localhost:8086/api/v1/delivery/cost) | API доставки, первый локальный экземпляр |
| [http://localhost:8087/api/v1/delivery/cost](http://localhost:8087/api/v1/delivery/cost) | API доставки, второй локальный экземпляр при масштабировании |
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
- fallback-поведение Feign-клиентов при недоступных зависимых сервисах;
- доступность Actuator health и Prometheus endpoint'ов во всех Spring-сервисах.

## API-документация

Swagger UI доступен для commerce-сервисов после запуска стека:

| Сервис | Swagger UI | OpenAPI JSON |
| --- | --- | --- |
| `shopping-store` | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) |
| `shopping-cart` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) |
| `warehouse` | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) |
| `order` | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) | [http://localhost:8084/v3/api-docs](http://localhost:8084/v3/api-docs) |
| `payment` | [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) | [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs) |
| `delivery` | [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html) | [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs) |

## Наблюдаемость

Каждый Spring-сервис отдаёт Actuator health и Prometheus-метрики. Docker Compose использует health endpoint для проверки состояния контейнеров, а Prometheus собирает метрики с Prometheus endpoint.

| Сервис | Health | Prometheus metrics |
| --- | --- | --- |
| `eureka-server` | [http://localhost:8761/actuator/health](http://localhost:8761/actuator/health) | [http://localhost:8761/actuator/prometheus](http://localhost:8761/actuator/prometheus) |
| `config-server` | [http://localhost:8888/actuator/health](http://localhost:8888/actuator/health) | [http://localhost:8888/actuator/prometheus](http://localhost:8888/actuator/prometheus) |
| `shopping-store` | [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health) | [http://localhost:8081/actuator/prometheus](http://localhost:8081/actuator/prometheus) |
| `shopping-cart` | [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health) | [http://localhost:8082/actuator/prometheus](http://localhost:8082/actuator/prometheus) |
| `warehouse` | [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health) | [http://localhost:8083/actuator/prometheus](http://localhost:8083/actuator/prometheus) |
| `order` | [http://localhost:8084/actuator/health](http://localhost:8084/actuator/health) | [http://localhost:8084/actuator/prometheus](http://localhost:8084/actuator/prometheus) |
| `payment` | [http://localhost:8085/actuator/health](http://localhost:8085/actuator/health) | [http://localhost:8085/actuator/prometheus](http://localhost:8085/actuator/prometheus) |
| `delivery` | [http://localhost:8086/actuator/health](http://localhost:8086/actuator/health) | [http://localhost:8086/actuator/prometheus](http://localhost:8086/actuator/prometheus) |
| `api-gateway` | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus) |

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

<a id="connection-guides-ru"></a>
<a href="#connection-guides-ru" aria-label="Подсказка для раскрывающихся разделов">
  <img src="docs/assets/click-to-expand-ru.svg" alt="Нажмите стрелочки ниже, чтобы раскрыть пункты" width="980">
</a>

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
- синхронизация заказов и статусов из `order`;
- синхронизация статусов оплат из `payment`;
- синхронизация статусов доставки из `delivery`.

В текущем спринте реализованы витрина, корзина, склад, заказы, оплата, доставка и Gateway. CRM-синхронизацию теперь можно строить поверх API заказов, оплат и доставок.

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
mvn -pl commerce/order spring-boot:run
mvn -pl commerce/payment spring-boot:run
mvn -pl commerce/delivery spring-boot:run
mvn -pl api-gateway spring-boot:run
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
- `POST /api/v1/warehouse/assembly`
- `POST /api/v1/warehouse/shipped`
- `POST /api/v1/warehouse/return`
- `GET /api/v1/warehouse/address`

### Order, Payment, Delivery

Маршруты Gateway:

- `PUT /order` перенаправляется в `order` как `/api/v1/order`
- `GET /order?username=alice` перенаправляется в `order` как `/api/v1/order?username=alice`
- `POST /order/calculate/delivery` перенаправляется в `order` как `/api/v1/order/calculate/delivery`
- `POST /order/calculate/total` перенаправляется в `order` как `/api/v1/order/calculate/total`
- `POST /order/assembly` перенаправляется в `order` как `/api/v1/order/assembly`
- `POST /order/payment` перенаправляется в `order` как `/api/v1/order/payment`
- `POST /order/delivery` перенаправляется в `order` как `/api/v1/order/delivery`
- `POST /order/completed` перенаправляется в `order` как `/api/v1/order/completed`
- `POST /order/return` перенаправляется в `order` как `/api/v1/order/return`
- `POST /payment` перенаправляется в `payment` как `/api/v1/payment`
- `POST /delivery/cost` перенаправляется в `delivery` как `/api/v1/delivery/cost`
- `POST /payment/productCost` перенаправляется в `payment` как `/api/v1/payment/productCost`
- `POST /payment/totalCost` перенаправляется в `payment` как `/api/v1/payment/totalCost`
- `POST /payment/refund` перенаправляется в `payment` как `/api/v1/payment/refund`
- `POST /payment/failed` перенаправляется в `payment` как `/api/v1/payment/failed`
- `PUT /delivery` перенаправляется в `delivery` как `/api/v1/delivery`
- `POST /delivery/picked` перенаправляется в `delivery` как `/api/v1/delivery/picked`
- `POST /delivery/successful` перенаправляется в `delivery` как `/api/v1/delivery/successful`
- `POST /delivery/failed` перенаправляется в `delivery` как `/api/v1/delivery/failed`

## Конфигурация

Файлы конфигурации находятся здесь:

```text
config-server/config-repo
```

Docker использует переменные окружения из `docker-compose.yml`, чтобы подключить сервисы к PostgreSQL, Eureka и Config Server. Для бизнес-сервисов используются отдельные схемы PostgreSQL:

- `shopping_store`
- `shopping_cart`
- `warehouse`
- `orders`
- `payment`
- `delivery`

## Поведение Circuit Breaker

Commerce-сервисы обращаются друг к другу через Feign-клиенты. Критичные вызовы используют Circuit Breaker fallback, поэтому недоступные зависимости возвращают понятный `503 Service Unavailable`, а не низкоуровневые Feign-ошибки.

Например, если `warehouse` недоступен во время проверки остатков из `shopping-cart`, ответ будет таким:

```json
{
  "error": "WAREHOUSE_UNAVAILABLE",
  "message": "Warehouse service is temporarily unavailable. Please try again later."
}
```

HTTP-статус: `503 Service Unavailable`.

Новые сценарии заказов, оплат и доставок также имеют fallback-и для вызовов между `order`, `payment`, `delivery`, `warehouse` и `shopping-store`.
