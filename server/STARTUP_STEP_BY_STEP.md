# 🚀 HƯỚNG DẪN KHỞI CHẠY TOÀN BỘ HỆ THỐNG

**Ngày:** 24/12/2025  
**Mục tiêu:** Khởi chạy services → Signup → Tạo Account → Chuyển Tiền  
**Thời gian:** 30-45 phút

---

## ⚠️ BẮT ĐẦUƯỚC 0: KIỂM TRA DOCKER DESKTOP

### Vấn đề: Docker daemon không chạy

**Giải pháp:**

```powershell
# 1. Mở Docker Desktop (Windows)
# Cách 1: Click biểu tượng Docker trên taskbar
# Cách 2: Chạy command
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"

# 2. Chờ Docker khởi động (5-10 phút)
# Kiểm tra xem Docker đã sẵn sàng
docker --version
# Expected: Docker version 24.0+

docker ps
# Expected: Danh sách containers (có thể trống)

# 3. Nếu vẫn lỗi, khởi động lại Docker
# Đóng Docker → Chờ 30 giây → Mở lại
```

**Kiểm tra Docker chạy OK:**
```powershell
# Nếu kết quả:
# - docker --version: Docker version 24.0+ ✅
# - docker ps: (Danh sách containers) ✅
# Thì có thể tiếp tục bước 1
```

---

## 🔧 BƯỚC 1: KHỞI CHẠY DOCKER COMPOSE

### Terminal: PowerShell (Admin)

```powershell
# 1. Điều hướng đến thư mục
cd D:\IT\Code\Java\BTL-final\online-banking-springboot-react\server

# 2. Xóa containers cũ (nếu có)
docker-compose down -v
# Expected: Tất cả containers dừng & volumes xóa

# 3. Khởi chạy services
docker-compose up -d

# 4. Kiểm tra status (chờ 30-60 giây)
docker-compose ps

# Expected output:
# NAME                      SERVICE              STATUS      PORTS
# server-account-service    account-service      Up          8083->8080/tcp
# server-auth-service       auth-service         Up          8081->8080/tcp
# server-customer-service   customer-service     Up          8082->8080/tcp
# server-notification       notification         Up          8084->8080/tcp
# server-gateway            gateway              Up          8080->8080/tcp
# server-discovery          discovery            Up          8761->8761/tcp
# server-mysql_account      mysql                Up          3309->3306/tcp
# server-mysql_auth         mysql                Up          3307->3306/tcp
# server-mysql_customer     mysql                Up          3308->3306/tcp
# server-rabbitmq           rabbitmq             Up          5672->5672/tcp, 15672->15672/tcp
```

### Kiểm Tra Services Healthy

```powershell
# Chờ 30-60 giây (services cần thời gian startup)

# Kiểm tra Gateway (API entry point)
curl -X GET http://localhost:8080/actuator/health

# Expected (200):
# {"status":"UP"}

# Kiểm tra Eureka (Service Discovery)
curl -X GET http://localhost:8761/eureka/apps -s | Select-String "UP" | Measure-Object

# Expected: 6 services UP

# Nếu tất cả OK → Đi bước 2 ✅
```

---

## 📝 BƯỚC 2: SIGNUP - ĐĂNG KÝ USER MỚI

### 2.1 Hiểu Flow Signup

```
User Input (Email, Password)
    ↓
Sign Up Service nhận request
    ↓
Hash password + Generate OTP
    ↓
Lưu user vào db_auth
    ↓
Gọi Notification Service để gửi OTP qua email
    ↓
Return token
```

### 2.2 Signup User #1

**PowerShell Command:**

```powershell
# Variables
$baseUrl = "http://localhost:8080"
$email1 = "user001@test.com"
$password = "password123"
$firstname = "John"
$lastname = "Doe"

# Signup Request
$response = curl -X POST "$baseUrl/api/v1/signup" `
  -H "Content-Type: application/json" `
  -d "{
    `"firstname`": `"$firstname`",
    `"lastname`": `"$lastname`",
    `"email`": `"$email1`",
    `"password`": `"$password`"
  }" -s

Write-Host "Response: $response"

# Expected (200):
# {
#   "status": "USER_CREATED",
#   "message": "User created successfully",
#   "data": {
#     "userId": "550e8400-e29b-41d4-a716-446655440000",
#     "email": "user001@test.com",
#     "firstname": "John",
#     "lastname": "Doe"
#   }
# }

# Save userId for next steps
$userId1 = "550e8400-e29b-41d4-a716-446655440000"  # Lấy từ response
Write-Host "User 1 ID: $userId1"
```

