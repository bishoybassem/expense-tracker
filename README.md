# Expense Tracker

[![Build Status](https://travis-ci.org/bishoybassem/expense-tracker.svg?branch=master)](https://travis-ci.org/bishoybassem/expense-tracker)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/31f708e31a814508ae405fb02c9acba2)](https://www.codacy.com/app/bishoybassem/expense-tracker?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bishoybassem/expense-tracker&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/31f708e31a814508ae405fb02c9acba2)](https://www.codacy.com/app/bishoybassem/expense-tracker?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bishoybassem/expense-tracker&amp;utm_campaign=Badge_Coverage)

This project implements a web application for tracking expenses, where users would log their daily expenses/incomes, and get back daily/monthly/yearly expense reports. The aim of this project is to practice microservices architecture, not only the application development aspect, but the whole continuous integration (CI) pipeline. 

## Implementation

The application's architecture is shown in the bellow diagram, in addition to brief descriptions for the webapps and Gradle modules within this project:

<img align="right" width="275" src="diagram.png"/>

* __authenticator__: a Spring Boot microservice that offers an API for user signup, persists the user data to a PostgreSQL database, and issues JSON Web Tokens for user login requests. The JWT holds the user's identity, and is required/verified by the other microservices.
  
* __tracker__: a Spring Boot microservice that offers a CRUD API for managing the transactions (expenses/incomes), persists the transaction data to a PostgreSQL database, and notifies other services about transaction events asynchronously through RabbitMQ.

* __reporter__: a Spring Boot microservice that consumes transaction events from RabbitMQ (posted by the tracker microservice), aggregates the transaction data to the corresponding reports, persists the reports to a MongoDB instance, and finally, exposes those reports via an API.

* __api-commons__: a module that holds common POJOs for API responses, along with common servlet filters for JWT verification and request logging.    

* __mq-messages__: a module that offers POJOs, used for the exchange of transaction events between the producers (tracker) and the consumers (reporter).
[Protobuf](https://developers.google.com/protocol-buffers) is used for generating the POJOs, as well as serialization/deserialization of the event messages.

* __blackbox-tests__: a module that includes full integration tests, simulating different workflows.

For new commits, an automatic build is triggered by [Travis CI](https://travis-ci.org/bishoybassem/expense-tracker) that does the following:
  1. Compiles the code.
  2. Runs unit tests.
  3. Starts all apps and services in containers.
  4. Runs integration/blackbox tests.
  5. Collects code coverage during unit and integration test executions.
  6. Uploads code coverage to [Codacy](https://app.codacy.com/project/bishoybassem/expense-tracker/dashboard).

Additionally, an automatic code review is triggered by [Codacy](https://app.codacy.com/project/bishoybassem/expense-tracker/dashboard), which would identify issues/bugs through static code analysis, and monitor code quality over time.

## Demo
To test the setup locally, the following needs to be present/installed:
* JDK (used OpenJDK version 11.0.6).
* Docker (used version 19.03.8-ce).
* Docker Compose (used version 1.25.4).

After installing the requirements listed above, do the following:
1. Clone the repository, and navigate to the clone directory.
2. Compile the code, assemble the webapps, and start all the services in Docker by executing this task:
   ```bash
   ./gradlew clean composeUp
   ```
3. Register a user:
   ```bash
   curl -X POST http://localhost:8080/v1/users \
     -H 'Content-Type: application/json' \
     -d '{"email": "test@gmail.com", "name": "test", "password": "Test1234"}'
   ```
4. Login with the created user:
   ```bash
   export TOKEN=$(curl -sX POST http://localhost:8080/v1/access-tokens \
     -H 'Content-Type: application/json' \
     -d '{"email": "test@gmail.com", "password": "Test1234"}' | jq -r .token)
   ```
5. Create a transaction using the access token from the previous step:
   ```bash
   curl -X POST http://localhost:8080/v1/transactions \
     -H 'Content-Type: application/json' \
     -H "X-Access-Token: $TOKEN" \
     -d '{"type": "EXPENSE","amount": "1.23","category": "abc","date": "2017/03/20","comment": "xyz"}'
   ```
6. Query the report using the access token from step 4:
   ```bash
   curl http://localhost:8080/v1/reports/2017/03/20 \
     -H "X-Access-Token: $TOKEN"
   ```