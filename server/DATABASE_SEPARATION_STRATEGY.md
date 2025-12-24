# 🏗️ Microservices Database Separation Strategy

## 📑 Mục Lục
1. [Bounded Context & Database Mapping](#bounded-context)
2. [Event-Driven Architecture](#event-architecture)
3. [Code Changes Required](#code-changes)
4. [Spring Boot Implementation Examples](#implementation)
5. [Data Migration Strategy](#migration)
6. [Benefits, Risks & Mitigation](#benefits-risks)
7. [Testing Strategy](#testing)
8. [Rollback Plan](#rollback)

---

## 🎯 Bounded Context & Database Mapping {#bounded-context}

### **1. Authentication Service (auth-db)**

**Database:** `db_auth`  
**Port:** 3307  
**Ownership:** User identity & security

**Tables:**
```sql
CREATE DATABASE db_auth;

CREATE TABLE userdata (
  userid VARCHAR(36) PRIMARY KEY,
  firstname VARCHAR(50),
  lastname VARCHAR(50),
  email VARCHAR(100) UNIQUE,
  password VARCHAR(255),
  role VARCHAR(20),
  enabled BOOLEAN,
  otp VARCHAR(10),
  otp_created TIMESTAMP,
  createdate DATE
);

CREATE TABLE role (
  rolename VARCHAR(20) PRIMARY KEY,
  description VARCHAR(255)
);

CREATE TABLE user_otp_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid VARCHAR(36),
  otp VARCHAR(10),
  created_at TIMESTAMP,
  verified_at TIMESTAMP,
  status VARCHAR(20)
);
```

**Bounded Context:**
- ✅ User credentials
- ✅ Authentication tokens (JWT)
- ✅ Password reset
- ✅ OTP verification
- ❌ User profile (belongs to Customer Service)
- ❌ User accounts (belongs to Account Service)

---

### **2. Customer Service (customer-db)**

**Database:** `db_customer`  
**Port:** 3308  
**Ownership:** Customer profile & relationships

**Tables:**
```sql
CREATE DATABASE db_customer;

CREATE TABLE userdetails (
  userdetailsid INT AUTO_INCREMENT PRIMARY KEY,
  userid VARCHAR(36) UNIQUE,
  firstname VARCHAR(50),
  lastname VARCHAR(50),
  email VARCHAR(100),
  mobile VARCHAR(20),
  pan VARCHAR(20),
  adhaar VARCHAR(20),
  dateofbirth DATE,
  gender CHAR(1),
  address VARCHAR(255),
  city VARCHAR(50),
  state VARCHAR(50),
  pin VARCHAR(10),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE beneficiaries (
  beneficiaryid INT AUTO_INCREMENT PRIMARY KEY,
  userid VARCHAR(36),
  beneficiaryname VARCHAR(100),
  beneaccountno BIGINT,
  relation VARCHAR(50),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE user_sync_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid VARCHAR(36),
  event_type VARCHAR(50),
  event_data JSON,
  synced_at TIMESTAMP,
  status VARCHAR(20)
);
```

**Bounded Context:**
- ✅ Customer profile information
- ✅ Personal details (address, phone, DOB)
- ✅ Beneficiary management
- ❌ User credentials (belongs to Auth Service)
- ❌ Financial accounts (belongs to Account Service)

---

### **3. Account Service (account-db)**

**Database:** `db_account`  
**Port:** 3309  
**Ownership:** Financial data & transactions

**Tables:**
```sql
CREATE DATABASE db_account;

CREATE TABLE bankaccount (
  accountno BIGINT PRIMARY KEY AUTO_INCREMENT,
  userid VARCHAR(36),
  accountType VARCHAR(50),
  dateCreated VARCHAR(10),
  timeCreated VARCHAR(8),
  balance DECIMAL(15,2),
  isactive BOOLEAN,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  INDEX idx_userid (userid)
);

CREATE TABLE transactions (
  transactionId INT AUTO_INCREMENT PRIMARY KEY,
  fromAccount BIGINT,
  toAccount BIGINT,
  amount DECIMAL(15,2),
  transactionStatus VARCHAR(20),
  transactionDate VARCHAR(10),
  transactionTime VARCHAR(8),
  description VARCHAR(255),
  created_at TIMESTAMP
);

CREATE TABLE loanaccount (
  loanaccountno BIGINT PRIMARY KEY AUTO_INCREMENT,
  userid VARCHAR(36),
  principleamount DECIMAL(15,2),
  rateofinterest DECIMAL(5,2),
  duration INT,
  approvaldate VARCHAR(10),
  isapproved BOOLEAN,
  created_at TIMESTAMP
);

CREATE TABLE account_sync_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid VARCHAR(36),
  event_type VARCHAR(50),
  event_data JSON,
  synced_at TIMESTAMP,
  status VARCHAR(20)
);
```

**Bounded Context:**
- ✅ Bank accounts
- ✅ Transaction history
- ✅ Loan accounts
- ✅ Balance management
- ❌ User profile (belongs to Customer Service)
- ❌ User credentials (belongs to Auth Service)

---

### **4. Notification Service (notification-db)**

**Database:** `db_notification`  
**Port:** 3310  
**Ownership:** Notification logs

**Tables:**
```sql
CREATE DATABASE db_notification;

CREATE TABLE mail (
  mailid INT AUTO_INCREMENT PRIMARY KEY,
  `to` VARCHAR(100),
  subject VARCHAR(255),
  body LONGTEXT,
  sentDate TIMESTAMP,
  status VARCHAR(20),
  error_message VARCHAR(500)
);

CREATE TABLE notification_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid VARCHAR(36),
  event_type VARCHAR(50),
  notification_type VARCHAR(50),
  status VARCHAR(20),
  sent_at TIMESTAMP
);
```

**Bounded Context:**
- ✅ Email logs
- ✅ Notification history
- ✅ Email delivery status
- ❌ All other business logic

---

## 🌊 Event-Driven Architecture {#event-architecture}

### **Event Types**

#### **1. UserCreatedEvent**
```json
{
  "eventId": "uuid",
  "eventType": "user.created",
  "timestamp": "2024-12-24T10:30:00Z",
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "email": "john@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "phone": "0123456789"
}
```
**Publisher:** Authentication Service  
**Listeners:** Customer Service  
**Action:** Create UserDetail record in customer-db

---

#### **2. UserProfileUpdatedEvent**
```json
{
  "eventId": "uuid",
  "eventType": "user.profile.updated",
  "timestamp": "2024-12-24T10:30:00Z",
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "address": "123 Main St",
  "city": "Ho Chi Minh",
  "phone": "0123456789"
}
```
**Publisher:** Customer Service  
**Listeners:** None (for now)  
**Action:** Log event in customer-db

---

#### **3. AccountCreatedEvent**
```json
{
  "eventId": "uuid",
  "eventType": "account.created",
  "timestamp": "2024-12-24T10:30:00Z",
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accountNo": 1000001,
  "accountType": "Savings",
  "balance": 10000.00
}
```
**Publisher:** Account Service  
**Listeners:** Notification Service  
**Action:** Send confirmation email

---

#### **4. TransactionCompletedEvent**
```json
{
  "eventId": "uuid",
  "eventType": "transaction.completed",
  "timestamp": "2024-12-24T10:30:00Z",
  "transactionId": 1,
  "fromAccount": 1000001,
  "toAccount": 1000002,
  "amount": 500.00,
  "status": "SUCCESS"
}
```
**Publisher:** Account Service  
**Listeners:** Notification Service  
**Action:** Send transaction confirmation email

---

## 🔧 Code Changes Required {#code-changes}

### **Dependencies to Add (pom.xml)**

```xml
<!-- Spring Cloud Stream for Event Streaming -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>

<!-- Kafka Alternative (choose one: RabbitMQ or Kafka) -->
<!-- <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-kafka</artifactId>
</dependency> -->

<!-- Lombok for code generation -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### **Architecture Pattern**

```
┌──────────────────┐
│ Authentication   │
│ Service (8081)   │
├──────────────────┤
│ When user signs  │
│ up, publishes    │
│ UserCreatedEvent │
└────────┬─────────┘
         │
         │ RabbitMQ / Kafka
         │ Topic: user.created
         │
         ├──────────────────────┐
         │                      │
    ┌────▼──────────┐  ┌─────────▼───────┐
    │ Customer Svc  │  │ Notification S. │
    │ (8082)        │  │ (8084)          │
    │               │  │                 │
    │ Consumes event│  │ Sends email     │
    │ Creates       │  │                 │
    │ UserDetail    │  │                 │
    └────────────────  └─────────────────┘
```

---

## 💻 Spring Boot Implementation Examples {#implementation}

### **Step 1: Create Event Classes (Shared)**

See Section 5 below for actual implementation

### **Step 2: Event Publisher (Authentication Service)**

See Section 5 below for actual implementation

### **Step 3: Event Listener (Customer Service)**

See Section 5 below for actual implementation

### **Step 4: Updated Service Layer**

See Section 5 below for actual implementation

---

## 📦 Data Migration Strategy {#migration}

### **Zero-Downtime Migration Approach**

**Phase 1: Dual Write (Days 1-3)**
```
Current State:
- Single database with all tables
- All services read/write to shared DB

Action:
- Deploy separate databases (empty)
- Update auth-service to write to both db_auth and db_onlinebanking
- Update customer-service to write to both db_customer and db_onlinebanking
- Update account-service to write to both db_account and db_onlinebanking
- Services continue reading from shared db_onlinebanking

Code Example:
@Transactional
public User createUser(User user) {
    // Write to new database
    userRepositoryNew.save(user);
    
    // Write to old database (for backward compatibility)
    userRepositoryOld.save(user);
    
    return user;
}
```

**Phase 2: Data Sync (Days 4-5)**
```
Action:
- Create migration scripts to copy existing data from shared DB to individual DBs
- Verify data integrity
- Update services to read from individual DBs but still write to both
- Validate read consistency

SQL Example:
-- Copy users to db_auth
INSERT INTO db_auth.userdata 
SELECT * FROM db_onlinebanking.userdata;

-- Copy user details to db_customer
INSERT INTO db_customer.userdetails 
SELECT * FROM db_onlinebanking.userdetails;

-- Copy accounts to db_account
INSERT INTO db_account.bankaccount 
SELECT * FROM db_onlinebanking.bankaccount;
```

**Phase 3: Read from New DB (Days 6-7)**
```
Action:
- Update services to read from individual DBs
- Keep writing to both DBs for safety
- Monitor consistency
- Run parallel testing

Config Change:
spring.datasource.url=jdbc:mysql://localhost:3307/db_auth  # for auth-service
spring.datasource.url=jdbc:mysql://localhost:3308/db_customer  # for customer-service
spring.datasource.url=jdbc:mysql://localhost:3309/db_account  # for account-service
```

**Phase 4: Event-Driven Sync (Days 8-10)**
```
Action:
- Deploy event streaming (RabbitMQ/Kafka)
- Setup event publishers in each service
- Setup event listeners in dependent services
- Test event flow end-to-end
- Enable event publishing in production code

Validation:
- User creates account in Account Service
- Account Service publishes AccountCreatedEvent
- Notification Service listens and sends email
- Verify email was sent
```

**Phase 5: Stop Dual Write (Day 11+)**
```
Action:
- Remove dual write code (only write to individual DB)
- Monitor for issues
- Keep old shared DB as backup for 30 days
- Document all changes

Final Code:
@Transactional
public User createUser(User user) {
    // Only write to new database
    userRepository.save(user);
    
    // Publish event for other services
    userEventPublisher.publishUserCreatedEvent(user);
    
    return user;
}
```

### **Rollback Plan**

If issues occur:
1. **Day 1-3:** Disable writes to new DBs (keep dual-write disabled)
2. **Day 4-5:** Restore reads from shared DB
3. **Day 6-10:** Disable new DB configuration
4. **Day 11+:** Disable individual DB reads, revert to shared DB

---

## ✅ Benefits, Risks & Mitigation {#benefits-risks}

### **Benefits ✅**

| Benefit | Description |
|---------|-------------|
| **Independent Scaling** | Scale each service's DB separately |
| **Reduced Coupling** | Services don't depend on shared schema |
| **Technology Freedom** | Each DB can use different tech (MySQL, PostgreSQL, etc.) |
| **Team Autonomy** | Different teams can own different DBs |
| **Performance** | No cross-DB locks or contention |
| **Easier Maintenance** | Smaller, focused databases |

---

### **Risks ⚠️**

| Risk | Impact | Severity |
|------|--------|----------|
| **Eventual Consistency** | Data may be temporarily inconsistent | HIGH |
| **Distributed Transactions** | ACID guarantees become complex | HIGH |
| **Data Duplication** | Same data in multiple DBs | MEDIUM |
| **Event Loss** | Events might not be delivered | HIGH |
| **Orphaned Data** | References to deleted data | MEDIUM |
| **Operational Complexity** | More databases to manage | MEDIUM |

---

### **Mitigation Strategies 🛡️**

#### **1. Eventual Consistency**
```java
// Add version field to handle stale reads
@Data
@Entity
public class UserDetail {
    @Version
    private Long version;
    
    // ... other fields
}

// Client code handles retry on conflict
public UserDetail updateUserDetail(UserDetail detail) {
    for (int i = 0; i < 3; i++) {
        try {
            return userDetailRepository.save(detail);
        } catch (OptimisticLockingFailureException e) {
            Thread.sleep(1000 * (i + 1)); // Exponential backoff
            detail = userDetailRepository.findById(detail.getId()).orElseThrow();
        }
    }
    throw new RuntimeException("Failed to update after retries");
}
```

#### **2. Event Loss Prevention**
```java
// Store events in database before publishing
@Entity
@Data
public class OutboxEvent {
    @Id
    private String eventId;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;
    private Boolean published = false;
}

// Transactional outbox pattern
@Transactional
public void createUserWithEvent(User user) {
    userRepository.save(user);
    
    // Create event in same transaction
    OutboxEvent event = OutboxEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("user.created")
            .payload(objectMapper.writeValueAsString(user))
            .createdAt(LocalDateTime.now())
            .build();
    
    outboxEventRepository.save(event);
    // Event published only when transaction commits
}

// Scheduled job to publish unpublished events
@Scheduled(fixedRate = 5000)
public void publishOutboxEvents() {
    List<OutboxEvent> unpublished = 
        outboxEventRepository.findByPublishedFalse();
    
    for (OutboxEvent event : unpublished) {
        try {
            streamBridge.send("user-created-out-0", event.getPayload());
            event.setPublished(true);
            outboxEventRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event.getEventId(), e);
        }
    }
}
```

#### **3. Data Duplication Control**
```java
// Use view-only replicas
@Entity
@Data
@IdClass(UserDetailViewId.class)
public class UserDetailView {
    @Id
    private String userId;
    
    private String email; // Replicated from auth-db
    
    @Column(insertable = false, updatable = false)
    private Boolean readOnly = true;
}

// Mark replicated fields as read-only
@Column(insertable = false, updatable = false)
private String emailFromAuth;
```

#### **4. Distributed Transaction Handling**
```java
// Saga Pattern for multi-step operations
@Service
public class UserRegistrationSaga {
    
    @Transactional
    public void registerUser(UserRegistrationRequest request) {
        // Step 1: Create user in auth-db
        User user = authService.createUser(request);
        
        try {
            // Step 2: Create profile in customer-db
            customerService.createUserDetail(user);
            
            // Step 3: Send email via notification-db
            notificationService.sendWelcomeEmail(user.getEmail());
            
        } catch (Exception e) {
            // Compensating transaction: delete user
            authService.deleteUser(user.getId());
            throw new SagaCompensationException("Registration failed", e);
        }
    }
}
```

#### **5. Monitoring & Alerting**
```java
@Component
public class DataConsistencyMonitor {
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void checkConsistency() {
        long authUserCount = authServiceClient.getUserCount();
        long customerDetailCount = customerServiceClient.getUserDetailCount();
        
        if (authUserCount != customerDetailCount) {
            log.warn("Data consistency issue: {} users in auth, {} in customer",
                    authUserCount, customerDetailCount);
            
            // Send alert
            alertingService.sendAlert(
                "Data consistency issue detected",
                "Mismatch between auth and customer DBs"
            );
        }
    }
}
```

---

## 🧪 Testing Strategy {#testing}

### **Unit Tests**
```java
@SpringBootTest
public class UserEventPublisherTest {
    
    @Test
    public void testUserCreatedEventPublished() {
        // Arrange
        User user = User.builder()
                .userId("test-123")
                .email("test@example.com")
                .build();
        
        // Act
        userEventPublisher.publishUserCreatedEvent(user);
        
        // Assert
        verify(streamBridge).send("user-created-out-0", any(UserCreatedEvent.class));
    }
}
```

### **Integration Tests**
```java
@SpringBootTest
public class EventListenerIntegrationTest {
    
    @Test
    public void testUserCreatedEventListener() {
        // Arrange
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId("test-123")
                .email("test@example.com")
                .build();
        
        // Act
        userSyncEventListener.handleUserCreatedEvent(event);
        
        // Assert
        UserDetail saved = userDetailRepository.findByUserId("test-123").orElseThrow();
        assertEquals("test@example.com", saved.getEmail());
    }
}
```

### **Contract Tests**
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserCreatedEventContractTest {
    
    @Test
    public void testEventSchema() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId("test-123")
                .email("test@example.com")
                .build();
        
        // Validate event has required fields
        assertNotNull(event.getUserId());
        assertNotNull(event.getEmail());
    }
}
```

---

## 🔄 Rollback Plan {#rollback}

### **Rollback Procedure**

| Step | Action | Time | Risk |
|------|--------|------|------|
| 1 | Disable event publishing | 5 min | LOW |
| 2 | Switch reads to shared DB | 10 min | LOW |
| 3 | Revert service configs | 5 min | LOW |
| 4 | Verify functionality | 15 min | MEDIUM |
| 5 | Keep backup DBs for 30 days | - | - |

### **Rollback Checklist**
- [ ] Backup current data
- [ ] Prepare rollback scripts
- [ ] Test rollback in staging
- [ ] Have DBA on standby
- [ ] Monitor error logs during rollback
- [ ] Verify all services functioning
- [ ] Notify stakeholders

---

## 📋 Implementation Checklist

- [ ] Review this document with team
- [ ] Create separate databases
- [ ] Add event streaming dependencies
- [ ] Create event classes
- [ ] Implement event publishers
- [ ] Implement event listeners
- [ ] Update service repositories
- [ ] Run unit tests
- [ ] Run integration tests
- [ ] Create migration scripts
- [ ] Execute Phase 1 (Dual Write)
- [ ] Execute Phase 2 (Data Sync)
- [ ] Execute Phase 3 (Read from New DB)
- [ ] Execute Phase 4 (Event-Driven)
- [ ] Execute Phase 5 (Stop Dual Write)
- [ ] Monitor for 2 weeks
- [ ] Document lessons learned

---

**Last Updated:** December 24, 2025  
**Status:** Ready for Implementation
