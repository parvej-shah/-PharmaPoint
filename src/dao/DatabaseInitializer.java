package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            if (connection == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            Statement statement = connection.createStatement();

            //User table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    dateOfBirth TEXT,
                    role TEXT NOT NULL
                );
            """);

            // Pharmacy table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS pharmacies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    address TEXT,
                    area TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """);

            // medicine table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS medicines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    pharmacy_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    generic_name TEXT,
                    brand TEXT,
                    price REAL NOT NULL,
                    quantity INTEGER NOT NULL,
                    expiry_date TEXT,
                    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id)
                );
        """);


            System.out.println("Database initialized successfully.");
            statement.close();

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
    }
}
