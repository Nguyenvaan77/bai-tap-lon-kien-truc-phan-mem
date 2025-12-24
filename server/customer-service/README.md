# Customer Service

## 📋 Tổng Quan (Overview)

**Customer Service** (Port: **8082**) quản lý:
- Hồ sơ khách hàng (Customer Profiles)
- Thông tin cá nhân (User Details - Address, Phone, etc.)
- Danh sách người thụ hưởng (Beneficiaries)

---

## 🔐 Authentication Required

**Tất cả endpoints cần JWT Token:**
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🔗 API Endpoints

### **1. Create Customer Profile / Tạo Hồ Sơ**

**Endpoint:** `POST /api/v1/customer/profile`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request:**
```json
{
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "mobile": "0123456789",
  "pan": "ABCDE1234F",
  "adhaar": "1234-5678-9012",
  "dateOfBirth": "1990-01-01",
  "gender": "M",
  "address": "123 Main Street",
  "city": "Ho Chi Minh",
  "state": "HCM",
  "pin": "70000"
}
```

**Response (200 OK):**
```json
{
  "userdetailsid": 1,
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "mobile": "0123456789",
  "pan": "ABCDE1234F",
  "address": "123 Main Street",
  "city": "Ho Chi Minh"
}
```

---

### **2. Get Customer Profile / Lấy Hồ Sơ**

**Endpoint:** `GET /api/v1/customer/profile/{userId}`

**Response (200 OK):**
```json
{
  "userdetailsid": 1,
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "mobile": "0123456789",
  "pan": "ABCDE1234F",
  "address": "123 Main Street",
  "city": "Ho Chi Minh",
  "state": "HCM"
}
```

---

### **3. Update Customer Profile / Cập Nhật Hồ Sơ**

**Endpoint:** `PUT /api/v1/customer/profile/{userId}`

**Request:**
```json
{
  "mobile": "9876543210",
  "address": "456 New Street",
  "city": "Da Nang"
}
```

**Response (200 OK):**
```json
{
  "message": "Profile updated successfully"
}
```

---

### **4. Add Beneficiary / Thêm Người Thụ Hưởng**

**Endpoint:** `POST /api/v1/customer/beneficiary`

**Request:**
```json
{
  "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "beneficiaryname": "Jane Doe",
  "beneaccountno": 1234567890,
  "relation": "Spouse"
}
```

**Response (200 OK):**
```json
{
  "beneficiaryid": 1,
  "beneficiaryname": "Jane Doe",
  "beneaccountno": 1234567890,
  "relation": "Spouse"
}
```

---

### **5. Get Beneficiaries / Lấy Danh Sách Thụ Hưởng**

**Endpoint:** `GET /api/v1/customer/beneficiary/{userId}`

**Response (200 OK):**
```json
[
  {
    "beneficiaryid": 1,
    "beneficiaryname": "Jane Doe",
    "beneaccountno": 1234567890,
    "relation": "Spouse"
  },
  {
    "beneficiaryid": 2,
    "beneficiaryname": "John Smith",
    "beneaccountno": 9876543210,
    "relation": "Friend"
  }
]
```

---

### **6. Delete Beneficiary / Xóa Thụ Hưởng**

**Endpoint:** `DELETE /api/v1/customer/beneficiary/{beneficiaryId}`

**Response (200 OK):**
```json
{
  "message": "Beneficiary deleted successfully"
}
```

---

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

Service runs on port 8082
