# FreshCart - Grocery Billing, Inventory and Sales Management System

A complete full-stack web application for grocery store billing, inventory management, and sales reporting.

## Technology Stack

- **Frontend:** React.js 18 with Vite
- **Backend:** Java 21 with Spring Boot 3.3.4
- **Database:** PostgreSQL
- **Security:** Spring Security with JWT
- **ORM:** Spring Data JPA & Hibernate
- **Build Tools:** Maven (Backend), npm (Frontend)
- **Charts:** Recharts
- **File Upload:** Multipart support

## Features

### 1. Authentication & User Management
- Secure login with username and password
- Account creation without customer name requirement
- Show/hide password functionality
- JWT-based authentication
- Role-based access control (USER, ADMIN)
- Secure password hashing with BCrypt

### 2. Billing System (3-Page Flow)
- **Page 1 (Login):** User authentication
- **Page 2 (Shop):** Search products, add quantities, view selected items with images
- **Page 3 (Bill):** Enter customer name, edit bill (add/remove/increase/decrease items), confirm checkout
- **Thank You:** Invoice display with print functionality

### 3. Inventory Management
- Real-time stock tracking
- Automatic stock deduction on final bill confirmation
- Stock replenishment through Settings
- Prevents overselling with stock validation
- Transaction-safe inventory updates with pessimistic locking

### 4. Settings Module (Admin Only)
- **Modify Grocery Prices:** Update product prices (affects future bills only)
- **Add Stock:** Replenish inventory without overwriting
- **Add New Grocery:** Add products with images
- **Data and Reports:** View transaction history and reports

### 5. Reports
- Transaction history with invoice details
- Daily, weekly, monthly reports
- Sales analytics

## Prerequisites

- Java 21 JDK
- Node.js 18+ and npm
- PostgreSQL 14+
- Maven 3.8+

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE freshcart;
```

2. Update database credentials in `backend/src/main/resources/application.yml` or use environment variables:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/freshcart
    username: your_username
    password: your_password
```

## Backend Setup (Windows PowerShell)

```powershell
# Navigate to backend directory
cd "D:\ITS SHOW TIME\freshcart\backend"

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will run on http://localhost:8080

## Frontend Setup (Windows PowerShell)

```powershell
# Navigate to frontend directory
cd "D:\ITS SHOW TIME\freshcart\frontend"

# Install dependencies
npm install

# Run development server
npm run dev
```

The frontend will run on http://localhost:5173

## Initial Administrator Setup

After starting the backend, create an admin account using the setup endpoint:

```powershell
# Using PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/setup/admin?username=admin&password=admin123" -Method Post
```

Or using curl:
```bash
curl -X POST "http://localhost:8080/api/setup/admin?username=admin&password=admin123"
```

You can then login with these credentials and access the Settings module.

## Environment Configuration

Create a `.env` file for production (not included in repo). Example variables:
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your-very-long-secret-key
UPLOAD_DIR=./uploads
```

## Project Structure

```
freshcart/
├── backend/
│   ├── src/main/java/com/freshcart/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   ├── dto/
│   │   ├── security/
│   │   └── config/
│   └── src/main/resources/
│       ├── db/migration/
│       └── application.yml
└── frontend/
    └── src/
        ├── pages/
        ├── api.js
        └── App.jsx
```

## Usage Guide

1. Start PostgreSQL database
2. Start backend server
3. Create admin account via setup endpoint
4. Start frontend
5. Login with credentials
6. Add products via Settings (Admin) or use the system
7. Create bills through the 3-page workflow
8. View reports in Settings

## Key Business Rules

- Inventory is deducted only on final bill confirmation
- Quantities can be edited on Page 3 without affecting stock
- Historical bills retain original prices
- Customer name is entered on Page 3, not during registration
- Only admins can access Settings
- Stock cannot go negative
- All transactions are saved permanently

## License

This project is for educational purposes.
