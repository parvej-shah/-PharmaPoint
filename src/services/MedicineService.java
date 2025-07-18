package services;

import dao.MedicineDAO;
import models.Medicine;
import models.Pharmacy;
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

    public List<Medicine> searchMedicineByName(String keyword) {
        return medicineDAO.searchMedicineByName(keyword);
    }

    public Medicine getMedicineById(int medicineId) {
        return medicineDAO.getMedicineById(medicineId);
    }
    public List<Pharmacy> getPharmaciesWithMedicine(String keyword) {
        return medicineDAO.findPharmaciesWithMedicine(keyword);
    }

}
