package ui;

import models.Medicine;
import models.Pharmacy;
import services.MedicineService;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMedicineFormUI extends JFrame {
    
    private Pharmacy pharmacy;
    private PharmacyDashboard parentDashboard;
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
    
    public AddMedicineFormUI(Pharmacy pharmacy, PharmacyDashboard parentDashboard) {
        this.pharmacy = pharmacy;
        this.parentDashboard = parentDashboard;
        this.medicineService = new MedicineService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("Add New Medicine - " + pharmacy.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(parentDashboard);
        setMinimumSize(new Dimension(450, 400));
    }
    
    private void initializeComponents() {
        // Text fields
        nameField = new JTextField(20);
        genericNameField = new JTextField(20);
        brandField = new JTextField(20);
        priceField = new JTextField(20);
        quantityField = new JTextField(20);
        expiryDateField = new JTextField(20);
        
        // Set larger font for text fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        nameField.setFont(fieldFont);
        genericNameField.setFont(fieldFont);
        brandField.setFont(fieldFont);
        priceField.setFont(fieldFont);
        quantityField.setFont(fieldFont);
        expiryDateField.setFont(fieldFont);
        
        // Buttons
        addButton = new JButton("Add Medicine");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(140, 40));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setPreferredSize(new Dimension(140, 40));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        add(titleLabel, gbc);
        
        // Reset gridwidth for form fields
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Medicine Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        JLabel nameLabel = new JLabel("Medicine Name:");
        nameLabel.setFont(labelFont);
        add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        add(nameField, gbc);
        
        // Generic Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel genericLabel = new JLabel("Generic Name:");
        genericLabel.setFont(labelFont);
        add(genericLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        add(genericNameField, gbc);
        
        // Brand
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel brandLabel = new JLabel("Brand:");
        brandLabel.setFont(labelFont);
        add(brandLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        add(brandField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel priceLabel = new JLabel("Price (BDT):");
        priceLabel.setFont(labelFont);
        add(priceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        add(priceField, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(labelFont);
        add(quantityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0;
        add(quantityField, gbc);
        
        // Expiry Date
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        JLabel expiryLabel = new JLabel("Expiry Date:");
        expiryLabel.setFont(labelFont);
        add(expiryLabel, gbc);
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
            
            // Validate form using centralized Validator
            if (!Validator.validateMedicineForm(name, genericName, brand, priceText, quantityText, expiryDate)) {
                if (Validator.isBlank(name) || Validator.isBlank(genericName) || Validator.isBlank(brand) || 
                    Validator.isBlank(priceText) || Validator.isBlank(quantityText) || Validator.isBlank(expiryDate)) {
                    showErrorMessage("Please fill in all fields.");
                    return;
                }
                
                if (!Validator.isValidPositiveNumber(priceText)) {
                    showErrorMessage("Please enter a valid price (positive number).");
                    return;
                }
                
                if (!Validator.isValidPositiveInteger(quantityText)) {
                    showErrorMessage("Please enter a valid quantity (positive integer).");
                    return;
                }
                
                if (!Validator.isValidDateFormat(expiryDate)) {
                    showErrorMessage("Please enter expiry date in YYYY-MM-DD format.");
                    return;
                }
                
                return;
            }
            
            // Parse validated values
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            
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
