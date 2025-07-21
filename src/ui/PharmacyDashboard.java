package ui;

import models.Pharmacy;
import models.User;
import services.MedicineService;
import services.PharmacyService;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PharmacyDashboard extends JFrame {
    
    private Pharmacy pharmacy;
    private User user;
    private MedicineService medicineService;
    private PharmacyService pharmacyService;
    
    // UI Components
    private JLabel welcomeLabel;
    private JButton addMedicineButton;
    private JButton viewMedicinesButton;
    private JButton createPharmacyButton;
    private JButton logoutButton;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    
    public PharmacyDashboard(User user, Pharmacy pharmacy) {
        this.user = user;
        this.pharmacy = pharmacy;
        this.medicineService = new MedicineService();
        this.pharmacyService = new PharmacyService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("Pharmacy Dashboard - PharmaPoint");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400));
    }
    
    // Constructor for user without pharmacy
    public PharmacyDashboard(User user) {
        this(user, null);
    }
    
    private void initializeComponents() {
        // Welcome label
        if (pharmacy != null) {
            welcomeLabel = new JLabel("Welcome to " + pharmacy.getName() + " Dashboard", 
                                    SwingConstants.CENTER);
        } else {
            welcomeLabel = new JLabel("Welcome to Pharmacy Dashboard", 
                                    SwingConstants.CENTER);
        }
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Buttons
        addMedicineButton = new JButton("Add New Medicine");
        viewMedicinesButton = new JButton("View All Medicines");
        createPharmacyButton = new JButton("Create Pharmacy");
        logoutButton = new JButton("Logout");
        
        // Style buttons
        styleButton(addMedicineButton, new Color(34, 139, 34)); // Forest Green
        styleButton(viewMedicinesButton, new Color(70, 130, 180)); // Steel Blue
        styleButton(createPharmacyButton, new Color(255, 140, 0)); // Dark Orange
        styleButton(logoutButton, new Color(220, 20, 60)); // Crimson
        
        // Enable/disable buttons based on pharmacy status
        updateButtonStates();
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void updateButtonStates() {
        boolean hasPharmacy = pharmacy != null;
        
        addMedicineButton.setEnabled(hasPharmacy);
        viewMedicinesButton.setEnabled(hasPharmacy);
        createPharmacyButton.setVisible(!hasPharmacy);
        
        if (!hasPharmacy) {
            welcomeLabel.setText("<html><center>Welcome to Pharmacy Dashboard<br>" +
                               "<small>Please create a pharmacy to start managing medicines</small></center></html>");
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Button panel with GridLayout
        buttonPanel = new JPanel(new GridLayout(0, 1, 10, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Add buttons to panel
        if (pharmacy != null) {
            buttonPanel.add(addMedicineButton);
            buttonPanel.add(viewMedicinesButton);
        } else {
            buttonPanel.add(createPharmacyButton);
        }
        buttonPanel.add(logoutButton);
        
        // Pharmacy info panel (if pharmacy exists)
        if (pharmacy != null) {
            JPanel infoPanel = createPharmacyInfoPanel();
            mainPanel.add(infoPanel, BorderLayout.EAST);
        }
        
        // Add components to main panel
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPharmacyInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Pharmacy Information"));
        infoPanel.setPreferredSize(new Dimension(250, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Pharmacy details
        JLabel nameLabel = new JLabel("Name: " + pharmacy.getName());
        JLabel addressLabel = new JLabel("<html>Address: " + pharmacy.getAddress() + "</html>");
        JLabel areaLabel = new JLabel("Area: " + pharmacy.getArea());
        JLabel idLabel = new JLabel("ID: " + pharmacy.getId());
        
        // Style labels
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        nameLabel.setFont(labelFont);
        addressLabel.setFont(labelFont);
        areaLabel.setFont(labelFont);
        idLabel.setFont(labelFont);
        
        // Add components with GridBagLayout
        gbc.gridy = 0;
        infoPanel.add(nameLabel, gbc);
        
        gbc.gridy = 1;
        infoPanel.add(addressLabel, gbc);
        
        gbc.gridy = 2;
        infoPanel.add(areaLabel, gbc);
        
        gbc.gridy = 3;
        infoPanel.add(idLabel, gbc);
        
        // Add vertical glue to push content to top
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(Box.createVerticalGlue(), gbc);
        
        return infoPanel;
    }
    
    private void setupEventHandlers() {
        addMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddMedicineForm();
            }
        });
        
        viewMedicinesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openViewMedicinesWindow();
            }
        });
        
        createPharmacyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreatePharmacyForm();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
    }
    
    private void openAddMedicineForm() {
        SwingUtilities.invokeLater(() -> {
            AddMedicineFormUI addForm = new AddMedicineFormUI(pharmacy, this);
            addForm.setVisible(true);
        });
    }
    
    private void openViewMedicinesWindow() {
        SwingUtilities.invokeLater(() -> {
            ViewMedicinesTableUI viewWindow = new ViewMedicinesTableUI(pharmacy, this);
            viewWindow.setVisible(true);
        });
    }
    
    private void openCreatePharmacyForm() {
        SwingUtilities.invokeLater(() -> {
            PharmacyRegistrationFormUI createForm = new PharmacyRegistrationFormUI();
            createForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            createForm.setVisible(true);
            
            // TODO: Add callback to refresh dashboard when pharmacy is created
            dispose(); // Close current dashboard
        });
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Clear the user session
            SessionManager.clearSession();
            
            dispose();
            // Open login window
            SwingUtilities.invokeLater(() -> {
                new UserAuthUI().setVisible(true);
            });
        }
    }
    
    // Method to refresh the dashboard (useful when medicines are added/removed)
    public void refreshDashboard() {
        // This can be called from child windows to refresh the dashboard
        repaint();
        revalidate();
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Test with dummy data
            User testUser = new User(1, "Test Pharmacist", "test@pharmacy.com", "password", "1990-01-01", "pharmacist");
            Pharmacy testPharmacy = new Pharmacy(1, 1, "City Pharmacy", "123 Main St", "Downtown");
            
            new PharmacyDashboard(testUser, testPharmacy).setVisible(true);
        });
    }
}