**Hoặc dùng JSON file (Khuyến nghị):**

```powershell
# Tạo file signup1.json
@{
    firstname = "John"
    lastname = "Doe"
    email = "user001@test.com"
    password = "password123"
} | ConvertTo-Json | Out-File signup1.json

# Chạy signup
curl -X POST "http://localhost:8080/api/v1/signup" `
  -H "Content-Type: application/json" `
  -d (Get-Content signup1.json -Raw) | jq .
```

### 2.3 Signup User #2 & #3 (Chuẩn bị cho transfer)

```powershell
# User 2
curl -X POST "http://localhost:8080/api/v1/signup" `
  -H "Content-Type: application/json" `
  -d '{
    "firstname": "Jane",
    "lastname": "Smith",
    "email": "user002@test.com",
    "password": "password123"
  }' | jq .

# User 3
curl -X POST "http://localhost:8080/api/v1/signup" `
  -H "Content-Type: application/json" `
  -d '{
    "firstname": "Bob",
    "lastname": "Johnson",
    "email": "user003@test.com",
    "password": "password123"
  }' | jq .

# Lưu UserIds từ responses
$userId1 = "user1-uuid-from-response"
$userId2 = "user2-uuid-from-response"
$userId3 = "user3-uuid-from-response"

Write-Host @"
Saved User IDs:
  User 1: $userId1
  User 2: $userId2
  User 3: $userId3
"@
```

### 2.4 Lưu Tokens

```powershell
# Login User 1
$loginResponse1 = curl -X POST "http://localhost:8080/api/v1/login" `
  -H "Content-Type: application/json" `
  -d '{
    "email": "user001@test.com",
    "password": "password123"
  }' -s | ConvertFrom-Json

$token1 = $loginResponse1.token

Write-Host "Token 1: $token1"

# Lặp lại cho user 2 & 3
$token2 = "..."  # Từ login user 2
$token3 = "..."  # Từ login user 3
```

---

## 👤 BƯỚC 3: CREATE ACCOUNT - KHỞI TẠO TÀI KHOẢN NGÂN HÀNG

### 3.1 Hiểu Flow Create Account

```
User Login (có token)
    ↓
POST /api/v1/account/create/{userId}
    ↓
Tạo tài khoản mới trong db_account
    ↓
Initial balance = 100,000 VND (default)
    ↓
Publish AccountCreatedEvent
    ↓
RabbitMQ → Notification Service gửi email
    ↓
Return accountId
```

### 3.2 Create Account cho User 1

**PowerShell Command:**

```powershell
# Variables
$baseUrl = "http://localhost:8080"
$token1 = "eyJhbGciOiJIUzUxMiJ9..."  # Token từ bước 2
$userId1 = "550e8400-e29b-41d4-a716-446655440000"  # UserID từ bước 2

# Create Account Request
$response = curl -X POST "$baseUrl/api/v1/account/create/$userId1" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token1" `
  -d '{
    "accountType": "SAVINGS",
    "initialBalance": 100000.00
  }' -s | ConvertFrom-Json

Write-Host "Response: $response"

# Expected (200):
# {
#   "accountId": 1,
#   "userId": "550e8400-e29b-41d4-a716-446655440000",
#   "accountType": "SAVINGS",
#   "balance": 100000.00,
#   "isActive": true,
#   "message": "Account created successfully"
# }

$accountId1 = $response.accountId
Write-Host "Account ID 1: $accountId1"
```

### 3.3 Create Account cho User 2 & 3

```powershell
# User 2
curl -X POST "http://localhost:8080/api/v1/account/create/$userId2" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token2" `
  -d '{
    "accountType": "SAVINGS",
    "initialBalance": 200000.00
  }' | jq .

# User 3
curl -X POST "http://localhost:8080/api/v1/account/create/$userId3" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token3" `
  -d '{
    "accountType": "SAVINGS",
    "initialBalance": 150000.00
  }' | jq .

# Lưu Account IDs
$accountId1 = "1"  # Từ response
$accountId2 = "2"
$accountId3 = "3"

Write-Host @"
Account IDs:
  Account 1: $accountId1 (Balance: 100,000)
  Account 2: $accountId2 (Balance: 200,000)
  Account 3: $accountId3 (Balance: 150,000)
"@
```

---

## 💰 BƯỚC 4: TRANSFER MONEY - CHUYỂN TIỀN

### 4.1 Hiểu Flow Transfer

```
User 1 có token + Account 1
    ↓
