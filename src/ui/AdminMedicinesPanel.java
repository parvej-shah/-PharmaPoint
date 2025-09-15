package ui;

import dao.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * AdminMedicinesPanel - Panel to display all medicines ranked by quantity
 */
public class AdminMedicinesPanel extends JPanel {
    private JTable medicinesTable;
    private DefaultTableModel tableModel;
    private MedicineDAO medicineDAO;
    private JTextField searchField;
    private JLabel totalMedicinesLabel;
    private JComboBox<String> sortComboBox;

    public AdminMedicinesPanel() {
        this.medicineDAO = new MedicineDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columnNames = {"Rank", "Medicine Name", "Generic Name", "Brand", "Pharmacy", "Quantity", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        medicinesTable = new JTable(tableModel);
        medicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicinesTable.setRowHeight(30);
        medicinesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        medicinesTable.setFont(new Font("Arial", Font.PLAIN, 13));

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        medicinesTable.setRowSorter(sorter);

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Sort combo box
        sortComboBox = new JComboBox<>(new String[]{
            "Quantity (High to Low)", 
            "Quantity (Low to High)", 
            "Medicine Name (A-Z)", 
            "Medicine Name (Z-A)",
            "Price (High to Low)",
            "Price (Low to High)"
        });
        sortComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Total medicines label
        totalMedicinesLabel = new JLabel("Total Medicines: 0");
        totalMedicinesLabel.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with search and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search and sort panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);
        controlsPanel.add(Box.createHorizontalStrut(15));
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlsPanel.add(sortLabel);
        controlsPanel.add(sortComboBox);
        
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(52, 152, 219));
        controlsPanel.add(refreshButton);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.add(totalMedicinesLabel);
        
        topPanel.add(controlsPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Medicines (Ranked by Quantity)"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout());
        
        JButton viewDetailsButton = new JButton("View Details");
        JButton lowStockButton = new JButton("Low Stock Alert");
        JButton exportButton = new JButton("Export Data");
        
        styleButton(viewDetailsButton, new Color(155, 89, 182));
        styleButton(lowStockButton, new Color(231, 76, 60));
        styleButton(exportButton, new Color(39, 174, 96));
        
        bottomPanel.add(viewDetailsButton);
        bottomPanel.add(lowStockButton);
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

        // Sort combo box
        sortComboBox.addActionListener(e -> applySorting());

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
                    button.addActionListener(e -> viewMedicineDetails());
                } else if (button.getText().contains("Low Stock")) {
                    button.addActionListener(e -> showLowStockAlert());
                } else if (button.getText().contains("Export")) {
                    button.addActionListener(e -> exportMedicineData());
                }
            }
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) medicinesTable.getRowSorter();
        
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        
        updateStats();
    }

    private void applySorting() {
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) medicinesTable.getRowSorter();
        
        String selectedSort = (String) sortComboBox.getSelectedItem();
        
        if (selectedSort != null) {
            switch (selectedSort) {
                case "Quantity (High to Low)":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(5, SortOrder.DESCENDING)));
                    break;
                case "Quantity (Low to High)":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(5, SortOrder.ASCENDING)));
                    break;
                case "Medicine Name (A-Z)":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
                    break;
                case "Medicine Name (Z-A)":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.DESCENDING)));
                    break;
                case "Price (High to Low)":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(6, SortOrder.DESCENDING)));
                    break;
                case "Price (Low to High)":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(6, SortOrder.ASCENDING)));
                    break;
            }
        }
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear existing data
                tableModel.setRowCount(0);
                
                // Get all medicines from database - need to implement this method
                List<Medicine> medicines = medicineDAO.getAllMedicinesWithPharmacyInfo();
                
                // Add medicines to table with ranking
                for (int i = 0; i < medicines.size(); i++) {
                    Medicine medicine = medicines.get(i);
                    Object[] rowData = {
                        i + 1, // Rank
                        medicine.getName(),
                        medicine.getGenericName(),
                        medicine.getBrand(),
                        "Pharmacy " + medicine.getPharmacyId(), // Placeholder - could be improved
                        medicine.getQuantity(),
                        String.format("৳%.2f", medicine.getPrice())
                    };
                    tableModel.addRow(rowData);
                }
                
                // Apply default sorting (by quantity, high to low)
                applySorting();
                updateStats();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading medicines: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateStats() {
        int totalMedicines = medicinesTable.getRowCount();
        int visibleMedicines = medicinesTable.getRowSorter() != null ? 
            medicinesTable.getRowSorter().getViewRowCount() : totalMedicines;
        
        if (visibleMedicines == totalMedicines) {
            totalMedicinesLabel.setText("Total Medicines: " + totalMedicines);
        } else {
            totalMedicinesLabel.setText("Showing: " + visibleMedicines + " of " + totalMedicines + " medicines");
        }
    }

    private void viewMedicineDetails() {
        int selectedRow = medicinesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a medicine to view details.", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get medicine data from selected row
        int modelRow = medicinesTable.convertRowIndexToModel(selectedRow);
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(modelRow, i);
        }

        // Show details dialog
        showMedicineDetailsDialog(rowData);
    }

    private void showMedicineDetailsDialog(Object[] medicineData) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Medicine Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Rank:", "Medicine Name:", "Generic Name:", "Brand:", "Pharmacy:", "Quantity:", "Price:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(label, gbc);
            gbc.gridx = 1;
            JLabel value = new JLabel(medicineData[i] != null ? medicineData[i].toString() : "N/A");
            value.setFont(new Font("Arial", Font.PLAIN, 12));
            // Highlight low stock in red
            if (i == 5 && medicineData[i] != null) { // Quantity column
                try {
                    int quantity = (Integer) medicineData[i];
                    if (quantity < 10) {
                        value.setForeground(Color.RED);
                        value.setText(value.getText() + " (LOW STOCK!)");
                    }
                } catch (Exception ignored) {}
            }
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

    private void showLowStockAlert() {
        StringBuilder lowStockMessage = new StringBuilder("Low Stock Medicines (Quantity < 10):\n\n");
        boolean hasLowStock = false;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object quantityObj = tableModel.getValueAt(i, 5);
            if (quantityObj instanceof Integer) {
                int quantity = (Integer) quantityObj;
                if (quantity < 10) {
                    hasLowStock = true;
                    String medicineName = (String) tableModel.getValueAt(i, 1);
                    String pharmacy = (String) tableModel.getValueAt(i, 4);
                    lowStockMessage.append("• ").append(medicineName)
                                  .append(" (").append(pharmacy).append("): ")
                                  .append(quantity).append(" units\n");
                }
            }
        }
        
        if (!hasLowStock) {
            lowStockMessage = new StringBuilder("Good news! No medicines are currently low in stock.");
        }
        
        JOptionPane.showMessageDialog(this, 
            lowStockMessage.toString(), 
            "Low Stock Alert", 
            hasLowStock ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportMedicineData() {
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
