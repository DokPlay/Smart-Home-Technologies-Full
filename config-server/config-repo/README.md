# Config repo for local development

This directory acts as the local config repository for the Spring Cloud Config Server (native profile).

Structure:
- `shopping-store.yml` — config for `shopping-store` service
- `shopping-cart.yml` — config for `shopping-cart` service
- `warehouse.yml` — config for `warehouse` service
- `order.yml` — config for `order` service
- `payment.yml` — config for `payment` service
- `delivery.yml` — config for `delivery` service
- `api-gateway.yml` — config for Gateway routes

How to use:
1. Run `config-server` (it reads `config-repo` via `spring.cloud.config.server.native.search-locations`).
2. Start `eureka-server` on port 8761.
3. Start services — they will fetch configuration from `http://localhost:8888/{application}.yml`.
