# Smart Home Technologies — Internet Store (skeleton)

Репозиторий содержит каркас мульти-модульного Maven проекта для интернет-магазина Smart Home Technologies.

Структура: корневой POM -> модуль `commerce` -> подпроекты `interaction-api`, `shopping-store`, `shopping-cart`, `warehouse`.

Ветки (локально):
- `main` — пустая (создана как пустой коммит).
- `develop` — рабочая ветка с каркасом.
- создайте `7-spring-cloud-microservices` от `develop` для дальнейшей реализации.

Примеры команд (локально):

```bash
# создать пустую main (если нужно повторить):
# git checkout --orphan main
# git commit --allow-empty -m "Empty main branch"
# git push origin main

# добавить изменения в develop и создать ветку задания:
git add .
git commit -m "Initial skeleton: commerce modules"
# создать ветку от develop для работы
git checkout -b 7-spring-cloud-microservices
```

Дальнейшие шаги: реализовать DTO, Feign клиенты, контроллеры и интеграцию с Spring Cloud Config и Eureka.

Запуск локально (рекомендуемая последовательность):

1) Запустить `eureka-server` (порт 8761):

```bash
cd eureka-server
mvn spring-boot:run
```

2) Запустить `config-server` (порт 8888):

```bash
cd config-server
mvn spring-boot:run
```

3) Запустить сервисы (в любом порядке). Они будут регистрироваться в Eureka и получать конфиги из Config Server:

```bash
cd commerce/warehouse
mvn spring-boot:run

cd ../shopping-store
mvn spring-boot:run

cd ../shopping-cart
mvn spring-boot:run
```

4) Тестирование Feign-вызова локально: отправьте POST на `shopping-cart`:

```bash
curl -X POST http://localhost:{cart-port}/api/cart/alice/items -H 'Content-Type: application/json' -d '{"productId":1, "quantity":2}'
```

Интеграционные тесты для `shopping-cart` содержат мок для `WarehouseFeignClient` и проверяют взаимодействие.
