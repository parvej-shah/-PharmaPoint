package services;

import dao.MedicineDAO;
import models.Medicine;
import models.Pharmacy;

import java.util.*;
import java.util.stream.Collectors;

public class PharmacySearchService {
    private final MedicineDAO medicineDAO = new MedicineDAO();

    /**
     * Find pharmacies that have the requested medicines and organize them by availability
     * @param requestedMedicines List of medicine names requested by patient
     * @return PharmacySearchResult containing organized pharmacy lists
     */
    public PharmacySearchResult findPharmaciesForMedicines(List<String> requestedMedicines) {
        if (requestedMedicines == null || requestedMedicines.isEmpty()) {
            return new PharmacySearchResult();
        }
        
        // Remove empty/null entries and normalize
        List<String> cleanMedicines = requestedMedicines.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
        
        if (cleanMedicines.isEmpty()) {
            return new PharmacySearchResult();
        }
        
        Map<Pharmacy, Map<String, Object>> pharmacyData = medicineDAO.findPharmaciesWithMedicines(cleanMedicines);
        
        return organizePharmacyResults(pharmacyData, cleanMedicines);
    }
    
    private PharmacySearchResult organizePharmacyResults(Map<Pharmacy, Map<String, Object>> pharmacyData, List<String> requestedMedicines) {
        PharmacySearchResult result = new PharmacySearchResult();
        
        if (pharmacyData.isEmpty()) {
            return result;
        }
        
        int totalRequested = requestedMedicines.size();
        
        // Separate pharmacies by availability
        List<PharmacyAvailability> allPharmacies = new ArrayList<>();
        
        for (Map.Entry<Pharmacy, Map<String, Object>> entry : pharmacyData.entrySet()) {
            Pharmacy pharmacy = entry.getKey();
            @SuppressWarnings("unchecked")
            List<Medicine> availableMedicines = (List<Medicine>) entry.getValue().get("availableMedicines");
            int availableCount = availableMedicines.size();
            
            PharmacyAvailability availability = new PharmacyAvailability(
                    pharmacy, 
                    availableMedicines, 
                    availableCount, 
                    totalRequested
            );
            
            allPharmacies.add(availability);
        }
        
        // Sort by availability count (descending) then by name (ascending)
        allPharmacies.sort((a, b) -> {
            int countCompare = Integer.compare(b.getAvailableCount(), a.getAvailableCount());
            if (countCompare != 0) return countCompare;
            return a.getPharmacy().getName().compareToIgnoreCase(b.getPharmacy().getName());
        });
        
        // Categorize results
        for (PharmacyAvailability availability : allPharmacies) {
            if (availability.hasAllMedicines()) {
                result.addCompletePharmacy(availability);
            } else {
                result.addPartialPharmacy(availability);
            }
        }
        
        return result;
    }
    
    /**
     * Inner class to hold search results
     */
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
    
    /**
     * Inner class to hold pharmacy availability info
     */
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
            if (totalRequested == 0) return 0.0;
            return (double) availableCount / totalRequested * 100.0;
        }
        
        public String getAvailabilityStatus() {
            if (hasAllMedicines()) {
                return "✓ ALL medicines available";
            } else {
                return String.format("⚠ %d of %d medicines available (%.0f%%)", 
                    availableCount, totalRequested, getAvailabilityPercentage());
            }
        }
        
        public String getAvailabilityText() {
            return availableCount + "/" + totalRequested + " medicines available";
        }
    }
}
