package ui;

import models.Medicine;
import models.Pharmacy;
import services.MedicineService;
import utils.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewMedicinesTableUI extends JFrame {
    
    private Pharmacy pharmacy;
    private PharmacyDashboard parentDashboard;
    private MedicineService medicineService;
    
    // UI Components
    private JTable medicinesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton closeButton;
    
    public ViewMedicinesTableUI(Pharmacy pharmacy, PharmacyDashboard parentDashboard) {
        this.pharmacy = pharmacy;
        this.parentDashboard = parentDashboard;
        this.medicineService = new MedicineService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("All Medicines - " + pharmacy.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(parentDashboard);
        setMinimumSize(new Dimension(900, 550));
        
        // Load initial data
        loadMedicinesData();
    }
    
    private void initializeComponents() {
        // Create table model
        String[] columns = {"ID", "Name", "Generic Name", "Brand", "Price", "Quantity", "Expiry Date", "Update", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7 || column == 8; // Only Update and Delete columns are editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7 || columnIndex == 8) {
                    return JButton.class;
                }
                return String.class;
            }
        };
        
        // Create table
        medicinesTable = new JTable(tableModel);
        medicinesTable.setRowHeight(40);
        medicinesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        medicinesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        medicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        medicinesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        medicinesTable.getColumnModel().getColumn(1).setPreferredWidth(130); // Name
        medicinesTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Generic Name
        medicinesTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Brand
        medicinesTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Price
        medicinesTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Quantity
        medicinesTable.getColumnModel().getColumn(6).setPreferredWidth(90);  // Expiry Date
        medicinesTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Update
        medicinesTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Delete
        
        // Set custom renderer and editor for action columns
        medicinesTable.getColumnModel().getColumn(7).setCellRenderer(new UpdateButtonRenderer());
        medicinesTable.getColumnModel().getColumn(7).setCellEditor(new UpdateButtonEditor());
        medicinesTable.getColumnModel().getColumn(8).setCellRenderer(new DeleteButtonRenderer());
        medicinesTable.getColumnModel().getColumn(8).setCellEditor(new DeleteButtonEditor());
        
        // Search components
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setToolTipText("Search by medicine name or generic name");
        searchButton = new JButton("Search");
        
        // Buttons
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");
        
        // Style buttons
        searchButton.setBackground(new Color(60, 179, 113));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        closeButton.setBackground(new Color(220, 20, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title label
        JLabel titleLabel = new JLabel("Medicines Inventory - " + pharmacy.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(titleLabel, gbc);
        
        // Search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Search Medicines", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(5, 5, 5, 5);
        
        searchGbc.gridx = 0; searchGbc.gridy = 0;
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(searchLabel, searchGbc);
        
        searchGbc.gridx = 1; searchGbc.gridy = 0;
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchGbc.weightx = 1.0;
        searchPanel.add(searchField, searchGbc);
        
        searchGbc.gridx = 2; searchGbc.gridy = 0;
        searchGbc.fill = GridBagConstraints.NONE;
        searchGbc.weightx = 0;
        searchPanel.add(searchButton, searchGbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.weighty = 0;
        add(searchPanel, gbc);
        
        // Table in scroll pane
        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Medicines"));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        add(scrollPane, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 5, 5, 5);
        
        btnGbc.gridx = 0; btnGbc.gridy = 0;
        buttonPanel.add(refreshButton, btnGbc);
        
        btnGbc.gridx = 1; btnGbc.gridy = 0;
        buttonPanel.add(closeButton, btnGbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(buttonPanel, gbc);
    }
    
    private void setupEventHandlers() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMedicinesData();
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        searchButton.addActionListener(e -> performSearch());
        
        // Add Enter key support for search field
        searchField.addActionListener(e -> performSearch());
    }
    
    private void loadMedicinesData() {
        loadMedicinesData(null); // Load all medicines
    }
    
    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadMedicinesData(); // Load all if search is empty
        } else {
            loadMedicinesData(keyword); // Load filtered results
        }
    }
    
    private void loadMedicinesData(String searchKeyword) {
        tableModel.setRowCount(0); // Clear existing data
        
        try {
            List<Medicine> medicines;
            
            if (searchKeyword == null || searchKeyword.isEmpty()) {
                medicines = medicineService.getAllMedicines(pharmacy.getId());
            } else {
                medicines = medicineService.searchMedicineByName(searchKeyword);
                // Filter by pharmacy ID since search returns all pharmacies
                medicines = medicines.stream()
                    .filter(medicine -> medicine.getPharmacyId() == pharmacy.getId())
                    .collect(java.util.stream.Collectors.toList());
            }
            
            for (Medicine medicine : medicines) {
                Object[] row = {
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getGenericName(),
                    medicine.getBrand(),
                    String.format("$%.2f", medicine.getPrice()),
                    medicine.getQuantity(),
                    medicine.getExpiryDate(),
                    "Update",
                    "Delete"
                };
                tableModel.addRow(row);
            }
            
            if (medicines.isEmpty()) {
                String message = searchKeyword != null ? "No medicines found for search: \"" + searchKeyword + "\"" : "No medicines found";
                Object[] emptyRow = {message, "", "", "", "", "", "", "", ""};
                tableModel.addRow(emptyRow);
            }
            
            // Update status
            String searchInfo = searchKeyword != null ? " (Search: \"" + searchKeyword + "\")" : "";
            setTitle("All Medicines - " + pharmacy.getName() + " (" + medicines.size() + " items)" + searchInfo);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading medicines: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateMedicine(int medicineId, String medicineName) {
        try {
            // Get the medicine details from service
            Medicine medicine = medicineService.getMedicineById(medicineId);
            if (medicine != null) {
                // Create update dialog with pre-filled data
                JPanel updatePanel = new JPanel(new GridLayout(6, 2, 5, 5));
                
                JTextField nameField = new JTextField(medicine.getName());
                JTextField genericField = new JTextField(medicine.getGenericName());
                JTextField brandField = new JTextField(medicine.getBrand());
                JTextField priceField = new JTextField(String.valueOf(medicine.getPrice()));
                JTextField quantityField = new JTextField(String.valueOf(medicine.getQuantity()));
                JTextField expiryField = new JTextField(medicine.getExpiryDate());
                
                updatePanel.add(new JLabel("Name:"));
                updatePanel.add(nameField);
                updatePanel.add(new JLabel("Generic Name:"));
                updatePanel.add(genericField);
                updatePanel.add(new JLabel("Brand:"));
                updatePanel.add(brandField);
                updatePanel.add(new JLabel("Price:"));
                updatePanel.add(priceField);
                updatePanel.add(new JLabel("Quantity:"));
                updatePanel.add(quantityField);
                updatePanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
                updatePanel.add(expiryField);
                
                int result = JOptionPane.showConfirmDialog(this, updatePanel, 
                    "Update Medicine: " + medicineName, JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    // Validate and update using centralized Validator
                    String name = nameField.getText().trim();
                    String generic = genericField.getText().trim();
                    String brand = brandField.getText().trim();
                    String priceText = priceField.getText().trim();
                    String quantityText = quantityField.getText().trim();
                    String expiry = expiryField.getText().trim();
                    
                    // Validate form using centralized Validator
                    if (!Validator.validateMedicineForm(name, generic, brand, priceText, quantityText, expiry)) {
                        if (Validator.isBlank(name) || Validator.isBlank(generic) || Validator.isBlank(brand) || 
                            Validator.isBlank(priceText) || Validator.isBlank(quantityText) || Validator.isBlank(expiry)) {
                            JOptionPane.showMessageDialog(this, 
                                "All fields are required.", 
                                "Validation Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        if (!Validator.isValidPositiveNumber(priceText)) {
                            JOptionPane.showMessageDialog(this, 
                                Validator.getNumberErrorMessage("Price", priceText), 
                                "Validation Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        if (!Validator.isValidPositiveInteger(quantityText)) {
                            JOptionPane.showMessageDialog(this, 
                                Validator.getNumberErrorMessage("Quantity", quantityText), 
                                "Validation Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        if (!Validator.isValidDateFormat(expiry)) {
                            JOptionPane.showMessageDialog(this, 
                                "Please enter expiry date in YYYY-MM-DD format.", 
                                "Validation Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        return;
                    }
                    
                    try {
                        double price = Double.parseDouble(priceText);
                        int quantity = Integer.parseInt(quantityText);
                        
                        // Update the medicine
                        Medicine updatedMedicine = new Medicine(medicineId, pharmacy.getId(), name, generic, brand, 
                                                              price, quantity, expiry);
                        medicineService.updateMedicine(updatedMedicine);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Medicine updated successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        loadMedicinesData(); // Refresh the table
                        
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid number format for price or quantity.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Medicine not found. Please refresh the table.",
                    "Medicine Not Found",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading medicine details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMedicine(int medicineId, String medicineName) {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete '" + medicineName + "'?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (medicineService.deleteMedicine(medicineId)) {
                    JOptionPane.showMessageDialog(this,
                        "Medicine '" + medicineName + "' has been deleted successfully.",
                        "Delete Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh data
                    loadMedicinesData();
                    
                    // Refresh parent dashboard
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete medicine. Please try again.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting medicine: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom button renderer for Update column
    class UpdateButtonRenderer extends JButton implements TableCellRenderer {
        public UpdateButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(60, 179, 113));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 10));
            setFocusPainted(false);
            setBorderPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "Update");
            return this;
        }
    }
    
    // Custom button renderer for Delete column
    class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(220, 20, 60));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 10));
            setFocusPainted(false);
            setBorderPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "Delete");
            return this;
        }
    }
    
    // Custom button editor for Update column
    class UpdateButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;
        
        public UpdateButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(60, 179, 113));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 10));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = value != null ? value.toString() : "Update";
            button.setText(label);
            selectedRow = row;
            clicked = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // Get medicine ID and name from the selected row
                int medicineId = (Integer) tableModel.getValueAt(selectedRow, 0);
                String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
                
                // Only update if it's not the "No medicines found" row
                if (!medicineName.startsWith("No medicines found")) {
                    SwingUtilities.invokeLater(() -> updateMedicine(medicineId, medicineName));
                }
            }
            clicked = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
    
    // Custom button editor for Delete column
    class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;
        
        public DeleteButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(220, 20, 60));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 10));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = value != null ? value.toString() : "Delete";
            button.setText(label);
            selectedRow = row;
            clicked = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // Get medicine ID and name from the selected row
                int medicineId = (Integer) tableModel.getValueAt(selectedRow, 0);
                String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
                
                // Only delete if it's not the "No medicines found" row
                if (!medicineName.startsWith("No medicines found")) {
                    SwingUtilities.invokeLater(() -> deleteMedicine(medicineId, medicineName));
                }
            }
            clicked = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
