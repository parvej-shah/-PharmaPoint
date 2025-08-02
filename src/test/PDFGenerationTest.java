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
        
        // Test 3: Generate PDF
        System.out.println("\n3. Generating PDF...");
        String pdfPath = PDFGenerator.generateInvoicePDF(sampleInvoice);
        
        if (pdfPath != null) {
            System.out.println("✓ PDF generated successfully!");
            System.out.println("PDF saved at: " + pdfPath);
            
            // Check if file exists
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                System.out.println("✓ PDF file exists and is accessible");
                System.out.println("File size: " + pdfFile.length() + " bytes");
            } else {
                System.err.println("✗ PDF file was not created");
            }
        } else {
            System.err.println("✗ PDF generation failed");
        }
        
        // Test 4: Test invoice service with PDF
        System.out.println("\n4. Testing InvoiceService with PDF generation...");
        InvoiceService invoiceService = new InvoiceService();
        InvoiceService.SaveInvoiceResult result = invoiceService.saveInvoiceWithPDF(sampleInvoice);
        
        System.out.println("Save result success: " + result.isSuccess());
        System.out.println("Save result message: " + result.getMessage());
        System.out.println("Has PDF: " + result.hasPdf());
        if (result.hasPdf()) {
            System.out.println("PDF path: " + result.getPdfPath());
        }
        
        // Test 5: Check invoice count
        System.out.println("\n5. Checking invoice count...");
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
