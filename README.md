[Russian version](README_RU.md)

# Smart Home Technologies - Commerce Microservices

Smart Home Technologies Commerce is a Spring Boot and Spring Cloud microservice application for an online smart-home device store. It provides product catalog, shopping cart, and warehouse services with shared API contracts, service discovery, externalized configuration, and Docker-based local infrastructure.

The project is designed as a scalable backend foundation for selling smart-home devices. Each business capability is implemented as a separate service with its own data model and database schema.

## Features

- Product catalog with category filtering, product details, soft deletion, and stock availability state.
- Shopping cart management by username, including add, remove, quantity change, and cart deactivation.
- Warehouse product registration, stock replenishment, stock availability checks, and warehouse address lookup.
- Shared DTO and API contracts in a dedicated `interaction-api` module.
- Service discovery through Eureka.
- Externalized service configuration through Spring Cloud Config.
- Feign-based REST communication between services.
- Circuit Breaker support for `shopping-cart -> warehouse` calls with a clear `503` response when the warehouse is unavailable.
- Swagger/OpenAPI documentation for the commerce HTTP APIs.
- Actuator health checks and Prometheus metrics for every Spring service.
- Dockerized Prometheus and Grafana with a preconfigured Smart Home dashboard.
- PostgreSQL schemas per service when running with Docker.
- Focused tests for service logic, API contracts, fallback behavior, and observability endpoints.

## Architecture

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

### Services

| Service | Port in Docker | Responsibility |
| --- | ---: | --- |
| `eureka-server` | `8761` | Service discovery |
| `config-server` | `8888` | Centralized configuration |
| `shopping-store` | `8081` | Product catalog |
| `shopping-cart` | `8082` | User shopping carts |
| `warehouse` | `8083` | Stock, booking checks, warehouse address |
| `postgres` | `5432` | Shared PostgreSQL server with separate schemas |
| `prometheus` | `9090` | Metrics scraping and querying |
| `grafana` | `3000` | Metrics dashboards |

## Requirements

- Docker Desktop or Docker Engine with Docker Compose.
- Java 17+ and Maven 3.9+ for local builds without Docker.

## Quick Start With Docker

From the repository root:

```bash
docker compose up --build -d
```

Check that all containers are running:

```bash
docker compose ps
```

Open the service registry:

