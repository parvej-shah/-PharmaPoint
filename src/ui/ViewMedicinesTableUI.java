package ui;

import models.Medicine;
import models.Pharmacy;
import services.MedicineService;

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
        setSize(900, 600);
        setLocationRelativeTo(parentDashboard);
        setMinimumSize(new Dimension(800, 500));
        
        // Load initial data
        loadMedicinesData();
    }
    
    private void initializeComponents() {
        // Create table model
        String[] columns = {"ID", "Name", "Generic Name", "Brand", "Price", "Quantity", "Expiry Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) {
                    return JButton.class;
                }
                return String.class;
            }
        };
        
        // Create table
        medicinesTable = new JTable(tableModel);
        medicinesTable.setRowHeight(35);
        medicinesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        medicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        medicinesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        medicinesTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        medicinesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Generic Name
        medicinesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Brand
        medicinesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Price
        medicinesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Quantity
        medicinesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Expiry Date
        medicinesTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Actions
        
        // Set custom renderer and editor for Actions column
        medicinesTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        medicinesTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());
        
        // Buttons
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");
        
        // Style buttons
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        
        closeButton.setBackground(new Color(220, 20, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title label
        JLabel titleLabel = new JLabel("Medicines Inventory - " + pharmacy.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(titleLabel, gbc);
        
        // Table in scroll pane
        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Medicines"));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
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
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
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
    }
    
    private void loadMedicinesData() {
        tableModel.setRowCount(0); // Clear existing data
        
        try {
            List<Medicine> medicines = medicineService.getAllMedicines(pharmacy.getId());
            
            for (Medicine medicine : medicines) {
                Object[] row = {
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getGenericName(),
                    medicine.getBrand(),
                    String.format("$%.2f", medicine.getPrice()),
                    medicine.getQuantity(),
                    medicine.getExpiryDate(),
                    "Delete"
                };
                tableModel.addRow(row);
            }
            
            if (medicines.isEmpty()) {
                Object[] emptyRow = {"No medicines found", "", "", "", "", "", "", ""};
                tableModel.addRow(emptyRow);
            }
            
            // Update status
            setTitle("All Medicines - " + pharmacy.getName() + " (" + medicines.size() + " items)");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading medicines: " + e.getMessage(),
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
    
    // Custom button renderer for Actions column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
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
    
    // Custom button editor for Actions column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;
        
        public ButtonEditor() {
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
                if (!medicineName.equals("No medicines found")) {
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
