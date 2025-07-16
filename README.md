# PharmaPoint

## 📋 Application Architecture Overview

This project follows a **layered architecture** to ensure modularity, maintainability, and scalability. The application is structured into four main layers: UI, Service, DAO, and Database.

## 🏗️ Architecture Diagram

```
┌─────────────────────┐
│   UI Layer          │
│   (Swing Forms)     │
└─────────────────────┘
          ⇅
┌─────────────────────┐
│   Service Layer     │
│   (Business Logic)  │
└─────────────────────┘
          ⇅
┌─────────────────────┐
│   DAO Layer         │
│   (SQL Queries)     │
└─────────────────────┘
          ⇅
┌─────────────────────┐
│   Database          │
│   (MySQL)           │
└─────────────────────┘
```

## 📚 Layer Descriptions

### 🎨 UI Layer (Swing Forms)
Provides the user interface using Java Swing components for user interaction and data display.

### ⚙️ Service Layer (Business Logic)
Encapsulates the application's core business logic, processing data between the UI and DAO layers.

### 🗃️ DAO Layer (SQL Queries)
Handles data access operations, executing SQL queries to interact with the MySQL database.

### 🗄️ Database (MySQL)
Stores the application's persistent data, accessed via the DAO layer.

---

> **Note:** The bidirectional arrows (⇅) represent the flow of data and requests between layers, ensuring clear separation of concerns.
