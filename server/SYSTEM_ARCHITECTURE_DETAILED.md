# 🏗️ KIẾN TRÚC HỆ THỐNG ONLINE BANKING MICROSERVICES - CHI TIẾT TOÀN DIỆN

**Ngày tạo:** 24/12/2025  
**Phiên bản:** 1.0.0  
**Trạng thái:** Sản xuất

---

## 📑 MỤC LỤC

1. [Tổng Quan Kiến Trúc](#1-tổng-quan-kiến-trúc)
2. [Kiến Trúc Microservices](#2-kiến-trúc-microservices)
3. [Database per Service Pattern](#3-database-per-service-pattern)
4. [Event-Driven Architecture](#4-event-driven-architecture)
5. [API Gateway & Routing](#5-api-gateway--routing)
6. [Service Discovery & Registry](#6-service-discovery--registry)
7. [Authentication & Authorization](#7-authentication--authorization)
8. [Data Consistency Model](#8-data-consistency-model)
9. [Communication Patterns](#9-communication-patterns)
10. [Deployment Architecture](#10-deployment-architecture)

---

## 1. TỔNG QUAN KIẾN TRÚC

### 1.1 Sơ Đồ Kiến Trúc Tổng Quan

```
┌──────────────────────────────────────────────────────────────────────────┐
│                                                                            │
│                        PRESENTATION LAYER                                 │
│                                                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │                                                                       │ │
│  │  Web Browser (React)    Mobile App    Desktop App    Admin Portal    │ │
│  │                                                                       │ │
│  └──────────────────────────────┬────────────────────────────────────────┘ │
│                                 │                                           │
│                                 │ HTTPS / REST / JSON                       │
│                                 ▼                                           │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                                                                       │  │
│  │  API GATEWAY SERVICE (Port 8080)                                     │  │
│  │  ├─ Spring Cloud Gateway                                            │  │
│  │  ├─ Load Balancing                                                   │  │
│  │  ├─ Rate Limiting                                                    │  │
│  │  ├─ CORS Handling                                                    │  │
│  │  └─ Request Routing                                                  │  │
│  │                                                                       │  │
│  └───┬────────────────────┬──────────────────┬────────────────────┬──────┘  │
│      │                    │                  │                    │          │
│      │                    │                  │                    │          │
│      ▼                    ▼                  ▼                    ▼          │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐ ┌──────────┐
│  │ AUTH SERVICE     │ │ CUSTOMER SERVICE │ │ ACCOUNT SERVICE  │ │NOTIF SVC │
│  │ (Port 8081)      │ │ (Port 8082)      │ │ (Port 8083)      │ │(Port8084)│
│  │                  │ │                  │ │                  │ │          │
│  │ ✓ User Registry  │ │ ✓ Profile Mgmt   │ │ ✓ Account Mgmt   │ │✓ Email   │
│  │ ✓ Login/Logout   │ │ ✓ User Details   │ │ ✓ Transactions   │ │✓ SMS     │
│  │ ✓ JWT Tokens     │ │ ✓ Beneficiaries  │ │ ✓ Fund Transfer  │ │✓ Logging │
│  │ ✓ OTP Mgmt       │ │                  │ │ ✓ Loan Accounts  │ │          │
│  │                  │ │                  │ │                  │ │          │
│  │ [db_auth]        │ │ [db_customer]    │ │ [db_account]     │ │[db_notif]│
│  └────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘ └────┬────┘
│           │                    │                    │               │
│           └────────────────────┼────────────────────┼───────────────┘
│                                │                    │
│                                └────────┬───────────┘
│                                         │
│                    INTER-SERVICE COMMUNICATION:
│                    ├─ OpenFeign (Synchronous)
│                    ├─ RabbitMQ (Asynchronous)
│                    └─ Eureka Discovery
│
│  ┌────────────────────────────────────────────────────────────────────┐
│  │                     SERVICE DISCOVERY LAYER                         │
│  │                                                                     │
│  │  Eureka Server (Port 8761)                                         │
│  │  ├─ Service Registry                                                │
│  │  ├─ Health Checks                                                   │
│  │  ├─ Load Balancing                                                  │
│  │  └─ Service Deregistration                                          │
│  │                                                                     │
│  └────────────────────────────────────────────────────────────────────┘
│
│  ┌────────────────────────────────────────────────────────────────────┐
│  │                     MESSAGE BROKER LAYER                            │
│  │                                                                     │
│  │  RabbitMQ (Port 5672)                                              │
│  │  ├─ Event Exchange (user.created)                                   │
│  │  ├─ Event Exchange (account.created)                                │
│  │  ├─ Event Exchange (transaction.completed)                          │
│  │  └─ Dead Letter Queue (failed events)                              │
│  │                                                                     │
│  │  Management UI (Port 15672)                                        │
│  │                                                                     │
│  └────────────────────────────────────────────────────────────────────┘
│
│  ┌────────────────────────────────────────────────────────────────────┐
│  │                     DATABASE LAYER                                  │
│  │                                                                     │
│  │  MySQL 8.0                                                          │
│  │  ├─ db_auth (Port 3307)           - Authentication Data            │
│  │  ├─ db_customer (Port 3308)       - Customer Profiles              │
│  │  ├─ db_account (Port 3309)        - Financial Data                 │
│  │  └─ db_notification (Port 3310)   - Notification Logs              │
│  │                                                                     │
│  └────────────────────────────────────────────────────────────────────┘
│
└──────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Công Nghệ Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| **Runtime** | Java | 21 LTS | Runtime environment |
| **Framework** | Spring Boot | 3.3.4 | Microservices framework |
| **Cloud** | Spring Cloud | 2023.0.3 | Cloud-native features |
| **Gateway** | Spring Cloud Gateway | 4.1.1 | API routing & load balancing |
| **Discovery** | Eureka | Netflix | Service registration/discovery |
| **Security** | Spring Security | 6.x | Authentication & authorization |
| **HTTP Client** | OpenFeign | 4.1.1 | Service-to-service HTTP calls |
| **Events** | Spring Cloud Stream | 4.1.1 | Event streaming framework |
| **Message Broker** | RabbitMQ | 3.12 | Async message processing |
| **ORM** | Spring Data JPA + Hibernate | 3.3.4 / 6.x | Database mapping |
| **JWT** | JJWT | 0.12.3 | Token generation & validation |
| **Database** | MySQL | 8.0 | Relational database |
| **Build Tool** | Maven | 3.8+ | Project build management |
| **Containerization** | Docker | 24.x | Container deployment |

---

## 2. KIẾN TRÚC MICROSERVICES

### 2.1 Authentication Service (Port 8081)

#### Responsibilities (Trách Nhiệm)
```
PRIMARY:
├─ User Registration
├─ User Authentication (Login/Logout)
├─ JWT Token Generation & Validation
├─ OTP Management
├─ Password Reset
└─ User Role Management

SECONDARY:
├─ Email Verification
├─ Account Activation
└─ Session Management
```

#### Database Schema (db_auth)
```sql
-- User Credentials
userdata
├─ userid (UUID) PRIMARY KEY
├─ firstname VARCHAR(50)
├─ lastname VARCHAR(50)
├─ email VARCHAR(100) UNIQUE
├─ password VARCHAR(255) -- Bcrypt hashed
├─ role VARCHAR(20) FOREIGN KEY
├─ enabled BOOLEAN
├─ otp VARCHAR(10)
├─ otp_created_at TIMESTAMP
└─ created_date DATE

-- User Roles
role
├─ rolename VARCHAR(20) PRIMARY KEY
├─ description VARCHAR(255)
└─ created_at TIMESTAMP

-- OTP Management
user_otp_log
├─ id INT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36) FOREIGN KEY
├─ otp VARCHAR(10)
├─ created_at TIMESTAMP
├─ verified_at TIMESTAMP
└─ status VARCHAR(20) -- PENDING, VERIFIED, EXPIRED
```

#### Key Endpoints
```
POST   /api/v1/signup              - Register new user
POST   /api/v1/login               - User login
POST   /api/v1/otp                 - Verify OTP
POST   /api/v1/resend-otp/{userId} - Resend OTP
POST   /api/v1/forgot-password      - Initiate password reset
POST   /api/v1/reset-password       - Reset password with token
```

#### Events Published
```
UserCreatedEvent
├─ eventId: UUID
├─ userId: String
├─ email: String
├─ firstname: String
├─ lastname: String
├─ phone: String
├─ role: String
└─ timestamp: LocalDateTime

Topic: user.created
Consumers: Customer Service
```

---

### 2.2 Customer Service (Port 8082)

#### Responsibilities (Trách Nhiệm)
```
PRIMARY:
├─ Customer Profile Management
├─ Personal Information (Address, Phone, etc.)
├─ Beneficiary Management
├─ Customer KYC (Know Your Customer)
└─ Preference Management

SECONDARY:
├─ Customer Search
├─ Customer List Management
└─ Customer History
```

#### Database Schema (db_customer)
```sql
-- Customer Details
userdetails
├─ userdetailsid INT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36) UNIQUE FOREIGN KEY -- From auth-db
├─ firstname VARCHAR(50)
├─ lastname VARCHAR(50)
├─ email VARCHAR(100)
├─ mobile VARCHAR(20)
├─ pan VARCHAR(20) -- Permanent Account Number
├─ adhaar VARCHAR(20) -- Government ID
├─ dateofbirth DATE
├─ gender CHAR(1)
├─ address VARCHAR(255)
├─ city VARCHAR(50)
├─ state VARCHAR(50)
├─ pin VARCHAR(10)
├─ created_at TIMESTAMP
└─ updated_at TIMESTAMP

-- Beneficiary Accounts
beneficiaries
├─ beneficiaryid INT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36) FOREIGN KEY
├─ beneficiaryname VARCHAR(100)
├─ beneaccountno BIGINT
├─ relation VARCHAR(50)
├─ created_at TIMESTAMP
└─ updated_at TIMESTAMP

-- Sync Log
user_sync_log
├─ id INT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36)
├─ event_type VARCHAR(50)
├─ event_data JSON
├─ synced_at TIMESTAMP
└─ status VARCHAR(20)
```

#### Key Endpoints
```
POST   /api/v1/customer/profile              - Create/Update profile
GET    /api/v1/customer/profile/{userId}    - Get customer profile
PUT    /api/v1/customer/profile/{userId}    - Update profile
DELETE /api/v1/customer/profile/{userId}    - Delete profile

POST   /api/v1/customer/beneficiary          - Add beneficiary
GET    /api/v1/customer/beneficiary/{userId} - Get beneficiaries
PUT    /api/v1/customer/beneficiary/{id}    - Update beneficiary
DELETE /api/v1/customer/beneficiary/{id}    - Delete beneficiary
```

#### Events Consumed
```
UserCreatedEvent (from Authentication Service)
├─ Action: Create UserDetail record
├─ Trigger: User_sync_log entry
└─ Result: New user in db_customer
```

#### Events Published
```
UserProfileUpdatedEvent
├─ eventId: UUID
├─ userId: String
├─ name: String
├─ address: String
├─ city: String
┌─────────────────┐
│  Auth Service   │─── Publishes ──→ UserCreatedEvent
└─────────────────┘
        │
        └─→ [db_auth]

        Event Flow:
        UserCreatedEvent
             │
             ├──→ [RabbitMQ]
             │
             ├──→ Customer Service (Consumes)
             │    └─→ [db_customer] ✓ Create UserDetail
             │
             └──→ Notification Service (Could consume)
                  └─→ [db_notification] ✓ Log signup└─ timestamp: LocalDateTime
```

---

### 2.3 Account Service (Port 8083)

#### Responsibilities (Trách Nhiệm)
```
PRIMARY:
├─ Bank Account Management
├─ Transaction Processing
├─ Fund Transfer
├─ Balance Management
├─ Transaction History
└─ Loan Account Management

SECONDARY:
├─ Account Statement Generation
├─ Account Closure
├─ Account Freeze
└─ Overdraft Management
```

#### Database Schema (db_account)
```sql
-- Bank Accounts
bankaccount
├─ accountno BIGINT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36) FOREIGN KEY
├─ accountType VARCHAR(50) -- Savings, Checking, Credit
├─ dateCreated VARCHAR(10)
├─ timeCreated VARCHAR(8)
├─ balance DECIMAL(15,2)
├─ isactive BOOLEAN
├─ created_at TIMESTAMP
├─ updated_at TIMESTAMP
└─ INDEX idx_userid

-- Transactions
transactions
├─ transactionId INT AUTO_INCREMENT PRIMARY KEY
├─ fromAccount BIGINT FOREIGN KEY
├─ toAccount BIGINT FOREIGN KEY
├─ amount DECIMAL(15,2)
├─ transactionStatus VARCHAR(20) -- PENDING, SUCCESS, FAILED
├─ transactionDate VARCHAR(10)
├─ transactionTime VARCHAR(8)
├─ description VARCHAR(255)
└─ created_at TIMESTAMP

-- Loan Accounts
loanaccount
├─ loanaccountno BIGINT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36) FOREIGN KEY
├─ principalAmount DECIMAL(15,2)
├─ rateofinterest DECIMAL(5,2)
├─ duration INT -- months
├─ approvaldate VARCHAR(10)
├─ isapproved BOOLEAN
└─ created_at TIMESTAMP

-- Sync Log
account_sync_log
├─ id INT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36)
├─ event_type VARCHAR(50)
├─ event_data JSON
├─ synced_at TIMESTAMP
└─ status VARCHAR(20)
```

#### Key Endpoints
```
POST   /api/v1/account/create                      - Create account
GET    /api/v1/account/{accountNo}                - Get account details
GET    /api/v1/account/user/{userId}              - Get all user accounts
DELETE /api/v1/account/{accountNo}                - Close account

POST   /api/v1/account/transfer                   - Transfer funds
GET    /api/v1/account/transactions/{accountNo}   - Get transaction history
GET    /api/v1/account/transaction/{transactionId} - Get transaction details
GET    /api/v1/account/balance/{accountNo}        - Check balance
```

#### Events Published
```
AccountCreatedEvent
├─ eventId: UUID
├─ userId: String
├─ accountNo: Long
├─ accountType: String
├─ balance: Double
├─ timestamp: LocalDateTime
└─ Topic: account.created

TransactionCompletedEvent
├─ eventId: UUID
├─ transactionId: Long
├─ fromAccount: Long
├─ toAccount: Long
├─ amount: Double
├─ status: String
├─ timestamp: LocalDateTime
└─ Topic: transaction.completed
```

---

### 2.4 Notification Service (Port 8084)

#### Responsibilities (Trách Nhiệm)
```
PRIMARY:
├─ Email Sending
├─ Notification Logging
├─ Delivery Status Tracking
├─ Retry Management
└─ Template Management

SECONDARY:
├─ SMS Sending (Future)
├─ Push Notifications (Future)
└─ Notification Preferences
```

#### Database Schema (db_notification)
```sql
-- Email/Notification Log
mail
├─ mailid INT AUTO_INCREMENT PRIMARY KEY
├─ to VARCHAR(100)
├─ subject VARCHAR(255)
├─ body LONGTEXT
├─ sentDate TIMESTAMP
├─ status VARCHAR(20) -- SENT, FAILED, BOUNCED
├─ error_message VARCHAR(500)
├─ retry_count INT
└─ created_at TIMESTAMP

-- Notification Log
notification_log
├─ id INT AUTO_INCREMENT PRIMARY KEY
├─ userid VARCHAR(36)
├─ event_type VARCHAR(50)
├─ notification_type VARCHAR(50) -- EMAIL, SMS, PUSH
├─ status VARCHAR(20)
├─ sent_at TIMESTAMP
└─ created_at TIMESTAMP
```

#### Key Endpoints
```
POST /api/v1/mail/send                - Send email
POST /api/v1/mail/send-otp            - Send OTP email
POST /api/v1/mail/send-transaction    - Send transaction confirmation
GET  /api/v1/notification/{userId}    - Get user notifications
```

#### Events Consumed
```
AccountCreatedEvent (from Account Service)
├─ Action: Send account creation confirmation email
└─ Log: notification_log entry

TransactionCompletedEvent (from Account Service)
├─ Action: Send transaction confirmation email
└─ Log: notification_log entry
```

---

## 3. DATABASE PER SERVICE PATTERN

### 3.1 Cách Tiếp Cận (Approach)

```
Trước (Monolithic):
┌──────────────────────────────┐
│    Single Database           │
│  db_onlinebanking            │
│                              │
│  ├─ userdata                 │
│  ├─ userdetails              │
│  ├─ bankaccount              │
│  ├─ transactions              │
│  └─ mail                      │
│                              │
│  Tất cả services dùng chung   │
└──────────────────────────────┘

Sau (Microservices):
┌──────────────────┐  ┌─────────────────┐  ┌──────────────┐  ┌───────────┐
│   db_auth        │  │  db_customer    │  │  db_account  │  │ db_notif  │
├──────────────────┤  ├─────────────────┤  ├──────────────┤  ├───────────┤
│ userdata         │  │ userdetails     │  │ bankaccount  │  │ mail      │
│ role             │  │ beneficiaries   │  │ transactions │  │ notif_log │
│ user_otp_log     │  │ user_sync_log   │  │ loanaccount  │  │           │
│                  │  │                 │  │ account_sync │  │           │
└──────────────────┘  └─────────────────┘  └──────────────┘  └───────────┘
   Auth Service          Customer Service      Account Service  Notif Svc
    Port 3307            Port 3308             Port 3309         Port 3310

Mỗi service chỉ truy cập database của mình
Services giao tiếp qua Events hoặc REST APIs
```

### 3.2 Data Ownership (Quyền Sở Hữu Dữ Liệu)

```
Authentication Service OWNS (Sở hữu):
├─ userdata table
├─ role table
├─ user_otp_log table
└─ Password hashes
   ONLY this service can:
   ├─ INSERT/UPDATE/DELETE user credentials
   ├─ Generate OTP
   └─ Verify passwords

Customer Service OWNS:
├─ userdetails table
├─ beneficiaries table
├─ user_sync_log table
   ONLY this service can:
   ├─ INSERT/UPDATE/DELETE customer profiles
   ├─ Manage beneficiaries
   └─ Log data syncs

Account Service OWNS:
├─ bankaccount table
├─ transactions table
├─ loanaccount table
├─ account_sync_log table
   ONLY this service can:
   ├─ INSERT/UPDATE/DELETE accounts
   ├─ Record transactions
   └─ Manage loans

Notification Service OWNS:
├─ mail table
├─ notification_log table
   ONLY this service can:
   ├─ Log emails sent
   └─ Track delivery status
```

### 3.3 Inter-Service Data Access

```
What Services CAN do:
✓ READ data by calling REST APIs
  Example: GET /api/v1/customer/profile/{userId}

✓ CONSUME events from other services
  Example: UserCreatedEvent → Create replica in customer-db

✓ REPLICATE data locally if needed
  Example: Cache user email from auth-db in customer-db

What Services CANNOT do:
✗ Directly access another service's database
  ✗ No direct MySQL connections across services

✗ Make arbitrary SQL queries on other DBs
  ✗ No cross-database JOINs

✗ Modify other service's data directly
  ✗ Must go through that service's APIs or events
```

---

## 4. EVENT-DRIVEN ARCHITECTURE

### 4.1 Event Model

```
┌────────────────────────────────────┐
│         Event Structure             │
├────────────────────────────────────┤
│ {                                  │
│   "eventId": "uuid-string",        │
│   "eventType": "user.created",     │
│   "timestamp": "2024-12-24T...",   │
│   "version": "1.0",                │
│   "correlationId": "correlation",  │
│   "source": "auth-service",        │
│   "payload": {                     │
│     "userId": "...",               │
│     "email": "...",                │
│     ... specific data ...          │
│   }                                │
│ }                                  │
└────────────────────────────────────┘
```

### 4.2 Event Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        EVENT: User Registration                          │
└─────────────────────────────────────────────────────────────────────────┘

1. USER SIGNUP
   │
   ├─► POST /api/v1/signup
   │   └─ SignUpController.signup(UserRequest)
   │
2. AUTHENTICATION SERVICE
   │
   ├─► SignUpServiceImpl.createUser()
   │   └─ Save to userdata table in db_auth
   │
3. EVENT PUBLISHING
   │
   ├─► UserEventPublisher.publishUserCreatedEvent()
   │   └─ Create UserCreatedEvent object
   │
   ├─► StreamBridge.send("user-created-out-0", event)
   │   └─ RabbitMQ receives event
   │
4. MESSAGE BROKER (RabbitMQ)
   │
   ├─► Topic: user.created
   │   └─ Exchange: user.created
   │       └─ Queue: user.created.customer-service
   │
5. CUSTOMER SERVICE CONSUMPTION
   │
   ├─► UserSyncEventListener.userCreatedEventConsumer()
   │   └─ Consumer receives event
   │
6. DATA CREATION
   │
   ├─► UserDetailRepository.save(UserDetail)
   │   └─ Create record in db_customer.userdetails
   │
   └─► Log in db_customer.user_sync_log

RESULT: User data synchronized across both databases
```

### 4.3 Event Topics & Routing

```
Topic: user.created
├─ Publisher: AuthenticationService
├─ Exchange: amq.topic
├─ Routing Key: user.created
├─ Queue: user.created.customer-service
└─ Consumers: CustomerService

Topic: account.created
├─ Publisher: AccountService
├─ Exchange: amq.topic
├─ Routing Key: account.created
├─ Queue: account.created.notification-service
└─ Consumers: NotificationService

Topic: transaction.completed
├─ Publisher: AccountService
├─ Exchange: amq.topic
├─ Routing Key: transaction.completed
├─ Queue: transaction.completed.notification-service
└─ Consumers: NotificationService
```

### 4.4 Error Handling in Events

```
Event Processing Success:
┌─────────────────────────────────┐
│ Event received                  │
├─────────────────────────────────┤
│ ↓                               │
│ Validate event schema           │
├─────────────────────────────────┤
│ ↓                               │
│ Process event (save to DB)      │
├─────────────────────────────────┤
│ ↓                               │
│ Acknowledge to broker           │
├─────────────────────────────────┤
│ ✓ Event marked as processed     │
└─────────────────────────────────┘

Event Processing Failure:
┌─────────────────────────────────┐
│ Event received                  │
├─────────────────────────────────┤
│ ↓                               │
│ Error during processing         │
├─────────────────────────────────┤
│ ↓                               │
│ Retry (max 3 times)            │
│ ├─ Delay: 1 second             │
│ ├─ Backoff: exponential        │
│ └─ Max delay: 10 seconds       │
├─────────────────────────────────┤
│ ↓                               │
│ Still failed?                   │
│ └─ Send to Dead Letter Queue    │
├─────────────────────────────────┤
│ ✗ Event logged for investigation│
└─────────────────────────────────┘

Dead Letter Queue (DLQ):
- Topic: dlx.user.created.dlq
- Purpose: Store failed events
- Action: Manual review & retry
```

---

## 5. API GATEWAY & ROUTING

### 5.1 Gateway Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│              CLIENT REQUESTS                                    │
│              (Web, Mobile, Desktop)                             │
│                                                                 │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ HTTP/HTTPS
                         ▼
            ┌────────────────────────────┐
            │   API GATEWAY (Port 8080)  │
            │  Spring Cloud Gateway      │
            └────────────┬───────────────┘
                         │
          ┌──────────────┼──────────────┬──────────┐
          │              │              │          │
          ▼              ▼              ▼          ▼
    [Auth Service]  [Customer]   [Account Svc] [Notification]
    (Port 8081)    (Port 8082)   (Port 8083)   (Port 8084)
```

### 5.2 Route Configuration

```properties
# Route 1: Authentication Service
spring.cloud.gateway.routes[0].id=authentication-service
spring.cloud.gateway.routes[0].uri=lb://authentication-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/signup,/api/v1/login,/api/v1/otp/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=0

Incoming Request:
POST http://localhost:8080/api/v1/signup
    ↓
Gateway matches: Path=/api/v1/signup
    ↓
Resolves: lb://authentication-service
    ↓
Forwards to: http://authentication-service:8081/api/v1/signup
    ↓
Auth Service processes request
    ↓
Response sent back through Gateway

# Route 2: Customer Service
spring.cloud.gateway.routes[1].id=customer-service
spring.cloud.gateway.routes[1].uri=lb://customer-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/v1/customer/**

# Route 3: Account Service
spring.cloud.gateway.routes[2].id=account-service
spring.cloud.gateway.routes[2].uri=lb://account-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/v1/account/**

# Route 4: Notification Service
spring.cloud.gateway.routes[3].id=notification-service
spring.cloud.gateway.routes[3].uri=lb://notification-service
spring.cloud.gateway.routes[3].predicates[0]=Path=/api/v1/mail/**
```

### 5.3 Load Balancing

```
Gateway Load Balancing Strategy:
┌──────────────────────────────────────────┐
│  Request arrives at Gateway              │
├──────────────────────────────────────────┤
│  1. Extract route predicate              │
│  2. Match against configured routes      │
│  3. Resolve service name via Eureka      │
│      Example: lb://customer-service      │
│      └─ Eureka returns all instances     │
│         └─ [customer-service:8082]       │
│         └─ [customer-service:8082]       │
│         └─ [customer-service:8082]       │
│  4. Round-robin load balancer selects    │
│     one instance                         │
│  5. Forward request to selected instance │
│  6. Return response to client            │
└──────────────────────────────────────────┘

Benefits:
✓ Automatic failover if instance down
✓ Distributes load across instances
✓ No hardcoded service URLs
✓ Dynamic service discovery
```

---

## 6. SERVICE DISCOVERY & REGISTRY

### 6.1 Eureka Architecture

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│           EUREKA SERVER (Port 8761)                     │
│          Netflix Service Registry                       │
│                                                          │
│   ┌────────────────────────────────────────────────┐   │
│   │                                                │   │
│   │  SERVICE REGISTRY                             │   │
│   │  ├─ authentication-service                    │   │
│   │  │  ├─ Host: 172.17.0.2                       │   │
│   │  │  ├─ Port: 8081                             │   │
│   │  │  ├─ Status: UP                             │   │
│   │  │  └─ Last Heartbeat: 2024-12-24 10:30:00  │   │
│   │  │                                             │   │
│   │  ├─ customer-service                          │   │
│   │  │  ├─ Host: 172.17.0.3                       │   │
│   │  │  ├─ Port: 8082                             │   │
│   │  │  └─ Status: UP                             │   │
│   │  │                                             │   │
│   │  └─ ... other services ...                    │   │
│   │                                                │   │
│   └────────────────────────────────────────────────┘   │
│                                                          │
│   ┌────────────────────────────────────────────────┐   │
│   │ HEALTH CHECKS                                  │   │
│   │ ├─ Every 30 seconds                            │   │
│   │ ├─ Check /actuator/health endpoint             │   │
│   │ ├─ Status: UP, DOWN, OUTOFSERVICE             │   │
│   │ └─ Auto-deregister after 90 seconds no beat   │   │
│   │                                                │   │
│   └────────────────────────────────────────────────┘   │
│                                                          │
└──────────────────────────────────────────────────────────┘

Management UI: http://localhost:8761
```

### 6.2 Service Registration Flow

```
STARTUP PHASE:
┌────────────────────────────────────┐
│ Service starts                      │
├────────────────────────────────────┤
│ ↓                                  │
│ Load ApplicationContext             │
├────────────────────────────────────┤
│ ↓                                  │
│ Detect @EnableDiscoveryClient      │
├────────────────────────────────────┤
│ ↓                                  │
│ Read eureka.client.serviceUrl      │
│ = http://localhost:8761/eureka/    │
├────────────────────────────────────┤
│ ↓                                  │
│ Register with Eureka               │
│ {                                  │
│   "appName": "AUTH-SERVICE",       │
│   "hostName": "auth-service",      │
│   "port": 8081,                    │
│   "status": "UP"                   │
│ }                                  │
├────────────────────────────────────┤
│ ✓ Registered                       │
│ ✓ Now discoverable by other svc    │
└────────────────────────────────────┘

RUNTIME PHASE (Every 30 seconds):
┌────────────────────────────────────┐
│ Send heartbeat to Eureka           │
├────────────────────────────────────┤
│ Eureka receives heartbeat          │
├────────────────────────────────────┤
│ Update "Last Heartbeat" timestamp   │
├────────────────────────────────────┤
│ Service remains registered          │
└────────────────────────────────────┘

SHUTDOWN PHASE:
┌────────────────────────────────────┐
│ Service shuts down                 │
├────────────────────────────────────┤
│ ↓                                  │
│ Send de-registration to Eureka     │
├────────────────────────────────────┤
│ ↓                                  │
│ Eureka removes from registry       │
├────────────────────────────────────┤
│ ✓ Not discoverable anymore        │
└────────────────────────────────────┘
```

---

## 7. AUTHENTICATION & AUTHORIZATION

### 7.1 JWT Token Flow

```
CLIENT REQUEST FLOW:

1. User Sends Credentials
   ┌─────────────────────────────────────────┐
   │ POST /api/v1/login                      │
   │ {                                       │
   │   "email": "john@example.com",          │
   │   "password": "SecurePassword@123"      │
   │ }                                       │
   └─────────────────────────────────────────┘

2. Auth Service Validates
   ┌─────────────────────────────────────────┐
   │ LoginServiceImpl.findByEmail()           │
   │ ├─ Query userdata table                 │
   │ ├─ Check: Email exists?                 │
   │ ├─ Check: Account enabled?              │
   │ ├─ Check: Password matches? (Bcrypt)    │
   │ └─ Return: User object                  │
   └─────────────────────────────────────────┘

3. JWT Token Generated
   ┌─────────────────────────────────────────┐
   │ JwtUtil.generateToken(email)            │
   │ ├─ Algorithm: HS512                     │
   │ ├─ Secret: jwt.secret (from props)      │
   │ ├─ Claims:                              │
   │ │  ├─ sub: email                        │
   │ │  ├─ iat: current time                 │
   │ │  ├─ exp: iat + 315360000000ms (10yr) │
   │ │  └─ roles: [USER, ADMIN]              │
   │ └─ Signature: HMAC-SHA512               │
   └─────────────────────────────────────────┘

4. Response Sent
   ┌─────────────────────────────────────────┐
   │ HTTP 200 OK                             │
   │ {                                       │
   │   "jwtToken": "eyJhbGciOi...",         │
   │   "user": {                             │
   │     "userId": "uuid",                   │
   │     "email": "john@example.com",        │
   │     "role": "USER"                      │
   │   }                                     │
   │ }                                       │
   └─────────────────────────────────────────┘

5. Client Stores Token
   ├─ localStorage (Web)
   ├─ Secure Storage (Mobile)
   └─ Application Memory (Desktop)

AUTHORIZED REQUEST FLOW:

6. Client Includes Token
   ┌─────────────────────────────────────────┐
   │ GET /api/v1/account/123                 │
   │ Authorization: Bearer eyJhbGciOi...     │
   │ Content-Type: application/json          │
   └─────────────────────────────────────────┘

7. Gateway Receives Request
   ├─ Extract Authorization header
   ├─ Extract token from "Bearer " prefix
   └─ Pass to downstream service

8. Service Validates Token
   ┌─────────────────────────────────────────┐
   │ JwtAuthenticationFilter                 │
   │ ├─ Intercept request                    │
   │ ├─ Extract token from header            │
   │ ├─ Validate signature (HMAC-SHA512)     │
   │ ├─ Check expiration                     │
   │ ├─ Extract claims (email, roles)        │
   │ ├─ Verify email exists in DB            │
   │ └─ Create SecurityContext                │
   └─────────────────────────────────────────┘

9. Access Control Check
   ┌─────────────────────────────────────────┐
   │ @PreAuthorize("hasRole('USER')")       │
   │ ├─ Check user has required role         │
   │ ├─ Check user not disabled              │
   │ └─ Allow/Deny access                    │
   └─────────────────────────────────────────┘

10. Response Sent
    ├─ If authorized: Process request, return data
    └─ If unauthorized: HTTP 401/403 + error message
```

### 7.2 Token Structure

```
JWT Format: header.payload.signature

HEADER:
{
  "alg": "HS512",
  "typ": "JWT"
}

PAYLOAD (Claims):
{
  "sub": "john@example.com",      # Subject (email)
  "iat": 1703503200,              # Issued At (Unix timestamp)
  "exp": 2019159200,              # Expiration (10 years)
  "iss": "online-banking-auth",   # Issuer
  "roles": ["ROLE_USER"]          # User roles
}

SIGNATURE:
HMAC-SHA512(
  base64url(header) + "." + 
  base64url(payload),
  "jwt.secret"
)

EXAMPLE:
eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9
.
eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzAzNTA
zMjAwLCJleHAiOjIwMTkxNTkyMDAsImlzcyI6Im9ubGluZS1iYW
5raW5nLWF1dGgiLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0
.
signature_bytes_base64url_encoded
```

---

## 8. DATA CONSISTENCY MODEL

### 8.1 Eventual Consistency

```
Strong Consistency (Within Service):
┌────────────────────────────────────────┐
│ User creates account in Account Service│
├────────────────────────────────────────┤
│ 1. Debit source account                │
│ 2. Credit destination account          │
│ 3. Record transaction                  │
│ 4. COMMIT (All or Nothing)             │
│ 5. ACID guaranteed                     │
│                                        │
│ Result: IMMEDIATELY consistent         │
└────────────────────────────────────────┘

Eventual Consistency (Cross Service):
┌────────────────────────────────────────┐
│ Time T0: User signs up (Auth Service)  │
│         ├─ Save to userdata table      │
│         ├─ Publish UserCreatedEvent    │
│         └─ ✓ User in db_auth           │
├────────────────────────────────────────┤
│ Time T0+100ms: Event queued in RabbitMQ│
│         └─ ✓ Event in broker           │
├────────────────────────────────────────┤
│ Time T0+500ms: Customer Service        │
│         receives event                 │
│         └─ ✓ Consuming...              │
├────────────────────────────────────────┤
│ Time T0+1000ms: UserDetail created     │
│         in db_customer                 │
│         └─ ✓ User in db_customer       │
├────────────────────────────────────────┤
│ Status: EVENTUALLY CONSISTENT          │
│ Lag: ~1 second                         │
│                                        │
│ During lag period:                     │
│ ├─ Auth DB: User exists                │
│ ├─ Customer DB: User doesn't exist (yet)
│ └─ Event in RabbitMQ: Pending delivery │
└────────────────────────────────────────┘
```

### 8.2 Handling Inconsistency

```
SCENARIO: Customer Service fails to process event

Timeline:
┌──────────────────────────────────────────────┐
│ T0: Event published                          │
│ T1: Customer Service receives event          │
│ T2: Database error occurs (connection lost)  │
│ T3: UserSyncEventListener throws exception   │
└──────────────────────────────────────────────┘

Retry Strategy:
┌──────────────────────────────────────────────┐
│ Attempt 1 (Immediately):                     │
│ ├─ Try to process event                      │
│ └─ FAILED - database connection error        │
├──────────────────────────────────────────────┤
│ Attempt 2 (After 1 second):                  │
│ ├─ Backoff: exponential delay                │
│ ├─ Try to process event                      │
│ └─ FAILED - still no connection              │
├──────────────────────────────────────────────┤
│ Attempt 3 (After 2 seconds):                 │
│ ├─ Backoff: increased delay                  │
│ ├─ Try to process event                      │
│ └─ SUCCESS - database back online            │
├──────────────────────────────────────────────┤
│ ✓ Event processed successfully               │
│ ✓ UserDetail created in db_customer          │
│ ✓ Max retries: 3                             │
│ ✓ Max backoff: 10 seconds                    │
└──────────────────────────────────────────────┘

If all retries fail:
├─ Send event to Dead Letter Queue (DLQ)
├─ Alert operations team
├─ Manual review & retry
└─ Prevent event loss
```

---

## 9. COMMUNICATION PATTERNS

### 9.1 Synchronous (REST/OpenFeign)

```
USE CASE: Customer Service needs user email from Auth Service

Architecture:
┌─────────────────────────────────────────────────┐
│ Customer Service (db_customer)                  │
│                                                 │
│ CustomerProfileService.createProfile()          │
│ └─ Need to verify user exists in Auth Service   │
│    └─ Call: AuthServiceClient.getUser(userId)   │
└────────────────┬────────────────────────────────┘
                 │
                 │ Synchronous REST Call
                 │ (OpenFeign)
                 ↓
┌─────────────────────────────────────────────────┐
│ Authentication Service (db_auth)                │
│                                                 │
│ UserController.getUser(userId)                  │
│ ├─ Query userdata table                         │
│ └─ Return User object                           │
└─────────────────────────────────────────────────┘

OpenFeign Client Code:
┌────────────────────────────────────────────┐
│ @FeignClient(                              │
│   name = "authentication-service",         │
│   url = "http://localhost:8081"            │
│ )                                          │
│ public interface AuthServiceClient {       │
│   @GetMapping("/api/v1/user/{userId}")     │
│   User getUser(@PathVariable String userId)│
│ }                                          │
└────────────────────────────────────────────┘

Pros:
✓ Real-time data
✓ No latency from message broker
✓ Immediate error feedback

Cons:
✗ Tight coupling
✗ Cascading failures
✗ Network latency adds up
✗ Blocking calls
```

### 9.2 Asynchronous (Events/RabbitMQ)

```
USE CASE: Send email when account is created

Architecture:
┌─────────────────────────────────────────────────┐
│ Account Service (db_account)                    │
│                                                 │
│ AccountController.createAccount()               │
│ ├─ Create BankAccount                           │
│ ├─ Save to db_account                           │
│ ├─ Publish AccountCreatedEvent                  │
│ └─ Return response immediately                  │
│    (Email sent asynchronously)                  │
└────────────────────────────────────────────────┘
                 │
                 │ Event Message
                 │ (Asynchronous)
                 ↓
            ┌─────────────┐
            │  RabbitMQ   │
            │             │
            │ Topic:      │
            │ account.    │
            │ created     │
            └──────┬──────┘
                   │
                   │ When ready...
                   ↓
┌─────────────────────────────────────────────────┐
│ Notification Service (db_notification)          │
│                                                 │
│ NotificationEventListener                       │
│ .accountCreatedEventConsumer()                  │
│ ├─ Consume event from queue                     │
│ ├─ Send email                                   │
│ ├─ Log to mail table                            │
│ └─ Acknowledge to RabbitMQ                      │
└─────────────────────────────────────────────────┘

Event Publishing Code:
┌──────────────────────────────────────────────┐
│ accountEventPublisher.                       │
│   publishAccountCreatedEvent(account)        │
│                                              │
│ streamBridge.send(                           │
│   "account-created-out-0",                   │
│   MessageBuilder                             │
│     .withPayload(event)                      │
│     .build()                                 │
│ )                                            │
└──────────────────────────────────────────────┘

Event Consuming Code:
┌──────────────────────────────────────────────┐
│ @Bean                                        │
│ public Consumer<AccountCreatedEvent>         │
│ accountCreatedEventConsumer() {              │
│   return event -> {                          │
│     // Process event                         │
│     mailService.sendEmail(...)               │
│   };                                         │
│ }                                            │
└──────────────────────────────────────────────┘

Pros:
✓ Loose coupling
✓ No cascading failures
✓ Parallel processing
✓ Better scalability

Cons:
✗ Eventual consistency
✗ Event delivery delays
✗ Complex debugging
✗ Event versioning needed
```

---

## 10. DEPLOYMENT ARCHITECTURE

### 10.1 Docker Compose Setup

```
Docker Network: onlinebanking-network

Containers:
┌─────────────────────────────────────────────────┐
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  RabbitMQ (Port 5672, 15672)             │  │
│  │  └─ Message Broker                       │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  MySQL - Auth DB (Port 3307)             │  │
│  │  Database: db_auth                       │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  MySQL - Customer DB (Port 3308)         │  │
│  │  Database: db_customer                   │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  MySQL - Account DB (Port 3309)          │  │
│  │  Database: db_account                    │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  MySQL - Notification DB (Port 3310)     │  │
│  │  Database: db_notification               │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  Eureka Server (Port 8761)               │  │
│  │  └─ Service Discovery                    │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  API Gateway (Port 8080)                 │  │
│  │  └─ Request Routing & Load Balancing     │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  Auth Service (Port 8081)                │  │
│  │  ├─ DB: db_auth                          │  │
│  │  └─ Publishes: UserCreatedEvent          │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  Customer Service (Port 8082)            │  │
│  │  ├─ DB: db_customer                      │  │
│  │  └─ Consumes: UserCreatedEvent           │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  Account Service (Port 8083)             │  │
│  │  ├─ DB: db_account                       │  │
│  │  └─ Publishes: AccountCreatedEvent,      │  │
│  │     TransactionCompletedEvent            │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │  Notification Service (Port 8084)        │  │
│  │  ├─ DB: db_notification                  │  │
│  │  └─ Consumes: AccountCreatedEvent,       │  │
│  │     TransactionCompletedEvent            │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
└─────────────────────────────────────────────────┘

Startup Order:
1. RabbitMQ (message broker)
2. MySQL Databases (4 instances)
3. Eureka Server (service discovery)
4. All Services (register with Eureka)
```

### 10.2 Start Services

```bash
# Start all services
docker-compose -f docker-compose-separated.yml up -d

# Check status
docker-compose -f docker-compose-separated.yml ps

# Output:
# NAME                    STATUS
# rabbitmq-broker         Up (healthy)
# mysql-auth              Up (healthy)
# mysql-customer          Up (healthy)
# mysql-account           Up (healthy)
# mysql-notification      Up (healthy)
# discovery-service       Up (healthy)
# gateway-service         Up
# authentication-service  Up
# customer-service        Up
# account-service         Up
# notification-service    Up

# View logs
docker-compose -f docker-compose-separated.yml logs -f

# Stop all services
docker-compose -f docker-compose-separated.yml down
```

---

## 📊 SYSTEM METRICS & MONITORING

### Health Check Endpoints

```
Service Health:
GET http://localhost:8081/actuator/health (Auth)
GET http://localhost:8082/actuator/health (Customer)
GET http://localhost:8083/actuator/health (Account)
GET http://localhost:8084/actuator/health (Notification)

Gateway Health:
GET http://localhost:8080/actuator/health

Eureka Dashboard:
http://localhost:8761

RabbitMQ Management:
http://localhost:15672 (guest/guest)
```

---

## 🔒 SECURITY CONSIDERATIONS

```
Authentication:
✓ JWT tokens (HS512 signature)
✓ 10-year expiration
✓ Bcrypt password hashing
✓ OTP for email verification

Authorization:
✓ Role-based access control (RBAC)
✓ @PreAuthorize annotations
✓ SecurityContext in each request

Network Security:
✓ HTTPS/TLS ready
✓ CORS configured
✓ No cross-DB connections
✓ Secure RabbitMQ connection

Data Security:
✓ Passwords never logged
✓ Sensitive data encrypted
✓ SQL injection prevention (JPA)
✓ CSRF protection enabled
```

---

**Document Status:** ✅ Complete  
**Last Updated:** 24/12/2025  
**Version:** 1.0.0