POST /api/v1/transfer/money
    From: Account 1 (100,000)
    To: Account 2 (200,000)
    Amount: 50,000
    ↓
Kiểm tra:
  ├─ Token hợp lệ? ✓
  ├─ Account 1 tồn tại? ✓
  ├─ Account 2 tồn tại? ✓
  └─ Balance đủ (100,000 ≥ 50,000)? ✓
    ↓
Thực hiện transfer:
  ├─ Account 1: 100,000 - 50,000 = 50,000 ✓
  ├─ Account 2: 200,000 + 50,000 = 250,000 ✓
  └─ Save Transaction to db_account
    ↓
Publish TransactionCompletedEvent
    ↓
RabbitMQ → Notification Service gửi email
    ↓
Return transactionId
```

### 4.2 Transfer #1: User 1 → User 2 (50,000 VND)

**PowerShell Command:**

```powershell
# Variables
$token1 = "eyJhbGciOiJIUzUxMiJ9..."  # User 1 token
$accountId1 = "1"   # User 1 account
$accountId2 = "2"   # User 2 account
$amount = 50000.00

# Transfer Request
$response = curl -X POST "http://localhost:8080/api/v1/transfer/money" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token1" `
  -d "{
    `"fromAccountId`": $accountId1,
    `"toAccountId`": $accountId2,
    `"amount`": $amount,
    `"description`": `"Payment for services`"
  }" -s | ConvertFrom-Json

Write-Host "Transfer Response:"
Write-Host ($response | ConvertTo-Json)

# Expected (200):
# {
#   "transactionId": "TXN-20241224-001",
#   "status": "SUCCESS",
#   "fromAccountId": 1,
#   "toAccountId": 2,
#   "amount": 50000.00,
#   "balanceAfter": 50000.00,
#   "timestamp": "2024-12-24T10:30:45",
#   "message": "Transfer completed successfully"
# }

$transactionId1 = $response.transactionId
Write-Host "Transaction ID: $transactionId1"
```

### 4.3 Verify Balance sau Transfer

```powershell
# Check Account 1 balance (phải là 50,000)
curl -X GET "http://localhost:8080/api/v1/account/get/$accountId1" `
  -H "Authorization: Bearer $token1" | jq .

# Expected:
# {
#   "accountId": 1,
#   "balance": 50000.00,  # 100,000 - 50,000
#   "userId": "user-id",
#   "accountType": "SAVINGS",
#   "isActive": true
# }

# Check Account 2 balance (phải là 250,000)
curl -X GET "http://localhost:8080/api/v1/account/get/$accountId2" `
  -H "Authorization: Bearer $token2" | jq .

# Expected:
# {
#   "accountId": 2,
#   "balance": 250000.00,  # 200,000 + 50,000
#   "userId": "user-id",
#   "accountType": "SAVINGS",
#   "isActive": true
# }
```

### 4.4 Transfer #2: User 2 → User 3 (30,000 VND)

```powershell
# User 2 transfer 30,000 to User 3
curl -X POST "http://localhost:8080/api/v1/transfer/money" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token2" `
  -d '{
    "fromAccountId": 2,
    "toAccountId": 3,
    "amount": 30000.00,
    "description": "Lunch money"
  }' | jq .

# Verify Account 2: 250,000 - 30,000 = 220,000
curl -X GET "http://localhost:8080/api/v1/account/get/2" `
  -H "Authorization: Bearer $token2" | jq .balance

# Verify Account 3: 150,000 + 30,000 = 180,000
curl -X GET "http://localhost:8080/api/v1/account/get/3" `
  -H "Authorization: Bearer $token3" | jq .balance
```

### 4.5 Transfer #3: User 1 → User 3 (25,000 VND)

```powershell
# User 1 transfer 25,000 to User 3 (User 1 balance: 50,000 left)
curl -X POST "http://localhost:8080/api/v1/transfer/money" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $token1" `
  -d '{
    "fromAccountId": 1,
    "toAccountId": 3,
    "amount": 25000.00,
    "description": "Dinner contribution"
  }' | jq .

# Final Balances:
# Account 1: 50,000 - 25,000 = 25,000
# Account 3: 180,000 + 25,000 = 205,000
```

---

## 📊 SUMMARY - KIỂM TRA TOÀN BỘ

```powershell
# Kiểm tra tất cả accounts
Write-Host "=== FINAL ACCOUNT BALANCES ==="

