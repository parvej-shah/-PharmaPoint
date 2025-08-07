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
│      (API)          │
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

## find . -name "*.class" -type f -delete
## javac -d out src/*.java src/dao/*.java src/models/*.java src/services/*.java src/ui/*.java src/utils/*.java && java -cp "out:src/lib/sqlite-jdbc-3.45.3.0.jar:src/lib/slf4j-api-2.0.13.jar" Main