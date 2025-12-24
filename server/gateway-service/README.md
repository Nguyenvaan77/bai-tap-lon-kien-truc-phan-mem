# API Gateway Service

## 📋 Tổng Quan (Overview)

**API Gateway Service** (Port: **8080**) là cổng vào duy nhất cho tất cả yêu cầu từ client.

**Chức năng:**
- Định tuyến yêu cầu đến các dịch vụ (Routing)
- Cân bằng tải (Load Balancing)
- Xác thực JWT tokens
- CORS handling

---

## 🔀 Route Configuration

| Path | Service | Port |
|------|---------|------|
| `/api/v1/signup`, `/api/v1/login`, `/api/v1/otp/**` | Authentication | 8081 |
| `/api/v1/customer/**`, `/api/v1/beneficiary/**` | Customer | 8082 |
| `/api/v1/account/**`, `/api/v1/transaction/**` | Account | 8083 |
| `/api/v1/mail/**` | Notification | 8084 |

---

## 🏗️ Architecture

```
Client (React)
     ↓
Gateway (8080)
     ↓
[Auth (8081), Customer (8082), Account (8083), Notification (8084)]
```

---

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

Service will be available at: http://localhost:8080
