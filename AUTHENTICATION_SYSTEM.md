# PharmaPoint Authentication System

## Overview
PharmaPoint now features a complete authentication system with session management and role-based navigation.

## Components Created/Updated

### 1. SessionManager (utils/SessionManager.java)
- **Purpose**: Manages user session state across the application
- **Key Methods**:
  - `setCurrentUser(User user)` - Set the logged-in user
  - `getCurrentUser()` - Get the current user
  - `isUserLoggedIn()` - Check if someone is logged in
  - `clearSession()` - Logout/clear session
  - `hasRole(String role)` - Check user role

### 2. AuthService (services/AuthService.java)
- **Purpose**: Handles user authentication logic
- **Key Methods**:
  - `authenticateUser(String email, String password)` - Main authentication method
  - `isValidEmail(String email)` - Email validation
  - `isValidPassword(String password)` - Password validation

### 3. Enhanced UserAuthUI (ui/UserAuthUI.java)
- **Purpose**: Login/Registration screen (updated to use new auth system)
- **Key Features**:
  - Uses AuthService for authentication
  - Sets user in SessionManager on successful login
  - Role-based navigation (pharmacy vs patient)
  - Proper error handling and validation

### 4. Enhanced PharmacyDashboard (ui/PharmacyDashboard.java)
- **Purpose**: Main dashboard for pharmacy users (updated for session management)
- **Key Features**:
  - Uses SessionManager for logout
  - Clears session on logout

### 5. Updated PharmacyService & PharmacyDAO
- **New Method**: `getPharmacyByUserId(int userId)` - Gets pharmacy owned by a user

### 6. Updated UserService & UserDAO
- **New Method**: `getUserByEmail(String email)` - Gets user by email only

### 7. Updated Main.java
- **Purpose**: Application entry point (updated to start with login)
- **Key Features**:
  - Initializes database
  - Opens UserAuthUI as the starting screen

## Authentication Flow

### Login Process:
1. User enters email/password in UserAuthUI
2. UserAuthUI calls `AuthService.authenticateUser(email, password)`
3. AuthService validates input and calls `UserDAO.findByEmailAndPassword()`
4. On success:
   - User is stored in `SessionManager.setCurrentUser(user)`
   - Based on user role:
     - **"pharmacy"** → Opens PharmacyDashboard (with or without existing pharmacy)
     - **"patient"** → Opens PatientDashboard (placeholder for now)
   - Login window is closed
5. On failure: Error message is shown

### Logout Process:
1. User clicks logout in any dashboard
2. `SessionManager.clearSession()` is called
3. Dashboard closes
4. UserAuthUI (login screen) is opened

## User Roles

### "pharmacy" Role:
- Can access PharmacyDashboard
- Can create/manage pharmacies
- Can add/edit/delete medicines
- Can view medicine inventory

### "patient" Role:
- Will access PatientDashboard (to be implemented)
- Can search for medicines/pharmacies
- Can view pharmacy information

## Database Schema

### Users Table:
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    dateOfBirth TEXT,
    role TEXT CHECK(role IN ('pharmacy', 'patient'))
);
```

### Pharmacies Table:
```sql
CREATE TABLE pharmacies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    area TEXT,
    FOREIGN KEY(user_id) REFERENCES users(id)
);
```

## Test Users (Created by TestDataSetup)

### Pharmacy User:
- **Email**: pharmacy@test.com
- **Password**: test123
- **Role**: pharmacy
- **Has Pharmacy**: "Test Pharmacy" in Downtown Area

### Patient User:
- **Email**: patient@test.com
- **Password**: test123
- **Role**: patient

## Running the Application

1. **Start Application**: `java -cp "src:src/lib/*" Main`
2. **Create Test Data**: `java -cp "src:src/lib/*" test.TestDataSetup`
3. **Login** with test credentials
4. **Navigate** based on role

## Security Considerations

- Passwords are stored in plain text (should be hashed in production)
- Basic email validation implemented
- Session management is in-memory (lost on app restart)
- Input validation for all authentication forms

## Future Enhancements

1. **Password Hashing**: Implement BCrypt or similar
2. **Remember Me**: Persistent sessions
3. **Password Reset**: Email-based password recovery
4. **User Profile**: Edit user information
5. **PatientDashboard**: Complete patient interface
6. **Admin Panel**: User management interface
7. **Role Permissions**: Fine-grained access control
