# Online banking System

- The goal of this project is to create an online bank management system for bank customers and administrators to manage customer’s bank accounts. This application can simulate real-world banking activities. It enables bank customers to conduct virtual bank transactions, view bank transactions, manage their accounts, send messages to administrators, and track their expenses.

- The server side is built using SpringBoot technology and JAVA as the primary programming language. All bank data will be stored in the MySQL database and retrieved as needed for display, and client side technologies such as ReactJs, TailwindCSS/BootStrap, HTML, and other technologies such as Docker and required APIs will be used as needed.

- This application will be a web application for the best convenience of the customer. The webpages will be screen-size responsive. To ensure that the application is user-friendly for all customers, it will be tested with various web browsers, mobile devices, and PCs.

## ER Diagram

How to Run

From the server directory, run:

docker-compose up --build

This will:
Build all service JARs automatically
Start all databases (MySQL instances)
Start RabbitMQ
Start Discovery Service (Eureka)
Start Gateway Service
Start all microservices (Authentication, Customer, Account, Notification)

All services will be available at:
Gateway: http://localhost:8080
Eureka Dashboard: http://localhost:8761
RabbitMQ Management: http://localhost:15672 (guest/guest)
The configuration is ready to use. Services will wait for their dependencies (databases, RabbitMQ, Eureka) to be healthy before starting.

## Load Testing with Locust

This project includes Dockerized HTTP load testing using Locust to test the microservices system through the API Gateway.

### Quick Start

1. **Build the Locust image:**

   ```bash
   docker build -f Dockerfile.locust -t bank-locust .
   ```

2. **Run using Docker:**

   ```bash
   docker run -p 8089:8089 bank-locust
   ```

3. **Or using Docker Compose:**

   ```bash
   docker compose -f docker-compose.locust.yml up
   ```

4. **Open Locust UI in browser:**
   Navigate to http://localhost:8089

5. **Configure test parameters:**
   - **Number of users**: Total concurrent users to simulate
   - **Spawn rate**: Users to start per second
   - **Host**: Optional override (default: http://host.docker.internal:8080)

### Locust UI Features

The Locust web interface provides real-time statistics including:

- **Latency metrics:**
  - Median (50th percentile)
  - 95th percentile
  - 99th percentile
- **Performance metrics:**
  - Requests per second (RPS)
  - Failure rate
  - Response time distribution
- **Real-time charts** showing performance over time

### Test Scenarios

The default `locustfile.py` includes test tasks for:

- GET `/api/v1/account/{accountNo}` - Get account details
- GET `/api/v1/account/user/{userId}` - Get user accounts
- GET `/api/v1/user/{id}` - Get customer profile
- GET `/api/v1/beneficiary/{id}` - Get beneficiary information
- Optional authentication via `/api/v1/login` (disabled by default)

### Best Practices

- **Target the API Gateway**: All load tests should go through the API Gateway (port 8080), not individual services
- **Realistic wait times**: Default wait_time is 1-3 seconds between requests to simulate real user behavior
- **Dynamic data**: Avoid hardcoding single IDs - use dynamic values from previous responses in production tests
- **Host override**: When deploying to cloud environments, override the host URL in Locust UI or via command line
- **Monitor failure rates**: Adjust load based on failure rates and response times
- **Start small**: Begin with low user counts and gradually increase to find system limits

### Running Locust Directly (Without Docker)

If you prefer to run Locust directly on your machine:

```bash
pip install locust
locust -f locustfile.py --host http://localhost:8080
```

Then access the UI at http://localhost:8089

### Notes

- The Locust container uses `host.docker.internal:8080` to access the Gateway running on the host machine
- On Linux, you may need to add `--add-host=host.docker.internal:host-gateway` to the docker run command
- Authentication is disabled by default in `locustfile.py` - set `use_auth = True` and provide valid credentials to enable
- All endpoints require JWT authentication in production - update the test file with valid credentials for authenticated testing
