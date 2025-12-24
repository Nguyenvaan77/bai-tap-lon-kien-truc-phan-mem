# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy the entire server directory into /app
COPY ./server/ .

# Build the gateway-service module
RUN mvn clean package -DskipTests -pl gateway-service -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/gateway-service/target/gateway-service-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