[http://localhost:8761](http://localhost:8761)

Expected registered applications:

- `SHOPPING-STORE`
- `SHOPPING-CART`
- `WAREHOUSE`

Useful local URLs:

| URL | Description |
| --- | --- |
| [http://localhost:8761](http://localhost:8761) | Eureka dashboard |
| [http://localhost:8888/shopping-cart/default](http://localhost:8888/shopping-cart/default) | Config Server sample response |
| [http://localhost:8081/api/v1/shopping-store](http://localhost:8081/api/v1/shopping-store) | Product catalog API |
| [http://localhost:8082/api/v1/shopping-cart](http://localhost:8082/api/v1/shopping-cart) | Shopping cart API |
| [http://localhost:8083/api/v1/warehouse/address](http://localhost:8083/api/v1/warehouse/address) | Warehouse address API |
| [http://localhost:9090/targets](http://localhost:9090/targets) | Prometheus scrape targets |
| [http://localhost:3000](http://localhost:3000) | Grafana dashboard, login `admin` / `admin` |

Stop the application:

```bash
docker compose down
```

Stop the application and remove database data:

```bash
docker compose down -v
```

## Build and Test Locally

Run the full Maven verification:

```bash
mvn clean verify
```

The test suite covers:

- product catalog contract and service behavior;
- shopping cart contract and business rules;
- warehouse stock logic and booking calculations;
- Feign fallback behavior for unavailable warehouse service;
- Actuator health and Prometheus endpoint exposure for all Spring services.

## API Documentation

Swagger UI is available for the commerce services when the stack is running:

| Service | Swagger UI | OpenAPI JSON |
| --- | --- | --- |
| `shopping-store` | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) |
| `shopping-cart` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) |
| `warehouse` | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) |

## Observability

Every Spring service exposes Actuator health and Prometheus metrics. Docker Compose health checks use the health endpoint, and Prometheus scrapes the metrics endpoint.

| Service | Health | Prometheus metrics |
| --- | --- | --- |
| `eureka-server` | [http://localhost:8761/actuator/health](http://localhost:8761/actuator/health) | [http://localhost:8761/actuator/prometheus](http://localhost:8761/actuator/prometheus) |
| `config-server` | [http://localhost:8888/actuator/health](http://localhost:8888/actuator/health) | [http://localhost:8888/actuator/prometheus](http://localhost:8888/actuator/prometheus) |
| `shopping-store` | [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health) | [http://localhost:8081/actuator/prometheus](http://localhost:8081/actuator/prometheus) |
| `shopping-cart` | [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health) | [http://localhost:8082/actuator/prometheus](http://localhost:8082/actuator/prometheus) |
| `warehouse` | [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health) | [http://localhost:8083/actuator/prometheus](http://localhost:8083/actuator/prometheus) |

Prometheus is available at [http://localhost:9090](http://localhost:9090). Use [http://localhost:9090/targets](http://localhost:9090/targets) to verify that all service targets are `UP`.

Grafana is available at [http://localhost:3000](http://localhost:3000), with local credentials `admin` / `admin`. In the local Docker profile the first-login password change prompt is disabled, so the provisioned `Smart Home Technologies Overview` dashboard opens immediately after sign-in. The Prometheus datasource is provisioned automatically.

If the default ports are already used by another local stack, override them before startup. The recommended local option is a `.env` file in the repository root:

```env
PROMETHEUS_HOST_PORT=9091
GRAFANA_HOST_PORT=3002
```

Then start the stack normally:

```bash
docker compose up --build -d
```

With the example above, open Grafana at [http://localhost:3002](http://localhost:3002).

You can also pass the ports directly in the startup command:

```bash
PROMETHEUS_HOST_PORT=9091 GRAFANA_HOST_PORT=3002 docker compose up --build -d
```

On Windows PowerShell:

```powershell
$env:PROMETHEUS_HOST_PORT = "9091"
$env:GRAFANA_HOST_PORT = "3002"
docker compose up --build -d
```

## Connecting To A Website, Hosting, CMS, Or CRM

This repository is the backend part of the store. A public website, admin panel, CMS, or CRM should connect to it through REST APIs. In production, expose only the public API entrypoint through HTTPS; keep PostgreSQL, Eureka, Config Server, Prometheus, and internal service ports private.

<details>
<summary>Website or frontend application</summary>

Use the store backend as an API for any frontend: React, Next.js, Vue, a static website, a mobile app, or a CMS theme.

Recommended frontend configuration:

```env
SHOP_API_BASE_URL=https://api.example.com
```

For browser frameworks, use the public environment variable format required by that framework, for example:

```env
NEXT_PUBLIC_SHOP_API_BASE_URL=https://api.example.com
VITE_SHOP_API_BASE_URL=https://api.example.com
```

Basic product catalog request:

```js
const apiBaseUrl = import.meta.env.VITE_SHOP_API_BASE_URL;

const response = await fetch(`${apiBaseUrl}/api/v1/shopping-store?category=SENSORS`);
const products = await response.json();
```

Basic shopping cart request:

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

For local development without a reverse proxy, the direct service URLs are:

| Service | Local URL |
| --- | --- |
| Product catalog | `http://localhost:8081` |
| Shopping cart | `http://localhost:8082` |
| Warehouse | `http://localhost:8083` |

For production websites, prefer one public API domain, for example `https://api.example.com`, with a reverse proxy routing each `/api/v1/...` path to the correct internal service.

</details>

<details>
<summary>Hosting with Docker Compose</summary>

Use this option for a VPS, dedicated server, or platform that supports Docker Compose.

1. Install Docker and Docker Compose on the server.
2. Copy or clone the repository to the server.
3. Create a `.env` file in the repository root for host-specific ports and secrets.
4. Start the stack:

```bash
docker compose up --build -d
```

5. Check containers:

```bash
docker compose ps
```

6. Check service health:

```bash
curl http://localhost:8761/actuator/health
curl http://localhost:8888/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

For production, do not publish internal infrastructure directly to the internet. Put the public domain in front of the commerce APIs and protect admin or monitoring endpoints.

</details>

<details>
<summary>Reverse proxy for one public API domain</summary>

A reverse proxy lets the website use one stable API base URL while services remain separated internally.

Example public API URL:

```text
https://api.example.com
```

Example Nginx routing:

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

Expose Swagger, Actuator, Prometheus, and Grafana only when you intentionally need them. For a public environment, protect them with a VPN, basic auth, IP allowlist, or a private network.

</details>

<details>
<summary>CMS or CRM integration</summary>

Connect a CMS or CRM as an external REST client. The CMS/CRM should call the same public API domain that the website uses.

Typical CMS integration:

- render product lists from `GET /api/v1/shopping-store`;
- render product details from `GET /api/v1/shopping-store/{productId}`;
- call the shopping cart API from custom server-side actions, plugins, or theme code;
- keep admin product-management calls behind a protected admin area.

Typical CRM integration:

- sync product catalog data from `shopping-store`;
- send cart or lead data from the website to the CRM through a small integration service;
- later, when order, payment, and delivery services are added, sync orders and order statuses from those services.

Current sprint scope includes product catalog, shopping cart, and warehouse. Full order, payment, and delivery CRM synchronization should be added after those services exist.

</details>

<details>
<summary>Production checklist before exposing the API</summary>

- Put HTTPS in front of public traffic.
- Add authentication and authorization before exposing admin endpoints.
- Configure CORS for the real website domain if browser requests come from another domain.
- Store secrets in environment variables or hosting secrets, not in Git.
- Keep PostgreSQL, Eureka, Config Server, Prometheus, and direct service ports private.
- Use database backups and persistent volumes.
- Monitor `/actuator/health`, Prometheus targets, and Grafana dashboards.
- Add rate limiting at the gateway or reverse proxy for public APIs.
- Use separate environment values for development, staging, and production.

</details>

## Running Services Without Docker

Start infrastructure first:

```bash
mvn -pl eureka-server spring-boot:run
mvn -pl config-server spring-boot:run
```

Then start the commerce services:

```bash
mvn -pl commerce/warehouse spring-boot:run
mvn -pl commerce/shopping-store spring-boot:run
mvn -pl commerce/shopping-cart spring-boot:run
```

When running without Docker, the commerce services use the configuration from `config-server/config-repo` and default to in-memory H2 databases unless datasource environment variables are provided.

## API Overview

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

## Configuration

Configuration files are stored in:

```text
config-server/config-repo
```

Docker uses environment variables from `docker-compose.yml` to connect services to PostgreSQL, Eureka, and Config Server. Business services use separate PostgreSQL schemas:

- `shopping_store`
- `shopping_cart`
- `warehouse`

## Circuit Breaker Behavior

`shopping-cart` calls `warehouse` through a Feign client. If the warehouse service is unavailable, the Circuit Breaker fallback returns an immediate meaningful response:

```json
{
  "error": "WAREHOUSE_UNAVAILABLE",
  "message": "Warehouse service is temporarily unavailable. Please try again later."
}
```

HTTP status: `503 Service Unavailable`.
