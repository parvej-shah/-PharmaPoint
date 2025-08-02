package services;

import dao.InvoiceDAO;
import models.Invoice;
import models.SaleItem;
import models.Pharmacy;
import utils.Validator;
import utils.PDFGenerator;

import java.util.List;

public class InvoiceService {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final MedicineService medicineService = new MedicineService();


    public boolean saveInvoice(Invoice invoice) {
        // Simple centralized validation
        String validationError = validateInvoiceForSave(invoice);
        if (!validationError.isEmpty()) {
            System.err.println("Invoice validation failed: " + validationError);
            return false;
        }

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

    /**
     * Saves invoice and generates PDF file
     * @param invoice The invoice to save
     * @return SaveResult containing success status and PDF path
     */
    public SaveInvoiceResult saveInvoiceWithPDF(Invoice invoice) {
        // First save the invoice normally
        boolean saved = saveInvoice(invoice);
        if (!saved) {
            return new SaveInvoiceResult(false, null, "Failed to save invoice to database");
        }

        // Generate PDF after successful save
        String pdfPath = PDFGenerator.generateInvoicePDF(invoice);
        if (pdfPath == null) {
            // Invoice was saved but PDF generation failed
            return new SaveInvoiceResult(true, null, "Invoice saved but PDF generation failed");
        }

        return new SaveInvoiceResult(true, pdfPath, "Invoice saved and PDF generated successfully");
    }

    /**
     * Result class for invoice save operations with PDF generation
     */
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

    public String validateInvoiceForSave(Invoice invoice) {
        // Basic null check
        if (invoice == null) {
            return "Invoice cannot be null";
        }

        // Use centralized validator for basic validation
        if (!Validator.isValidInvoiceBasic(invoice)) {
            return "Basic invoice validation failed";
        }

        // Validate patient information
        String nameError = Validator.getPatientNameError(invoice.getPatientName());
        if (!nameError.isEmpty()) {
            return nameError;
        }

        String phoneError = Validator.getPatientPhoneError(invoice.getPatientPhone());
        if (!phoneError.isEmpty()) {
            return phoneError;
        }

        // Validate sale items
        for (SaleItem item : invoice.getItems()) {
            if (!Validator.isValidSaleItem(item)) {
                return "Invalid sale item found";
            }
            
            // Check stock availability using simplified validator
            String stockError = Validator.getMedicineStockError(item.getMedicine(), item.getQuantity());
            if (!stockError.isEmpty()) {
                return stockError;
            }
        }

        return ""; // No errors
    }

    /**
     * Creates a new invoice object
     * @param pharmacyId The pharmacy ID
     * @param patientName The patient name
     * @param patientPhone The patient phone (optional)
     * @param items The sale items
     * @param pharmacy The pharmacy object for name and area
     * @return Invoice object
     */
    public Invoice createInvoice(int pharmacyId, String patientName, String patientPhone, 
                                List<SaleItem> items, Pharmacy pharmacy) {
        String pharmacyName = pharmacy != null ? pharmacy.getName() : "Unknown Pharmacy";
        String pharmacyArea = pharmacy != null ? pharmacy.getArea() : "Unknown Area";
        
        return new Invoice(pharmacyId, patientName, patientPhone, items, pharmacyName, pharmacyArea);
    }

    /**
     * Retrieves an invoice by ID
     * @param invoiceId The invoice ID
     * @return Invoice object or null if not found
     */
    public Invoice getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("Invalid invoice ID");
            return null;
        }
        
        return invoiceDAO.getInvoiceById(invoiceId);
    }

    /**
     * Retrieves all invoices for a pharmacy
     * @param pharmacyId The pharmacy ID
     * @return List of invoices
     */
    public List<Invoice> getInvoicesByPharmacyId(int pharmacyId) {
        if (pharmacyId <= 0) {
            System.err.println("Invalid pharmacy ID");
            return List.of(); // Return empty list
        }
        
        return invoiceDAO.getInvoicesByPharmacyId(pharmacyId);
    }

    /**
     * Deletes an invoice
     * @param invoiceId The invoice ID
     * @return true if successful, false otherwise
     */
    public boolean deleteInvoice(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("Invalid invoice ID");
            return false;
        }
        
        return invoiceDAO.deleteInvoice(invoiceId);
    }

    /**
     * Validates patient information (SIMPLIFIED)
     * @param patientName The patient name
     * @param patientPhone The patient phone
     * @return true if valid, false otherwise
     */
    public boolean validatePatientInfo(String patientName, String patientPhone) {
        return Validator.isValidPatientName(patientName) && Validator.isValidPatientPhone(patientPhone);
    }

    /**
     * Gets detailed validation error message for patient info
     * @param patientName The patient name
     * @param patientPhone The patient phone
     * @return Error message or empty string if valid
     */
    public String getPatientValidationError(String patientName, String patientPhone) {
        String nameError = Validator.getPatientNameError(patientName);
        if (!nameError.isEmpty()) {
            return nameError;
        }
        
        String phoneError = Validator.getPatientPhoneError(patientPhone);
        if (!phoneError.isEmpty()) {
            return phoneError;
        }
        
        return "";
    }

    /**
     * Calculates total sales for a pharmacy in a given period
     * @param pharmacyId The pharmacy ID
     * @return Total sales amount
     */
    public double getTotalSales(int pharmacyId) {
        List<Invoice> invoices = getInvoicesByPharmacyId(pharmacyId);
        return invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
    }

    /**
     * Gets the count of invoices for a pharmacy
     * @param pharmacyId The pharmacy ID
     * @return Number of invoices
     */
    public int getInvoiceCount(int pharmacyId) {
        List<Invoice> invoices = getInvoicesByPharmacyId(pharmacyId);
        return invoices.size();
    }
}
