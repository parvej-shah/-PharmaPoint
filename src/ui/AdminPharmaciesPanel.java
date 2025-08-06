package ui;

import dao.PharmacyDAO;
import models.Pharmacy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * AdminPharmaciesPanel - Panel to display and manage all pharmacies in a table
 */
public class AdminPharmaciesPanel extends JPanel {
    private JTable pharmaciesTable;
    private DefaultTableModel tableModel;
    private PharmacyDAO pharmacyDAO;
    private JTextField searchField;
    private JLabel totalPharmaciesLabel;

    public AdminPharmaciesPanel() {
        this.pharmacyDAO = new PharmacyDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columnNames = {"ID", "Pharmacy Name", "Owner/Manager", "Address", "Area", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        pharmaciesTable = new JTable(tableModel);
        pharmaciesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pharmaciesTable.setRowHeight(30);
        pharmaciesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        pharmaciesTable.setFont(new Font("Arial", Font.PLAIN, 13));

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        pharmaciesTable.setRowSorter(sorter);

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Total pharmacies label
        totalPharmaciesLabel = new JLabel("Total Pharmacies: 0");
        totalPharmaciesLabel.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with search and stats
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search Pharmacies:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        styleButton(refreshButton, new Color(52, 152, 219));
        searchPanel.add(refreshButton);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.add(totalPharmaciesLabel);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(pharmaciesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Registered Pharmacies"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout());
        
        JButton viewDetailsButton = new JButton("👁 View Details");
        JButton viewMedicinesButton = new JButton("💊 View Medicines");
        JButton exportButton = new JButton("📄 Export Data");
        
        styleButton(viewDetailsButton, new Color(155, 89, 182));
        styleButton(viewMedicinesButton, new Color(230, 126, 34));
        styleButton(exportButton, new Color(39, 174, 96));
        
        bottomPanel.add(viewDetailsButton);
        bottomPanel.add(viewMedicinesButton);
        bottomPanel.add(exportButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Search functionality
        searchField.addActionListener(e -> filterTable());
        
        // Real-time search as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        // Refresh button
        Component[] components = ((JPanel) getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if (subComp instanceof JButton && ((JButton) subComp).getText().contains("Refresh")) {
                        ((JButton) subComp).addActionListener(e -> refreshData());
                        break;
                    }
                }
            }
        }

        // Bottom panel buttons
        Component[] bottomComponents = ((JPanel) getComponent(2)).getComponents();
        for (Component comp : bottomComponents) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().contains("View Details")) {
                    button.addActionListener(e -> viewPharmacyDetails());
                } else if (button.getText().contains("View Medicines")) {
                    button.addActionListener(e -> viewPharmacyMedicines());
                } else if (button.getText().contains("Export")) {
                    button.addActionListener(e -> exportPharmacyData());
                }
            }
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) pharmaciesTable.getRowSorter();
        
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        
        updateStats();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear existing data
                tableModel.setRowCount(0);
                
                // Get all pharmacies from database
                List<Pharmacy> pharmacies = pharmacyDAO.getPharmacies();
                
                // Add pharmacies to table
                for (Pharmacy pharmacy : pharmacies) {
                    // Get owner name (you might need to implement this in PharmacyDAO)
                    String ownerName = "N/A"; // Placeholder
                    
                    Object[] rowData = {
                        pharmacy.getId(),
                        pharmacy.getName(),
                        ownerName,
                        pharmacy.getAddress(),
                        pharmacy.getArea(),
                        "Active" // Status placeholder
                    };
                    tableModel.addRow(rowData);
                }
                
                updateStats();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading pharmacies: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateStats() {
        int totalPharmacies = pharmaciesTable.getRowCount();
        int visiblePharmacies = pharmaciesTable.getRowSorter() != null ? 
            pharmaciesTable.getRowSorter().getViewRowCount() : totalPharmacies;
        
        if (visiblePharmacies == totalPharmacies) {
            totalPharmaciesLabel.setText("Total Pharmacies: " + totalPharmacies);
        } else {
            totalPharmaciesLabel.setText("Showing: " + visiblePharmacies + " of " + totalPharmacies + " pharmacies");
        }
    }

    private void viewPharmacyDetails() {
        int selectedRow = pharmaciesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a pharmacy to view details.", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get pharmacy data from selected row
        int modelRow = pharmaciesTable.convertRowIndexToModel(selectedRow);
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(modelRow, i);
        }

        // Show details dialog
        showPharmacyDetailsDialog(rowData);
    }

    private void showPharmacyDetailsDialog(Object[] pharmacyData) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pharmacy Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"ID:", "Pharmacy Name:", "Owner/Manager:", "Address:", "Area:", "Status:"};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            JLabel value = new JLabel(pharmacyData[i] != null ? pharmacyData[i].toString() : "N/A");
            value.setFont(new Font("Arial", Font.PLAIN, 12));
            panel.add(value, gbc);
        }

        dialog.add(panel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewPharmacyMedicines() {
        int selectedRow = pharmaciesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a pharmacy to view medicines.", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = pharmaciesTable.convertRowIndexToModel(selectedRow);
        int pharmacyId = (Integer) tableModel.getValueAt(modelRow, 0);
        String pharmacyName = (String) tableModel.getValueAt(modelRow, 1);
        
        // Show medicines dialog (you can implement this later)
        JOptionPane.showMessageDialog(this, 
            "Medicine list for " + pharmacyName + " (ID: " + pharmacyId + ") will be shown in a separate window.", 
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportPharmacyData() {
        JOptionPane.showMessageDialog(this, 
            "Export functionality will be implemented in future version.", 
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
