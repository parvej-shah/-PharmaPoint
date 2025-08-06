package test;

import dao.DatabaseInitializer;

public class InitializeDatabase {
    public static void main(String[] args) {
        System.out.println("Initializing database with new invoice tables...");
        try {
            DatabaseInitializer.initialize();
            System.out.println("Database initialization completed successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
