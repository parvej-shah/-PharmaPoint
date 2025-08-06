package test;

import ui.PharmacySearchResultsUI;
import services.PharmacySearchService;
import services.PharmacySearchService.PharmacySearchResult;

import javax.swing.SwingUtilities;
import java.util.Arrays;

public class UITest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PharmacySearchService searchService = new PharmacySearchService();
            
            // Test search for Paracetamol
            PharmacySearchResult result = searchService.findPharmaciesForMedicines(Arrays.asList("Paracetamol"));
            
            // Create and show results window
            PharmacySearchResultsUI resultsWindow = new PharmacySearchResultsUI(
                null, result, Arrays.asList("Paracetamol"), Arrays.asList("1"), "Test Patient");
            
            resultsWindow.setVisible(true);
            resultsWindow.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        });
    }
}
