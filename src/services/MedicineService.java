package services;

import dao.MedicineDAO;
import models.Medicine;
import models.Pharmacy;
import models.SaleItem;
import models.Invoice;

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
        return medicineDAO.updateMedicine(medicine);
    }

    public List<Medicine> searchMedicineByName(String keyword) {    //returns list of medicines with similar names
        return medicineDAO.searchMedicineByName(keyword);
    }

    public Medicine getMedicineById(int medicineId) {
        return medicineDAO.getMedicineById(medicineId);
    }
    public List<Pharmacy> getPharmaciesWithMedicine(String keyword) {
        return medicineDAO.findPharmaciesWithMedicine(keyword);
    }


    public boolean sellMedicines(List<SaleItem> saleItems) {
        try {
            for (SaleItem item : saleItems) {
                Medicine medicine = getMedicineById(item.getMedicine().getId());
                int newQuantity = medicine.getQuantity() - item.getQuantity();
                medicine.setQuantity(newQuantity);
                
                // Update medicine quantity in database
                if (!medicineDAO.updateMedicine(medicine)) {
                    System.err.println("Failed to update medicine in database: " + medicine.getName());
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Couldn't process sale: " + e.getMessage());
            return false;
        }
    }

    public Invoice createInvoice(List<SaleItem> saleItems, String pharmacistName, String pharmacyName) {
        return new Invoice(saleItems, pharmacistName, pharmacyName);
    }

}
