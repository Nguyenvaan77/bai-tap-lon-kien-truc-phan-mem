# 💰 HƯỚNG DẪN TEST CHUYỂN TIỀN (Transfer Money)

**Mục tiêu:** Khởi chạy services & test endpoint chuyển tiền với benchmark  
**Thời gian:** 30-45 phút  
**Đối tượng:** Microservices architecture

---

## 📑 MỤC LỤC

1. [Khởi Chạy Services](#1-khởi-chạy-services)
2. [Chuẩn Bị Test Data](#2-chuẩn-bị-test-data)
3. [Test Manual với Curl](#3-test-manual-với-curl)
4. [Tạo JMeter Test Plan](#4-tạo-jmeter-test-plan)
5. [Chạy Load Test](#5-chạy-load-test)
6. [Phân Tích Kết Quả](#6-phân-tích-kết-quả)

---

## 1. KHỞI CHẠY SERVICES

### 1.1 Kiểm Tra Yêu Cầu

```bash
# Kiểm tra Docker
docker --version
# Expected: Docker version 20.10+

# Kiểm tra Docker Compose
docker-compose --version
# Expected: Docker Compose version 2.0+

# Kiểm tra Java
java -version
# Expected: Java 21+ LTS

# Kiểm tra MySQL installed (locally)
mysql --version
# OR check Docker images
docker images | grep mysql
```

### 1.2 Dừng Services Cũ (Nếu Đang Chạy)

```bash
# Dừng tất cả Docker containers
docker-compose down

# Xóa volumes (reset data - Optional)
docker-compose down -v

# Xóa tất cả unused containers
docker system prune -a
```

### 1.3 Khởi Chạy Services

**Terminal 1: Khởi chạy Docker Compose**

```bash
# Điều hướng đến server directory
cd d:\IT\Code\Java\BTL-final\online-banking-springboot-react\server

# Khởi chạy tất cả services
docker-compose up -d

# Hoặc nếu dùng file separated
docker-compose -f docker-compose-separated.yml up -d

# Kiểm tra status
docker-compose ps

# Expected output:
# NAME                      COMMAND                  SERVICE          STATUS      PORTS
# bank-account-service      "java -jar account..."   account-service  Up (healthy) 8083->8080/tcp
# bank-auth-service         "java -jar auth..."      auth-service     Up (healthy) 8081->8080/tcp
# bank-customer-service     "java -jar customer..."  customer-service Up (healthy) 8082->8080/tcp
# bank-notification-service "java -jar notif..."     notify-service   Up (healthy) 8084->8080/tcp
# bank-gateway-service      "java -jar gateway..."   gateway-service  Up (healthy) 8080->8080/tcp
# bank-discovery-service    "java -jar eureka..."    discovery-service Up         8761->8761/tcp
# mysql_auth                "docker-entrypoint..."   mysql-auth       Up          3307->3306/tcp
# mysql_customer            "docker-entrypoint..."   mysql-customer   Up          3308->3306/tcp
# mysql_account             "docker-entrypoint..."   mysql-account    Up          3309->3306/tcp
# rabbitmq                  "docker-entrypoint..."   rabbitmq         Up          5672->5672/tcp, 15672->15672/tcp
```

**Terminal 2: Monitor Logs**

```bash
# Theo dõi logs tất cả services
docker-compose logs -f

# HOẶC theo dõi logs từng service
docker-compose logs -f account-service
docker-compose logs -f gateway-service
docker-compose logs -f rabbitmq
```

### 1.4 Kiểm Tra Services Healthy

```bash
# Chờ 30-60 giây để services fully startup

# Kiểm tra API Gateway
curl -X GET http://localhost:8080/actuator/health -s | jq .

# Expected:
# {
#   "status": "UP"
# }

# Kiểm tra từng service
curl -X GET http://localhost:8081/actuator/health -s | jq .  # Auth Service
curl -X GET http://localhost:8082/actuator/health -s | jq .  # Customer Service
curl -X GET http://localhost:8083/actuator/health -s | jq .  # Account Service
curl -X GET http://localhost:8084/actuator/health -s | jq .  # Notification Service

# Kiểm tra Eureka Discovery
curl -X GET http://localhost:8761/eureka/apps -s | grep "UP" | wc -l
# Expected: 6 services UP

# Kiểm tra RabbitMQ
curl -X GET http://localhost:15672/api/overview -u guest:guest -s | jq .

# Kiểm tra MySQL connections
docker exec bank-mysql_account mysql -uroot -ppassword -e "SHOW PROCESSLIST;" | head -20
```

### 1.5 Khởi Chạy JVM Monitoring (Optional nhưng Khuyến Nghị)

**Terminal 3: Start VisualVM hoặc JConsole**

```bash
# Cách 1: JConsole (Windows)
jconsole

# Cách 2: VisualVM
jvisualvm

# Sau đó: Thêm JMX Connection
# localhost:9010 (hoặc port khác nếu cấu hình khác)
```

---

## 2. CHUẨN BỊ TEST DATA

### 2.1 Tạo Test Users & Accounts

**Terminal: Chạy SQL Scripts**

```bash
# Connect vào MySQL Account Service
docker exec -it bank-mysql_account mysql -uroot -ppassword db_account

# Hoặc local MySQL (nếu port 3309 open)
mysql -h 127.0.0.1 -P 3309 -u root -p db_account
# Password: password
```

**SQL Script 1: Tạo Test Users (Authentication Service)**

```sql
-- Connect to db_auth
USE db_auth;

-- Insert test users
INSERT INTO userdata (userid, firstname, lastname, email, password_hash, enabled, otp, role, created_date) VALUES
('user-001-transfer', 'John', 'Doe', 'john.transfer@test.com', 'hashed_password_123', 1, '000000', 'USER', NOW()),
('user-002-transfer', 'Jane', 'Smith', 'jane.transfer@test.com', 'hashed_password_123', 1, '000000', 'USER', NOW()),
('user-003-transfer', 'Bob', 'Johnson', 'bob.transfer@test.com', 'hashed_password_123', 1, '000000', 'USER', NOW()),
('user-004-transfer', 'Alice', 'Williams', 'alice.transfer@test.com', 'hashed_password_123', 1, '000000', 'USER', NOW()),
('user-005-transfer', 'Charlie', 'Brown', 'charlie.transfer@test.com', 'hashed_password_123', 1, '000000', 'USER', NOW());

-- Verify
SELECT userid, email, enabled FROM userdata WHERE userid LIKE '%transfer%';
```

**SQL Script 2: Tạo Test Accounts (Account Service)**

```sql
-- Connect to db_account
USE db_account;

-- Insert test accounts
INSERT INTO bankaccount (userid, accounttype, balance, isactive, datecreated, timecreated) VALUES
('user-001-transfer', 'SAVINGS', 500000.00, 1, CURDATE(), CURTIME()),
('user-002-transfer', 'SAVINGS', 300000.00, 1, CURDATE(), CURTIME()),
('user-003-transfer', 'SAVINGS', 250000.00, 1, CURDATE(), CURTIME()),
('user-004-transfer', 'SAVINGS', 100000.00, 1, CURDATE(), CURTIME()),
('user-005-transfer', 'SAVINGS', 50000.00, 1, CURDATE(), CURTIME());

-- Verify
SELECT accountid, userid, accounttype, balance, isactive FROM bankaccount WHERE userid LIKE '%transfer%';

-- Get accountids untuk dùng trong tests
SELECT accountid, userid, balance FROM bankaccount WHERE userid LIKE '%transfer%';
```

**SQL Script 3: Tạo Test Customers (Customer Service)**

```sql
-- Connect to db_customer
USE db_customer;

-- Insert test customers (sync từ auth service)
INSERT INTO customers (userid, firstname, lastname, email, phonenumber, address, city, dateofbirth, created_date) VALUES
('user-001-transfer', 'John', 'Doe', 'john.transfer@test.com', '0901234567', '123 Main St', 'New York', '1990-01-15', NOW()),
('user-002-transfer', 'Jane', 'Smith', 'jane.transfer@test.com', '0901234568', '456 Oak Ave', 'Los Angeles', '1992-05-20', NOW()),
('user-003-transfer', 'Bob', 'Johnson', 'bob.transfer@test.com', '0901234569', '789 Pine Rd', 'Chicago', '1988-12-10', NOW()),
('user-004-transfer', 'Alice', 'Williams', 'alice.transfer@test.com', '0901234570', '321 Elm St', 'Houston', '1995-08-25', NOW()),
('user-005-transfer', 'Charlie', 'Brown', 'charlie.transfer@test.com', '0901234571', '654 Maple Dr', 'Phoenix', '1991-03-05', NOW());

-- Verify
SELECT userid, firstname, lastname, email FROM customers WHERE userid LIKE '%transfer%';
```

### 2.2 Lấy JWT Tokens cho Test

**Terminal: Lấy Login Tokens**

```bash
# User 1 Login
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.transfer@test.com",
    "password": "password123"
  }' | jq .

# Response sẽ chứa token:
# {
#   "token": "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJ1c2VyLTAwMS10cmFuc2Zlciis...",
#   "status": 200,
#   "message": "User logged in successfully"
# }
```

**Lưu Tokens vào File (Để sử dụng sau)**

```bash
# File: tokens.txt (Windows PowerShell)
@"
USER_1_TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJ1c2VyLTAwMS10cmFuc2Zlciis...
USER_1_ID=user-001-transfer
USER_1_ACCOUNT=1  # account id từ DB

USER_2_TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJ1c2VyLTAwMi10cmFuc2Zlciis...
USER_2_ID=user-002-transfer
USER_2_ACCOUNT=2

USER_3_TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJ1c2VyLTAwMy10cmFuc2Zlciis...
USER_3_ID=user-003-transfer
USER_3_ACCOUNT=3
"@ | Out-File tokens.txt
```

---

## 3. TEST MANUAL DENGAN CURL

### 3.1 Transfer Money Endpoint Details

**API Spec:**
```
POST /api/v1/transfer/money
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 10000.00,
  "description": "Payment for services"
}

Response (200):
{
  "transactionId": "TXN-001",
  "status": "SUCCESS",
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 10000.00,
  "transactionTime": "2024-12-24T10:30:45",
  "message": "Transfer completed successfully"
}
```

### 3.2 Test Transfer #1: Simple Success Case

```bash
# Variables
$TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJ1c2VyLTAwMS10cmFuc2Zlciis..."
$FROM_ACCOUNT = 1
$TO_ACCOUNT = 2
$AMOUNT = 10000

# Test 1: Single transfer
curl -X POST http://localhost:8080/api/v1/transfer/money `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d "{
    \"fromAccountId\": $FROM_ACCOUNT,
    \"toAccountId\": $TO_ACCOUNT,
    \"amount\": $AMOUNT,
    \"description\": \"Test transfer 1\"
  }" | jq .

# Expected Response (200):
# {
#   "transactionId": "TXN-20241224-001",
#   "status": "SUCCESS",
#   "fromAccountId": 1,
#   "toAccountId": 2,
#   "amount": 10000,
#   "balanceAfter": 490000,
#   "transactionTime": "2024-12-24T10:30:45"
# }
```

### 3.3 Test Transfer #2: Verify Balances

```bash
# Sau khi transfer, kiểm tra balance
curl -X GET http://localhost:8080/api/v1/account/get/1 `
  -H "Authorization: Bearer $TOKEN" | jq .

# Expected:
# {
#   "accountId": 1,
#   "userId": "user-001-transfer",
#   "accountType": "SAVINGS",
#   "balance": 490000.00,  # 500000 - 10000
#   "isActive": true
# }

# Kiểm tra account 2
curl -X GET http://localhost:8080/api/v1/account/get/2 `
  -H "Authorization: Bearer $TOKEN" | jq .

# Expected:
# {
#   "accountId": 2,
#   "userId": "user-002-transfer",
#   "accountType": "SAVINGS",
#   "balance": 310000.00,  # 300000 + 10000
#   "isActive": true
# }
```

### 3.4 Test Transfer #3: Error Cases

**Test 3a: Insufficient Balance**
```bash
$TOKEN = "user-005-transfer token"
$AMOUNT = 100000  # Số tiền > balance (50000)

curl -X POST http://localhost:8080/api/v1/transfer/money `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d "{
    \"fromAccountId\": 5,
    \"toAccountId\": 1,
    \"amount\": $AMOUNT,
    \"description\": \"Insufficient balance test\"
  }" | jq .

# Expected (400):
# {
#   "status": "ERROR",
#   "message": "Insufficient balance",
#   "code": "INSUFFICIENT_FUNDS"
# }
```

**Test 3b: Invalid Account**
```bash
curl -X POST http://localhost:8080/api/v1/transfer/money `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d "{
    \"fromAccountId\": 1,
    \"toAccountId\": 999,  # Account tidak tồn tại
    \"amount\": 5000,
    \"description\": \"Invalid account test\"
  }" | jq .

# Expected (404):
# {
#   "status": "ERROR",
#   "message": "Account not found",
#   "code": "ACCOUNT_NOT_FOUND"
# }
```

**Test 3c: Same Account Transfer**
```bash
curl -X POST http://localhost:8080/api/v1/transfer/money `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d "{
    \"fromAccountId\": 1,
    \"toAccountId\": 1,  # Same account
    \"amount\": 5000,
    \"description\": \"Self transfer\"
  }" | jq .

# Expected (400):
# {
#   "status": "ERROR",
#   "message": "Cannot transfer to same account",
#   "code": "INVALID_TRANSFER"
# }
```

### 3.5 Performance Baseline (Single User)

```bash
# Measure response time
Measure-Command {
  curl -X POST http://localhost:8080/api/v1/transfer/money `
    -H "Content-Type: application/json" `
    -H "Authorization: Bearer $TOKEN" `
    -d "{
      \"fromAccountId\": 1,
      \"toAccountId\": 2,
      \"amount\": 1000,
      \"description\": \"Perf test\"
    }" -o $null -s
}

# Expected: 50-200ms (single user baseline)
```

---

## 4. TẠO JMETER TEST PLAN

### 4.1 Tải JMeter

```bash
# Download JMeter
# Website: https://jmeter.apache.org/download_jmeter.html

# OR install via Chocolatey (Windows)
choco install jmeter

# Verify
jmeter --version
# Expected: Apache JMeter 5.6+
```

### 4.2 Tạo Test Plan File

**File: transfer_money_test.jmx**

Cách 1: Tạo bằng GUI (Recommended)

```bash
# Mở JMeter GUI
jmeter

# Menu: File → New
# Hoặc tạo từ command line
```

**Cách 2: Tạo từ XML Configuration**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Transfer Money Load Test">
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments"/>
      <stringProp name="TestPlan.comments">Test transfer money endpoint under load</stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
        <collectionProp name="Arguments.arguments">
          <elementProp name="BASE_URL" elementType="Argument">
            <stringProp name="Argument.name">BASE_URL</stringProp>
            <stringProp name="Argument.value">http://localhost:8080</stringProp>
          </elementProp>
          <elementProp name="TOKEN" elementType="Argument">
            <stringProp name="Argument.name">TOKEN</stringProp>
            <stringProp name="Argument.value">eyJhbGciOiJIUzUxMiJ9...</stringProp>
          </elementProp>
          <elementProp name="FROM_ACCOUNT" elementType="Argument">
            <stringProp name="Argument.name">FROM_ACCOUNT</stringProp>
            <stringProp name="Argument.value">1</stringProp>
          </elementProp>
          <elementProp name="TO_ACCOUNT" elementType="Argument">
            <stringProp name="Argument.name">TO_ACCOUNT</stringProp>
            <stringProp name="Argument.value">2</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
      <hashTree>
        <!-- Thread Group -->
        <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Transfer Money Users">
          <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller">
            <boolProp name="LoopController.continue_forever">false</boolProp>
            <stringProp name="LoopController.loops">10</stringProp>
          </elementProp>
          <stringProp name="ThreadGroup.num_threads">10</stringProp>
          <stringProp name="ThreadGroup.ramp_time">10</stringProp>
          <elementProp name="ThreadGroup.scheduler" elementType="SchedulerConfigGui" guiclass="SchedulerConfigGui" testclass="SchedulerConfigGui" testname="Scheduler Configuration">
            <boolProp name="SchedulerConfigGui.continue_forever">false</boolProp>
            <boolProp name="SchedulerConfigGui.enabled">false</boolProp>
            <longProp name="SchedulerConfigGui.duration">300</longProp>
            <longProp name="SchedulerConfigGui.delay">0</longProp>
          </elementProp>
          <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
          <boolProp name="ThreadGroup.scheduler">false</boolProp>
          <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        </ThreadGroup>
        <hashTree>
          <!-- HTTP Request: Transfer Money -->
          <HTTPSampler guiclass="HttpTestSampleGui" testclass="HTTPSampler" testname="Transfer Money">
            <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
              <collectionProp name="Arguments.arguments"/>
            </elementProp>
            <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
            <stringProp name="HTTPSampler.port">8080</stringProp>
            <stringProp name="HTTPSampler.protocol">http</stringProp>
            <stringProp name="HTTPSampler.path">/api/v1/transfer/money</stringProp>
            <stringProp name="HTTPSampler.method">POST</stringProp>
            <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
            <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
            <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
            <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
            <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
            <stringProp name="HTTPSampler.connect_timeout"></stringProp>
            <stringProp name="HTTPSampler.response_timeout"></stringProp>
          </HTTPSampler>
          <hashTree>
            <!-- Headers -->
            <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP Header Manager">
              <collectionProp name="HeaderManager.headers">
                <elementProp name="Content-Type" elementType="Header">
                  <stringProp name="Header.name">Content-Type</stringProp>
                  <stringProp name="Header.value">application/json</stringProp>
                </elementProp>
                <elementProp name="Authorization" elementType="Header">
                  <stringProp name="Header.name">Authorization</stringProp>
                  <stringProp name="Header.value">Bearer ${TOKEN}</stringProp>
                </elementProp>
              </collectionProp>
            </HeaderManager>
            <hashTree/>
            <!-- Request Body -->
            <Arguments guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables">
              <collectionProp name="Arguments.arguments">
                <elementProp name="Body" elementType="Argument">
                  <stringProp name="Argument.name">Body</stringProp>
                  <stringProp name="Argument.value">{"fromAccountId": 1, "toAccountId": 2, "amount": 5000.00, "description": "Transfer test"}</stringProp>
                </elementProp>
              </collectionProp>
            </Arguments>
            <hashTree/>
          </hashTree>
          <!-- Response Assertion -->
          <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Assertion">
            <collectionProp name="Asserions">
              <elementProp name="" elementType="ResponseAssertion">
                <boolProp name="Assertion.test_first_fragment">true</boolProp>
                <stringProp name="Assertion.test_type">6</stringProp>
                <stringProp name="Assertion.test_strings">SUCCESS</stringProp>
                <boolProp name="Assertion.assume_success">false</boolProp>
                <intProp name="Assertion.test_type">6</intProp>
              </elementProp>
            </collectionProp>
          </ResponseAssertion>
          <hashTree/>
        </hashTree>
        <!-- Listeners -->
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector" testname="Summary Report">
          <elementProp name="ResultCollector.sample_listener" elementType="ResultCollector" guiclass="ResultCollectorGui" testclass="ResultCollector" testname="Result Collector"/>
          <stringProp name="filename">results.jtl</stringProp>
          <boolProp name="ResultCollector.error_logging">false</boolProp>
          <objProp>
            <name>samplers</name>
            <value class="SampleResult"/>
            <value class="java.util.ArrayList"/>
          </objProp>
        </ResultCollector>
        <hashTree/>
      </hashTree>
    </TestPlan>
    <hashTree/>
  </hashTree>
</jmeterTestPlan>
```

### 4.3 Chạy Test Manual (GUI Mode)

```bash
# Mở JMeter
jmeter

# Steps:
# 1. File → Open → transfer_money_test.jmx
# 2. Cập nhật TOKEN trong "User Defined Variables"
# 3. Run → Start (hoặc Ctrl+Enter)
# 4. Xem results real-time
```

---

## 5. CHẠY LOAD TEST

### 5.1 Chuẩn Bị Test Scenarios

**Scenario 1: Baseline (10 users, 10 loops)**
```bash
# Expected: ~1-2 minutes
# Users: 10
# Requests per user: 10
# Total: 100 requests
# Expected throughput: 50-80 req/s
# Expected response time: 50-150ms
```

**Scenario 2: Normal Load (50 users, 10 loops)**
```bash
# Expected: ~2-3 minutes
# Users: 50
# Requests per user: 10
# Total: 500 requests
# Expected throughput: 100-200 req/s
# Expected response time: 100-300ms
```

**Scenario 3: Heavy Load (100 users, 5 loops)**
```bash
# Expected: ~2-3 minutes
# Users: 100
# Requests per user: 5
# Total: 500 requests
# Expected throughput: 150-300 req/s
# Expected response time: 200-500ms
```

### 5.2 Chạy Từ Command Line (Recommended)

```bash
# Scenario 1: Baseline
jmeter -n -t transfer_money_test.jmx `
  -Jnum_threads=10 `
  -Jloop_count=10 `
  -l results_baseline.jtl `
  -j jmeter_baseline.log

# Scenario 2: Normal Load
jmeter -n -t transfer_money_test.jmx `
  -Jnum_threads=50 `
  -Jloop_count=10 `
  -l results_normal.jtl `
  -j jmeter_normal.log

# Scenario 3: Heavy Load
jmeter -n -t transfer_money_test.jmx `
  -Jnum_threads=100 `
  -Jloop_count=5 `
  -l results_heavy.jtl `
  -j jmeter_heavy.log

# All scenarios trong sequence
for ($i = 1; $i -le 3; $i++) {
  Write-Host "Running scenario $i..."
  jmeter -n -t transfer_money_test.jmx -l "results_$i.jtl" -j "jmeter_$i.log"
  Start-Sleep -Seconds 30
}
```

### 5.3 Monitor During Test

**Terminal 1: Chạy JMeter test**

```bash
jmeter -n -t transfer_money_test.jmx -l results.jtl
```

**Terminal 2: Monitor Actuator Metrics**

```bash
# Real-time HTTP requests
watch -n 1 'curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq .mean'

# JVM Memory usage
watch -n 1 'curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq .measurements[0].value'

# Active threads
watch -n 1 'curl -s http://localhost:8080/actuator/metrics/jvm.threads.live | jq .measurements[0].value'
```

**Terminal 3: Monitor Docker Stats**

```bash
# CPU & Memory per container
docker stats --no-stream

# Or continuous
watch -n 1 'docker stats --no-stream'
```

**Terminal 4: Monitor Database**

```bash
# Connect to MySQL and check
docker exec -it bank-mysql_account mysql -uroot -ppassword db_account

# Check transaction volume
SELECT COUNT(*) as total_transactions FROM transactions;

# Check last 10 transactions
SELECT transactionid, fromaccount, toaccount, amount, transactiondate FROM transactions ORDER BY transactiondate DESC LIMIT 10;
```

---

## 6. PHÂN TÍCH KẾT QUẢ

### 6.1 Generate HTML Report

```bash
# Từ .jtl file tạo HTML report
jmeter -g results_baseline.jtl -o report_baseline/
jmeter -g results_normal.jtl -o report_normal/
jmeter -g results_heavy.jtl -o report_heavy/

# Mở report trong browser
start report_baseline/index.html
```

### 6.2 Parse Results Manually

```bash
# View raw .jtl file
cat results_baseline.jtl | Select-String -Pattern "200|SUCCESS" | Measure-Object

# Count successful requests
(Select-String -Path results_baseline.jtl -Pattern '"SUCCESS"' | Measure-Object).Count

# Extract response times
$data = Import-Csv results_baseline.jtl
$data | Measure-Object -Property elapsed -Average -Minimum -Maximum -StandardDeviation
```

### 6.3 Expected Results

**Baseline (10 users × 10 = 100 transfers):**
```
Total Requests:        100
Successful:            100 (100%)
Failed:                0

Response Time:
  Min:                 45ms
  Max:                 250ms
  Average:             95ms
  Median (P50):        85ms
  P95:                 150ms
  P99:                 200ms

Throughput:            50-80 req/s

Database:
  Transactions:        100
  Total Amount:        500,000 (sample transfers)
  Avg Time:            50-100ms

Errors:                None (clean run)
```

**Normal Load (50 users × 10 = 500 transfers):**
```
Total Requests:        500
Successful:            499 (99.8%)
Failed:                1 (network timeout)

Response Time:
  Min:                 40ms
  Max:                 800ms
  Average:             180ms
  Median (P50):        150ms
  P95:                 400ms
  P99:                 700ms

Throughput:            150-200 req/s

Database:
  Transactions:        500
  Total Amount:        2,500,000
  Avg Time:            100-150ms

Errors:                1 timeout (0.2%)
```

**Heavy Load (100 users × 5 = 500 transfers):**
```
Total Requests:        500
Successful:            480 (96%)
Failed:                20 (network/timeout)

Response Time:
  Min:                 60ms
  Max:                 3000ms
  Average:             500ms
  Median (P50):        350ms
  P95:                 1500ms
  P99:                 2500ms

Throughput:            200-250 req/s (degraded)

Database:
  Transactions:        480
  Total Amount:        2,400,000
  Avg Time:            200-250ms

Errors:                20 timeouts (4%)
  - Gateway response time exceeds 2s
  - Database connection pool near limit
```

### 6.4 Performance Analysis

**Bottleneck Detection:**

```
Baseline: ✓ Excellent
├─ CPU usage: 15-20%
├─ Memory: 200MB
├─ DB connections: 5/20
├─ Latency: <100ms
└─ Status: Optimal

Normal Load: ✓ Good
├─ CPU usage: 40-50%
├─ Memory: 350MB
├─ DB connections: 15/20
├─ Latency: 100-400ms
└─ Status: Expected behavior

Heavy Load: ⚠ Degraded
├─ CPU usage: 80-90% (Approaching limit)
├─ Memory: 650MB (Growing)
├─ DB connections: 20/20 (At limit!)
├─ Latency: 300-3000ms (High variance)
└─ Status: Needs optimization
  - DB connection pool too small
  - Gateway buffering requests
  - Thread pool saturation
```

### 6.5 Recommendations

```
Based on Results:

1. Connection Pool (Short-term)
   ├─ Increase MySQL max_connections from 20 to 50
   ├─ Increase HikariCP poolSize from 10 to 25
   └─ Restart services

2. Caching (Medium-term)
   ├─ Add Redis cache for account lookups
   ├─ Cache account balances (TTL: 5s)
   └─ Expected improvement: 2-3x throughput

3. Async Processing (Long-term)
   ├─ Make RabbitMQ async for notifications
   ├─ Batch transaction logging
   └─ Expected improvement: 1.5x response time reduction

4. Database Optimization
   ├─ Add index on (fromAccountId, toAccountId)
   ├─ Partition transactions table by date
   └─ Expected improvement: 30-40% faster queries
```

---

## 📊 TEST EXECUTION CHECKLIST

### Pre-Test
- [ ] All services running & healthy
- [ ] Test data (users, accounts) created
- [ ] JWT tokens obtained
- [ ] JMeter installed
- [ ] Test plan created & validated
- [ ] Monitoring tools ready

### During Test
- [ ] Monitor CPU/Memory
- [ ] Check logs for errors
- [ ] Verify database transactions
- [ ] Track response times
- [ ] Note any failures/timeouts

### Post-Test
- [ ] Generate HTML reports
- [ ] Extract key metrics
- [ ] Compare across scenarios
- [ ] Identify bottlenecks
- [ ] Create optimization plan

---

## 📁 FILE ORGANIZATION

```
d:\IT\Code\Java\BTL-final\online-banking-springboot-react\server\

├─ transfer_money_test.jmx          # JMeter test plan
├─ tokens.txt                        # Saved JWT tokens
├─ results_baseline.jtl              # Baseline test results
├─ results_normal.jtl                # Normal load results
├─ results_heavy.jtl                 # Heavy load results
├─ report_baseline/                  # HTML reports
├─ report_normal/
└─ report_heavy/
```

---

## ⏱️ TIMELINE ESTIMATE

```
Total Duration: 45-60 minutes

Setup:             10 min
  ├─ Start services: 5 min
  └─ Create test data: 5 min

Manual Testing:    10 min
  ├─ Curl tests: 5 min
  └─ Verify balances: 5 min

Load Testing:      20-30 min
  ├─ Scenario 1 (Baseline): 5 min
  ├─ Scenario 2 (Normal): 8 min
  └─ Scenario 3 (Heavy): 10 min

Analysis:          10 min
  ├─ Generate reports: 3 min
  ├─ Extract metrics: 4 min
  └─ Create summary: 3 min
```

---

**Last Updated:** 24/12/2025  
**Status:** ✅ Ready for Testing
