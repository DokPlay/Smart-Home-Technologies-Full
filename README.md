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
- PostgreSQL schemas per service when running with Docker.
- Focused tests for service logic, API contracts, and fallback behavior.

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
- Feign fallback behavior for unavailable warehouse service.

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
