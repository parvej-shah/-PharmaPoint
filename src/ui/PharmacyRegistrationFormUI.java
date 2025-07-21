package ui;

import models.Pharmacy;
import models.User;
import services.PharmacyService;
import utils.SessionManager;

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
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
    }
    
    private void initializeComponents() {
        userIdField = new JTextField(20);
        pharmacyNameField = new JTextField(20);
        addressField = new JTextField(20);
        areaField = new JTextField(20);
        registerButton = new JButton("Register Pharmacy");
        
        // Auto-fill user ID from the passed user object
        if (user != null) {
            userIdField.setText(String.valueOf(user.getId()));
            userIdField.setEditable(false); // Make it read-only since it's auto-filled
            userIdField.setBackground(new Color(240, 240, 240)); // Light gray to indicate read-only
        }
        
        // Set button style
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // User ID Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Pharmacy Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        add(new JLabel("Pharmacy Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        add(pharmacyNameField, gbc);
        
        // Address Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(addressField, gbc);
        
        // Area Label and Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        add(new JLabel("Area:"), gbc);
        
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
        
        // Validate input fields
        if (userIdText.isEmpty() || pharmacyName.isEmpty() || 
            address.isEmpty() || area.isEmpty()) {
            
            JOptionPane.showMessageDialog(
                this,
                "Please fill in all fields.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Validate User ID is numeric
        int userId;
        try {
            userId = Integer.parseInt(userIdText);
            if (userId <= 0) {
                throw new NumberFormatException("User ID must be positive");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "User ID must be a valid positive number.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        
        // Create Pharmacy object
        Pharmacy pharmacy = new Pharmacy(userId, pharmacyName, address, area);
        
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
                        new PharmacyDashboard(user, createdPharmacy).setVisible(true);
                    } else {
                        // Fallback - open dashboard without pharmacy
                        new PharmacyDashboard(user).setVisible(true);
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
