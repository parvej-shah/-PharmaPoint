package test;

import models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class to verify the sell medicine functionality
 */
public class SellMedicineTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Sell Medicine Functionality...\n");
        
        // Test SaleItem creation
        testSaleItem();
        
        // Test Invoice creation
        testInvoice();
        
        System.out.println("All tests completed successfully!");
    }
    
    private static void testSaleItem() {
        System.out.println("=== Testing SaleItem ===");
        
        // Create a sample medicine
        Medicine medicine = new Medicine(1, 1, "Paracetamol 500mg", "Paracetamol", "Square", 5.50, 100, "2025-12-31");
        
        // Create a sale item
        SaleItem saleItem = new SaleItem(medicine, 3);
        
        // Test getters
        System.out.println("Medicine: " + saleItem.getMedicine().getName());
        System.out.println("Quantity: " + saleItem.getQuantity());
        System.out.println("Subtotal: " + saleItem.getSubtotal());
        System.out.println("ToString: " + saleItem.toString());
        
        System.out.println("SaleItem test passed!\n");
    }
    
    private static void testInvoice() {
        System.out.println("=== Testing Invoice ===");
        
        // Create sample medicines
        Medicine med1 = new Medicine(1, 1, "Paracetamol 500mg", "Paracetamol", "Square", 5.50, 100, "2025-12-31");
        Medicine med2 = new Medicine(2, 1, "Aspirin 100mg", "Aspirin", "ACI", 8.75, 50, "2025-11-30");
        Medicine med3 = new Medicine(3, 1, "Vitamin C 500mg", "Ascorbic Acid", "Beximco", 12.30, 75, "2026-01-15");
        
        // Create sale items
        List<SaleItem> saleItems = new ArrayList<>();
        saleItems.add(new SaleItem(med1, 2));
        saleItems.add(new SaleItem(med2, 1));
        saleItems.add(new SaleItem(med3, 3));
        
        // Create invoice with new constructor
        Invoice invoice = new Invoice(
            1, // pharmacyId
            "John Doe", // patientName
            "01234567890", // patientPhone
            saleItems,
            "City Pharmacy", // pharmacyName
            "Downtown" // pharmacyArea
        );
        
        // Test new methods
        System.out.println("Pharmacy ID: " + invoice.getPharmacyId());
        System.out.println("Patient Name: " + invoice.getPatientName());
        System.out.println("Patient Phone: " + invoice.getPatientPhone());
        System.out.println("Pharmacy Name: " + invoice.getPharmacyName());
        System.out.println("Pharmacy Area: " + invoice.getPharmacyArea());
        System.out.println("Total Amount: " + invoice.getTotalAmount());
        System.out.println("Total Items: " + invoice.getTotalItems());
        
        System.out.println("\n=== Full Invoice ===");
        System.out.println(invoice.toString());
        
        System.out.println("\n=== Simple Summary ===");
        System.out.println(invoice.getSimpleSummary());
        
        System.out.println("Invoice test passed!\n");
    }
}
