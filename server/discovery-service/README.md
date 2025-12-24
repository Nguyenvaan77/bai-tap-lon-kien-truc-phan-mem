# Discovery Service (Eureka Server)

## 📋 Tổng Quan (Overview)

**Discovery Service** (Port: **8761**) là **Eureka Server** - nơi tất cả các microservices đăng ký và tìm kiếm lẫn nhau.

**Chức năng:**
- Đăng ký dịch vụ (Service Registration)
- Tìm kiếm dịch vụ (Service Discovery)
- Kiểm tra sức khỏe dịch vụ (Health Checks)
- Cung cấp danh sách dịch vụ khả dụng

---

## 🔍 Eureka Dashboard

**URL:** `http://localhost:8761`

**Hiển thị:**
- Danh sách tất cả registered services
- Trạng thái của mỗi service (UP, DOWN)
- Số lượng instances
- Thông tin replicas

---

## 📋 Registered Services

| Service Name | Port | Status |
|---|---|---|
| authentication-service | 8081 | UP |
| customer-service | 8082 | UP |
| account-service | 8083 | UP |
| notification-service | 8084 | UP |
| gateway-service | 8080 | UP |

---

## 🔗 Service Discovery Workflow

```
1. Service Startup
   ↓
2. Service registers with Eureka
   ↓
3. Eureka stores service info (name, host, port, health)
   ↓
4. Other services query Eureka for service locations
   ↓
5. Services communicate via discovered locations
```

---

## 📝 Configuration

```properties
spring.application.name=discovery-service
server.port=8761

# Eureka Server settings
eureka.instance.hostname=localhost
eureka.client.registerWithEureka=false
eureka.client.fetchRegistry=false
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
```

---

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

Service will be available at: http://localhost:8761

---

## ✅ Health Check Endpoints

**Eureka Health:**
```bash
curl http://localhost:8761/eureka/status
```

**All Services:**
```bash
curl http://localhost:8761/eureka/apps
```

**Specific Service:**
```bash
curl http://localhost:8761/eureka/apps/AUTHENTICATION-SERVICE
```

---

## ⚠️ Important Notes

- Eureka must start **FIRST** before other services
- Clients automatically register on startup
- Heartbeat sent every 30 seconds
- Service removed after 90 seconds of missed heartbeats
- Self-preservation mode enabled for production

---

**Last Updated:** December 24, 2025
