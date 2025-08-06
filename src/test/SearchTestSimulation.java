package test;

import services.PharmacySearchService;
import services.PharmacySearchService.PharmacySearchResult;
import services.PharmacySearchService.PharmacyAvailability;

import java.util.Arrays;
import java.util.List;

public class SearchTestSimulation {
    
    public static void main(String[] args) {
        PharmacySearchService searchService = new PharmacySearchService();
        
        System.out.println("=== Testing Patient Medicine Search Feature ===\n");
        
        // Test 1: Single medicine search
        System.out.println("Test 1: Searching for 'Paracetamol'");
        testSearch(searchService, Arrays.asList("Paracetamol"));
        
        // Test 2: Multiple medicine search
        System.out.println("\nTest 2: Searching for 'Paracetamol + Vitamin C'");
        testSearch(searchService, Arrays.asList("Paracetamol", "Vitamin C"));
        
        // Test 3: Heart/diabetes medicines
        System.out.println("\nTest 3: Searching for 'Metformin + Amlodipine'");
        testSearch(searchService, Arrays.asList("Metformin", "Amlodipine"));
        
        // Test 4: Non-existent medicine
        System.out.println("\nTest 4: Searching for non-existent medicine");
        testSearch(searchService, Arrays.asList("NonExistentMedicine"));
        
        System.out.println("\n=== Search Tests Completed Successfully! ===");
    }
    
    private static void testSearch(PharmacySearchService service, List<String> medicines) {
        try {
            PharmacySearchResult result = service.findPharmaciesForMedicines(medicines);
            
            System.out.println("Searched for: " + String.join(", ", medicines));
            System.out.println("Complete pharmacies found: " + result.getCompletePharmacies().size());
            System.out.println("Partial pharmacies found: " + result.getPartialPharmacies().size());
            
            if (result.hasCompleteMatch()) {
                System.out.println("✓ Found pharmacies with ALL medicines:");
                for (PharmacyAvailability availability : result.getCompletePharmacies()) {
                    System.out.println("  - " + availability.getPharmacy().getName() + 
                                     " (" + availability.getAvailabilityText() + ")");
                }
            }
            
            if (!result.getPartialPharmacies().isEmpty()) {
                System.out.println("⚠ Found pharmacies with SOME medicines:");
                for (PharmacyAvailability availability : result.getPartialPharmacies()) {
                    System.out.println("  - " + availability.getPharmacy().getName() + 
                                     " (" + availability.getAvailabilityText() + ")");
                }
            }
            
            if (!result.hasResults()) {
                System.out.println("✗ No pharmacies found with requested medicines");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error during search: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
