# 🗄️ PharmaPoint Database Design

## 📋 Overview

This document describes the database architecture for the **Medicine Finder & Inventory System** designed for private pharmacies. The system uses a **centralized relational database** to store user data, pharmacy profiles, medicine catalog, inventory stock, and patient prescriptions.

---

## 🏗️ Core Entities and Relationships

```
┌────────────────┐          ┌──────────────────┐          ┌────────────────────┐
│    users       │1        1│    pharmacies    │1        *│     inventory      │
│────────────────│──────────│──────────────────│──────────│────────────────────│
│ id (PK)        │          │ id (PK)          │          │ id (PK)            │
│ name           │          │ user_id (FK)     │          │ pharmacy_id (FK)   │
│ email          │          │ name             │          │ medicine_id (FK)   │
│ password       │          │ area             │          │ quantity           │
│ role           │          │ address          │          │ expiry_date        │
└────────────────┘          └──────────────────┘          └────────────────────┘
                                     │                                      │
                                     │                                      │
                                     │                                      │
                                     │                                      │
                             ┌──────────────────┐                   ┌─────────────────┐
                             │    medicines     │                   │  prescriptions  │
                             │──────────────────│                   │─────────────────│
                             │ id (PK)          │                   │ id (PK)         │
                             │ name             │                   │ user_id (FK)    │
                             │ type             │                   │ date            │
                             │ manufacturer     │                   └─────────────────┘
                             └──────────────────┘                            │
                                                                         1   │   
                                                                         │   * 
                                                             ┌──────────────────────────┐
                                                             │ prescription_items       │
                                                             │──────────────────────────│
                                                             │ id (PK)                  │
                                                             │ prescription_id (FK)     │
                                                             │ medicine_name            │
                                                             │ dose                     │
                                                             │ frequency                │
                                                             └──────────────────────────┘
```

---

## 📚 Table Descriptions

| Table                     | Description                                                       |
|---------------------------|-------------------------------------------------------------------|
| **users**                 | Stores both patients and pharmacy user credentials and roles      |
| **pharmacies**            | Pharmacy profile linked to a user                                |
| **medicines**             | Master list of medicines available in the system                 |
| **inventory**             | Tracks medicine stock per pharmacy, including quantity and expiry |
| **prescriptions**         | Records patient prescription submissions                          |
| **prescription_items**    | Details individual medicines within a prescription                |

---

## 🔗 Relationships Between Tables

### 1. **`users` ↔ `pharmacies`**
- **One-to-One (1:1)**
- Each pharmacy is **linked to one user account** (i.e., login credential).
- Each user **may or may not** be a pharmacy (users can also be patients).

**Example:**
```
User ID = 5 → owns Pharmacy ID = 2
→ Ensures pharmacy can log in, manage its data only.
```

---

### 2. **`users` ↔ `prescriptions`**
- **One-to-Many (1:N)**
- A single patient user can **submit multiple prescriptions** over time.
- Each prescription is **linked to the user who submitted it**.

**Example:**
```
User ID = 8 → Prescription ID = 101, 102, 109
→ Tracks patient history.
```

---

### 3. **`prescriptions` ↔ `prescription_items`**
- **One-to-Many (1:N)**
- Each prescription can contain **multiple medicines** (items).
- `prescription_items` stores medicine name, dosage, and frequency.

**Example:**
```
Prescription ID = 102 → 3 rows in `prescription_items`
→ Paracetamol, Napa, Losectil
```

---

### 4. **`pharmacies` ↔ `inventory`**
- **One-to-Many (1:N)**
- A pharmacy manages **many medicines** in its inventory.
- Each inventory entry records quantity, expiry, and which medicine.

**Example:**
```
Pharmacy ID = 3 → Inventory rows for Napa, Seclo, Losectil
→ Manages stock.
```

---

### 5. **`medicines` ↔ `inventory`**
- **One-to-Many (1:N)**
- Each medicine can exist in the inventory of **multiple pharmacies**.
- One medicine → many stock entries across different pharmacies.

**Example:**
```
Medicine ID = 10 (Napa)
→ Inventory rows for Pharmacy 1, 2, 4
→ Tracks where Napa is available.
```

---

## ⚙️ Summary of Relationships

| Table A            | Table B               | Relationship | Description                                 |
|--------------------|-----------------------|--------------|---------------------------------------------|
| `users`            | `pharmacies`          | 1:1          | Each pharmacy has one user account          |
| `users`            | `prescriptions`       | 1:N          | A patient can submit multiple prescriptions |
| `prescriptions`    | `prescription_items`  | 1:N          | Each prescription has multiple medicines    |
| `pharmacies`       | `inventory`           | 1:N          | A pharmacy can stock many medicines         |
| `medicines`        | `inventory`           | 1:N          | A medicine can appear in many pharmacies    |

---

## 🎯 Design Notes

- **Centralized inventory** allows real-time search and easy aggregation.
- User roles (`patient`, `pharmacy`) restrict access to features accordingly.
- Foreign key constraints maintain data integrity.
- The `inventory` table uses a composite uniqueness on `(pharmacy_id, medicine_id)` to prevent duplicates.
- Prescriptions and prescription items track patient medicine requests for analytics and future demand prediction.

---

## 🔄 Data Flow

1. **User Registration**: Users register with role-based access (patient or pharmacy)
2. **Pharmacy Setup**: Pharmacy users create their profile and link to their user account
3. **Inventory Management**: Pharmacies add medicines to their inventory with stock details
4. **Prescription Submission**: Patients submit prescriptions with multiple medicine items
5. **Medicine Search**: System searches across all pharmacy inventories for medicine availability

---

> **Note:** This database design ensures scalability, data integrity, and efficient querying for the PharmaPoint application's core functionalities.
