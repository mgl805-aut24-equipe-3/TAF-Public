FROM maven:3.8.6-eclipse-temurin-17 as builder
WORKDIR /testapi
COPY pom.xml .
COPY . .
# COPY ./src ./src
RUN mvn clean install
EXPOSE 8082
ENTRYPOINT ["mvn", "spring-boot:run" ]