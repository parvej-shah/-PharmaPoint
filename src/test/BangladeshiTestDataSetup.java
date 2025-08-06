package test;

import dao.DatabaseInitializer;
import dao.UserDAO;
import dao.PharmacyDAO;
import dao.MedicineDAO;
import models.User;
import models.Pharmacy;
import models.Medicine;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BangladeshiTestDataSetup {
    
    private UserDAO userDAO = new UserDAO();
    private PharmacyDAO pharmacyDAO = new PharmacyDAO();
    private MedicineDAO medicineDAO = new MedicineDAO();
    
    public static void main(String[] args) {
        BangladeshiTestDataSetup setup = new BangladeshiTestDataSetup();
        
        try {
            // Initialize database
            DatabaseInitializer.initialize();
            System.out.println("Database initialized successfully!");
            
            // Create test data
            setup.createTestUsers();
            setup.createTestPharmacies();
            setup.addTestMedicines();
            
            System.out.println("\n=== Test Data Created Successfully! ===");
            System.out.println("You can now test the Patient Medicine Search feature!");
            System.out.println("Login with any of the patient credentials and search for medicines.");
            
            // Print credentials for easy testing
            setup.printTestCredentials();
            
        } catch (Exception e) {
            System.err.println("Error setting up test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTestUsers() {
        System.out.println("Creating test users...");
        
        // Create 5 pharmacists (Modern Bangladeshi names in Banglish)
        List<User> pharmacists = Arrays.asList(
            new User("Abdul Karim Bhai", "karim.pharmacy@gmail.com", "password123", "1985-03-15", "pharmacist"),
            new User("Rahima Khatun", "rahima.medicine@yahoo.com", "password123", "1990-07-22", "pharmacist"),
            new User("Nurul Islam Mia", "nurul.pharma@hotmail.com", "password123", "1982-11-08", "pharmacist"),
            new User("Salma Begum", "salma.health@gmail.com", "password123", "1988-05-12", "pharmacist"),
            new User("Md. Rafiq Uddin", "rafiq.medical@outlook.com", "password123", "1975-09-30", "pharmacist")
        );
        
        // Create 5 patients (Modern Bangladeshi names in Banglish)
        List<User> patients = Arrays.asList(
            new User("Amina Khatun", "amina.patient@gmail.com", "patient123", "1995-01-20", "patient"),
            new User("Md. Saiful Islam", "saiful.dhaka@yahoo.com", "patient123", "1987-06-14", "patient"),
            new User("Rashida Begum", "rashida.old@hotmail.com", "patient123", "1992-04-03", "patient"),
            new User("Kamal Hossain", "kamal.patient@gmail.com", "patient123", "1980-12-25", "patient"),
            new User("Fatema Sultana", "fatema.dhaka@outlook.com", "patient123", "1998-08-17", "patient")
        );
        
        // Add pharmacists
        for (User pharmacist : pharmacists) {
            if (userDAO.save(pharmacist)) {
                System.out.println("✓ Created pharmacist: " + pharmacist.getName());
            } else {
                System.out.println("✗ Failed to create pharmacist: " + pharmacist.getName());
            }
        }
        
        // Add patients
        for (User patient : patients) {
            if (userDAO.save(patient)) {
                System.out.println("✓ Created patient: " + patient.getName());
            } else {
                System.out.println("✗ Failed to create patient: " + patient.getName());
            }
        }
    }
    
    private void createTestPharmacies() {
        System.out.println("\nCreating test pharmacies...");
        
        // Get pharmacist user IDs
        List<User> allUsers = userDAO.findAll();
        
        // Create 5 pharmacies with modern Bangladeshi names and areas in Banglish
        String[][] pharmacyData = {
            {"Karim Pharmacy", "123 New Market, Dhaka", "New Market"},
            {"Rahima Medicine Corner", "456 Dhanmondi 27, Dhaka", "Dhanmondi"},
            {"Nurul Health Care", "789 Gulshan-2, Dhaka", "Gulshan"},
            {"Salma Drug House", "321 Banani, Dhaka", "Banani"},
            {"Rafiq Medical Store", "654 Uttara Sector-7, Dhaka", "Uttara"}
        };
        
        int pharmacistIndex = 0;
        for (User user : allUsers) {
            if ("pharmacist".equals(user.getRole()) && pharmacistIndex < pharmacyData.length) {
                String[] data = pharmacyData[pharmacistIndex];
                Pharmacy pharmacy = new Pharmacy(user.getId(), data[0], data[1], data[2]);
                
                if (pharmacyDAO.addPharmacy(pharmacy)) {
                    System.out.println("✓ Created pharmacy: " + data[0] + " in " + data[2]);
                } else {
                    System.out.println("✗ Failed to create pharmacy: " + data[0]);
                }
                pharmacistIndex++;
            }
        }
    }
    
    private void addTestMedicines() {
        System.out.println("\nAdding test medicines...");
        
        // Get all pharmacies
        List<Pharmacy> pharmacies = pharmacyDAO.getPharmacies();
        
        // Common medicines in Bangladesh
        String[][] medicineData = {
            // Name, Generic, Brand, Price
            {"Paracetamol", "Paracetamol", "Napa", "2.50"},
            {"Omeprazole", "Omeprazole", "Losec", "8.00"},
            {"Metformin", "Metformin", "Diabex", "5.50"},
            {"Amlodipine", "Amlodipine", "Amdocal", "12.00"},
            {"Atorvastatin", "Atorvastatin", "Atorva", "15.00"},
            {"Azithromycin", "Azithromycin", "Azithrocin", "18.50"},
            {"Cetirizine", "Cetirizine", "Cetrin", "3.00"},
            {"Salbutamol", "Salbutamol", "Ventolin", "25.00"},
            {"Vitamin C", "Ascorbic Acid", "C-Vit", "4.50"},
            {"Calcium", "Calcium Carbonate", "Calbo", "6.00"},
            {"Iron", "Ferrous Sulfate", "Fefol", "7.50"},
            {"Multivitamin", "Multivitamin", "A-Z", "12.50"},
            {"Aspirin", "Acetylsalicylic Acid", "Ecosprin", "3.50"},
            {"Ibuprofen", "Ibuprofen", "Brufen", "4.00"},
            {"Ranitidine", "Ranitidine", "Rantac", "5.00"}
        };
        
        // Add medicines to each pharmacy with different availability patterns
        for (int i = 0; i < pharmacies.size(); i++) {
            Pharmacy pharmacy = pharmacies.get(i);
            System.out.println("\nAdding medicines to: " + pharmacy.getName());
            
            // Each pharmacy gets a different set of medicines for testing
            int[] medicineIndices = getMedicineIndicesForPharmacy(i);
            
            for (int medicineIndex : medicineIndices) {
                String[] data = medicineData[medicineIndex];
                
                // Calculate expiry date (1-2 years from now)
                LocalDate expiryDate = LocalDate.now().plusMonths(12 + (medicineIndex % 12));
                
                // Random quantity between 10-100
                int quantity = 10 + (medicineIndex * 7) % 91;
                
                Medicine medicine = new Medicine(
                    pharmacy.getId(),
                    data[0],                    // name
                    data[1],                    // generic name
                    data[2],                    // brand
                    Double.parseDouble(data[3]), // price
                    quantity,                   // quantity
                    expiryDate.toString()       // expiry date
                );
                
                if (medicineDAO.addMedicine(medicine)) {
                    System.out.println("  ✓ Added: " + data[0] + " (" + data[2] + ") - Qty: " + quantity);
                } else {
                    System.out.println("  ✗ Failed to add: " + data[0]);
                }
            }
        }
        
        System.out.println("\n=== Medicine Distribution Summary ===");
        System.out.println("Karim Pharmacy: Common medicines (Paracetamol, Omeprazole, Metformin, etc.)");
        System.out.println("Rahima Medicine: Heart & diabetes medicines (Amlodipine, Atorvastatin, etc.)");
        System.out.println("Nurul Health Care: Antibiotics & vitamins (Azithromycin, Vitamin C, etc.)");
        System.out.println("Salma Drug House: Pain relief & supplements (Aspirin, Ibuprofen, Calcium, etc.)");
        System.out.println("Rafiq Medical: Mixed medicines with some overlaps");
    }
    
    private int[] getMedicineIndicesForPharmacy(int pharmacyIndex) {
        // Different medicine sets for each pharmacy to test search functionality
        switch (pharmacyIndex) {
            case 0: // Karim Pharmacy - Common medicines
                return new int[]{0, 1, 2, 3, 6, 8, 12}; // Paracetamol, Omeprazole, Metformin, Amlodipine, Cetirizine, Vitamin C, Aspirin
            case 1: // Rahima Medicine - Heart & diabetes
                return new int[]{2, 3, 4, 9, 11, 14}; // Metformin, Amlodipine, Atorvastatin, Calcium, Multivitamin, Ranitidine
            case 2: // Nurul Health Care - Antibiotics & vitamins
                return new int[]{0, 5, 6, 8, 9, 10, 11}; // Paracetamol, Azithromycin, Cetirizine, Vitamin C, Calcium, Iron, Multivitamin
            case 3: // Salma Drug House - Pain relief & supplements
                return new int[]{0, 9, 10, 12, 13, 14}; // Paracetamol, Calcium, Iron, Aspirin, Ibuprofen, Ranitidine
            case 4: // Rafiq Medical - Mixed with overlaps
                return new int[]{1, 4, 7, 8, 11, 13}; // Omeprazole, Atorvastatin, Salbutamol, Vitamin C, Multivitamin, Ibuprofen
            default:
                return new int[]{0, 1, 2}; // Default case
        }
    }
    
    public void printTestCredentials() {
        System.out.println("\n=== Test Login Credentials ===");
        System.out.println("\nPatients (for testing search functionality):");
        System.out.println("1. Email: amina.patient@gmail.com | Password: patient123 | Name: Amina Khatun");
        System.out.println("2. Email: saiful.dhaka@yahoo.com | Password: patient123 | Name: Md. Saiful Islam");
        System.out.println("3. Email: rashida.old@hotmail.com | Password: patient123 | Name: Rashida Begum");
        System.out.println("4. Email: kamal.patient@gmail.com | Password: patient123 | Name: Kamal Hossain");
        System.out.println("5. Email: fatema.dhaka@outlook.com | Password: patient123 | Name: Fatema Sultana");
        
        System.out.println("\nPharmacists:");
        System.out.println("1. Email: karim.pharmacy@gmail.com | Password: password123 | Name: Abdul Karim Bhai");
        System.out.println("2. Email: rahima.medicine@yahoo.com | Password: password123 | Name: Rahima Khatun");
        System.out.println("3. Email: nurul.pharma@hotmail.com | Password: password123 | Name: Nurul Islam Mia");
        System.out.println("4. Email: salma.health@gmail.com | Password: password123 | Name: Salma Begum");
        System.out.println("5. Email: rafiq.medical@outlook.com | Password: password123 | Name: Md. Rafiq Uddin");
        
        System.out.println("\n=== Search Test Suggestions ===");
        System.out.println("Try searching for:");
        System.out.println("• 'Paracetamol' - Available in multiple pharmacies");
        System.out.println("• 'Paracetamol + Vitamin C' - Test multiple medicine search");
        System.out.println("• 'Metformin + Amlodipine' - Heart/diabetes medicines");
        System.out.println("• 'Azithromycin + Iron' - Test partial availability");
        System.out.println("• 'Aspirin + Ibuprofen + Calcium' - Pain relief combo");
    }
}
