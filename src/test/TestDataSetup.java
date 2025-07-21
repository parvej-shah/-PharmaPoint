package test;

import dao.DatabaseInitializer;
import models.User;
import models.Pharmacy;
import services.UserService;
import services.PharmacyService;

/**
 * TestDataSetup - Creates test data for demonstrating the login system
 */
public class TestDataSetup {
    
    public static void main(String[] args) {
        // Initialize database
        DatabaseInitializer.initialize();
        
        UserService userService = new UserService();
        PharmacyService pharmacyService = new PharmacyService();
        
        // Create test pharmacy user
        User pharmacyUser = new User("John Pharmacy", "pharmacy@test.com", "test123", "1985-06-15", "pharmacy");
        
        if (userService.registerUser(pharmacyUser)) {
            System.out.println("✅ Test pharmacy user created successfully!");
            System.out.println("   Email: pharmacy@test.com");
            System.out.println("   Password: test123");
            System.out.println("   Role: pharmacy");
            
            // Get the created user to get the ID
            User createdUser = userService.getUserByEmail("pharmacy@test.com");
            if (createdUser != null) {
                // Create a pharmacy for this user
                Pharmacy pharmacy = new Pharmacy(createdUser.getId(), "Test Pharmacy", 
                                               "123 Main Street, Downtown", "Downtown Area");
                
                if (pharmacyService.registerPharmacy(pharmacy)) {
                    System.out.println("✅ Test pharmacy created successfully!");
                    System.out.println("   Name: Test Pharmacy");
                    System.out.println("   Address: 123 Main Street, Downtown");
                    System.out.println("   Area: Downtown Area");
                }
            }
        } else {
            System.out.println("❌ Failed to create test pharmacy user (might already exist)");
        }
        
        // Create test patient user
        User patientUser = new User("Jane Patient", "patient@test.com", "test123", "1990-03-20", "patient");
        
        if (userService.registerUser(patientUser)) {
            System.out.println("✅ Test patient user created successfully!");
            System.out.println("   Email: patient@test.com");
            System.out.println("   Password: test123");
            System.out.println("   Role: patient");
        } else {
            System.out.println("❌ Failed to create test patient user (might already exist)");
        }
        
        System.out.println("\n🎯 Test data setup complete!");
        System.out.println("You can now test the login system with the created users.");
    }
}
