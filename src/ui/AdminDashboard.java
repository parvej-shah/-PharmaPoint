package ui;

import models.User;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private User currentAdmin;
    private JTabbedPane tabbedPane;
    
    // Tab panels
    private AdminUsersPanel usersPanel;
    private AdminPharmaciesPanel pharmaciesPanel;
    private AdminMedicinesPanel medicinesPanel;

    public AdminDashboard(User admin) {
        this.currentAdmin = admin;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("PharmaPoint Admin Dashboard - " + admin.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // Create tab panels
        usersPanel = new AdminUsersPanel();
        pharmaciesPanel = new AdminPharmaciesPanel();
        medicinesPanel = new AdminMedicinesPanel();
        
        // Add tabs
        tabbedPane.addTab("Users", usersPanel);
        tabbedPane.addTab("Pharmacies", pharmaciesPanel);
        tabbedPane.addTab("Medicines", medicinesPanel);
        
        // Style the tabbed pane
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content (tabbed pane)
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title and admin info
        JLabel titleLabel = new JLabel("PharmaPoint Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel adminLabel = new JLabel("Welcome, " + currentAdmin.getName());
        adminLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        adminLabel.setForeground(Color.WHITE);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(adminLabel);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(231, 76, 60));
        logoutButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                SessionManager.clearSession();
                dispose();
                new UserAuthUI().setVisible(true);
            }
        });
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel footerLabel = new JLabel("PharmaPoint Admin System © 2025");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        footerLabel.setForeground(Color.WHITE);
        
        panel.add(footerLabel);
        return panel;
    }

    private void setupEventHandlers() {
        // Tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // Users tab
                    usersPanel.refreshData();
                    break;
                case 1: // Pharmacies tab
                    pharmaciesPanel.refreshData();
                    break;
                case 2: // Medicines tab
                    medicinesPanel.refreshData();
                    break;
            }
        });
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(90, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void showAdminDashboard(User user) {
        if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
            SwingUtilities.invokeLater(() -> {
                new AdminDashboard(user).setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Access denied. Admin privileges required.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
