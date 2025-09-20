FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/unilist-0.0.1-API.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]