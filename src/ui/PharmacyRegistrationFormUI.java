package ui;

import models.Pharmacy;
import services.PharmacyService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PharmacyRegistrationFormUI extends JFrame {
    
    private JTextField userIdField;
    private JTextField pharmacyNameField;
    private JTextField addressField;
    private JTextField areaField;
    private JButton registerButton;
    
    private PharmacyService pharmacyService;
    
    public PharmacyRegistrationFormUI() {
        pharmacyService = new PharmacyService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("Pharmacy Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        add(new JLabel("User ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(userIdField, gbc);
        
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
        userIdField.addActionListener(enterKeyListener);
        pharmacyNameField.addActionListener(enterKeyListener);
        addressField.addActionListener(enterKeyListener);
        areaField.addActionListener(enterKeyListener);
    }
    
    private void handleRegistration() {
        // Get input values
        String userIdText = userIdField.getText().trim();
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
                
                // Clear form fields after successful registration
                clearForm();
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
    
    private void clearForm() {
        userIdField.setText("");
        pharmacyNameField.setText("");
        addressField.setText("");
        areaField.setText("");
        userIdField.requestFocus(); // Set focus back to first field
    }
    
    // Main method for testing the form
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PharmacyRegistrationFormUI().setVisible(true);
            }
        });
    }
}
