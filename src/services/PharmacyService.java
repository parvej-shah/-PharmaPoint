package services;

import dao.PharmacyDAO;
import models.Pharmacy;

import java.util.List;

public class PharmacyService {
    private final PharmacyDAO pharmacyDAO = new PharmacyDAO();

    public boolean registerPharmacy(Pharmacy pharmacy) {
        return this.pharmacyDAO.addPharmacy(pharmacy);
    }

    public List<Pharmacy> getAllPharmacies() {
        return this.pharmacyDAO.getPharmacies();
    }
    public Pharmacy getPharmacyById(int id) {
        return pharmacyDAO.getPharmacyById(id);
    }
}
