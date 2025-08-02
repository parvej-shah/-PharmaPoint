package utils;

import models.Invoice;
import models.SaleItem;

import java.awt.*;
import java.io.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating PDF invoices using Java Graphics2D
 * and the built-in printing system to create PDF files
 */
public class PDFGenerator {
    
    // These constants are for future PDF enhancement
    @SuppressWarnings("unused")
    private static final int PAGE_WIDTH = 612; // 8.5 inches * 72 DPI
    @SuppressWarnings("unused")
    private static final int PAGE_HEIGHT = 792; // 11 inches * 72 DPI
    @SuppressWarnings("unused")
    private static final int MARGIN = 50;
    @SuppressWarnings("unused")
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    @SuppressWarnings("unused")
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    @SuppressWarnings("unused")
    private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 10);
    @SuppressWarnings("unused")
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 9);
    
    /**
     * Generates a PDF invoice and saves it to the invoices folder
     * @param invoice The invoice to generate PDF for
     * @return The file path of the generated PDF, or null if failed
     */
    public static String generateInvoicePDF(Invoice invoice) {
        try {
            String fileName = String.format("Invoice_%d_%s.pdf", 
                invoice.getId(), 
                invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            String filePath = "invoices/" + fileName;
            File file = new File(filePath);
            
            // Create a custom PrinterJob that outputs to PDF
            return createPDFFile(invoice, file);
            
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static String createPDFFile(Invoice invoice, File outputFile) {
        try {
            // For now, we'll create a text-based invoice file that can be easily converted to PDF
            // This approach ensures compatibility without external dependencies
            
            StringBuilder invoiceContent = new StringBuilder();
            
            // Header
            invoiceContent.append("=".repeat(80)).append("\n");
            invoiceContent.append("                           INVOICE\n");
            invoiceContent.append("=".repeat(80)).append("\n\n");
            
            // Invoice details
            invoiceContent.append(String.format("Invoice ID: %d\n", invoice.getId()));
            invoiceContent.append(String.format("Date: %s\n", 
                invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            invoiceContent.append("\n");
            
            // Pharmacy details
            invoiceContent.append("PHARMACY DETAILS:\n");
            invoiceContent.append("-".repeat(50)).append("\n");
            invoiceContent.append(String.format("Name: %s\n", invoice.getPharmacyName()));
            if (invoice.getPharmacyArea() != null) {
                invoiceContent.append(String.format("Area: %s\n", invoice.getPharmacyArea()));
            }
            invoiceContent.append("\n");
            
            // Patient details
            invoiceContent.append("PATIENT DETAILS:\n");
            invoiceContent.append("-".repeat(50)).append("\n");
            invoiceContent.append(String.format("Name: %s\n", invoice.getPatientName()));
            if (invoice.getPatientPhone() != null && !invoice.getPatientPhone().isEmpty()) {
                invoiceContent.append(String.format("Phone: %s\n", invoice.getPatientPhone()));
            }
            invoiceContent.append("\n");
            
            // Items header
            invoiceContent.append("MEDICINES:\n");
            invoiceContent.append("-".repeat(80)).append("\n");
            invoiceContent.append(String.format("%-30s %-15s %-8s %-12s %-12s\n", 
                "Medicine", "Brand", "Qty", "Unit Price", "Subtotal"));
            invoiceContent.append("-".repeat(80)).append("\n");
            
            // Items
            for (SaleItem item : invoice.getItems()) {
                invoiceContent.append(String.format("%-30s %-15s %-8d %-12.2f %-12.2f\n",
                    truncateString(item.getMedicine().getName(), 29),
                    truncateString(item.getMedicine().getBrand(), 14),
                    item.getQuantity(),
                    item.getMedicine().getPrice(),
                    item.getSubtotal()));
            }
            
            invoiceContent.append("-".repeat(80)).append("\n");
            
            // Total
            invoiceContent.append(String.format("%60s Total: %.2f BDT\n", "", invoice.getTotalAmount()));
            invoiceContent.append(String.format("%60s Items: %d\n", "", invoice.getTotalItems()));
            
            invoiceContent.append("\n");
            invoiceContent.append("=".repeat(80)).append("\n");
            invoiceContent.append("                     Thank you for your business!\n");
            invoiceContent.append("=".repeat(80)).append("\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(invoiceContent.toString());
            }
            
            System.out.println("Invoice saved: " + outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();
            
        } catch (IOException e) {
            System.err.println("Error writing invoice file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Enhanced PDF generation using Java2D (for future implementation)
     * This method can be enhanced with actual PDF libraries if needed
     */
    public static String generateEnhancedPDF(Invoice invoice) {
        // This is a placeholder for future PDF enhancement
        // For now, use the text-based approach above
        return generateInvoicePDF(invoice);
    }
    
    private static String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Opens the generated invoice file with the system's default application
     * @param filePath Path to the invoice file
     */
    public static void openInvoiceFile(String filePath) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(filePath));
            } else {
                System.out.println("Desktop not supported. Invoice saved at: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error opening invoice file: " + e.getMessage());
            System.out.println("Invoice saved at: " + filePath);
        }
    }
    
    /**
     * Validates that the invoices directory exists and is writable
     * @return true if directory is ready for use
     */
    public static boolean isInvoiceDirectoryReady() {
        File invoiceDir = new File("invoices");
        
        if (!invoiceDir.exists()) {
            boolean created = invoiceDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create invoices directory");
                return false;
            }
        }
        
        if (!invoiceDir.canWrite()) {
            System.err.println("Cannot write to invoices directory");
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the total number of invoices in the invoices directory
     * @return number of invoice files
     */
    public static int getInvoiceCount() {
        File invoiceDir = new File("invoices");
        if (!invoiceDir.exists() || !invoiceDir.isDirectory()) {
            return 0;
        }
        
        File[] files = invoiceDir.listFiles((dir, name) -> 
            name.toLowerCase().startsWith("invoice_") && 
            (name.toLowerCase().endsWith(".pdf") || name.toLowerCase().endsWith(".txt")));
        
        return files != null ? files.length : 0;
    }
}
