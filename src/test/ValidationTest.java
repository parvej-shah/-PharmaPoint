package test;

import models.*;
import services.InvoiceService;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class to verify the centralized validation system
 */
public class ValidationTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Centralized Validation System...\n");
        
        testPatientValidation();
        testMedicineValidation();
        testInvoiceValidation();
        testErrorMessages();
        
        System.out.println("All validation tests completed!");
    }
    
    private static void testPatientValidation() {
        System.out.println("=== Testing Patient Validation ===");
        
        // Valid cases
        System.out.println("Valid name 'John Doe': " + Validator.isValidPatientName("John Doe"));
        System.out.println("Valid phone '01712345678': " + Validator.isValidPatientPhone("01712345678"));
        System.out.println("Empty phone (optional): " + Validator.isValidPatientPhone(""));
        System.out.println("Phone with spaces '+880 171 234 5678': " + Validator.isValidPatientPhone("+880 171 234 5678"));
        
        // Invalid cases
        System.out.println("Invalid name 'A': " + Validator.isValidPatientName("A")); // Too short
        System.out.println("Invalid name empty: " + Validator.isValidPatientName(""));
        System.out.println("Invalid phone 'abc123': " + Validator.isValidPatientPhone("abc123"));
        
        System.out.println("Patient validation test passed!\n");
    }
    
    private static void testMedicineValidation() {
        System.out.println("=== Testing Medicine Validation ===");
        
        // Valid medicine
        Medicine validMedicine = new Medicine(1, 1, "Paracetamol", "Paracetamol", "Square", 10.0, 50, "2025-12-31");
        System.out.println("Valid medicine: " + Validator.isValidMedicineForSale(validMedicine));
        System.out.println("Stock available (50 requested 10): " + Validator.hasStockAvailable(validMedicine, 10));
        
        // Invalid medicine
        Medicine invalidMedicine = new Medicine(0, 1, "", "Paracetamol", "Square", 0.0, 50, "2025-12-31");
        System.out.println("Invalid medicine (ID=0, name empty, price=0): " + Validator.isValidMedicineForSale(invalidMedicine));
        
        // Out of stock
        Medicine outOfStock = new Medicine(1, 1, "Aspirin", "Aspirin", "ACI", 10.0, 5, "2025-12-31");
        System.out.println("Stock available (5 requested 10): " + Validator.hasStockAvailable(outOfStock, 10));
        
        System.out.println("Medicine validation test passed!\n");
    }
    
    private static void testInvoiceValidation() {
        System.out.println("=== Testing Invoice Validation ===");
        
        // Create valid invoice
        Medicine medicine = new Medicine(1, 1, "Test Med", "Generic", "Brand", 10.0, 100, "2025-12-31");
        SaleItem item = new SaleItem(medicine, 2);
        List<SaleItem> items = new ArrayList<>();
        items.add(item);
        
        Invoice validInvoice = new Invoice(1, "John Doe", "01712345678", items, "Test Pharmacy", "Area");
        
        System.out.println("Valid invoice: " + Validator.isValidInvoiceBasic(validInvoice));
        System.out.println("Valid sale item: " + Validator.isValidSaleItem(item));
        
        // Test with InvoiceService
        InvoiceService service = new InvoiceService();
        String validationError = service.validateInvoiceForSave(validInvoice);
        System.out.println("Validation error (should be empty): '" + validationError + "'");
        
        System.out.println("Invoice validation test passed!\n");
    }
    
    private static void testErrorMessages() {
        System.out.println("=== Testing Error Messages ===");
        
        // Patient name errors
        System.out.println("Empty name error: " + Validator.getPatientNameError(""));
        System.out.println("Short name error: " + Validator.getPatientNameError("A"));
        System.out.println("Long name error: " + Validator.getPatientNameError("A".repeat(101)));
        System.out.println("Valid name error: " + Validator.getPatientNameError("John Doe"));
        
        // Phone errors
        System.out.println("Invalid phone error: " + Validator.getPatientPhoneError("abc123"));
        System.out.println("Valid phone error: " + Validator.getPatientPhoneError("01712345678"));
        
        // Medicine stock errors
        Medicine medicine = new Medicine(1, 1, "Test Med", "Generic", "Brand", 10.0, 5, "2025-12-31");
        System.out.println("Stock error: " + Validator.getMedicineStockError(medicine, 10));
        System.out.println("No stock error: " + Validator.getMedicineStockError(medicine, 3));
        
        System.out.println("Error messages test passed!\n");
    }
}
