# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy the entire server directory into /app
COPY ./server/ .

# Build the account-service module
RUN mvn clean package -DskipTests -pl account-service -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/account-service/target/account-service-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
