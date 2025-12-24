# 📱 HƯỚNG DẪN TRIỂN KHAI & TEST TẤT CẢ ENDPOINTS

**Ngày tạo:** 24/12/2025  
**Phiên bản:** 1.0.0  
**Trạng thái:** Sản xuất

---

## 📑 MỤC LỤC

1. [Prerequisites & Chuẩn Bị](#1-prerequisites--chuẩn-bị)
2. [Triển Khai Hệ Thống](#2-triển-khai-hệ-thống)
3. [Authentication Service Endpoints](#3-authentication-service-endpoints)
4. [Customer Service Endpoints](#4-customer-service-endpoints)
5. [Account Service Endpoints](#5-account-service-endpoints)
6. [Notification Service Endpoints](#6-notification-service-endpoints)
7. [Testing Workflow](#7-testing-workflow)
8. [Troubleshooting](#8-troubleshooting)

---

## 1. PREREQUISITES & CHUẨN BỊ

### 1.1 Yêu Cầu Hệ Thống

```bash
# Kiểm tra phiên bản
java -version              # Java 21 LTS
mvn -version              # Maven 3.8+
docker --version          # Docker 24.x
docker-compose --version  # Docker Compose
mysql --version           # MySQL 8.0 (optional, nếu chạy local)
```

### 1.2 Tools Cần Cài Đặt

```bash
# 1. cURL (mặc định trên Linux/Mac, có sẵn trên Windows 10+)
curl --version

# 2. Postman (Optional - GUI để test)
# Download: https://www.postman.com/downloads/

# 3. Visual Studio Code (Optional - editor)
# Download: https://code.visualstudio.com/

# 4. Git (Clone repository)
git --version
```

### 1.3 Clone Repository

```bash
# Điều hướng đến thư mục làm việc
cd d:\IT\Code\Java\BTL-final\online-banking-springboot-react

# Xem cấu trúc
server/
├── authentication-service/
├── customer-service/
├── account-service/
├── notification-service/
├── discovery-service/
├── gateway-service/
├── docker-compose-separated.yml
└── ...
```

---

## 2. TRIỂN KHAI HỆ THỐNG

### 2.1 Option 1: Chạy Với Docker Compose (Recommended)

#### Step 1: Khởi Động Tất Cả Services

```bash
# Điều hướng đến thư mục server
cd d:\IT\Code\Java\BTL-final\online-banking-springboot-react\server

# Khởi động tất cả containers
docker-compose -f docker-compose-separated.yml up -d

# Kiểm tra trạng thái
docker-compose -f docker-compose-separated.yml ps

# Output mong đợi:
# NAME                    STATUS         PORTS
# rabbitmq-broker         Up (healthy)   5672->5672, 15672->15672
# mysql-auth              Up (healthy)   3307->3306
# mysql-customer          Up (healthy)   3308->3306
# mysql-account           Up (healthy)   3309->3306
# mysql-notification      Up (healthy)   3310->3306
# discovery-service       Up (healthy)   8761->8761
# gateway-service         Up             8080->8080
# authentication-service  Up             8081->8081
# customer-service        Up             8082->8082
# account-service         Up             8083->8083
# notification-service    Up             8084->8084
```

#### Step 2: Kiểm Tra Services

```bash
# Eureka Dashboard (Service Registry)
curl http://localhost:8761
# Hoặc mở browser: http://localhost:8761

# RabbitMQ Management UI
# URL: http://localhost:15672
# Username: guest
# Password: guest

# Gateway Health
curl http://localhost:8080/actuator/health

# Individual Services Health
curl http://localhost:8081/actuator/health  # Auth
curl http://localhost:8082/actuator/health  # Customer
curl http://localhost:8083/actuator/health  # Account
curl http://localhost:8084/actuator/health  # Notification
```

#### Step 3: Xem Logs

```bash
# View all logs
docker-compose -f docker-compose-separated.yml logs -f

# View specific service
docker-compose -f docker-compose-separated.yml logs -f authentication-service
docker-compose -f docker-compose-separated.yml logs -f customer-service
docker-compose -f docker-compose-separated.yml logs -f account-service
docker-compose -f docker-compose-separated.yml logs -f notification-service

# Follow last 50 lines
docker-compose -f docker-compose-separated.yml logs --tail=50 -f
```

#### Step 4: Dừng Services

```bash
# Stop all services
docker-compose -f docker-compose-separated.yml down

# Stop and remove volumes
docker-compose -f docker-compose-separated.yml down -v
```

---

### 2.2 Option 2: Chạy Locally Với Maven

#### Step 1: Build All Services

```bash
# Đi đến thư mục server
cd server/

# Build tất cả modules
mvn clean install -DskipTests

# Output mong đợi:
# [INFO] BUILD SUCCESS
# [INFO] Total time: ~20 seconds
```

#### Step 2: Khởi Động Services (6 Terminal Windows)

**Terminal 1 - Discovery Service:**
```bash
cd discovery-service
mvn spring-boot:run

# Log: o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8761
```

**Terminal 2 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run

# Log: o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8084
```

**Terminal 3 - Authentication Service:**
```bash
cd authentication-service
mvn spring-boot:run

# Log: o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8081
```

**Terminal 4 - Customer Service:**
```bash
cd customer-service
mvn spring-boot:run

# Log: o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8082
```

**Terminal 5 - Account Service:**
```bash
cd account-service
mvn spring-boot:run

# Log: o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8083
```

**Terminal 6 - Gateway Service:**
```bash
cd gateway-service
mvn spring-boot:run

# Log: o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8080
```

---

## 3. AUTHENTICATION SERVICE ENDPOINTS

**Base URL:** `http://localhost:8080` (via Gateway)  
**Direct:** `http://localhost:8081`

### 3.1 Sign Up (Đăng Ký)

#### Endpoint
```
POST /api/v1/signup
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@example.com",
    "password": "SecurePassword@123"
  }'
```

#### Response (200 OK)
```json
{
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "otp": "123456",
  "role": "USER",
  "enabled": false
}
```

#### Expected Behavior
- ✅ User saved to db_auth.userdata
- ✅ OTP generated (6 digits)
- ✅ UserCreatedEvent published to RabbitMQ
- ✅ Customer Service receives event (check logs)
- ✅ Account NOT verified yet (enabled: false)

---

### 3.2 Verify OTP (Xác Minh OTP)

#### Endpoint
```
POST /api/v1/otp
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/otp \
  -H "Content-Type: application/json" \
  -d '{
    "otp": "123456"
  }'
```

#### Response (200 OK)
```json
{
  "message": "Email verified successfully",
  "status": "VERIFIED"
}
```

#### Expected Behavior
- ✅ OTP verified in db_auth.user_otp_log
- ✅ User enabled = true
- ✅ Account can now login

#### Test Cases
```
Case 1: Valid OTP
├─ Input: Correct OTP from signup
└─ Expected: 200 OK

Case 2: Invalid OTP
├─ Input: Wrong OTP
└─ Expected: 400 Bad Request (Invalid OTP)

Case 3: Expired OTP
├─ Input: OTP older than 10 minutes
└─ Expected: 400 Bad Request (OTP Expired)
```

---

### 3.3 Resend OTP (Gửi Lại OTP)

#### Endpoint
```
POST /api/v1/resend-otp/{userId}
```

#### Request
```bash
# Replace {userId} with actual userId from signup
curl -X POST http://localhost:8080/api/v1/resend-otp/a1b2c3d4-e5f6-7890-abcd-ef1234567890 \
  -H "Content-Type: application/json"
```

#### Response (200 OK)
```json
{
  "message": "OTP sent successfully",
  "email": "john.doe@example.com"
}
```

#### Expected Behavior
- ✅ New OTP generated
- ✅ Old OTP invalidated
- ✅ Email sent (check Notification Service logs)
- ✅ Update db_auth.user_otp_log

---

### 3.4 Login (Đăng Nhập)

#### Endpoint
```
POST /api/v1/login
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword@123"
  }'
```

#### Response (200 OK)
```json
{
  "jwtToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTcwMzQyMzQwMCwiZXhwIjoyMDE5MjAxMjAwfQ.signature",
  "user": {
    "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@example.com",
    "role": "USER",
    "enabled": true
  }
}
```

#### Save JWT Token
```bash
# For next requests, save token in variable
export JWT_TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTcwMzQyMzQwMCwiZXhwIjoyMDE5MjAxMjAwfQ.signature"

# Verify token is saved
echo $JWT_TOKEN
```

#### Expected Behavior
- ✅ User verified from db_auth.userdata
- ✅ Password checked (Bcrypt comparison)
- ✅ JWT token generated (HS512, 10-year expiration)
- ✅ Token includes: sub (email), iat, exp, roles

#### Test Cases
```
Case 1: Valid credentials
├─ Email: exists
├─ Password: correct
├─ User enabled: true
└─ Expected: 200 OK + JWT

Case 2: Wrong password
├─ Email: exists
├─ Password: incorrect
└─ Expected: 401 Unauthorized

Case 3: Email not exists
├─ Email: invalid
└─ Expected: 401 Unauthorized

Case 4: User not verified
├─ Email: exists
├─ Password: correct
├─ User enabled: false (OTP not verified)
└─ Expected: 403 Forbidden (Email not verified)
```

---

## 4. CUSTOMER SERVICE ENDPOINTS

**Base URL:** `http://localhost:8080` (via Gateway)  
**Direct:** `http://localhost:8082`  
**All endpoints require JWT token in Authorization header**

### 4.1 Create/Update Customer Profile (Tạo/Cập Nhật Hồ Sơ)

#### Endpoint
```
POST /api/v1/customer/profile
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/customer/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@example.com",
    "mobile": "0123456789",
    "pan": "ABCDE1234F",
    "adhaar": "1234-5678-9012",
    "dateofbirth": "1990-01-01",
    "gender": "M",
    "address": "123 Main Street",
    "city": "Ho Chi Minh",
    "state": "HCM",
    "pin": "70000"
  }'
```

#### Response (200 OK)
```json
{
  "userdetailsid": 1,
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "mobile": "0123456789",
  "pan": "ABCDE1234F",
  "adhaar": "1234-5678-9012",
  "dateofbirth": "1990-01-01",
  "gender": "M",
  "address": "123 Main Street",
  "city": "Ho Chi Minh",
  "state": "HCM",
  "pin": "70000",
  "created_at": "2024-12-24T10:30:00Z",
  "updated_at": "2024-12-24T10:30:00Z"
}
```

#### Expected Behavior
- ✅ Record created in db_customer.userdetails
- ✅ Linked to userid from auth-db
- ✅ Update user_sync_log

---

### 4.2 Get Customer Profile (Lấy Hồ Sơ)

#### Endpoint
```
GET /api/v1/customer/profile/{userId}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/customer/profile/a1b2c3d4-e5f6-7890-abcd-ef1234567890 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
{
  "userdetailsid": 1,
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "firstname": "John",
  "lastname": "Doe",
  "address": "123 Main Street",
  "city": "Ho Chi Minh"
}
```

#### Expected Behavior
- ✅ Retrieved from db_customer.userdetails
- ✅ Returns all customer profile fields

---

### 4.3 Update Customer Profile (Cập Nhật Hồ Sơ)

#### Endpoint
```
PUT /api/v1/customer/profile/{userId}
```

#### Request
```bash
curl -X PUT http://localhost:8080/api/v1/customer/profile/a1b2c3d4-e5f6-7890-abcd-ef1234567890 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "address": "456 New Street",
    "city": "Da Nang",
    "mobile": "9876543210"
  }'
```

#### Response (200 OK)
```json
{
  "message": "Profile updated successfully",
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

#### Expected Behavior
- ✅ Updated in db_customer.userdetails
- ✅ updated_at timestamp changed

---

### 4.4 Add Beneficiary (Thêm Người Thụ Hưởng)

#### Endpoint
```
POST /api/v1/customer/beneficiary
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/customer/beneficiary \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "beneficiaryname": "Jane Doe",
    "beneaccountno": 1000001,
    "relation": "Spouse"
  }'
```

#### Response (200 OK)
```json
{
  "beneficiaryid": 1,
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "beneficiaryname": "Jane Doe",
  "beneaccountno": 1000001,
  "relation": "Spouse",
  "created_at": "2024-12-24T10:35:00Z"
}
```

#### Expected Behavior
- ✅ Record created in db_customer.beneficiaries
- ✅ Linked to userid

---

### 4.5 Get Beneficiaries (Lấy Danh Sách Thụ Hưởng)

#### Endpoint
```
GET /api/v1/customer/beneficiary/{userId}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/customer/beneficiary/a1b2c3d4-e5f6-7890-abcd-ef1234567890 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
[
  {
    "beneficiaryid": 1,
    "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "beneficiaryname": "Jane Doe",
    "beneaccountno": 1000001,
    "relation": "Spouse"
  },
  {
    "beneficiaryid": 2,
    "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "beneficiaryname": "John Smith",
    "beneaccountno": 1000002,
    "relation": "Friend"
  }
]
```

#### Expected Behavior
- ✅ Retrieved all beneficiaries from db_customer.beneficiaries
- ✅ Returns array of beneficiaries

---

### 4.6 Delete Beneficiary (Xóa Thụ Hưởng)

#### Endpoint
```
DELETE /api/v1/customer/beneficiary/{beneficiaryId}
```

#### Request
```bash
curl -X DELETE http://localhost:8080/api/v1/customer/beneficiary/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
{
  "message": "Beneficiary deleted successfully",
  "beneficiaryid": 1
}
```

#### Expected Behavior
- ✅ Deleted from db_customer.beneficiaries

---

## 5. ACCOUNT SERVICE ENDPOINTS

**Base URL:** `http://localhost:8080` (via Gateway)  
**Direct:** `http://localhost:8083`  
**All endpoints require JWT token**

### 5.1 Create Bank Account (Tạo Tài Khoản)

#### Endpoint
```
POST /api/v1/account/create
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/account/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "accountType": "Savings",
    "balance": 10000.00
  }'
```

#### Response (200 OK)
```json
{
  "accountno": 1000001,
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accountType": "Savings",
  "balance": 10000.00,
  "isactive": true,
  "dateCreated": "2024-12-24",
  "timeCreated": "10:40:00"
}
```

#### Expected Behavior
- ✅ Account created in db_account.bankaccount
- ✅ AccountCreatedEvent published to RabbitMQ
- ✅ Notification Service receives event (sends confirmation email)
- ✅ accountno auto-generated

---

### 5.2 Get Account Details (Lấy Chi Tiết Tài Khoản)

#### Endpoint
```
GET /api/v1/account/{accountNo}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/account/1000001 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
{
  "accountno": 1000001,
  "userid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accountType": "Savings",
  "balance": 10000.00,
  "isactive": true
}
```

---

### 5.3 Get All User Accounts (Lấy Tất Cả Tài Khoản)

#### Endpoint
```
GET /api/v1/account/user/{userId}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/account/user/a1b2c3d4-e5f6-7890-abcd-ef1234567890 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
[
  {
    "accountno": 1000001,
    "accountType": "Savings",
    "balance": 10000.00,
    "isactive": true
  },
  {
    "accountno": 1000002,
    "accountType": "Checking",
    "balance": 5000.00,
    "isactive": true
  }
]
```

---

### 5.4 Check Balance (Kiểm Tra Số Dư)

#### Endpoint
```
GET /api/v1/account/balance/{accountNo}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/account/balance/1000001 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
{
  "accountno": 1000001,
  "balance": 10000.00,
  "currency": "VND"
}
```

---

### 5.5 Fund Transfer (Chuyển Tiền)

#### Endpoint
```
POST /api/v1/account/transfer
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/account/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "fromAccount": 1000001,
    "toAccount": 1000002,
    "amount": 500.00,
    "description": "Payment for services"
  }'
```

#### Response (200 OK)
```json
{
  "transactionId": 1,
  "fromAccount": 1000001,
  "toAccount": 1000002,
  "amount": 500.00,
  "transactionStatus": "SUCCESS",
  "transactionDate": "2024-12-24",
  "transactionTime": "10:45:30",
  "description": "Payment for services"
}
```

#### Expected Behavior
- ✅ Debit from source account (fromAccount)
- ✅ Credit to destination account (toAccount)
- ✅ Transaction recorded in db_account.transactions
- ✅ TransactionCompletedEvent published
- ✅ Notification Service receives event (sends confirmation email)

#### Test Cases
```
Case 1: Valid transfer
├─ From account: has sufficient balance
├─ To account: exists
└─ Expected: 200 OK + Transaction created

Case 2: Insufficient balance
├─ From account: balance < amount
└─ Expected: 400 Bad Request (Insufficient balance)

Case 3: Account doesn't exist
├─ From/To account: invalid
└─ Expected: 404 Not Found (Account not found)

Case 4: Zero or negative amount
├─ Amount: <= 0
└─ Expected: 400 Bad Request (Invalid amount)
```

---

### 5.6 Get Transaction History (Lịch Sử Giao Dịch)

#### Endpoint
```
GET /api/v1/account/transactions/{accountNo}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/account/transactions/1000001 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
[
  {
    "transactionId": 1,
    "fromAccount": 1000001,
    "toAccount": 1000002,
    "amount": 500.00,
    "transactionStatus": "SUCCESS",
    "transactionDate": "2024-12-24",
    "transactionTime": "10:45:30"
  },
  {
    "transactionId": 2,
    "fromAccount": 1000003,
    "toAccount": 1000001,
    "amount": 200.00,
    "transactionStatus": "SUCCESS",
    "transactionDate": "2024-12-24",
    "transactionTime": "11:00:15"
  }
]
```

---

### 5.7 Get Transaction Details (Chi Tiết Giao Dịch)

#### Endpoint
```
GET /api/v1/account/transaction/{transactionId}
```

#### Request
```bash
curl -X GET http://localhost:8080/api/v1/account/transaction/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### Response (200 OK)
```json
{
  "transactionId": 1,
  "fromAccount": 1000001,
  "toAccount": 1000002,
  "amount": 500.00,
  "transactionStatus": "SUCCESS",
  "transactionDate": "2024-12-24",
  "transactionTime": "10:45:30",
  "description": "Payment for services"
}
```

---

## 6. NOTIFICATION SERVICE ENDPOINTS

**Base URL:** `http://localhost:8080` (via Gateway)  
**Direct:** `http://localhost:8084`  
**Can be called by other services via Feign OR directly via REST**

### 6.1 Send Email (Gửi Email)

#### Endpoint
```
POST /api/v1/mail/send
```

#### Request
```bash
curl -X POST http://localhost:8080/api/v1/mail/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "john.doe@example.com",
    "subject": "Welcome to Online Banking",
    "body": "Thank you for registering with us!"
  }'
```

#### Response (200 OK)
```json
{
  "message": "Email sent successfully",
  "sentDate": "2024-12-24T10:50:00Z",
  "to": "john.doe@example.com"
}
```

#### Expected Behavior
- ✅ Email recorded in db_notification.mail
- ✅ Sent via SMTP (Gmail)
- ✅ Status: SENT (if successful)
- ✅ Status: FAILED (if error)

---

## 7. TESTING WORKFLOW

### 7.1 Complete User Journey Test

```bash
# ============ STEP 1: SIGN UP ============
# Register new user
SIGNUP_RESPONSE=$(curl -X POST http://localhost:8080/api/v1/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Test",
    "lastname": "User",
    "email": "testuser@example.com",
    "password": "TestPassword@123"
  }')

echo "Signup Response: $SIGNUP_RESPONSE"

# Extract userId and OTP from response (using jq)
USER_ID=$(echo $SIGNUP_RESPONSE | jq -r '.userid')
OTP=$(echo $SIGNUP_RESPONSE | jq -r '.otp')

echo "User ID: $USER_ID"
echo "OTP: $OTP"

# ============ STEP 2: VERIFY OTP ============
curl -X POST http://localhost:8080/api/v1/otp \
  -H "Content-Type: application/json" \
  -d "{\"otp\": \"$OTP\"}"

echo "OTP Verified ✓"

# ============ STEP 3: LOGIN ============
LOGIN_RESPONSE=$(curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "TestPassword@123"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Extract JWT token
JWT_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.jwtToken')
echo "JWT Token: $JWT_TOKEN"

# ============ STEP 4: CREATE CUSTOMER PROFILE ============
curl -X POST http://localhost:8080/api/v1/customer/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"userid\": \"$USER_ID\",
    \"firstname\": \"Test\",
    \"lastname\": \"User\",
    \"email\": \"testuser@example.com\",
    \"mobile\": \"0123456789\",
    \"address\": \"123 Test St\",
    \"city\": \"Test City\",
    \"state\": \"TS\",
    \"pin\": \"12345\"
  }"

echo "Profile Created ✓"

# ============ STEP 5: CREATE BANK ACCOUNT ============
ACCOUNT_RESPONSE=$(curl -X POST http://localhost:8080/api/v1/account/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"userid\": \"$USER_ID\",
    \"accountType\": \"Savings\",
    \"balance\": 10000.00
  }")

echo "Account Created: $ACCOUNT_RESPONSE"

# Extract account number
ACCOUNT_1=$(echo $ACCOUNT_RESPONSE | jq -r '.accountno')
echo "Account 1: $ACCOUNT_1"

# ============ STEP 6: CREATE SECOND ACCOUNT ============
ACCOUNT_2_RESPONSE=$(curl -X POST http://localhost:8080/api/v1/account/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"userid\": \"$USER_ID\",
    \"accountType\": \"Checking\",
    \"balance\": 5000.00
  }")

ACCOUNT_2=$(echo $ACCOUNT_2_RESPONSE | jq -r '.accountno')
echo "Account 2: $ACCOUNT_2"

# ============ STEP 7: TRANSFER FUNDS ============
curl -X POST http://localhost:8080/api/v1/account/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"fromAccount\": $ACCOUNT_1,
    \"toAccount\": $ACCOUNT_2,
    \"amount\": 500.00,
    \"description\": \"Transfer to checking\"
  }"

echo "Transfer Completed ✓"

# ============ STEP 8: CHECK BALANCE ============
curl -X GET http://localhost:8080/api/v1/account/balance/$ACCOUNT_1 \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

echo "Balance: $ACCOUNT_1 should be 9500"

# ============ STEP 9: GET TRANSACTION HISTORY ============
curl -X GET http://localhost:8080/api/v1/account/transactions/$ACCOUNT_1 \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

echo "Transaction History Retrieved ✓"

# ============ STEP 10: ADD BENEFICIARY ============
curl -X POST http://localhost:8080/api/v1/customer/beneficiary \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d "{
    \"userid\": \"$USER_ID\",
    \"beneficiaryname\": \"Beneficiary Name\",
    \"beneaccountno\": $ACCOUNT_2,
    \"relation\": \"Friend\"
  }"

echo "Beneficiary Added ✓"

# ============ ALL TESTS COMPLETED ============
echo "✅ ALL TESTS PASSED!"
```

### 7.2 Using Postman Collection

#### Import Collection

```json
{
  "info": {
    "name": "Online Banking API",
    "version": "1.0.0"
  },
  "item": [
    {
      "name": "1. Sign Up",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/v1/signup",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"firstname\":\"Test\",\"lastname\":\"User\",\"email\":\"test@example.com\",\"password\":\"Password@123\"}"
        }
      }
    },
    {
      "name": "2. Verify OTP",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/v1/otp",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"otp\":\"123456\"}"
        }
      }
    },
    {
      "name": "3. Login",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/v1/login",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"email\":\"test@example.com\",\"password\":\"Password@123\"}"
        }
      }
    }
  ]
}
```

---

## 8. TROUBLESHOOTING

### Issue 1: "Connection refused" (Port 8080)

```bash
# Problem: Gateway not running

# Solution:
docker-compose -f docker-compose-separated.yml ps
# Check if gateway-service is running

# If not running:
docker-compose -f docker-compose-separated.yml up -d gateway-service
```

### Issue 2: "401 Unauthorized" on protected endpoints

```bash
# Problem: Missing or invalid JWT token

# Solution:
# 1. Login first to get token
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"email":"...","password":"..."}'

# 2. Copy jwtToken from response

# 3. Include in Authorization header
curl -X GET http://localhost:8080/api/v1/account/user/xxx \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

### Issue 3: "Database connection failed"

```bash
# Problem: MySQL container not running

# Solution:
docker-compose -f docker-compose-separated.yml ps

# Check if mysql-auth, mysql-customer, etc. running
# If not:
docker-compose -f docker-compose-separated.yml up -d mysql-auth mysql-customer mysql-account mysql-notification
```

### Issue 4: "Event not consumed" (Notification email not sent)

```bash
# Problem: RabbitMQ not running or Notification Service not consuming

# Solution:
# 1. Check RabbitMQ
docker-compose -f docker-compose-separated.yml ps rabbitmq

# 2. Check Notification Service logs
docker-compose -f docker-compose-separated.yml logs notification-service

# 3. View RabbitMQ Management UI
# http://localhost:15672
# Check queues and bindings
```

### Issue 5: "OTP verification failed"

```bash
# Problem: Wrong OTP or OTP expired

# Solution:
# 1. Check OTP from signup response
# 2. OTP is valid for 10 minutes
# 3. Use resend-otp endpoint if expired
curl -X POST http://localhost:8080/api/v1/resend-otp/{userId}
```

---

## 📋 TESTING CHECKLIST

- [ ] All Docker containers running
- [ ] Eureka dashboard accessible (http://localhost:8761)
- [ ] RabbitMQ management accessible (http://localhost:15672)
- [ ] Sign up new user
- [ ] Verify OTP
- [ ] Login successfully
- [ ] Create customer profile
- [ ] Create bank account
- [ ] Check balance
- [ ] Add beneficiary
- [ ] Transfer funds between accounts
- [ ] Get transaction history
- [ ] Receive confirmation emails
- [ ] Check logs in all services

---

**Document Status:** ✅ Complete  
**Last Updated:** 24/12/2025  
**Version:** 1.0.0
