# Notification Service

## 📋 Tổng Quan (Overview)

**Notification Service** (Port: **8084**) quản lý:
- Gửi email (Email Notifications)
- OTP via Email
- Transaction Confirmations
- Account Notifications

---

## 🔗 API Endpoints

### **1. Send Email / Gửi Email**

**Endpoint:** `POST /api/v1/mail/send`

**Headers:**
```
Content-Type: application/json
```

**Request:**
```json
{
  "to": "user@example.com",
  "subject": "Welcome to Online Banking",
  "body": "Thank you for registering with us!"
}
```

**Response (200 OK):**
```json
{
  "message": "Email sent successfully",
  "sentDate": "2024-12-24T14:30:00",
  "to": "user@example.com"
}
```

**Error (500):**
```json
{
  "error": "Failed to send email",
  "reason": "SMTP connection failed"
}
```

---

### **2. Send OTP Email / Gửi OTP**

**Endpoint:** `POST /api/v1/mail/send-otp`

**Request:**
```json
{
  "to": "john@example.com",
  "otp": "123456",
  "userName": "John Doe"
}
```

**Response (200 OK):**
```json
{
  "message": "OTP email sent successfully",
  "sentDate": "2024-12-24T14:30:00"
}
```

---

### **3. Send Transaction Confirmation / Xác Nhận Giao Dịch**

**Endpoint:** `POST /api/v1/mail/send-transaction`

**Request:**
```json
{
  "to": "john@example.com",
  "transactionId": 1,
  "amount": 500.00,
  "description": "Transfer to Jane Doe",
  "status": "SUCCESS"
}
```

**Response (200 OK):**
```json
{
  "message": "Transaction confirmation sent",
  "sentDate": "2024-12-24T14:30:00"
}
```

---

## 📧 Email Configuration

**SMTP Settings (Gmail):**

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

**Setup Google App Password:**
1. Go to https://myaccount.google.com/apppasswords
2. Select Mail & Windows PC
3. Copy the 16-character password
4. Paste in `spring.mail.password`

---

## 📨 Email Templates

### OTP Email Template:
```
Subject: Online Banking - OTP Verification

Dear {{userName}},

Your OTP for email verification is: {{otp}}

This OTP will expire in 10 minutes.

Do not share this OTP with anyone.

Best regards,
Online Banking Team
```

### Transaction Confirmation:
```
Subject: Transaction Confirmation - {{transactionId}}

Dear {{userName}},

Your transaction has been {{status}}.

Amount: {{amount}} VND
Description: {{description}}
Date: {{date}}

Thank you,
Online Banking Team
```

---

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

Service runs on port 8084

---

## 🔗 Internal Communication

This service receives requests from:
- **Authentication Service** (8081) - Send OTP emails
- **Account Service** (8083) - Send transaction confirmations
- **Customer Service** (8082) - Send account notifications

Accessed via **Feign Client** inter-service communication
