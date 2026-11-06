# FinFlow – Authentication & Error Reporting API

## 1. Project Overview

FinFlow is a RESTful web application developed using Java and Spring Boot. It provides secure user authentication, password management, database integration and centralized error handling.

The application uses JSON Web Tokens (JWT) to authenticate users and Spring Security to protect secured endpoints.

It also implements centralized exception handling and a global error notifier to log application errors for easier monitoring and debugging.

## 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Security | Authentication and endpoint security |
| Spring Data JPA | Database interaction |
| Hibernate | Object-relational mapping |
| MySQL | Relational database |
| Flyway | Database schema versioning |
| JWT (JJWT) | Token-based authentication |
| BCrypt | Password hashing |
| Gradle | Build and dependency management |
| SLF4J / Logback | Application logging |
| Jakarta Validation | Request validation |

## 3. Application Architecture

The application follows a layered architecture to maintain separation of responsibilities.

```text
Client (Postman / Frontend)
          |
          v
    AuthController
          |
          v
      UserService
          |
          v
    UserRepository
          |
          v
       MySQL

Security:
JWT Authentication Filter
          |
          v
    Spring Security
          |
          v
    Protected APIs

Error Handling:
Controller / Service Exceptions
          |
          v
 GlobalExceptionHandler
          |
          v
    GlobalErrorNotifier
          |
          v
   Application Logs
```

### Main Components

- **Controller Layer:** Exposes REST APIs and handles incoming HTTP requests.
- **Service Layer:** Contains business logic for registration, login and password management.
- **Repository Layer:** Handles database operations using Spring Data JPA.
- **Security Layer:** Validates JWT tokens and manages authenticated requests.
- **Exception Handling:** Provides centralized exception handling through `@RestControllerAdvice`.
- **Global Error Notifier:** Logs application errors with request details using SLF4J.

## 4. Features

### User Registration
- Registers new users.
- Validates incoming request data.
- Checks for duplicate email addresses.
- Hashes passwords using BCrypt before storing them.
- Assigns a default user role and account status.

### User Login
- Authenticates users using email and password.
- Verifies passwords using BCrypt.
- Generates a signed JWT access token after successful authentication.
- Returns the access token to the client.

### JWT Authentication
- Uses stateless JWT-based authentication.
- Validates token signatures and expiration.
- Extracts user ID and role from JWT claims.
- Sets the authenticated user in Spring Security's SecurityContext.
- Protects endpoints that require authentication.

### Change Password
- Requires an authenticated user.
- Verifies the current password.
- Prevents changing the password to the same existing password.
- Hashes the new password using BCrypt before updating the database.

### Global Exception Handling
- Uses `@RestControllerAdvice` to handle application exceptions centrally.
- Returns consistent API error responses.
- Handles validation errors and business exceptions.
- Uses a global error notifier to log errors.

### Global Error Notification
- Logs application errors using SLF4J.
- Captures HTTP method and request endpoint.
- Records exception messages and stack traces.
- Avoids logging sensitive information such as passwords and JWT tokens.

## 5. Database Design

The application uses MySQL as its relational database.

The `users` table stores user information, including:

| Column | Description |
|---|---|
| id | Primary key |
| name | User name |
| email | Unique email address |
| password_hash | BCrypt hashed password |
| role | User role |
| status | Account status |
| created_at | Account creation timestamp |

### Database Migration

Flyway is used to manage database schema changes through version-controlled SQL migration scripts.

Migration files are maintained under:

```text
src/main/resources/db/migration/
```

Example:

```text
V1__create_users_table.sql
V2__add_user_status.sql
```

Flyway tracks executed migrations and applies pending migrations when the application starts.

## 6. Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.finflow.finflow_api
│   │       ├── auth
│   │       ├── config
│   │       ├── common
│   │       │   ├── dto
│   │       │   ├── exception
│   │       │   └── logging
│   │       ├── dto
│   │       └── user
│   │           ├── controller
│   │           ├── entity
│   │           ├── repository
│   │           └── service
│   │
│   └── resources
│       ├── application.properties
│       └── db
│           └── migration
│
└── test
    └── java
```

## 7. Prerequisites

Before running the application, ensure the following are installed:

- Java 17 or later compatible with the project configuration.
- MySQL Server.
- IntelliJ IDEA or another Java IDE.
- Git.

## 8. Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE finflow;
```

Configure the following environment variables:

```text
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_base64_encoded_secret
```

The JWT secret should be a securely generated Base64-encoded key suitable for the selected HMAC algorithm.

The application uses the following configuration:

```properties
spring.application.name=finflow-api

spring.datasource.url=jdbc:mysql://localhost:3306/finflow
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate

jwt.secret=${JWT_SECRET}

spring.flyway.enabled=true
```

**Note:** Database credentials and JWT secrets should be provided through environment variables and must not be committed to the Git repository.

## 9. Running the Application

Clone the repository:

```bash
git clone <your-github-repository-url>
```

Navigate to the project directory:

```bash
cd finflow-api
```

Run the application:

**Windows:**

```bash
gradlew.bat bootRun
```

**Linux / macOS:**

```bash
./gradlew bootRun
```

The application will start on:

```text
http://localhost:8080
```

## 10. API Documentation

Base URL:

```text
http://localhost:8080
```

### 10.1 Register User

**Endpoint:** `POST /api/auth/register`

**Authentication:** Not required.

Request:

```json
{
  "name": "John",
  "email": "john@example.com",
  "password": "Password@123"
}
```

Expected response: `201 Created`

```json
{
  "success": true,
  "message": "user registered successfully",
  "data": {
    "userId": 1,
    "name": "John",
    "email": "john@example.com"
  }
}
```

### 10.2 User Login

**Endpoint:** `POST /api/auth/login`

**Authentication:** Not required.

Request:

```json
{
  "email": "john@example.com",
  "password": "Password@123"
}
```

Expected response: `200 OK`

```json
{
  "success": true,
  "message": "Logged in successfully",
  "data": {
    "accessToken": "<JWT_TOKEN>",
    "tokenType": "Bearer"
  }
}
```

### 10.3 Change Password

**Endpoint:** `PUT /api/auth/change-password`

**Authentication:** Bearer token required.

Request headers:

```text
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

Request:

```json
{
  "currentPassword": "Password@123",
  "newPassword": "NewPassword@123"
}
```

Expected response: `200 OK`

```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null
}
```

## 11. Error Handling

The application uses a centralized exception-handling mechanism.

`GlobalExceptionHandler` handles application exceptions and returns structured error responses.

`GlobalErrorNotifier` records errors using the application logging framework.

Examples of handled scenarios:

| Scenario | HTTP Status |
|---|---|
| Invalid login credentials | 401 Unauthorized |
| Missing or invalid JWT | 401 Unauthorized |
| Duplicate email registration | 400 Bad Request |
| Invalid current password | 400 Bad Request |
| Invalid request validation | 400 Bad Request |

Example error response:

```json
{
  "success": false,
  "message": "Current password is incorrect",
  "data": null
}
```

## 12. Security Considerations

- Passwords are stored as BCrypt hashes rather than plain text.
- JWT tokens are signed and validated before granting access.
- Protected endpoints require authentication.
- The application uses stateless session management.
- Database credentials and JWT signing secrets are supplied through environment variables.
- Sensitive information is excluded from application logs.



**FinFlow** – A Spring Boot REST API demonstrating JWT authentication, database integration, secure password management and centralized error reporting.