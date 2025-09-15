package services;

import dao.InvoiceDAO;
import java.util.List;
import models.Invoice;
import models.Pharmacy;
import models.SaleItem;
import utils.PDFGenerator;

public class InvoiceService {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final MedicineService medicineService = new MedicineService();


    public boolean saveInvoice(Invoice invoice) {

        if (!medicineService.sellMedicines(invoice.getItems())) {           //calling sellMedicines method which will update in the DB 
                                                                            // like reduce the quantity of medicines sold
            System.err.println("Failed to update medicine inventory");
            return false;
        }

        // Save invoice to database
        boolean saved = invoiceDAO.saveInvoice(invoice);      // calling saveInvoice method which will insert the invoice and items into the DB
        if (!saved) {
            System.err.println("Failed to save invoice to database");
            return false;
        }

        return true;
    }

    
    //save invoice in DB by calling saveInvoice and then generate PDF file by calling generateInvoiceFile
    public SaveInvoiceResult saveInvoiceWithPDF(Invoice invoice) {
        // First save the invoice normally in DB
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


    public record SaveInvoiceResult(boolean success, String pdfPath, String message) {

        public boolean hasPdf() {
                return pdfPath != null && !pdfPath.isEmpty();
            }
        }



            //to create an invoice object from raw data from the UI,
            //then we may send the invoice object to do other staffs like saving in DB or generating PDF
    public Invoice createInvoice(int pharmacyId, String patientName, String patientPhone, 
                                List<SaleItem> items, Pharmacy pharmacy) {
        String pharmacyName = pharmacy != null ? pharmacy.getName() : "Unknown Pharmacy";
        String pharmacyArea = pharmacy != null ? pharmacy.getArea() : "Unknown Area";

        return new Invoice(pharmacyId, patientName, patientPhone, items, pharmacyName, pharmacyArea);
    }
}
