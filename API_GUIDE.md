# MediBook API - Complete Guide

## 📋 Table of Contents
1. [Quick Start](#quick-start)
2. [Authentication & Security](#authentication--security)
3. [All API Endpoints](#all-api-endpoints)
4. [Step-by-Step Testing](#step-by-step-testing)
5. [Using Admin Token](#using-admin-token)
6. [Troubleshooting](#troubleshooting)

---

## 🚀 Quick Start

### Prerequisites
1. **PostgreSQL** running on port 5432
2. **Spring Boot application** running on port 8080
3. **Testing tool**: Postman (recommended) or cURL

### Base URL
```
http://localhost:8080
```

---

## 🔐 Authentication & Security

### Public Endpoints (No Token Required)
- `/admin/login` - Admin login (step 1)
- `/admin/verify-otp` - Verify OTP (step 2)
- `/patient/register` - Patient registration (**No password required**)
- `/patient/login` - Patient login (**Requires admin approval & temp password**)
- `/doctor/register` - Doctor registration (**No password required**)
- `/doctor/login` - Doctor login (**Requires admin approval & temp password**)
- `/api/auth/change-password-first-login` - **NEW:** Set new password for first-time login

### Protected Endpoints (Admin Token Required)
All patient and doctor management endpoints require admin authentication:
- Approval/rejection endpoints
- Listing endpoints (pending, approved, rejected, all)

### ⚠️ Important: Registration & Approval Flow

**New Secure Flow:**
1. **Register**: Patient/Doctor registers **without** a password. Status: `PENDING`.
2. **Approve**: Admin approves. System generates a **Temporary Password** and sends it via Email. Status: `ACTIVE`.
3. **First Login**: User logs in with Email + Temporary Password.
   - Response: `FIRST_LOGIN_PASSWORD_RESET_REQUIRED`
4. **Set Password**: User sends `currentPassword` (temp), `newPassword`, and `confirmPassword` to `/api/auth/change-password-first-login`.
5. **Final Login**: User logs in with Email + New Password. Success.

---

## 📍 All API Endpoints

### Admin Endpoints
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/admin/login` | ❌ No | Login with email + password (sends OTP) |
| POST | `/admin/verify-otp?otp={code}` | ❌ No | Verify OTP (returns JWT token) |

### Auth Endpoints (New)
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/auth/change-password-first-login` | ✅ Yes (Temp Token) | Change password after first login |

### Patient Endpoints
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/patient/register` | ❌ No | Register new patient (No password) |
| POST | `/patient/login` | ❌ No | Patient login |
| GET | `/patient/pending` | ✅ Admin | List pending patients |
| GET | `/patient/approved` | ✅ Admin | List approved patients |
| GET | `/patient/rejected` | ✅ Admin | List rejected patients |
| GET | `/patient/all` | ✅ Admin | List all patients |
| PUT | `/patient/{id}/approve` | ✅ Admin | Approve patient (sends temp password) |
| PUT | `/patient/{id}/reject` | ✅ Admin | Reject patient |

### Doctor Endpoints
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/doctor/register` | ❌ No | Register new doctor (No password) |
| POST | `/doctor/login` | ❌ No | Doctor login |
| GET | `/doctor/pending` | ✅ Admin | List pending doctors |
| GET | `/doctor/approved` | ✅ Admin | List approved doctors |
| GET | `/doctor/rejected` | ✅ Admin | List rejected doctors |
| GET | `/doctor/all` | ✅ Admin | List all doctors |
| PUT | `/doctor/{id}/approve` | ✅ Admin | Approve doctor (sends temp password) |
| PUT | `/doctor/{id}/reject` | ✅ Admin | Reject doctor |

**Total: 23 endpoints**

---

## 🧪 Step-by-Step Testing

### Test 1: Admin Login & Get Token

#### Step 1.1: Login with Email/Password
```bash
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"chmounyasri@gmail.com","password":"Admin123"}'
```

**Expected:** `"OTP sent to admin email"`

**Action:** Check email for 5-digit OTP

#### Step 1.2: Verify OTP & Get Token
```bash
curl -X POST "http://localhost:8080/admin/verify-otp?otp=12345"
```
*(Replace 12345 with actual OTP)*

**Expected Response:**
```json
{
  "message": "Admin login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4i...",
  "role": "ADMIN"
}
```

**💾 Save this token! You'll need it for all admin operations.**

---

### Test 2: Patient Registration & Approval

#### Step 2.1: Register Patient (No Password)
```bash
curl -X POST http://localhost:8080/patient/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "age": 30
  }'
```

**Expected:** `{"status": "PENDING", ...}`

#### Step 2.2: Try Login (Should Fail - Not Approved/Password Unknown)
```bash
curl -X POST http://localhost:8080/patient/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"anything"}'
```
**Expected Error:**
```json
{
  "error": "Your account is pending approval."
}
```
*(or "Invalid Credentials" if checking password first)*

#### Step 2.3: Get Pending Patients (With Admin Token)
```bash
curl http://localhost:8080/patient/pending \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```
**Expected:** Array with John Doe (status: PENDING)

#### Step 2.4: Approve Patient (With Admin Token)
```bash
curl -X PUT http://localhost:8080/patient/1/approve \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```
**Expected:** `{"message": "Patient approved with id = 1"}`
**Action:** Check email for **Temporary Password** (e.g., `temp1234`)

#### Step 2.5: First Login (With Temp Password)
```bash
curl -X POST http://localhost:8080/patient/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"temp1234"}'
```
**Expected Response:**
```json
{
  "status": "FIRST_LOGIN_PASSWORD_RESET_REQUIRED",
  "message": "You must change your password.",
  "mustChangePassword": true,
  "token": "eyJhb...", 
  "role": "PATIENT"
}
```
**💾 Save this temp token!**

#### Step 2.6: Set New Password
```bash
curl -X POST http://localhost:8080/api/auth/change-password-first-login \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TEMP_TOKEN_FROM_STEP_2.5" \
  -d '{
    "currentPassword": "temp1234",
    "newPassword": "newSecretPassword1!",
    "confirmPassword": "newSecretPassword1!"
  }'
```
**Expected:** `{"message": "Password updated successfully."}`

#### Step 2.7: Login with New Password
```bash
curl -X POST http://localhost:8080/patient/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"newSecretPassword1!"}'
```
**Expected:** `{"message": "Login successful", ...}`

---

### Test 3: Doctor Registration & Approval

#### Step 3.1: Register Doctor (No Password)

**Using cURL:**
```bash
curl -X POST http://localhost:8080/doctor/register \
  -F "fullName=Dr. Jane Smith" \
  -F "medicalLicenseNo=MED123456" \
  -F "clinicName=City Hospital" \
  -F "gender=Female" \
  -F "yearsOfExperience=10" \
  -F "hospitalDepartment=Cardiology" \
  -F "specialization=Cardiologist" \
  -F "contactPhone=9876543210" \
  -F "email=jane.smith@hospital.com" \
  -F "consultationType=In-person" \
  -F "licenseFile=@/path/to/file.pdf"
```

**Expected:** `{"message": "Doctor registered...", "id": ...}`

#### Step 3.2: Approve Doctor (With Admin Token)
```bash
curl -X PUT http://localhost:8080/doctor/1/approve \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```
**Expected:** `{"message": "Doctor approved with id = 1"}`
**Action:** Check email for **Temporary Password** (e.g., `temp1234`)

#### Step 3.3: First Login (With Temp Password)
```bash
curl -X POST http://localhost:8080/doctor/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane.smith@hospital.com","password":"temp1234"}'
```
**Expected Response:**
```json
{
  "status": "FIRST_LOGIN_PASSWORD_RESET_REQUIRED",
  "message": "You must change your password.",
  "mustChangePassword": true,
  "token": "eyJhb...", 
  }'
```
**Expected:** `{"message": "Password updated successfully."}`

#### Step 3.5: Login with New Password
```bash
curl -X POST http://localhost:8080/doctor/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane.smith@hospital.com","password":"newSecretPassword1!"}'
```
**Expected:** `{"message": "Login successful", ...}`

---

## 🔑 Using Admin Token

### How to Get Admin Token

1. **Login:** POST `/admin/login` with email/password
2. **Check Email:** Get 5-digit OTP
3. **Verify:** POST `/admin/verify-otp?otp=12345`
4. **Save Token:** Copy the token from response

### How to Use Token in Requests

#### cURL Example
```bash
# Set token as variable
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4i..."

# Use in requests
curl http://localhost:8080/patient/pending \
  -H "Authorization: Bearer $TOKEN"

curl -X PUT http://localhost:8080/patient/1/approve \
  -H "Authorization: Bearer $TOKEN"
```

#### PowerShell Example
```powershell
$token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4i..."

Invoke-RestMethod -Uri "http://localhost:8080/patient/pending" `
### Token Details
- **Format:** JWT (JSON Web Token)
- **Validity:** 24 hours
- **Contains:** Admin email and role
- **Required for:** All patient/doctor management endpoints

---

## 📦 Postman Collection
*(If you have a collection, update it to remove "password" from registration requests and add the new "Set Password" request)*

---

## 🔑 Key Features

✅ **Password Encryption** - BCrypt hashing for all passwords  
✅ **JWT Authentication** - Token-based auth for all roles  
✅ **Admin 2FA** - Email OTP verification for admin  
✅ **Role-Based Access** - ADMIN, PATIENT, DOCTOR roles  
✅ **Status Management** - PENDING → APPROVED/REJECTED workflow  
✅ **File Upload** - Doctor license document upload  
✅ **Secure Endpoints** - Admin token required for management  

---

## 📊 Status Values

| Status | Description |
|--------|-------------|
| `PENDING` | Newly registered, awaiting admin approval |
| `APPROVED` / `ACTIVE` | Approved by admin, can login |
| `REJECTED` | Rejected by admin, cannot login |

---
