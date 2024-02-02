# Java-explore-with-me project 

## Description :memo::
A multi-service app which allows to post info on interesting events and find companians to attend them. 
![example of app how it can look](https://github.com/ElenaSsV/java-explore-with-me/blob/main/View%20Example.jpg)
It consists of 2 services:

**Statistics service** - collects information on number of user requests to lists of events and on number of requests for detailed information about the event. Based on this information, it generates statistics about the operation of the application.

Endpoints [specification](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json) can be opened in [Swagger](https://editor-next.swagger.io/).

**Main service** - contains all the logic. API of main service is devided into:
  - ***public*** which is accessible by any user without registration;
  - ***private*** which is for registered users;
  - ***admin*** - for service admins.

Main service endpoints [specification](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json).

## Stack :hammer::
Java 11, Spring Boot, Hibernate, Spring Data, PostgreSQL, Maven, Docker, Lombok, Postman

## Instructions to deploy:
Requirements: JDK11, Docker

1. Clone [repository](https://github.com/ElenaSsV/java-explore-with-me);
2. mvn clean package;
3. docker-compose up




