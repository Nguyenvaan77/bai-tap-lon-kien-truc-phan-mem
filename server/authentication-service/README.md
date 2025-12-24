# Authentication Service

## 📋 Tổng Quan (Overview)

**Authentication Service** (Port: **8081**) là dịch vụ chính chịu trách nhiệm:
- Quản lý tài khoản người dùng (User Management)
- Xác thực người dùng (Authentication - Login/Signup)
- Tạo và xác thực JWT Tokens
- Quản lý OTP cho xác minh email

---

## 🔗 API Endpoints

### **1. Sign Up / Đăng Ký**

**Endpoint:** `POST /api/v1/signup`

**Request:**
```json
{
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword@123"
}
```

**Response (200 OK):**
```json
{
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "otp": "123456",
  "role": "USER"
}
```

**Error:** `400` - Email already exists

---

### **2. Verify OTP / Xác Minh OTP**

**Endpoint:** `POST /api/v1/otp`

**Request:**
```json
{
  "otp": "123456"
}
```

**Response (200 OK):**
```json
{
  "message": "OTP verified successfully"
}
```

---

### **3. Resend OTP / Gửi Lại OTP**

**Endpoint:** `POST /api/v1/resend-otp/{userId}`

**Response (200 OK):**
```json
{
  "message": "OTP sent successfully"
}
```

---

### **4. Login / Đăng Nhập**

**Endpoint:** `POST /api/v1/login`

**Request:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword@123"
}
```

**Response (200 OK):**
```json
{
  "jwtToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@example.com",
    "role": "USER"
  }
}
```

---

## 🔐 JWT Token Usage

**Format:** `Authorization: Bearer <JWT_TOKEN>`

**Token Expiration:** 24 giờ

---

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

Service runs on port 8081
