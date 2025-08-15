package utils;

import java.awt.*;
import java.io.*;
import java.time.format.DateTimeFormatter;
import models.Invoice;
import models.SaleItem;

public class PDFGenerator {

    public static String generateInvoiceFile(Invoice invoice) {
        try {
            String fileName = String.format("Invoice_%d_%s.txt",    //for example: Invoice_123_20231001_123456.txt
                invoice.getId(), 
                invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            String filePath = "invoices/" + fileName;
            File file = new File(filePath);    //just create an object,not actual file
            
            return createInvoiceFile(invoice, file);    //this creates the actual file
            
        } catch (Exception e) {
            System.err.println("Error generating invoice: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static String createInvoiceFile(Invoice invoice, File outputFile) {
        try {

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
            invoiceContent.append(String.format("Area: %s\n", invoice.getPharmacyArea()));
            
            invoiceContent.append("\n");
            
            // Patient details
            invoiceContent.append("PATIENT DETAILS:\n");
            invoiceContent.append("-".repeat(50)).append("\n");
            invoiceContent.append(String.format("Name: %s\n", invoice.getPatientName()));
            invoiceContent.append(String.format("Phone: %s\n", invoice.getPatientPhone()));
            
            invoiceContent.append("\n");
            
            // Items
            invoiceContent.append("MEDICINES:\n");
            invoiceContent.append("-".repeat(80)).append("\n");
            invoiceContent.append(String.format("%-30s %-15s %-8s %-12s %-12s\n", 
                "Medicine", "Brand", "Qty", "Unit Price", "Subtotal"));
            invoiceContent.append("-".repeat(80)).append("\n");
            
            // Items
            for (SaleItem item : invoice.getItems()) {
                invoiceContent.append(String.format("%-30s %-15s %-8d %-12.2f %-12.2f\n",
                    item.getMedicine().getName(),
                    item.getMedicine().getBrand(),
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
                writer.write(invoiceContent.toString());        //.write() expects string, not stringbuilder
            }
            
            System.out.println("Invoice saved in: " + outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();
            
        } catch (IOException e) {
            System.err.println("Error writing invoice file: " + e.getMessage());
            return null;
        }
    }

    
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

}
