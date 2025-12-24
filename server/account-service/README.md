# Account Service

## 📋 Tổng Quan (Overview)

**Account Service** (Port: **8083**) quản lý:
- Tài khoản ngân hàng (Bank Accounts)
- Giao dịch (Transactions)
- Chuyển tiền (Fund Transfers)
- Truy vấn số dư (Balance Inquiry)

---

## 🔐 Authentication Required

**Tất cả endpoints cần JWT Token:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🔗 API Endpoints

### **1. Create Bank Account / Tạo Tài Khoản**

**Endpoint:** `POST /api/v1/account/create`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accountType": "Savings",
  "balance": 10000.00
}
```

**Response (200 OK):**
```json
{
  "accountno": 1000001,
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accountType": "Savings",
  "balance": 10000.00,
  "isactive": true,
  "dateCreated": "2024-12-24"
}
```

---

### **2. Get Account Details / Lấy Chi Tiết Tài Khoản**

**Endpoint:** `GET /api/v1/account/{accountNo}`

**Response (200 OK):**
```json
{
  "accountno": 1000001,
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "accountType": "Savings",
  "balance": 10000.00,
  "isactive": true
}
```

---

### **3. Get User Accounts / Lấy Tất Cả Tài Khoản**

**Endpoint:** `GET /api/v1/account/user/{userId}`

**Response (200 OK):**
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

### **4. Fund Transfer / Chuyển Tiền**

**Endpoint:** `POST /api/v1/account/transfer`

**Request:**
```json
{
  "fromAccount": 1000001,
  "toAccount": 1000002,
  "amount": 500.00,
  "description": "Payment for services"
}
```

**Response (200 OK):**
```json
{
  "transactionId": 1,
  "fromAccount": 1000001,
  "toAccount": 1000002,
  "amount": 500.00,
  "transactionStatus": "SUCCESS",
  "transactionDate": "2024-12-24",
  "transactionTime": "14:30:45",
  "description": "Payment for services"
}
```

**Error (400):**
```json
{
  "error": "Insufficient balance"
}
```

---

### **5. Get Transaction History / Lịch Sử Giao Dịch**

**Endpoint:** `GET /api/v1/account/transactions/{accountNo}`

**Response (200 OK):**
```json
[
  {
    "transactionId": 1,
    "fromAccount": 1000001,
    "toAccount": 1000002,
    "amount": 500.00,
    "transactionStatus": "SUCCESS",
    "transactionDate": "2024-12-24",
    "transactionTime": "14:30:45"
  },
  {
    "transactionId": 2,
    "fromAccount": 1000002,
    "toAccount": 1000001,
    "amount": 200.00,
    "transactionStatus": "SUCCESS",
    "transactionDate": "2024-12-23",
    "transactionTime": "10:15:20"
  }
]
```

---

### **6. Check Balance / Kiểm Tra Số Dư**

**Endpoint:** `GET /api/v1/account/balance/{accountNo}`

**Response (200 OK):**
```json
{
  "accountno": 1000001,
  "balance": 9500.00,
  "currency": "VND"
}
```

---

### **7. Get Transaction Details / Chi Tiết Giao Dịch**

**Endpoint:** `GET /api/v1/account/transaction/{transactionId}`

**Response (200 OK):**
```json
{
  "transactionId": 1,
  "fromAccount": 1000001,
  "toAccount": 1000002,
  "amount": 500.00,
  "transactionStatus": "SUCCESS",
  "transactionDate": "2024-12-24",
  "transactionTime": "14:30:45",
  "description": "Payment for services"
}
```

---

## 💳 Account Types

| Type | Description |
|------|-------------|
| Savings | Tài khoản tiết kiệm |
| Checking | Tài khoản thanh toán |
| Credit | Tài khoản tín dụng |

---

## 🔄 Transaction Status

| Status | Meaning |
|--------|---------|
| PENDING | Đang xử lý |
| SUCCESS | Thành công |
| FAILED | Thất bại |
| CANCELLED | Đã hủy |

---

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

Service runs on port 8083
