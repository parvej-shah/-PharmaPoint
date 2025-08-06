package services;

import dao.InvoiceDAO;
import models.Invoice;
import models.SaleItem;
import models.Pharmacy;
import utils.PDFGenerator;

import java.util.List;

public class InvoiceService {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final MedicineService medicineService = new MedicineService();


    public boolean saveInvoice(Invoice invoice) {
        // Process the medicine sale (update inventory)
        if (!medicineService.sellMedicines(invoice.getItems())) {
            System.err.println("Failed to update medicine inventory");
            return false;
        }

        // Save invoice to database
        boolean saved = invoiceDAO.saveInvoice(invoice);
        if (!saved) {
            System.err.println("Failed to save invoice to database");
            return false;
        }

        return true;
    }

    public SaveInvoiceResult saveInvoiceWithPDF(Invoice invoice) {
        // First save the invoice normally
        boolean saved = saveInvoice(invoice);
        if (!saved) {
            return new SaveInvoiceResult(false, null, "Failed to save invoice to database");
        }

        // Generate invoice file after successful save
        String invoicePath = PDFGenerator.generateInvoiceFile(invoice);
        if (invoicePath == null) {
            // Invoice was saved but file generation failed
            return new SaveInvoiceResult(true, null, "Invoice saved but file generation failed");
        }

        return new SaveInvoiceResult(true, invoicePath, "Invoice saved and file generated successfully");
    }
    public static class SaveInvoiceResult {
        private final boolean success;
        private final String pdfPath;
        private final String message;

        public SaveInvoiceResult(boolean success, String pdfPath, String message) {
            this.success = success;
            this.pdfPath = pdfPath;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getPdfPath() {
            return pdfPath;
        }

        public String getMessage() {
            return message;
        }

        public boolean hasPdf() {
            return pdfPath != null && !pdfPath.isEmpty();
        }
    }


    public Invoice createInvoice(int pharmacyId, String patientName, String patientPhone, 
                                List<SaleItem> items, Pharmacy pharmacy) {
        String pharmacyName = pharmacy != null ? pharmacy.getName() : "Unknown Pharmacy";
        String pharmacyArea = pharmacy != null ? pharmacy.getArea() : "Unknown Area";
        
        return new Invoice(pharmacyId, patientName, patientPhone, items, pharmacyName, pharmacyArea);
    }

    public Invoice getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("Invalid invoice ID");
            return null;
        }
        
        return invoiceDAO.getInvoiceById(invoiceId);
    }


    public List<Invoice> getInvoicesByPharmacyId(int pharmacyId) {
        if (pharmacyId <= 0) {
            System.err.println("Invalid pharmacy ID");
            return List.of(); // Return empty list
        }
        
        return invoiceDAO.getInvoicesByPharmacyId(pharmacyId);
    }

    public boolean deleteInvoice(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("Invalid invoice ID");
            return false;
        }
        
        return invoiceDAO.deleteInvoice(invoiceId);
    }


    public double getTotalSales(int pharmacyId) {
        List<Invoice> invoices = getInvoicesByPharmacyId(pharmacyId);
        return invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
    }
}
