# Online Banking Microservices

Microservices-based architecture for an online banking application built with Spring Boot 3.3.4 and Java 21.

## Architecture

The application is divided into 6 microservices:

1. **Discovery Service** (Port 8761)
   - Eureka Server for service discovery and registration
   - All other services register themselves here

2. **Gateway Service** (Port 8080)
   - API Gateway using Spring Cloud Gateway
   - Routes requests to appropriate microservices
   - Single entry point for all clients

3. **Authentication Service** (Port 8081)
   - User signup and login
   - JWT token generation and validation
   - User authentication for other services

4. **Customer Service** (Port 8082)
   - User profile management
   - Customer information
   - Beneficiary management

5. **Account Service** (Port 8083)
   - Bank account management
   - Transaction history
   - Fund transfers between accounts

6. **Notification Service** (Port 8084)
   - Email notifications
   - OTP generation and sending
   - Transaction confirmations

## Technology Stack

- **Java**: 21 LTS
- **Spring Boot**: 3.3.4
- **Spring Cloud**: 2023.0.3
- **Build Tool**: Maven
- **Database**: MySQL 8.0
- **Container**: Docker & Docker Compose
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Inter-service Communication**: OpenFeign
- **Authentication**: JWT (JJWT 0.12.3)
- **Logging**: SLF4J with Logback

## Service Startup Order

1. **MySQL Database** - Store all data
2. **Discovery Service** - Service registry
3. **Gateway Service** - API gateway
4. **Authentication Service** - User auth
5. **Customer Service** - User management
6. **Account Service** - Account operations
7. **Notification Service** - Notifications

## Running the Services

### Using Docker Compose (Recommended)

```bash
cd server
docker-compose up --build
```

This will start all services with MySQL database.

### Running Locally (Without Docker)

Make sure MySQL is running locally first:
```bash
mysql -u root -p < database.sql
```

Then start each service in order:

```bash
# Terminal 1: Discovery Service
cd discovery-service
mvn spring-boot:run

# Terminal 2: Gateway Service
cd gateway-service
mvn spring-boot:run

# Terminal 3: Authentication Service
cd authentication-service
mvn spring-boot:run

# Terminal 4: Customer Service
cd customer-service
mvn spring-boot:run

# Terminal 5: Account Service
cd account-service
mvn spring-boot:run

# Terminal 6: Notification Service
cd notification-service
mvn spring-boot:run
```

## API Endpoints

All API calls go through the Gateway (http://localhost:8080):

### Authentication Endpoints
- `POST /api/v1/signup` - User registration
- `POST /api/v1/login` - User login
- `POST /api/v1/otp` - OTP verification
- `POST /api/v1/resend-otp/{userId}` - Resend OTP

### Customer Endpoints
- `GET /api/v1/user/{id}` - Get user profile
- `PUT /api/v1/user/{id}` - Update user profile
- `POST /api/v1/beneficiary` - Add beneficiary
- `GET /api/v1/beneficiary/{id}` - Get beneficiary

### Account Endpoints
- `POST /api/v1/account/open` - Open new account
- `GET /api/v1/account/{id}` - Get account details
- `GET /api/v1/transaction` - Get transactions
- `POST /api/v1/transfer` - Transfer funds

### Notification Endpoints
- `POST /api/v1/notification/mail` - Send email

## Database

The application uses MySQL 8.0. Database migrations are handled automatically via Spring Data JPA with Hibernate.

Connection details:
- URL: `jdbc:mysql://localhost:3306/onlinebanking`
- Username: `root`
- Password: `password`

## Configuration

Each service has its own `application.properties` file in `src/main/resources/`:

- Service port
- Database connection details
- Eureka server URL
- JWT secret and expiration
- Email configuration

## Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Gateway**: http://localhost:8080
- Individual service health checks available at each service port

## Inter-service Communication

Services communicate with each other using **OpenFeign** HTTP client for:
- Authentication service → Notification service (sending emails)
- Customer service → Notification service (user notifications)
- Account service → Notification service (transaction notifications)

## Migration from Monolithic Architecture

This structure represents a migration from the original monolithic `onlinebanking` application to a microservices architecture similar to the `bank` project structure, providing:

- Better scalability
- Independent deployment of services
- Clear separation of concerns
- Improved fault isolation
- Better load distribution

## Future Enhancements

- Add API rate limiting
- Implement circuit breakers (Hystrix/Resilience4j)
- Add distributed tracing (Sleuth/Jaeger)
- Implement event sourcing and CQRS patterns
- Add API documentation (Swagger/Springdoc)
- Implement service-to-service authentication
- Add monitoring and metrics (Micrometer/Prometheus)

## License

This project is a demonstration of microservices architecture with Spring Boot.
