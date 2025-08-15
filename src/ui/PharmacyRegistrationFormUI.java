package ui;

import models.Pharmacy;
import models.User;
import services.PharmacyService;
import utils.SessionManager;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PharmacyRegistrationFormUI extends JFrame {
    
    private User user; // The user creating the pharmacy
    private JTextField userIdField;
    private JTextField pharmacyNameField;
    private JTextField addressField;
    private JTextField areaField;
    private JButton registerButton;
    
    private PharmacyService pharmacyService;
    
    // Constructor that takes a User parameter
    public PharmacyRegistrationFormUI() {
        this.user = SessionManager.getCurrentUser();
        pharmacyService = new PharmacyService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("Pharmacy Registration Form - " + user.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
    }
    
    private void initializeComponents() {
        userIdField = new JTextField(20);
        pharmacyNameField = new JTextField(20);
        addressField = new JTextField(20);
        areaField = new JTextField(20);
        registerButton = new JButton("Register Pharmacy");
        
        // Set larger fonts
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        userIdField.setFont(fieldFont);
        pharmacyNameField.setFont(fieldFont);
        addressField.setFont(fieldFont);
        areaField.setFont(fieldFont);
        
        // Auto-fill user ID from the passed user object
        if (user != null) {
            userIdField.setText(String.valueOf(user.getId()));
            userIdField.setEditable(false); // Make it read-only since it's auto-filled
            userIdField.setBackground(new Color(240, 240, 240)); // Light gray to indicate read-only
        }
        
        // Set button style
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setPreferredSize(new Dimension(180, 40));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Title
        JLabel titleLabel = new JLabel("Pharmacy Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        add(titleLabel, gbc);
        
        // Reset gridwidth
        gbc.gridwidth = 1;
        
        // Pharmacy Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Pharmacy Name:");
        nameLabel.setFont(labelFont);
        add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        add(pharmacyNameField, gbc);
        
        // Address Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(labelFont);
        add(addressLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(addressField, gbc);
        
        // Area Label and Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel areaLabel = new JLabel("Area:");
        areaLabel.setFont(labelFont);
        add(areaLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        add(areaField, gbc);
        
        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(registerButton, gbc);
    }
    
    private void setupEventHandlers() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
        
        // Add Enter key support for all text fields
        ActionListener enterKeyListener = e -> handleRegistration();
        pharmacyNameField.addActionListener(enterKeyListener);
        addressField.addActionListener(enterKeyListener);
        areaField.addActionListener(enterKeyListener);
    }
    
    private void handleRegistration() {
        // Get input values
        String userIdText = String.valueOf(user.getId());
        String pharmacyName = pharmacyNameField.getText().trim();
        String address = addressField.getText().trim();
        String area = areaField.getText().trim();
        
        // Validate form using centralized Validator
        if (!Validator.validatePharmacyForm(userIdText, pharmacyName, address, area)) {
            if (!Validator.isNotEmpty(pharmacyName) || !Validator.isNotEmpty(address) || !Validator.isNotEmpty(area)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            if (!Validator.isValidNumber(userIdText, true)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid user ID.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
        
        // Parse validated user ID
        int userId = Integer.parseInt(userIdText);
        
        // Create Pharmacy object
        Pharmacy pharmacy = new Pharmacy(userId, pharmacyName, address, area);
        
        // Validate pharmacy object
        if (!Validator.validatePharmacy(pharmacy)) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid pharmacy data. Please check your inputs.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Attempt to register pharmacy
        try {
            boolean success = pharmacyService.registerPharmacy(pharmacy);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pharmacy registered successfully!\n\n" +
                    "User ID: " + userId + "\n" +
                    "Pharmacy Name: " + pharmacyName + "\n" +
                    "Address: " + address + "\n" +
                    "Area: " + area,
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Get the newly created pharmacy from database
                Pharmacy createdPharmacy = pharmacyService.getPharmacyByUserId(userId);
                
                // Close this registration form
                dispose();
                
                // Open PharmacyDashboard with the user and newly created pharmacy
                SwingUtilities.invokeLater(() -> {
                    if (createdPharmacy != null) {
                        new PharmacyDashboard(createdPharmacy).setVisible(true);
                    } else {
                        // Fallback - open dashboard without pharmacy
                        new PharmacyDashboard().setVisible(true);
                    }
                });
                
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to register pharmacy. Please try again.\n" +
                    "This could be due to:\n" +
                    "- Invalid User ID\n" +
                    "- Database connection issues\n" +
                    "- Duplicate pharmacy name",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "An error occurred during registration:\n" + ex.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
