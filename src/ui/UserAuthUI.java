package ui;

import models.User;
import models.Pharmacy;
import services.UserService;
import services.AuthService;
import services.PharmacyService;
import utils.SessionManager;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class UserAuthUI extends JFrame {
    
    // Registration form components
    private JTextField regNameField;
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JComboBox<String> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> regRoleCombo;
    private JButton regButton;
    
    // Login form components
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    
    // Services
    private UserService userService;
    private AuthService authService;
    private PharmacyService pharmacyService;
    
    public UserAuthUI() {
        userService = new UserService();
        authService = new AuthService();
        pharmacyService = new PharmacyService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("PharmaPoint - User Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        // Registration components
        regNameField = new JTextField(20);
        regEmailField = new JTextField(20);
        regPasswordField = new JPasswordField(20);
        
        // Date of Birth Components
        dayCombo = new JComboBox<>();
        monthCombo = new JComboBox<>(new String[]{
                "01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12"
        });
        yearCombo = new JComboBox<>();
        
        // Populate day combo
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(String.format("%02d", i));
        }
        
        // Populate year combo
        for (int i = 2024; i >= 1900; i--) {
            yearCombo.addItem(Integer.toString(i));
        }
        
        regRoleCombo = new JComboBox<>(new String[]{"patient", "pharmacist"});
        regButton = new JButton("Register");
        
        // Login components
        loginEmailField = new JTextField(20);
        loginPasswordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        
        // Style buttons
        styleButton(regButton);
        styleButton(loginButton);
    }
    
    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
    }
    
    private void setupLayout() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create registration panel
        JPanel registerPanel = createRegistrationPanel();
        
        // Create login panel
        JPanel loginPanel = createLoginPanel();
        
        // Add tabs
        tabbedPane.addTab("Register", registerPanel);
        tabbedPane.addTab("Login", loginPanel);
        
        add(tabbedPane);
    }
    
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panel.add(regNameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panel.add(regEmailField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        panel.add(regPasswordField, gbc);
        
        // Date of Birth field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Date of Birth:"), gbc);
        
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dobPanel.add(new JLabel("Day:"));
        dobPanel.add(dayCombo);
        dobPanel.add(new JLabel("Month:"));
        dobPanel.add(monthCombo);
        dobPanel.add(new JLabel("Year:"));
        dobPanel.add(yearCombo);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        panel.add(dobPanel, gbc);
        
        // Role field
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        panel.add(regRoleCombo, gbc);
        
        // Register button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(regButton, gbc);
        
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panel.add(loginEmailField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panel.add(loginPasswordField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginButton, gbc);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Registration button action
        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
        
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Add Enter key support
        addEnterKeySupport();
    }
    
    private void addEnterKeySupport() {
        ActionListener regEnterListener = e -> handleRegistration();
        regNameField.addActionListener(regEnterListener);
        regEmailField.addActionListener(regEnterListener);
        regPasswordField.addActionListener(regEnterListener);
        
        ActionListener loginEnterListener = e -> handleLogin();
        loginEmailField.addActionListener(loginEnterListener);
        loginPasswordField.addActionListener(loginEnterListener);
    }
    
    private void handleRegistration() {
        try {
            // Get form data
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String role = Objects.requireNonNull(regRoleCombo.getSelectedItem()).toString();
            String dob = yearCombo.getSelectedItem() + "-" +
                        monthCombo.getSelectedItem() + "-" +
                        dayCombo.getSelectedItem();
            
            // Validate form using centralized Validator
            if (!Validator.validateRegistrationForm(name, email, password, dob, role)) {
                if (Validator.isBlank(name) || Validator.isBlank(email) || Validator.isBlank(password)) {
                    showErrorMessage("Please fill in all fields.");
                    return;
                }
                
                if (!Validator.isValidEmail(email)) {
                    showErrorMessage(Validator.getEmailErrorMessage(email));
                    return;
                }
                
                if (!Validator.isValidPassword(password)) {
                    showErrorMessage(Validator.getPasswordErrorMessage(password));
                    return;
                }
                
                if (!Validator.isValidRole(role)) {
                    showErrorMessage("Please select a valid role.");
                    return;
                }
                
                if (!Validator.isValidDateFormat(dob)) {
                    showErrorMessage("Invalid date format.");
                    return;
                }
                
                return;
            }
            
            // Create user object
            User user = new User(name, email, password, dob, role);
            
            // Additional validation for user registration
            if (!Validator.validateUserForRegistration(user)) {
                showErrorMessage("Invalid user data. Please check your inputs.");
                return;
            }
            
            // Register user
            if (userService.registerUser(user)) {
                showSuccessMessage("Registration Successful!", 
                    "User registered successfully!\n\n" +
                    "Name: " + name + "\n" +
                    "Email: " + email + "\n" +
                    "Date of Birth: " + dob + "\n" +
                    "Role: " + role);
                
                clearRegistrationForm();
            } else {
                showErrorMessage("Registration failed. This email might already be registered.");
            }
            
        } catch (Exception ex) {
            showErrorMessage("An error occurred during registration: " + ex.getMessage());
        }
    }
    
    private void handleLogin() {
        try {
            String email = loginEmailField.getText().trim();
            String password = new String(loginPasswordField.getPassword());
            
            // Basic validation
            if (email.isEmpty() || password.isEmpty()) {
                showErrorMessage("Please enter both email and password.");
                return;
            }
            
            // Validate email format
            if (!authService.isValidEmail(email)) {
                showErrorMessage("Please enter a valid email address.");
                return;
            }
            
            // Attempt authentication using AuthService
            User user = authService.authenticateUser(email, password);
            
            if (user != null) {
                // Save user in session
                SessionManager.setCurrentUser(user);
                
                // Navigate based on user role
                if ("pharmacist".equals(user.getRole())) {
                    openPharmacyDashboard(user);
                } else if ("patient".equals(user.getRole())) {
                    openPatientDashboard(user);
                } else {
                    showErrorMessage("Unknown user role: " + user.getRole());
                    SessionManager.clearSession();
                    return;
                }
                
                // Close login window
                dispose();
                clearLoginForm();
                
            } else {
                showErrorMessage("Invalid email or password. Please try again.");
            }
            
        } catch (Exception ex) {
            showErrorMessage("An error occurred during login: " + ex.getMessage());
            SessionManager.clearSession(); // Clear session on error
        }
    }
    
    private void openPharmacyDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to get pharmacy for this user
                Pharmacy pharmacy = pharmacyService.getPharmacyByUserId(user.getId());
                
                PharmacyDashboard dashboard;
                if (pharmacy != null) {
                    dashboard = new PharmacyDashboard(pharmacy);
                } else {
                    // User doesn't have a pharmacy yet
                    dashboard = new PharmacyDashboard();
                }
                
                dashboard.setVisible(true);
                
            } catch (Exception e) {
                showErrorMessage("Error opening pharmacy dashboard: " + e.getMessage());
                // Fallback (it's optional) - open dashboard without pharmacy
                new PharmacyDashboard().setVisible(true);
            }
        });
    }
    
    private void openPatientDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            // TODO: Create PatientDashboard class
            showSuccessMessage("Login Successful!", 
                "Welcome, " + user.getName() + "!\n\n" +
                "Patient Dashboard is not implemented yet.\n" +
                "Role: " + user.getRole());
            
            // For now, just show success message
            // When PatientDashboard is created, replace this with:
            // new PatientDashboard(user).setVisible(true);
        });
    }
    
    private void clearRegistrationForm() {
        regNameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        dayCombo.setSelectedIndex(0);
        monthCombo.setSelectedIndex(0);
        yearCombo.setSelectedIndex(0);
        regRoleCombo.setSelectedIndex(0);
        regNameField.requestFocus();
    }
    
    private void clearLoginForm() {
        loginEmailField.setText("");
        loginPasswordField.setText("");
        loginEmailField.requestFocus();
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserAuthUI().setVisible(true);
            }
        });
    }
}
