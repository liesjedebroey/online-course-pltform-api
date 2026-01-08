# Postman Testing Guide voor Course Platform API

## Setup

### 1. Environment Variables aanmaken
Maak een Postman Environment aan met:
```
BASE_URL = http://localhost:8080
ADMIN_TOKEN = (wordt automatisch gevuld na login)
INSTRUCTOR_TOKEN = (wordt automatisch gevuld na login)
STUDENT_TOKEN = (wordt automatisch gevuld na login)
```

### 2. Authorization Header Setup
Voor beveiligde endpoints gebruik je:
- **Type**: Bearer Token
- **Token**: `{{ADMIN_TOKEN}}` of `{{INSTRUCTOR_TOKEN}}` of `{{STUDENT_TOKEN}}`

---

## Test Flow (in volgorde)

### Phase 1: Authentication

#### 1.1 Register Admin
```
POST {{BASE_URL}}/api/auth/register
Body (JSON):
{
  "userName": "test_admin",
  "email": "testadmin@test.com",
  "password": "password123",
  "role": "ADMIN"
}

Expected: 200 OK
Response: "User registered successfully!"
```

#### 1.2 Login Admin
```
POST {{BASE_URL}}/api/auth/login
Body (JSON):
{
  "userName": "admin",
  "password": "admin123"
}

Expected: 200 OK
Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN"
}

✅ Test Script (Tab "Tests"):
pm.environment.set("ADMIN_TOKEN", pm.response.json().token);
```

#### 1.3 Login Instructor
```
POST {{BASE_URL}}/api/auth/login
Body (JSON):
{
  "userName": "john_instructor",
  "password": "password123"
}

✅ Test Script:
pm.environment.set("INSTRUCTOR_TOKEN", pm.response.json().token);
```

#### 1.4 Login Student
```
POST {{BASE_URL}}/api/auth/login
Body (JSON):
{
  "userName": "alice_student",
  "password": "password123"
}

✅ Test Script:
pm.environment.set("STUDENT_TOKEN", pm.response.json().token);
```

---

### Phase 2: Course CRUD (Public + Auth)

#### 2.1 Get All Courses (Public)
```
GET {{BASE_URL}}/api/courses

Authorization: None (public endpoint)
Expected: 200 OK met lijst van courses uit DataLoader
```

#### 2.2 Get Course By ID (Public)
```
GET {{BASE_URL}}/api/courses/1

Authorization: None
Expected: 200 OK met course details
```

#### 2.3 Create Course (Instructor)
```
POST {{BASE_URL}}/api/courses
Authorization: Bearer {{INSTRUCTOR_TOKEN}}

Body (JSON):
{
  "title": "Advanced React",
  "description": "Deep dive into React hooks"
}

Expected: 200 OK
```

#### 2.4 Create Course (Student) ❌ Should Fail
```
POST {{BASE_URL}}/api/courses
Authorization: Bearer {{STUDENT_TOKEN}}

Body (JSON):
{
  "title": "Hacker Course",
  "description": "Should not work"
}

Expected: 403 FORBIDDEN
```

#### 2.5 Update Course (Owner)
```
PUT {{BASE_URL}}/api/courses/1
Authorization: Bearer {{INSTRUCTOR_TOKEN}}

Body (JSON):
{
  "title": "Java Masterclass - Updated",
  "description": "New description"
}

Expected: 200 OK (als john_instructor eigenaar is van course 1)
```

#### 2.6 Update Course (Non-Owner) ❌ Should Fail
```
PUT {{BASE_URL}}/api/courses/2
Authorization: Bearer {{INSTRUCTOR_TOKEN}}

Body (JSON):
{
  "title": "Trying to hack",
  "description": "Should fail"
}

Expected: 403 FORBIDDEN (als john niet eigenaar is van course 2)
```

#### 2.7 Delete Course (Admin)
```
DELETE {{BASE_URL}}/api/courses/3
Authorization: Bearer {{ADMIN_TOKEN}}

Expected: 204 NO CONTENT
```

