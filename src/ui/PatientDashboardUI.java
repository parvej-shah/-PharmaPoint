package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import utils.SessionManager;
import models.User;
import services.PharmacySearchService;
import services.PharmacySearchService.PharmacySearchResult;

public class PatientDashboardUI extends JFrame {
    private JPanel medicineListPanel;
    private JScrollPane scrollPane;
    private JButton addMedicineButton;
    private JButton submitButton;
    private List<MedicineInputRow> medicineRows;
    private String patientName;
    private PharmacySearchService pharmacySearchService;

    public PatientDashboardUI() {
        User currentUser = SessionManager.getCurrentUser();
        this.patientName = (currentUser != null) ? currentUser.getName() : "";
        this.pharmacySearchService = new PharmacySearchService();
        medicineRows = new ArrayList<>();
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Patient Dashboard - Add Desired Medicines");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 400));
    }

    private void initializeComponents() {
        medicineListPanel = new JPanel();
        medicineListPanel.setLayout(new BoxLayout(medicineListPanel, BoxLayout.Y_AXIS));
        medicineListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(medicineListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(450, 300));

        addMedicineButton = new JButton("Add Another Medicine");
        styleButton(addMedicineButton, new Color(70, 130, 180));
        submitButton = new JButton("Submit");
        styleButton(submitButton, new Color(34, 139, 34));

        addMedicineRow(); // Add initial row
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with patient name and title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel nameLabel = new JLabel("Logged in as: " + (patientName.isEmpty() ? "Patient" : patientName), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel titleLabel = new JLabel("Patient Dashboard - Add Desired Medicines", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(nameLabel);
        topPanel.add(titleLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(addMedicineButton);
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void setupEventHandlers() {
        addMedicineButton.addActionListener(e -> addMedicineRow());
        submitButton.addActionListener(e -> handleSubmit());
    }

    private void addMedicineRow() {
        MedicineInputRow row = new MedicineInputRow();
        medicineRows.add(row);
        medicineListPanel.add(row);
        medicineListPanel.revalidate();
        medicineListPanel.repaint();
    }

    private void handleSubmit() {
        List<String> medicineNames = new ArrayList<>();
        List<String> quantities = new ArrayList<>();
        
        for (MedicineInputRow row : medicineRows) {
            String name = row.getMedicineName();
            String qty = row.getQuantity();
            if (!name.isEmpty() && !qty.isEmpty()) {
                medicineNames.add(name);
                quantities.add(qty);
            }
        }
        
        if (medicineNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one medicine.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show loading message
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            // Search for pharmacies with the requested medicines
            PharmacySearchResult searchResult = pharmacySearchService.findPharmaciesForMedicines(medicineNames);
            
            // Display results
            displaySearchResults(searchResult, medicineNames, quantities);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching for pharmacies: " + e.getMessage(), 
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void displaySearchResults(PharmacySearchResult result, List<String> requestedMedicines, List<String> quantities) {
        if (!result.hasResults()) {
            JOptionPane.showMessageDialog(this,
                "No pharmacies found with the requested medicines:\n" + String.join(", ", requestedMedicines),
                "No Results Found",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create and show results window
        SwingUtilities.invokeLater(() -> {
            PharmacySearchResultsUI resultsWindow = new PharmacySearchResultsUI(
                this, result, requestedMedicines, quantities, patientName);
            resultsWindow.setVisible(true);
        });
    }

    // Small component for a single medicine input row
    private static class MedicineInputRow extends JPanel {
        private JTextField medicineNameField;
        private JTextField quantityField;

        public MedicineInputRow() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            setMaximumSize(new Dimension(1000, 40));

            medicineNameField = new JTextField(15);
            quantityField = new JTextField(5);

            add(new JLabel("Medicine Name:"));
            add(medicineNameField);
            add(new JLabel("Quantity:"));
            add(quantityField);
        }

        public String getMedicineName() {
            return medicineNameField.getText().trim();
        }

        public String getQuantity() {
            return quantityField.getText().trim();
        }
    }

    // For testing/demo
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientDashboardUI().setVisible(true));
    }
}
