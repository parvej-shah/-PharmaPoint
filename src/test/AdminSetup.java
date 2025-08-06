package test;

import dao.DatabaseInitializer;
import dao.UserDAO;
import models.User;

/**
 * AdminSetup - Create an admin user in the database
 */
public class AdminSetup {
    public static void main(String[] args) {
        System.out.println("=== Admin User Setup ===\n");
        
        try {
            // Initialize database
            DatabaseInitializer.initialize();
            System.out.println("Database initialized successfully.\n");
            
            // Create admin user
            UserDAO userDAO = new UserDAO();
            
            // Check if admin already exists
            User existingAdmin = userDAO.findByEmail("admin@pharmapoint.com");
            if (existingAdmin != null) {
                System.out.println("Admin user already exists:");
                System.out.println("Email: " + existingAdmin.getEmail());
                System.out.println("Name: " + existingAdmin.getName());
                System.out.println("Role: " + existingAdmin.getRole());
                return;
            }
            
            // Create new admin user
            User adminUser = new User(
                "System Administrator",
                "admin@pharmapoint.com", 
                "admin123", // Password: admin123
                "1990-01-01",
                "admin"
            );
            
            boolean saved = userDAO.save(adminUser);
            
            if (saved) {
                System.out.println("✅ Admin user created successfully!");
                System.out.println("Email: admin@pharmapoint.com");
                System.out.println("Password: admin123");
                System.out.println("Role: admin");
                System.out.println("\nYou can now login as admin to access the admin dashboard.");
            } else {
                System.out.println("❌ Failed to create admin user.");
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
