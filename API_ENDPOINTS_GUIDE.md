# API Endpoints - Hướng Dẫn Chi Tiết

## 📋 Mục Lục
1. [Xác Thực & Đăng Ký](#xác-thực--đăng-ký)
2. [Tài Khoản](#tài-khoản)
3. [Giao Dịch](#giao-dịch)
4. [Chuyển Tiền](#chuyển-tiền)
5. [Người Thụ Hưởng](#người-thụ-hưởng)
6. [Người Dùng](#người-dùng)

---

## 🔐 Xác Thực & Đăng Ký

### 1. Đăng Ký Tài Khoản
**Endpoint:** `POST /api/v1/signup`

**Mô tả:** Đăng ký tài khoản người dùng mới

**Base URL:** `http://localhost:8080`

**Request Body:**
```json
{
  "firstName": "Nguyễn",
  "lastName": "Văn A",
  "email": "nguyenvana@gmail.com",
  "password": "SecurePassword123!"
}
```

**Response (201 Created):**
```json
{
  "id": "user_123",
  "firstName": "Nguyễn",
  "lastName": "Văn A",
  "email": "nguyenvana@gmail.com",
  "createdDate": "2025-12-24T10:30:00",
  "roles": ["USER"]
}
```

**Status Codes:**
- `201 Created`: Đăng ký thành công
- `400 Bad Request`: Dữ liệu không hợp lệ
- `409 Conflict`: Email đã tồn tại

---

### 2. Đăng Nhập
**Endpoint:** `POST /api/v1/login`

**Mô tả:** Đăng nhập và nhận JWT token

**Request Body:**
```json
{
  "email": "nguyenvana@gmail.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userName": "nguyenvana@gmail.com",
  "expireIn": 315360000000,
  "userId": "user_123"
}
```

**Status Codes:**
- `200 OK`: Đăng nhập thành công
- `401 Unauthorized`: Sai email hoặc mật khẩu
- `404 Not Found`: Người dùng không tồn tại

---

### 3. Kiểm Tra OTP
**Endpoint:** `POST /api/v1/otp`

**Mô tả:** Xác minh mã OTP gửi qua email

**Request Body:**
```json
{
  "userId": "user_123",
  "otp": "123456"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP xác minh thành công"
}
```

---

### 4. Gửi Lại OTP
**Endpoint:** `POST /api/v1/resend-otp/{userId}`

**Mô tả:** Gửi lại mã OTP nếu người dùng chưa nhận được

**Path Parameters:**
- `userId` (string): ID của người dùng

**Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP đã được gửi lại đến email của bạn"
}
```

---

## 💳 Tài Khoản

### 1. Tạo Tài Khoản Ngân Hàng Mới
**Endpoint:** `POST /account/create/{userId}`

**Mô tả:** Tạo tài khoản ngân hàng mới cho người dùng

**Path Parameters:**
- `userId` (string): ID của người dùng

**Request Body:**
```json
{
  "accounttype": "SAVINGS",
  "balance": 0
}
```

**Response (200 OK):**
```json
{
  "accountno": 1234567890,
  "accounttype": "SAVINGS",
  "balance": 0,
  "isactive": true,
  "dateCreated": "2025-12-24",
  "timeCreated": "10:30:00"
}
```

**Status Codes:**
- `200 OK`: Tài khoản tạo thành công
- `400 Bad Request`: Dữ liệu không hợp lệ
- `404 Not Found`: Người dùng không tồn tại

---

### 2. Lấy Chi Tiết Tài Khoản
**Endpoint:** `GET /account/accountdetails/{accountNo}`

**Mô tả:** Lấy thông tin chi tiết của một tài khoản

**Path Parameters:**
- `accountNo` (long): Số tài khoản

**Response (200 OK):**
```json
{
  "accountno": 1234567890,
  "accounttype": "SAVINGS",
  "balance": 50000.00,
  "isactive": true,
  "dateCreated": "2025-12-24",
  "timeCreated": "10:30:00"
}
```

---

### 3. Kiểm Tra Số Dư
**Endpoint:** `GET /account/checkbal/{accountno}`

**Mô tả:** Kiểm tra số dư tài khoản

**Path Parameters:**
- `accountno` (long): Số tài khoản

**Response (200 OK):**
```json
[
  {
    "accountno": 1234567890,
    "balance": 50000.00,
    "accounttype": "SAVINGS"
  }
]
```

**Status Codes:**
- `200 OK`: Thành công
- `404 Not Found`: Tài khoản không tồn tại

---

### 4. Lấy Tất Cả Tài Khoản
**Endpoint:** `GET /account/accounts/{all}`

**Mô tả:** Lấy danh sách tài khoản

**Path Parameters:**
- `all` (int): 
  - `0`: Chỉ người dùng có tài khoản
  - `1`: Chỉ người dùng không có tài khoản
  - `2`: Tất cả người dùng

**Response (200 OK):**
```json
[
  [
    {
      "id": "user_123",
      "firstName": "Nguyễn",
      "lastName": "Văn A",
      "email": "nguyenvana@gmail.com",
      "accounts": [
        {
          "accountno": 1234567890,
          "balance": 50000.00
        }
      ]
    }
  ]
]
```

---

### 5. Lấy Tài Khoản Theo Email
**Endpoint:** `GET /account/accounts/mail?email={email}`

**Mô tả:** Lấy thông tin người dùng theo email

**Query Parameters:**
- `email` (string): Email người dùng

**Response (200 OK):**
```json
{
  "id": "user_123",
  "firstName": "Nguyễn",
  "lastName": "Văn A",
  "email": "nguyenvana@gmail.com",
  "accounts": [...]
}
```

---

### 6. Tạm Dừng Tài Khoản
**Endpoint:** `PUT /account/accounts/suspend/{accountno}`

**Mô tả:** Tạm dừng tài khoản

**Path Parameters:**
- `accountno` (long): Số tài khoản

**Response (200 OK):**
```json
{
  "accountno": 1234567890,
  "isactive": false,
  "message": "Tài khoản đã bị tạm dừng"
}
```

---

### 7. Kích Hoạt Tài Khoản
**Endpoint:** `PUT /account/accounts/activate/{accountno}`

**Mô tả:** Kích hoạt tài khoản đã bị tạm dừng

**Path Parameters:**
- `accountno` (long): Số tài khoản

**Response (200 OK):**
```json
{
  "accountno": 1234567890,
  "isactive": true,
  "message": "Tài khoản đã được kích hoạt"
}
```

---

### 8. Tạo Tiền Gửi Có Kỳ Hạn
**Endpoint:** `POST /account/accounts/fixdeposit`

**Mô tả:** Tạo một tiền gửi có kỳ hạn

**Request Body:**
```json
{
  "accountno": 1234567890,
  "amount": 100000,
  "duration": 12,
  "interestRate": 7.5
}
```

**Response (200 OK):**
```json
{
  "fixdepositid": "FD_123",
  "accountno": 1234567890,
  "amount": 100000,
  "duration": 12,
  "interestRate": 7.5,
  "maturityAmount": 107500,
  "startDate": "2025-12-24",
  "maturityDate": "2026-12-24"
}
```

---

### 9. Xóa Tài Khoản
**Endpoint:** `DELETE /account/accounts/{accountno}`

**Mô tả:** Xóa một tài khoản

**Path Parameters:**
- `accountno` (long): Số tài khoản

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tài khoản đã được xóa"
}
```

---

## 💰 Giao Dịch

### 1. Lấy Tất Cả Giao Dịch
**Endpoint:** `GET /transactions/transaction`

**Mô tả:** Lấy danh sách tất cả giao dịch

**Response (200 OK):**
```json
[
  {
    "transactionid": 1,
    "fromAccount": 1234567890,
    "toAccount": 9876543210,
    "amount": 50000,
    "transactiondate": "2025-12-24",
    "transactiontime": "10:30:00",
    "type": "TRANSFER",
    "description": "Chuyển tiền thanh toán"
  }
]
```

---

### 2. Lấy Giao Dịch Theo Tài Khoản Gửi
**Endpoint:** `GET /transactions/sender/{fromAccount}`

**Mô tả:** Lấy các giao dịch từ tài khoản gửi

**Path Parameters:**
- `fromAccount` (long): Số tài khoản gửi

**Response (200 OK):**
```json
[
  {
    "transactionid": 1,
    "fromAccount": 1234567890,
    "toAccount": 9876543210,
    "amount": 50000,
    "transactiondate": "2025-12-24"
  }
]
```

---

### 3. Lấy Giao Dịch Theo Tài Khoản Nhận
**Endpoint:** `GET /transactions/receiver/{toAccount}`

**Mô tả:** Lấy các giao dịch tới tài khoản nhận

**Path Parameters:**
- `toAccount` (long): Số tài khoản nhận

**Response (200 OK):**
```json
[
  {
    "transactionid": 1,
    "fromAccount": 1234567890,
    "toAccount": 9876543210,
    "amount": 50000,
    "transactiondate": "2025-12-24"
  }
]
```

---

### 4. Lấy Giao Dịch Theo Số Tài Khoản
**Endpoint:** `GET /transactions/bankaccount/{accountno}`

**Mô tả:** Lấy tất cả giao dịch của tài khoản (gửi và nhận)

**Path Parameters:**
- `accountno` (long): Số tài khoản

**Response (200 OK):**
```json
[
  {
    "transactionid": 1,
    "fromAccount": 1234567890,
    "toAccount": 9876543210,
    "amount": 50000
  }
]
```

---

### 5. Lấy Giao Dịch Theo ID
**Endpoint:** `GET /transactions/transactionId/{transactionId}`

**Mô tả:** Lấy chi tiết một giao dịch cụ thể

**Path Parameters:**
- `transactionId` (int): ID giao dịch

**Response (200 OK):**
```json
{
  "transactionid": 1,
  "fromAccount": 1234567890,
  "toAccount": 9876543210,
  "amount": 50000,
  "transactiondate": "2025-12-24",
  "transactiontime": "10:30:00",
  "description": "Chuyển tiền"
}
```

---

## 💸 Chuyển Tiền

### 1. Chuyển Tiền Giữa Tài Khoản
**Endpoint:** `POST /fund/transfer`

**Mô tả:** Chuyển tiền từ tài khoản này sang tài khoản khác

**Request Body:**
```json
{
  "accountno": 1234567890
}
```

**Query Parameters:**
- `toAccount` (long): Số tài khoản nhận
- `amount` (double): Số tiền chuyển
- `description` (string): Mô tả giao dịch

**Response (200 OK):**
```json
{
  "transactionid": 1,
  "success": true,
  "fromAccount": 1234567890,
  "toAccount": 9876543210,
  "amount": 50000,
  "message": "Chuyển tiền thành công"
}
```

**Status Codes:**
- `200 OK`: Chuyển tiền thành công
- `400 Bad Request`: Không đủ tiền hoặc dữ liệu không hợp lệ
- `404 Not Found`: Tài khoản không tồn tại
- `409 Conflict`: Tài khoản bị tạm dừng

---

### 2. Chuyển Tiền Cho Người Thụ Hưởng
**Endpoint:** `POST /fund/transfer/benficiary/{benId}`

**Mô tả:** Chuyển tiền đến người thụ hưởng đã lưu

**Path Parameters:**
- `benId` (int): ID người thụ hưởng

**Request Body:**
```json
{
  "accountno": 1234567890
}
```

**Query Parameters:**
- `amount` (double): Số tiền chuyển
- `description` (string): Mô tả giao dịch

**Response (200 OK):**
```json
{
  "transactionid": 1,
  "success": true,
  "message": "Chuyển tiền cho người thụ hưởng thành công"
}
```

---

## 👥 Người Thụ Hưởng

### 1. Thêm Người Thụ Hưởng
**Endpoint:** `POST /beneficiaries/add`

**Mô tả:** Thêm người thụ hưởng mới

**Request Body:**
```json
{
  "name": "Trần Văn B",
  "accountno": 9876543210,
  "bankname": "Vietcombank",
  "ifsccode": "VIETCOMBANK123"
}
```

**Response (201 Created):**
```json
{
  "benid": 1,
  "name": "Trần Văn B",
  "accountno": 9876543210,
  "bankname": "Vietcombank"
}
```

---

### 2. Tạo Người Thụ Hưởng Cho Người Dùng
**Endpoint:** `POST /beneficiaries/create/{userId}`

**Mô tả:** Tạo người thụ hưởng liên kết với người dùng

**Path Parameters:**
- `userId` (string): ID người dùng

**Request Body:**
```json
{
  "name": "Trần Văn B",
  "accountno": 9876543210,
  "bankname": "Vietcombank"
}
```

**Response (200 OK):**
```json
{
  "benid": 1,
  "name": "Trần Văn B",
  "accountno": 9876543210,
  "userId": "user_123"
}
```

---

## 👤 Người Dùng

### 1. Lấy Tất Cả Người Dùng
**Endpoint:** `GET /api/v1/user/users`

**Mô tả:** Lấy danh sách tất cả người dùng

**Response (200 OK):**
```json
[
  {
    "id": "user_123",
    "firstName": "Nguyễn",
    "lastName": "Văn A",
    "email": "nguyenvana@gmail.com",
    "createdDate": "2025-12-24T10:30:00",
    "roles": ["USER"]
  }
]
```

---

### 2. Lấy Một Người Dùng
**Endpoint:** `GET /api/v1/user/auser?userid={userId}`

**Mô tả:** Lấy thông tin một người dùng cụ thể

**Query Parameters:**
- `userid` (string): ID người dùng

**Response (200 OK):**
```json
{
  "id": "user_123",
  "firstName": "Nguyễn",
  "lastName": "Văn A",
  "email": "nguyenvana@gmail.com",
  "accounts": [...]
}
```

---

### 3. Tạo Hồ Sơ Người Dùng
**Endpoint:** `PUT /api/v1/user/createprofile/{userId}`

**Mô tả:** Tạo hoặc cập nhật hồ sơ người dùng

**Path Parameters:**
- `userId` (string): ID người dùng

**Request Body:**
```json
{
  "phone": "0912345678",
  "address": "123 Đường ABC, Hà Nội",
  "city": "Hà Nội",
  "state": "Hà Nội",
  "pincode": "100000",
  "dob": "1990-01-15",
  "gender": "MALE"
}
```

**Response (200 OK):**
```json
{
  "userdetailid": 1,
  "userId": "user_123",
  "phone": "0912345678",
  "address": "123 Đường ABC, Hà Nội",
  "city": "Hà Nội",
  "dob": "1990-01-15"
}
```

---

### 4. Tải Lên Ảnh Đại Diện
**Endpoint:** `POST /api/v1/user/image/{userId}`

**Mô tả:** Tải lên ảnh đại diện người dùng

**Path Parameters:**
- `userId` (string): ID người dùng

**Request:**
- Form Data: `image` (file)

**Response (200 OK):**
```json
{
  "imageName": "user_123_avatar.jpg",
  "message": "Ảnh đã được tải lên thành công"
}
```

---

### 5. Lấy Ảnh Đại Diện
**Endpoint:** `GET /api/v1/user/image/{userId}`

**Mô tả:** Lấy ảnh đại diện của người dùng

**Path Parameters:**
- `userId` (string): ID người dùng

**Response:**
- Content-Type: `image/jpeg`
- Body: Dữ liệu ảnh

---

## 📝 Yêu Cầu Mở Tài Khoản

### 1. Gửi Yêu Cầu Mở Tài Khoản
**Endpoint:** `PUT /api/v1/user/acopreq/{userId}`

**Mô tả:** Gửi yêu cầu mở tài khoản mới

**Path Parameters:**
- `userId` (string): ID người dùng

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Yêu cầu mở tài khoản đã được gửi"
}
```

---

### 2. Kiểm Tra Trạng Thái Yêu Cầu
**Endpoint:** `GET /api/v1/user/acopreqchng/{userId}`

**Mô tả:** Kiểm tra trạng thái yêu cầu mở tài khoản

**Path Parameters:**
- `userId` (string): ID người dùng

**Response (200 OK):**
```json
{
  "userId": "user_123",
  "requestStatus": "PENDING",
  "requestDate": "2025-12-24",
  "approvalDate": null
}
```

---

## 🔑 Authentication

### Header Yêu Cầu
Tất cả các endpoint (ngoại trừ signup, login, otp) đều cần JWT token trong header:

```
Authorization: Bearer <jwtToken>
```

### Ví Dụ cURL:
```bash
curl -X GET http://localhost:8080/account/accountdetails/1234567890 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📊 Status Code Chung

| Status Code | Ý Nghĩa |
|-------------|---------|
| 200 | OK - Yêu cầu thành công |
| 201 | Created - Tài nguyên được tạo thành công |
| 400 | Bad Request - Dữ liệu không hợp lệ |
| 401 | Unauthorized - Token không hợp lệ/hết hạn |
| 403 | Forbidden - Không có quyền truy cập |
| 404 | Not Found - Tài nguyên không tồn tại |
| 409 | Conflict - Xung đột (ví dụ: Email đã tồn tại) |
| 500 | Internal Server Error - Lỗi máy chủ |

---

## 🧪 Ví Dụ Quy Trình Sử Dụng Hoàn Chỉnh

### 1. Đăng Ký Người Dùng Mới
```bash
curl -X POST http://localhost:8080/api/v1/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Nguyễn",
    "lastName": "Văn A",
    "email": "nguyenvana@gmail.com",
    "password": "SecurePassword123!"
  }'
```

### 2. Đăng Nhập
```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nguyenvana@gmail.com",
    "password": "SecurePassword123!"
  }'
```

### 3. Lưu JWT Token
```bash
# Lưu token từ response
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```
eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiI2MDMyNDg1ZS1mNzM2LTRhODMtOWQ1Mi1mOWQ2OGQwOWM0Y2MiLCJzdWIiOiJuZ3V5ZW52YW5hQGdtYWlsLmNvbSIsImlhdCI6MTc2NjUzNTcyNywiZXhwIjoxNzY2NTUzNzI3fQ.8cLp2LIaj4LQSWXe-XiNGUwDeUesIQ2LKMgJkWpOMGSEbekVx4Y_xYxoWthv892FRut_OQXRFYpxK2u-9HO0RQ

### 4. Tạo Tài Khoản Ngân Hàng
```bash
curl -X POST http://localhost:8080/account/create/user_123 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accounttype": "SAVINGS",
    "balance": 0
  }'
```

### 5. Kiểm Tra Số Dư
```bash
curl -X GET http://localhost:8080/account/checkbal/1234567890 \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Chuyển Tiền
```bash
curl -X POST "http://localhost:8080/fund/transfer?toAccount=9876543210&amount=50000&description=Chuyển tiền thanh toán" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accountno": 1234567890
  }'
```

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề khi sử dụng API, vui lòng:
1. Kiểm tra lại URL endpoint
2. Đảm bảo JWT token còn hạn
3. Kiểm tra dữ liệu request
4. Xem logs của server để tìm lỗi chi tiết

---

**Cập nhật lần cuối:** 24/12/2025