curl -X GET "http://localhost:8080/api/v1/account/get/1" -H "Authorization: Bearer $token1" | jq '{accountId: .accountId, balance: .balance}'
curl -X GET "http://localhost:8080/api/v1/account/get/2" -H "Authorization: Bearer $token2" | jq '{accountId: .accountId, balance: .balance}'
curl -X GET "http://localhost:8080/api/v1/account/get/3" -H "Authorization: Bearer $token3" | jq '{accountId: .accountId, balance: .balance}'

# Expected:
# { "accountId": 1, "balance": 25000 }
# { "accountId": 2, "balance": 220000 }
# { "accountId": 3, "balance": 205000 }

# Total: 25,000 + 220,000 + 205,000 = 450,000 (unchanged ✓)
```

---

## 🔍 KIỂM TRA DATABASE (Optional)

```powershell
# Connect to db_account
docker exec -it server-mysql_account mysql -uroot -ppassword db_account

# View all transactions
mysql> SELECT transactionid, fromaccount, toaccount, amount, transactiondate FROM transactions ORDER BY transactionid;

# Expected:
# transactionid | fromaccount | toaccount | amount | transactiondate
# 1             | 1           | 2         | 50000  | 2024-12-24
# 2             | 2           | 3         | 30000  | 2024-12-24
# 3             | 1           | 3         | 25000  | 2024-12-24

# View account balances
mysql> SELECT accountid, userid, accounttype, balance FROM bankaccount;

# Expected:
# accountid | userid | accounttype | balance
# 1         | user1  | SAVINGS     | 25000
# 2         | user2  | SAVINGS     | 220000
# 3         | user3  | SAVINGS     | 205000
```

---

## 📈 KIỂM TRA EVENTS (RabbitMQ)

```powershell
# Mở RabbitMQ Management Console
# URL: http://localhost:15672
# Username: guest
# Password: guest

# Xem trong "Queues" tab:
# ├─ transaction.completed (3 messages - từ 3 transfers)
# ├─ account.created (3 messages - từ 3 account creations)
# └─ user.created (3 messages - từ 3 signups)

# Xem logs để confirm emails đã được gửi
docker logs server-notification | grep "Email sent"
```

---

## 🛑 TROUBLESHOOTING

### Lỗi 1: "Cannot connect to localhost:8080"
```
✓ Kiểm tra docker-compose ps
✓ Chờ 60 giây (services đang startup)
✓ Xem logs: docker-compose logs gateway
```

### Lỗi 2: "User not found" sau signup
```
✓ Kiểm tra email bạn dùng có unique không
✓ Xem logs: docker-compose logs auth-service
```

### Lỗi 3: "Insufficient balance"
```
✓ Check balance hiện tại trước khi transfer
✓ Chắc amount < balance
```

### Lỗi 4: "Invalid token"
```
✓ Lấy token mới bằng login
✓ Ensure token không expired
✓ Include "Bearer " prefix
```

---

## ✅ CHECKLIST

- [ ] Docker Desktop running
- [ ] docker-compose ps: Tất cả 6 services UP
- [ ] curl http://localhost:8080/actuator/health: Status UP
- [ ] Signup 3 users ✓
- [ ] Login & lấy 3 tokens ✓
- [ ] Create 3 accounts ✓
- [ ] Transfer #1: User 1 → User 2 (50K) ✓
- [ ] Verify balance Account 1 & 2 ✓
- [ ] Transfer #2: User 2 → User 3 (30K) ✓
- [ ] Verify balance Account 2 & 3 ✓
- [ ] Transfer #3: User 1 → User 3 (25K) ✓
- [ ] Final balance: 25K + 220K + 205K = 450K ✓

---

## 📚 COMMANDS QUICK REFERENCE

```powershell
# Docker
docker-compose up -d                          # Khởi chạy
docker-compose ps                             # Kiểm tra status
docker-compose logs -f [service]              # Xem logs
docker-compose down -v                        # Dừng & reset

# Test Endpoints
curl -X GET "http://localhost:8080/actuator/health"  # Health check
curl -X POST "http://localhost:8080/api/v1/signup"   # Signup
curl -X POST "http://localhost:8080/api/v1/login"    # Login
curl -X POST "http://localhost:8080/api/v1/account/create/{userId}"  # Create account
curl -X POST "http://localhost:8080/api/v1/transfer/money"           # Transfer

# Database
docker exec -it server-mysql_account mysql -uroot -ppassword db_account
SELECT * FROM bankaccount;
SELECT * FROM transactions;
```

---

**Status:** ✅ Sẵn sàng thực hành  
**Tổng thời gian:** 30-45 phút  
**Tiếp theo:** Chạy JMeter load test
