
# PharmaPoint Authentication System (Simple Summary)

PharmaPoint lets users register and log in as either a pharmacist or a patient. The system manages sessions and shows different dashboards based on user role.

---

## How it Works

1. **Register**: Enter name, email, password, date of birth, and role (pharmacist/patient).
2. **Login**: Enter email and password.
3. **On Success**:
   - If pharmacist: Opens Pharmacy Dashboard (manage medicines, inventory, etc.)
   - If patient: Opens Patient Dashboard (search/view pharmacies, coming soon)
4. **On Logout**: Session is cleared, returns to login screen.

---

## Key Features

- Registration and login with validation
- Session management (remembers who is logged in)
- Role-based navigation (pharmacist vs patient)
- Error messages for invalid login/registration
- Test users for quick demo

---

## Test Users

- Pharmacist:  
  Email: pharmacy@test.com  
  Password: test123
- Patient:  
  Email: patient@test.com  
  Password: test123

---

**Note:** Passwords are plain text (for demo only). Session is in-memory. Patient dashboard is a placeholder.
