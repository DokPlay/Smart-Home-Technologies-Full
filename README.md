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
