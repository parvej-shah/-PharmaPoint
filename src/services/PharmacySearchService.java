package services;

import dao.MedicineDAO;
import models.Medicine;
import models.Pharmacy;

import java.util.*;

public class PharmacySearchService {
    private MedicineDAO medicineDAO = new MedicineDAO();

    public PharmacySearchResult findPharmaciesForMedicines(List<String> requestedMedicines) {
        if (requestedMedicines == null || requestedMedicines.isEmpty()) {
            return new PharmacySearchResult();
        }

        Map<Pharmacy, Map<String, Object>> pharmacyData = medicineDAO.findPharmaciesWithMedicines(requestedMedicines);
        return organizePharmacyResults(pharmacyData, requestedMedicines);
    }
    
    private PharmacySearchResult organizePharmacyResults(Map<Pharmacy, Map<String, Object>> pharmacyData, List<String> requestedMedicines) {
        PharmacySearchResult result = new PharmacySearchResult();
        
        if (pharmacyData.isEmpty()) {
            return result;
        }
        
        int totalMedicinesRequested = requestedMedicines.size();
        List<PharmacyAvailability> allPharmacies = new ArrayList<>();
        
        // Step 1: Create availability info for each pharmacy
        createPharmacyAvailabilityList(pharmacyData, totalMedicinesRequested, allPharmacies);
        
        // Step 2: Sort pharmacies by how many medicines they have
        sortPharmaciesByAvailability(allPharmacies);
        
        // Step 3: Put pharmacies into complete and partial lists
        categorizePharmacies(allPharmacies, result);
        
        return result;
    }
    
    private void createPharmacyAvailabilityList(Map<Pharmacy, Map<String, Object>> pharmacyData, 
                                               int totalMedicinesRequested, 
                                               List<PharmacyAvailability> allPharmacies) {
        
        for (Pharmacy pharmacy : pharmacyData.keySet()) {
            
            Map<String, Object> pharmacyInfo = pharmacyData.get(pharmacy);
            
            // Get the medicines this pharmacy has
            @SuppressWarnings("unchecked")
            List<Medicine> medicinesTheyHave = (List<Medicine>) pharmacyInfo.get("availableMedicines");
            
            // Count how many medicines they have
            int howManyTheyHave = medicinesTheyHave.size();
            
            // Create a simple info object
            PharmacyAvailability pharmacyInfo_obj = new PharmacyAvailability(
                    pharmacy, 
                    medicinesTheyHave, 
                    howManyTheyHave, 
                    totalMedicinesRequested);
            
            // Add this pharmacy to our list
            allPharmacies.add(pharmacyInfo_obj);
        }
    }
    
    private void sortPharmaciesByAvailability(List<PharmacyAvailability> allPharmacies) {
        // Sort so best pharmacies come first
        for (int i = 0; i < allPharmacies.size() - 1; i++) {
            for (int j = i + 1; j < allPharmacies.size(); j++) {
                PharmacyAvailability pharmacy1 = allPharmacies.get(i);
                PharmacyAvailability pharmacy2 = allPharmacies.get(j);
                
                // If pharmacy2 has more medicines, swap them
                if (shouldSwapPharmacies(pharmacy1, pharmacy2)) {
                    allPharmacies.set(i, pharmacy2);
                    allPharmacies.set(j, pharmacy1);
                }
            }
        }
    }
    
    private boolean shouldSwapPharmacies(PharmacyAvailability pharmacy1, PharmacyAvailability pharmacy2) {
        // First check: which has more medicines?
        if (pharmacy2.getAvailableCount() > pharmacy1.getAvailableCount()) {
            return true;
        }
        
        // Second check: if same count, sort by name alphabetically
        if (pharmacy2.getAvailableCount() == pharmacy1.getAvailableCount()) {
            String name1 = pharmacy1.getPharmacy().getName().toLowerCase();
            String name2 = pharmacy2.getPharmacy().getName().toLowerCase();
            return name2.compareTo(name1) < 0;
        }
        
        return false;
    }
    
    private void categorizePharmacies(List<PharmacyAvailability> allPharmacies, PharmacySearchResult result) {
        for (PharmacyAvailability availability : allPharmacies) {
            if (availability.hasAllMedicines()) {
                result.addCompletePharmacy(availability);
            } else {
                result.addPartialPharmacy(availability);
            }
        }
    }
 
    
    public static class PharmacySearchResult {
        private List<PharmacyAvailability> completePharmacies = new ArrayList<>();
        private List<PharmacyAvailability> partialPharmacies = new ArrayList<>();
        private List<PharmacyAvailability> rankedPharmacies = new ArrayList<>();
        
        public List<PharmacyAvailability> getCompletePharmacies() {
            return completePharmacies;
        }
        
        public List<PharmacyAvailability> getPartialPharmacies() {
            return partialPharmacies;
        }
        
        public List<PharmacyAvailability> getRankedPharmacies() {
            return rankedPharmacies;
        }
        
        public void addCompletePharmacy(PharmacyAvailability availability) {
            completePharmacies.add(availability);
            rankedPharmacies.add(availability);
        }
        
        public void addPartialPharmacy(PharmacyAvailability availability) {
            partialPharmacies.add(availability);
            rankedPharmacies.add(availability);
        }
        
        public boolean hasResults() {
            return !completePharmacies.isEmpty() || !partialPharmacies.isEmpty();
        }
        
        public boolean hasCompleteMatch() {
            return !completePharmacies.isEmpty();
        }
    }
    
    public static class PharmacyAvailability {
        private Pharmacy pharmacy;
        private List<Medicine> availableMedicines;
        private int availableCount;
        private int totalRequested;
        
        public PharmacyAvailability(Pharmacy pharmacy, List<Medicine> availableMedicines, 
                                  int availableCount, int totalRequested) {
            this.pharmacy = pharmacy;
            this.availableMedicines = availableMedicines;
            this.availableCount = availableCount;
            this.totalRequested = totalRequested;
        }
        
        public Pharmacy getPharmacy() {
            return pharmacy;
        }
        
        public List<Medicine> getAvailableMedicines() {
            return availableMedicines;
        }
        
        public int getAvailableCount() {
            return availableCount;
        }
        
        public int getTotalRequested() {
            return totalRequested;
        }
        
        public boolean hasAllMedicines() {
            return availableCount >= totalRequested;
        }
        
        public double getAvailabilityPercentage() {
            if (totalRequested == 0) {
                return 0.0;
            }
            return (double) availableCount / totalRequested * 100.0;
        }
        
        public String getAvailabilityStatus() {
            if (hasAllMedicines()) {
                return "ALL medicines available";
            } else {
                double percentage = getAvailabilityPercentage();
                return String.format("%d of %d medicines available (%.0f%%)", 
                    availableCount, totalRequested, percentage);
            }
        }
        
        public String getAvailabilityText() {
            return availableCount + "/" + totalRequested + " medicines available";
        }
    }

}
