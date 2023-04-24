Дипломная работа МТС Финтех 
«ОФОРМЛЕНИЕ КРЕДИТА»

Технологии:

    - Java
    - Spring
    - Liquibase
    - JdbcTemplate
    - Testcontainers
    - Swagger
    - PostgreSql
    - Lombok

API:

    Метод получения тарифов
        GET /loan-service/getTariffs  

    Метод подачи заявки на кредит
        POST /loan-service/order
        body: {
                "userId":{userId},
                "tariffId":{tariffId}
              }

    Метод получения статуса заявки
        GET /loan-service/getStatusOrder?orderId={orderId}

    Метод удаления заявки
        DELETE /loan-service/deleteOrder
        body: {
                "userId":{userId},
                "orderId":"{orderId}"
              }

Для запуска проекта:

    1. Создать базу данных
    2. Установить url базы в application.properties
        spring:
            datasource:
                url: {url}
    