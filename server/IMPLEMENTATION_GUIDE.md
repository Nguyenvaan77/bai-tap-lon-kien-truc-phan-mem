# 🚀 Database Separation Implementation Guide

## 📋 Quick Start Checklist

- [ ] Review DATABASE_SEPARATION_STRATEGY.md
- [ ] Update pom.xml with Spring Cloud Stream dependencies
- [ ] Create event classes (done - in shared-events module)
- [ ] Create event publishers (done - in each service)
- [ ] Create event listeners (done - in each service)
- [ ] Update docker-compose for separate databases
- [ ] Configure RabbitMQ
- [ ] Test event flow
- [ ] Execute migration
- [ ] Monitor and validate

---

## 📚 Files Created

### 1. **Strategy Document**
- `DATABASE_SEPARATION_STRATEGY.md` - Comprehensive architecture and migration plan

### 2. **Event Classes** (in shared-events module)
- `UserCreatedEvent.java` - Published by Auth Service
- `AccountCreatedEvent.java` - Published by Account Service
- `TransactionCompletedEvent.java` - Published by Account Service

### 3. **Event Publishers**
- `authentication-service/event/UserEventPublisher.java`
- `account-service/event/AccountEventPublisher.java`

### 4. **Event Listeners**
- `customer-service/event/UserSyncEventListener.java`
- `notification-service/event/NotificationEventListener.java`

### 5. **Configuration**
- `EVENT_STREAMING_CONFIG.properties` - RabbitMQ/Kafka configuration
- `docker-compose-separated.yml` - Docker setup with separate DBs and RabbitMQ

---

## 🔧 Step 1: Update Parent POM

Add these dependencies to `server/pom.xml`:

```xml
<!-- Spring Cloud Stream for Event Streaming -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    <version>4.1.1</version>
</dependency>

<!-- Lombok for reducing boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>

<!-- Jackson for JSON serialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.1</version>
</dependency>
```

---

## 📦 Step 2: Create Shared Events Module

```bash
# In server/ directory
mvn archetype:generate -DgroupId=bankproject -DartifactId=shared-events \
  -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

# Add to parent pom.xml
<module>shared-events</module>
```

Add event classes to `shared-events/src/main/java/bankproject/shared/event/`

---

## 📝 Step 3: Update Service Application Properties

### **Authentication Service** (`authentication-service/src/main/resources/application.properties`)

Add these lines:
```properties
# Spring Cloud Stream - User Created Event Publisher
spring.cloud.stream.bindings.user-created-out-0.destination=user.created
spring.cloud.stream.bindings.user-created-out-0.contentType=application/json

# RabbitMQ
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${RABBITMQ_PORT:5672}
spring.rabbitmq.username=${RABBITMQ_USERNAME:guest}
spring.rabbitmq.password=${RABBITMQ_PASSWORD:guest}

# New Database
spring.datasource.url=jdbc:mysql://localhost:3307/db_auth
```

### **Customer Service** (`customer-service/src/main/resources/application.properties`)

Add these lines:
```properties
# Spring Cloud Stream - User Created Event Subscriber
spring.cloud.stream.bindings.user-created-in-0.destination=user.created
spring.cloud.stream.bindings.user-created-in-0.group=customer-service
spring.cloud.stream.bindings.user-created-in-0.contentType=application/json

# RabbitMQ
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${RABBITMQ_PORT:5672}
spring.rabbitmq.username=${RABBITMQ_USERNAME:guest}
spring.rabbitmq.password=${RABBITMQ_PASSWORD:guest}

# New Database
spring.datasource.url=jdbc:mysql://localhost:3308/db_customer
```

### **Account Service** (`account-service/src/main/resources/application.properties`)

Add these lines:
```properties
# Spring Cloud Stream - Publishers
spring.cloud.stream.bindings.account-created-out-0.destination=account.created
spring.cloud.stream.bindings.account-created-out-0.contentType=application/json
spring.cloud.stream.bindings.transaction-completed-out-0.destination=transaction.completed
spring.cloud.stream.bindings.transaction-completed-out-0.contentType=application/json

# RabbitMQ
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${RABBITMQ_PORT:5672}
spring.rabbitmq.username=${RABBITMQ_USERNAME:guest}
spring.rabbitmq.password=${RABBITMQ_PASSWORD:guest}

# New Database
spring.datasource.url=jdbc:mysql://localhost:3309/db_account
```

### **Notification Service** (`notification-service/src/main/resources/application.properties`)

Add these lines:
```properties
# Spring Cloud Stream - Subscribers
spring.cloud.stream.bindings.account-created-in-0.destination=account.created
spring.cloud.stream.bindings.account-created-in-0.group=notification-service
spring.cloud.stream.bindings.account-created-in-0.contentType=application/json

spring.cloud.stream.bindings.transaction-completed-in-0.destination=transaction.completed
spring.cloud.stream.bindings.transaction-completed-in-0.group=notification-service
spring.cloud.stream.bindings.transaction-completed-in-0.contentType=application/json

# RabbitMQ
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${RABBITMQ_PORT:5672}
spring.rabbitmq.username=${RABBITMQ_USERNAME:guest}
spring.rabbitmq.password=${RABBITMQ_PASSWORD:guest}

# New Database
spring.datasource.url=jdbc:mysql://localhost:3310/db_notification
```

---

## 🐳 Step 4: Start Services with Docker Compose

