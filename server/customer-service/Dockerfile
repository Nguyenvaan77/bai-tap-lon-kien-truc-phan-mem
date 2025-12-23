FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/customer-service-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
