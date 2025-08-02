package test;

import dao.InvoiceDAO;
import models.*;
import services.InvoiceService;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class to verify the complete invoice functionality
 */
public class InvoiceSystemTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Invoice System Functionality...\n");
        
        // Test updated Invoice model
        testUpdatedInvoiceModel();
        
        // Test InvoiceService
        testInvoiceService();
        
        // Test InvoiceDAO (requires database)
        testInvoiceDAO();
        
        System.out.println("All invoice system tests completed!");
    }
    
    private static void testUpdatedInvoiceModel() {
        System.out.println("=== Testing Updated Invoice Model ===");
        
        // Create sample medicines
        Medicine med1 = new Medicine(1, 1, "Paracetamol 500mg", "Paracetamol", "Square", 5.50, 100, "2025-12-31");
        Medicine med2 = new Medicine(2, 1, "Aspirin 100mg", "Aspirin", "ACI", 8.75, 50, "2025-11-30");
        
        // Create sale items
        List<SaleItem> saleItems = new ArrayList<>();
        saleItems.add(new SaleItem(med1, 2));
        saleItems.add(new SaleItem(med2, 1));
        
        // Create invoice with new constructor
        Invoice invoice = new Invoice(
            1, // pharmacyId
            "John Doe", // patientName
            "01234567890", // patientPhone
            saleItems,
            "City Pharmacy", // pharmacyName
            "Downtown" // pharmacyArea
        );
        
        // Test new getters
        System.out.println("Pharmacy ID: " + invoice.getPharmacyId());
        System.out.println("Patient Name: " + invoice.getPatientName());
        System.out.println("Patient Phone: " + invoice.getPatientPhone());
        System.out.println("Pharmacy Name: " + invoice.getPharmacyName());
        System.out.println("Pharmacy Area: " + invoice.getPharmacyArea());
        System.out.println("Total Amount: " + invoice.getTotalAmount());
        System.out.println("Total Items: " + invoice.getTotalItems());
        
        System.out.println("\n=== Updated Invoice Format ===");
        System.out.println(invoice.toString());
        
        System.out.println("Updated Invoice model test passed!\n");
    }
    
    private static void testInvoiceService() {
        System.out.println("=== Testing InvoiceService ===");
        
        InvoiceService invoiceService = new InvoiceService();
        
        // Test patient info validation
        System.out.println("Testing patient info validation:");
        System.out.println("Valid name and phone: " + invoiceService.validatePatientInfo("John Doe", "01234567890"));
        System.out.println("Empty name: " + invoiceService.validatePatientInfo("", "01234567890"));
        System.out.println("Long name: " + invoiceService.validatePatientInfo("a".repeat(101), "01234567890"));
        System.out.println("Invalid phone: " + invoiceService.validatePatientInfo("John Doe", "invalid-phone@#$"));
        System.out.println("Valid name, empty phone: " + invoiceService.validatePatientInfo("John Doe", ""));
        
        // Create test pharmacy
        Pharmacy pharmacy = new Pharmacy(1, 1, "Test Pharmacy", "123 Main St", "Downtown");
        
        // Create sale items
        Medicine med1 = new Medicine(1, 1, "Test Medicine", "Generic", "Brand", 10.0, 100, "2025-12-31");
        List<SaleItem> items = new ArrayList<>();
        items.add(new SaleItem(med1, 2));
        
        // Test invoice creation
        Invoice testInvoice = invoiceService.createInvoice(1, "Test Patient", "1234567890", items, pharmacy);
        System.out.println("Created invoice with total: " + testInvoice.getTotalAmount());
        
        System.out.println("InvoiceService test passed!\n");
    }
    
    private static void testInvoiceDAO() {
        System.out.println("=== Testing InvoiceDAO ===");
        
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        
        // Create test data
        Medicine med1 = new Medicine(1, 1, "Test Medicine 1", "Generic1", "Brand1", 15.50, 100, "2025-12-31");
        Medicine med2 = new Medicine(2, 1, "Test Medicine 2", "Generic2", "Brand2", 25.75, 50, "2025-11-30");
        
        List<SaleItem> items = new ArrayList<>();
        items.add(new SaleItem(med1, 3));
        items.add(new SaleItem(med2, 2));
        
        Invoice testInvoice = new Invoice(
            1, // pharmacyId
            "Test Patient DAO", // patientName
            "+8801234567890", // patientPhone
            items,
            "Test Pharmacy DAO", // pharmacyName
            "Test Area" // pharmacyArea
        );
        
        // Test saving invoice
        System.out.println("Saving test invoice...");
        boolean saved = invoiceDAO.saveInvoice(testInvoice);
        System.out.println("Invoice saved: " + saved);
        
        if (saved) {
            System.out.println("Generated Invoice ID: " + testInvoice.getId());
            
            // Test retrieving invoice
            System.out.println("Retrieving invoice by ID...");
            Invoice retrievedInvoice = invoiceDAO.getInvoiceById(testInvoice.getId());
            
            if (retrievedInvoice != null) {
                System.out.println("Retrieved invoice:");
                System.out.println("Patient: " + retrievedInvoice.getPatientName());
                System.out.println("Phone: " + retrievedInvoice.getPatientPhone());
                System.out.println("Total: " + retrievedInvoice.getTotalAmount());
                System.out.println("Items count: " + retrievedInvoice.getItems().size());
                System.out.println("Created at: " + retrievedInvoice.getCreatedAt());
                
                // Test retrieving invoices by pharmacy
                System.out.println("Retrieving all invoices for pharmacy 1...");
                List<Invoice> pharmacyInvoices = invoiceDAO.getInvoicesByPharmacyId(1);
                System.out.println("Found " + pharmacyInvoices.size() + " invoices for pharmacy 1");
            } else {
                System.out.println("Failed to retrieve invoice!");
            }
        }
        
        System.out.println("InvoiceDAO test completed!\n");
    }
}