```bash
# Start with separate databases and RabbitMQ
docker-compose -f docker-compose-separated.yml up -d

# Check status
docker-compose -f docker-compose-separated.yml ps

# View RabbitMQ Management UI
# URL: http://localhost:15672
# Username: guest
# Password: guest

# View logs
docker-compose -f docker-compose-separated.yml logs -f authentication-service
docker-compose -f docker-compose-separated.yml logs -f customer-service
docker-compose -f docker-compose-separated.yml logs -f account-service
docker-compose -f docker-compose-separated.yml logs -f notification-service
```

---

## 🧪 Step 5: Test Event Flow

### **Test 1: User Creation Event**

```bash
# 1. Create a user (Auth Service publishes event)
curl -X POST http://localhost:8080/api/v1/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "John",
    "lastname": "Doe",
    "email": "john@example.com",
    "password": "Password@123"
  }'

# 2. Check logs
docker-compose -f docker-compose-separated.yml logs authentication-service | grep "UserCreatedEvent"
docker-compose -f docker-compose-separated.yml logs customer-service | grep "Received UserCreatedEvent"

# 3. Verify data in customer-db
mysql -h 127.0.0.1 -P 3308 -u root -p160220057a db_customer
SELECT * FROM userdetails WHERE userid = 'xxx';
```

### **Test 2: Account Creation Event**

```bash
# 1. Login to get JWT token
JWT_TOKEN=$(curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"Password@123"}' | jq -r '.jwtToken')

# 2. Create an account (Account Service publishes event)
curl -X POST http://localhost:8080/api/v1/account/create \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "xxx",
    "accountType": "Savings",
    "balance": 10000.00
  }'

# 3. Check logs
docker-compose -f docker-compose-separated.yml logs account-service | grep "AccountCreatedEvent"
docker-compose -f docker-compose-separated.yml logs notification-service | grep "Received AccountCreatedEvent"

# 4. Verify account was created
mysql -h 127.0.0.1 -P 3309 -u root -p160220057a db_account
SELECT * FROM bankaccount WHERE userid = 'xxx';
```

---

## 🔄 Step 6: Verify RabbitMQ Topics

```bash
# View RabbitMQ exchanges and queues
curl -u guest:guest http://localhost:15672/api/exchanges/%2F

# List all queues
curl -u guest:guest http://localhost:15672/api/queues/%2F

# Check queue bindings
curl -u guest:guest http://localhost:15672/api/bindings/%2F

# Purge a queue (delete messages)
curl -u guest:guest -X DELETE http://localhost:15672/api/queues/%2F/user.created/purge
```

---

## 📊 Step 7: Monitor Message Flow

### **Using RabbitMQ Management UI**

1. Go to http://localhost:15672
2. Login with guest/guest
3. Navigate to "Queues" tab
4. View message counts for:
   - `user.created`
   - `account.created`
   - `transaction.completed`

### **Using Logs**

```bash
# Watch all service logs
docker-compose -f docker-compose-separated.yml logs -f

# Watch specific service
docker-compose -f docker-compose-separated.yml logs -f notification-service --tail=50
```

---

## ⚠️ Troubleshooting

### **Issue: "Cannot find bean of type StreamBridge"**
**Solution:**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-test-support</artifactId>
    <scope>test</scope>
</dependency>
```

### **Issue: "Connection refused to RabbitMQ"**
**Solution:**
```bash
# Check RabbitMQ is running
docker-compose -f docker-compose-separated.yml ps rabbitmq

# Check logs
docker-compose -f docker-compose-separated.yml logs rabbitmq

# Restart RabbitMQ
docker-compose -f docker-compose-separated.yml restart rabbitmq
```

### **Issue: "Event not being consumed"**
**Solution:**
1. Verify `@Bean` method in EventListener class
2. Check consumer group name matches in properties
3. Check destination/topic name matches in publisher and listener
4. View RabbitMQ UI to confirm queue exists

### **Issue: "Database connection failed"**
**Solution:**
```bash
# Check database is running
docker-compose -f docker-compose-separated.yml ps mysql-*

# Test connection
mysql -h 127.0.0.1 -P 3307 -u root -p160220057a db_auth -e "SELECT 1;"

# Check datasource URL in application.properties
```

---

## 📈 Performance Monitoring

### **Database Connections**
```bash
# Check connection pool
curl http://localhost:8081/actuator/health

# Expected output:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "pool": {
          "active": 5,
          "pending": 0,
          "idle": 5,
          "size": 10
        }
      }
    }
  }
}
```

### **Message Broker Health**
```bash
# Check RabbitMQ
curl -u guest:guest http://localhost:15672/api/healthchecks/node

# Expected output:
{
  "status": "ok"
}
```

---

## 🔄 Migration Phases (Recap)

| Phase | Duration | Action | Status |
|-------|----------|--------|--------|
| 1 | Days 1-3 | Dual Write Setup | Ready |
| 2 | Days 4-5 | Data Sync | Ready |
| 3 | Days 6-7 | Read from New DB | Ready |
| 4 | Days 8-10 | Event-Driven Sync | Ready |
| 5 | Day 11+ | Stop Dual Write | Ready |

---

## 📞 Support & Documentation

- **RabbitMQ Docs:** https://www.rabbitmq.com/documentation.html
- **Spring Cloud Stream:** https://spring.io/projects/spring-cloud-stream
- **Database Separation Guide:** See DATABASE_SEPARATION_STRATEGY.md

---

**Last Updated:** December 24, 2025  
**Version:** 1.0.0  
**Status:** Ready for Implementation
