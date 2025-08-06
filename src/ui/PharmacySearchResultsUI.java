package ui;

import models.Medicine;
import models.Pharmacy;
import services.PharmacySearchService.PharmacyAvailability;
import services.PharmacySearchService.PharmacySearchResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PharmacySearchResultsUI extends JFrame {
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private PharmacySearchResult searchResult;
    private List<String> requestedMedicines;
    private List<String> quantities;
    private String patientName;

    public PharmacySearchResultsUI(PatientDashboardUI parent, PharmacySearchResult result, 
                                 List<String> medicines, List<String> quantities, String patientName) {
        this.searchResult = result;
        this.requestedMedicines = medicines;
        this.quantities = quantities;
        this.patientName = patientName;
        
        initializeComponents();
        setupLayout();
        
        setTitle("Pharmacy Search Results - " + patientName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(700, 450));
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Results content
        populateResults();
        add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(70, 130, 180));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Pharmacy Search Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel patientLabel = new JLabel("Patient: " + patientName, SwingConstants.CENTER);
        patientLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        patientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel requestLabel = new JLabel("Requested: " + String.join(", ", requestedMedicines), SwingConstants.CENTER);
        requestLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        requestLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(patientLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(requestLabel);
        
        return panel;
    }

    private void populateResults() {
        // Show combined ranked results
        if (!searchResult.getRankedPharmacies().isEmpty()) {
            addSectionHeader("🏥 Pharmacies ranked by medicine availability:");
            
            int count = 0;
            for (PharmacyAvailability availability : searchResult.getRankedPharmacies()) {
                if (count >= 10) break; // Show top 10 results
                addRankedPharmacyPanel(availability, count + 1);
                count++;
            }
        } else {
            // No results message
            JLabel noResultsLabel = new JLabel("No pharmacies found with the requested medicines.");
            noResultsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noResultsLabel);
        }
    }

    private void addSectionHeader(String text) {
        JLabel headerLabel = new JLabel(text);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(34, 139, 34));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(headerLabel);
    }

    private void addRankedPharmacyPanel(PharmacyAvailability availability, int rank) {
        Pharmacy pharmacy = availability.getPharmacy();
        
        JPanel pharmacyPanel = new JPanel();
        pharmacyPanel.setLayout(new BoxLayout(pharmacyPanel, BoxLayout.Y_AXIS));
        
        // Color based on availability
        Color borderColor = availability.hasAllMedicines() ? 
            new Color(34, 139, 34) : new Color(255, 140, 0);
        
        pharmacyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pharmacyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Rank and pharmacy name header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rankLabel.setForeground(new Color(70, 130, 180));
        rankLabel.setPreferredSize(new Dimension(40, 25));
        
        JLabel nameLabel = new JLabel(pharmacy.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        headerPanel.add(rankLabel);
        headerPanel.add(Box.createHorizontalStrut(10));
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createHorizontalGlue());
        
        pharmacyPanel.add(headerPanel);
        pharmacyPanel.add(Box.createVerticalStrut(5));

        // Availability status
        JLabel statusLabel = new JLabel(availability.getAvailabilityStatus());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(availability.hasAllMedicines() ? 
            new Color(34, 139, 34) : new Color(255, 140, 0));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pharmacyPanel.add(statusLabel);
        
        pharmacyPanel.add(Box.createVerticalStrut(5));

        // Address
        JLabel addressLabel = new JLabel("Address: " + pharmacy.getAddress());
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pharmacyPanel.add(addressLabel);
        
        pharmacyPanel.add(Box.createVerticalStrut(8));

        // Available medicines
        JLabel medicinesTitle = new JLabel("Available Medicines:");
        medicinesTitle.setFont(new Font("Arial", Font.BOLD, 12));
        medicinesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pharmacyPanel.add(medicinesTitle);
        
        pharmacyPanel.add(Box.createVerticalStrut(3));

        // List available medicines
        for (Medicine medicine : availability.getAvailableMedicines()) {
            JLabel medicineLabel = new JLabel("• " + medicine.getName() + " (" + medicine.getBrand() + ")");
            medicineLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            medicineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pharmacyPanel.add(medicineLabel);
        }

        mainPanel.add(pharmacyPanel);
        mainPanel.add(Box.createVerticalStrut(12));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
