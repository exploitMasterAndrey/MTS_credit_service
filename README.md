Дипломная работа МТС Финтех 
«ОФОРМЛЕНИЕ КРЕДИТА»

Технологии:

    - Java
    - Spring
    - Kafka
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
    
    Метод входа в систему (username = andrey, password = andrey)
        POST /loan-service/login
        body: {
                "username":{username},
                "password":"{password}"
              }

    Метод добавления тарифов (access_token приходит в ответе на запрос login)
        POST /loan-service/tairff
        headers: {
                   "Authorization": {access_token}
                 }
        body: {
               "type":{type}
               "interest_rate":{interest_rate}
              }

Для запуска проекта:

    1. Установить Docker с оффициального сайта https://www.docker.com/
    2. Выбрать папку, где будет храниться проект
    3. В выбранной папке прописать команду "git clone https://github.com/exploitMasterAndrey/MTS_credit_service.git"
    4. В скаченном проекте найти файл "mvnw" и в нем поменять Line Separator с "CRLF" на "LF"
    5. В терминале прописать команду docker-compose up