#### 2.8 Delete Course (Instructor) ❌ Should Fail
```
DELETE {{BASE_URL}}/api/courses/1
Authorization: Bearer {{INSTRUCTOR_TOKEN}}

Expected: 403 FORBIDDEN
```

---

### Phase 3: Enrollments

#### 3.1 Enroll in Course (Student)
```
POST {{BASE_URL}}/api/courses/1/enroll
Authorization: Bearer {{STUDENT_TOKEN}}

Expected: 200 OK met enrollment details
```

#### 3.2 Enroll Again (Duplicate) ❌ Should Fail
```
POST {{BASE_URL}}/api/courses/1/enroll
Authorization: Bearer {{STUDENT_TOKEN}}

Expected: 400 BAD REQUEST
Response: "You are already enrolled in this course..."
```

#### 3.3 Get My Enrollments (Student)
```
GET {{BASE_URL}}/api/enrollments/me
Authorization: Bearer {{STUDENT_TOKEN}}

Expected: 200 OK met lijst van eigen enrollments
```

#### 3.4 Get Instructor Enrollments
```
GET {{BASE_URL}}/api/instructor/enrollments
Authorization: Bearer {{INSTRUCTOR_TOKEN}}

Expected: 200 OK met enrollments van courses die instructor geeft
```

#### 3.5 Get All Enrollments (Admin)
```
GET {{BASE_URL}}/api/admin/enrollments
Authorization: Bearer {{ADMIN_TOKEN}}

Expected: 200 OK met alle enrollments
```

#### 3.6 Cancel Enrollment (Student - own)
```
DELETE {{BASE_URL}}/api/enrollments/1
Authorization: Bearer {{STUDENT_TOKEN}}

Expected: 204 NO CONTENT (als enrollment 1 van deze student is)
```

#### 3.7 Cancel Enrollment (Student - other's) ❌ Should Fail
```
DELETE {{BASE_URL}}/api/enrollments/2
Authorization: Bearer {{STUDENT_TOKEN}}

Expected: 403 FORBIDDEN (als enrollment 2 niet van deze student is)
```

---

### Phase 4: Admin User Management

#### 4.1 Get All Users
```
GET {{BASE_URL}}/api/admin/users
Authorization: Bearer {{ADMIN_TOKEN}}

Expected: 200 OK met lijst van alle users
```

#### 4.2 Change User Role
```
PUT {{BASE_URL}}/api/admin/users/3/role
Authorization: Bearer {{ADMIN_TOKEN}}

Body (JSON):
{
  "role": "INSTRUCTOR"
}

Expected: 200 OK
```

#### 4.3 Delete User
```
DELETE {{BASE_URL}}/api/admin/users/6
Authorization: Bearer {{ADMIN_TOKEN}}

Expected: 204 NO CONTENT
```

#### 4.4 Admin Operations as Student ❌ Should Fail
```
GET {{BASE_URL}}/api/admin/users
Authorization: Bearer {{STUDENT_TOKEN}}

Expected: 403 FORBIDDEN
```

---

## Automated Tests (Postman Scripts)

### Bij elke request kun je tests toevoegen:

```javascript
// Check status code
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Check response time
pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// Check JSON structure
pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('token');
});

// Check error message
pm.test("Error message is correct", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.include("already enrolled");
});
```

---

## Expected Test Results Summary

✅ **Should PASS:**
- Public endpoints zonder auth
- Login met correcte credentials
- Create course als INSTRUCTOR/ADMIN
- Update own course als INSTRUCTOR
- Delete course als ADMIN
- Enroll als STUDENT
- View own enrollments als STUDENT
- Admin operations als ADMIN

❌ **Should FAIL:**
- Login met verkeerde password
- Create course als STUDENT
- Update other's course als INSTRUCTOR
- Delete course als INSTRUCTOR
- Duplicate enrollment
- View other's enrollments als STUDENT
- Admin operations als STUDENT/INSTRUCTOR

---

## Export Instructie

1. Klik op je Collection
2. Klik op "..." (drie puntjes)
3. Choose "Export"
4. Select "Collection v2.1"
5. Save as `CourseAPI_Postman_Collection.json`

Dit bestand lever je in met je project!
