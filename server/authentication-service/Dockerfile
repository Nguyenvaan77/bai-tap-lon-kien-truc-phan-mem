# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy the entire server directory into /app
COPY ./server/ .

# Build the authentication-service module
RUN mvn clean package -DskipTests -pl authentication-service -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/authentication-service/target/authentication-service-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
