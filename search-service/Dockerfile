FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]