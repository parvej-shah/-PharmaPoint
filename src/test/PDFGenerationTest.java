package test;

import models.*;
import services.InvoiceService;
import utils.PDFGenerator;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Test class to verify PDF generation functionality
 */
public class PDFGenerationTest {
    
    public static void main(String[] args) {
        System.out.println("=== PDF Generation Test ===");
        
        // Test 1: Check if invoices directory is ready
        System.out.println("\n1. Testing invoices directory...");
        boolean directoryReady = PDFGenerator.isInvoiceDirectoryReady();
        System.out.println("Directory ready: " + directoryReady);
        
        if (!directoryReady) {
            System.err.println("Failed to create/access invoices directory");
            return;
        }
        
        // Test 2: Create a sample invoice
        System.out.println("\n2. Creating sample invoice...");
        Invoice sampleInvoice = createSampleInvoice();
        System.out.println("Sample invoice created with ID: " + sampleInvoice.getId());
        
        // Test 3: Generate invoice file
        System.out.println("\n3. Generating invoice file...");
        String invoicePath = PDFGenerator.generateInvoiceFile(sampleInvoice);
        
        if (invoicePath != null) {
            System.out.println("✓ Invoice file generated successfully!");
            System.out.println("Invoice saved at: " + invoicePath);
            
            File invoiceFile = new File(invoicePath);
            if (invoiceFile.exists()) {
                System.out.println("✓ Invoice file exists and is accessible");
                System.out.println("File size: " + invoiceFile.length() + " bytes");
            } else {
                System.out.println("✗ Invoice file not found");
            }
        } else {
            System.out.println("✗ Invoice file generation failed");
        }
        
        // Test 4: Test legacy method (backward compatibility)
        System.out.println("\n4. Testing legacy method (generateInvoicePDF)...");
        String legacyPath = PDFGenerator.generateInvoicePDF(sampleInvoice);
        
        if (legacyPath != null) {
            System.out.println("✓ Legacy method works correctly!");
            System.out.println("Legacy saved at: " + legacyPath);
            
            // Check if file exists
            File legacyFile = new File(legacyPath);
            if (legacyFile.exists()) {
                System.out.println("✓ Legacy file exists and is accessible");
                System.out.println("File size: " + legacyFile.length() + " bytes");
            } else {
                System.err.println("✗ Legacy file was not created");
            }
        } else {
            System.err.println("✗ Legacy method failed");
        }
        
        // Test 5: Test invoice service with invoice file generation
        System.out.println("\n5. Testing InvoiceService with invoice file generation...");
        InvoiceService invoiceService = new InvoiceService();
        InvoiceService.SaveInvoiceResult result = invoiceService.saveInvoiceWithPDF(sampleInvoice);
        
        System.out.println("Save result success: " + result.isSuccess());
        System.out.println("Save result message: " + result.getMessage());
        System.out.println("Has Invoice File: " + result.hasPdf());
        if (result.hasPdf()) {
            System.out.println("Invoice File path: " + result.getPdfPath());
        }
        
        // Test 6: Check invoice count
        System.out.println("\n6. Checking invoice count...");
        int invoiceCount = PDFGenerator.getInvoiceCount();
        System.out.println("Total invoices in directory: " + invoiceCount);
        
        System.out.println("\n=== PDF Generation Test Complete ===");
    }
    
    private static Invoice createSampleInvoice() {
        // Create sample medicines
        Medicine medicine1 = new Medicine(1, 1, "Paracetamol", "Acetaminophen", "Square", 5.0, 100, "2025-12-31");
        Medicine medicine2 = new Medicine(2, 1, "Aspirin", "Acetylsalicylic Acid", "Bayer", 12.0, 50, "2025-11-30");
        
        // Create sale items
        List<SaleItem> saleItems = new ArrayList<>();
        saleItems.add(new SaleItem(medicine1, 2)); // 2 Paracetamol
        saleItems.add(new SaleItem(medicine2, 1)); // 1 Aspirin
        
        // Create invoice
        Invoice invoice = new Invoice(
            1, // pharmacyId
            "John Doe", // patientName
            "01712345678", // patientPhone
            saleItems,
            "Green Life Pharmacy", // pharmacyName
            "Dhanmondi" // pharmacyArea
        );
        
        // Set a sample ID (normally set by database)
        invoice.setId(12345);
        
        return invoice;
    }
}
