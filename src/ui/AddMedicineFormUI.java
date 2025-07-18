package ui;

import models.Medicine;
import models.Pharmacy;
import services.MedicineService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMedicineFormUI extends JFrame {
    
    private Pharmacy pharmacy;
    private PharmacyDashboardUI parentDashboard;
    private MedicineService medicineService;
    
    // Form components
    private JTextField nameField;
    private JTextField genericNameField;
    private JTextField brandField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField expiryDateField;
    private JButton addButton;
    private JButton cancelButton;
    
    public AddMedicineFormUI(Pharmacy pharmacy, PharmacyDashboardUI parentDashboard) {
        this.pharmacy = pharmacy;
        this.parentDashboard = parentDashboard;
        this.medicineService = new MedicineService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("Add New Medicine - " + pharmacy.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(parentDashboard);
        setMinimumSize(new Dimension(400, 350));
    }
    
    private void initializeComponents() {
        // Text fields
        nameField = new JTextField(20);
        genericNameField = new JTextField(20);
        brandField = new JTextField(20);
        priceField = new JTextField(20);
        quantityField = new JTextField(20);
        expiryDateField = new JTextField(20);
        
        // Buttons
        addButton = new JButton("Add Medicine");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        
        // Set placeholder text for expiry date
        expiryDateField.setToolTipText("Format: YYYY-MM-DD (e.g., 2025-12-31)");
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title label
        JLabel titleLabel = new JLabel("Add New Medicine", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        add(titleLabel, gbc);
        
        // Reset gridwidth for form fields
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        
        // Medicine Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Medicine Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        add(nameField, gbc);
        
        // Generic Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        add(new JLabel("Generic Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        add(genericNameField, gbc);
        
        // Brand
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        add(brandField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        add(priceField, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0;
        add(quantityField, gbc);
        
        // Expiry Date
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        add(new JLabel("Expiry Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 1.0;
        add(expiryDateField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(buttonPanel, gbc);
        
        // Add vertical glue to push content up
        gbc.gridy = 8; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        add(Box.createVerticalGlue(), gbc);
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddMedicine();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Add Enter key support for all text fields
        ActionListener enterKeyListener = e -> handleAddMedicine();
        nameField.addActionListener(enterKeyListener);
        genericNameField.addActionListener(enterKeyListener);
        brandField.addActionListener(enterKeyListener);
        priceField.addActionListener(enterKeyListener);
        quantityField.addActionListener(enterKeyListener);
        expiryDateField.addActionListener(enterKeyListener);
    }
    
    private void handleAddMedicine() {
        try {
            // Get form data
            String name = nameField.getText().trim();
            String genericName = genericNameField.getText().trim();
            String brand = brandField.getText().trim();
            String priceText = priceField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String expiryDate = expiryDateField.getText().trim();
            
            // Basic validation
            if (name.isEmpty() || genericName.isEmpty() || brand.isEmpty() || 
                priceText.isEmpty() || quantityText.isEmpty() || expiryDate.isEmpty()) {
                showErrorMessage("Please fill in all fields.");
                return;
            }
            
            // Validate and parse numeric values
            double price;
            int quantity;
            
            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) {
                    throw new NumberFormatException("Price must be positive");
                }
            } catch (NumberFormatException ex) {
                showErrorMessage("Please enter a valid price (positive number).");
                return;
            }
            
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    throw new NumberFormatException("Quantity must be positive");
                }
            } catch (NumberFormatException ex) {
                showErrorMessage("Please enter a valid quantity (positive integer).");
                return;
            }
            
            // Validate date format (basic check)
            if (!expiryDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                showErrorMessage("Please enter expiry date in YYYY-MM-DD format.");
                return;
            }
            
            // Create Medicine object
            Medicine medicine = new Medicine(
                pharmacy.getId(),
                name,
                genericName,
                brand,
                price,
                quantity,
                expiryDate
            );
            
            // Add medicine using service
            if (medicineService.addMedicine(medicine)) {
                showSuccessMessage("Medicine Added Successfully!", 
                    "Medicine '" + name + "' has been added to " + pharmacy.getName() + " inventory.");
                
                // Refresh parent dashboard
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
                
                // Clear form for next entry
                clearForm();
            } else {
                showErrorMessage("Failed to add medicine. Please try again.");
            }
            
        } catch (Exception ex) {
            showErrorMessage("An error occurred while adding medicine: " + ex.getMessage());
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        genericNameField.setText("");
        brandField.setText("");
        priceField.setText("");
        quantityField.setText("");
        expiryDateField.setText("");
        nameField.requestFocus();
    }
    
    private void showSuccessMessage(String title, String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
