package services;

import dao.MedicineDAO;
import models.Medicine;
import models.Pharmacy;
import models.SaleItem;
import models.Invoice;
import utils.Validator;

import java.util.List;

public class MedicineService {
    private final MedicineDAO medicineDAO =  new MedicineDAO();

    public boolean addMedicine(Medicine medicine){
        return this.medicineDAO.addMedicine(medicine);
    }

    public List<Medicine> getAllMedicines(int pharmacyId){
        return this.medicineDAO.getMedicinesByPharmacyId(pharmacyId);
    }

    public boolean deleteMedicine(int medicineId) {
        return medicineDAO.deleteMedicine(medicineId);
    }

    public boolean updateMedicine(Medicine medicine) {
        if (!Validator.validateMedicine(medicine)) {
            System.err.println("Invalid medicine input for update.");
            return false;
        }
        return medicineDAO.updateMedicine(medicine);
    }

    public boolean updateMedicineForSale(Medicine medicine) {
        // Simple validation for sale updates
        if (medicine == null || medicine.getId() <= 0) {
            System.err.println("Invalid medicine for sale update");
            return false;
        }
        
        if (medicine.getQuantity() < 0) {
            System.err.println("Medicine quantity cannot be negative");
            return false;
        }
        
        return medicineDAO.updateMedicine(medicine);
    }

    public List<Medicine> searchMedicineByName(String keyword) {
        return medicineDAO.searchMedicineByName(keyword);
    }

    public Medicine getMedicineById(int medicineId) {
        return medicineDAO.getMedicineById(medicineId);
    }
    public List<Pharmacy> getPharmaciesWithMedicine(String keyword) {
        return medicineDAO.findPharmaciesWithMedicine(keyword);
    }

    /**
     * Processes a sale by updating medicine quantities (SIMPLIFIED)
     * @param saleItems List of items being sold
     * @return true if sale was successful, false otherwise
     */
    public boolean sellMedicines(List<SaleItem> saleItems) {
        try {
            // Validate all items first using centralized validator
            for (SaleItem item : saleItems) {
                // Use the centralized validator for basic checks
                if (!utils.Validator.isValidSaleItem(item)) {
                    System.err.println("Invalid sale item: " + 
                        (item.getMedicine() != null ? item.getMedicine().getName() : "unknown"));
                    return false;
                }
                
                // Get fresh medicine data from database
                Medicine currentMedicine = getMedicineById(item.getMedicine().getId());
                if (currentMedicine == null) {
                    System.err.println("Medicine not found: " + item.getMedicine().getName());
                    return false;
                }
                
                // Check stock using centralized validator
                if (!utils.Validator.hasStockAvailable(currentMedicine, item.getQuantity())) {
                    System.err.println(utils.Validator.getMedicineStockError(currentMedicine, item.getQuantity()));
                    return false;
                }
            }

            // If all validations pass, update the quantities
            for (SaleItem item : saleItems) {
                Medicine medicine = getMedicineById(item.getMedicine().getId());
                int newQuantity = medicine.getQuantity() - item.getQuantity();
                medicine.setQuantity(newQuantity);
                
                // Use simpler update method for sales
                if (!updateMedicineForSale(medicine)) {
                    System.err.println("Failed to update medicine in database: " + medicine.getName());
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error processing sale: " + e.getMessage());
            return false;
        }
    }

    public Invoice createInvoice(List<SaleItem> saleItems, String pharmacistName, String pharmacyName) {
        return new Invoice(saleItems, pharmacistName, pharmacyName);
    }

}
