package ui;

import models.Medicine;
import models.Pharmacy;
import services.MedicineService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ViewMedicinesTableUI extends JFrame {
    
    // Simple variables to store data
    private Pharmacy pharmacy;
    private PharmacyDashboard parentDashboard;
    private MedicineService medicineService;
    
    // UI Components
    private JTable medicinesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton closeButton;

    
    public ViewMedicinesTableUI(Pharmacy pharmacy, PharmacyDashboard parentDashboard) {
        this.pharmacy = pharmacy;
        this.parentDashboard = parentDashboard;
        this.medicineService = new MedicineService();
        
        createComponents();
        arrangeComponents();
        addButtonActions();
        
        // Set up the window
        setTitle("All Medicines - " + pharmacy.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(parentDashboard);

        loadAllMedicines();
    }
    
    // Create all the UI components
    private void createComponents() {
        // Create simple table with column names
        String[] columns = {"ID", "Name", "Generic Name", "Brand", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0);
        
        // Create the table
        medicinesTable = new JTable(tableModel);
        medicinesTable.setRowHeight(30);
        medicinesTable.setFont(new Font("Arial", Font.PLAIN, 22));
        medicinesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 25));
        
        // Set column widths
        medicinesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        medicinesTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        medicinesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Generic Name
        medicinesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Brand
        medicinesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Price
        medicinesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Quantity
        
        // Create search components
        searchField = new JTextField(40);
        searchField.setFont(new Font("Arial", Font.PLAIN, 25));
        searchButton = new JButton("Search");
        
        // Create action buttons
        refreshButton = new JButton("Refresh");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        closeButton = new JButton("Close");
        
        // Style the buttons with simple colors
        searchButton.setBackground(Color.GREEN);
        searchButton.setForeground(Color.WHITE);
        
        refreshButton.setBackground(Color.BLUE);
        refreshButton.setForeground(Color.WHITE);
        
        updateButton.setBackground(Color.ORANGE);
        updateButton.setForeground(Color.WHITE);
        
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        
        closeButton.setBackground(Color.GRAY);
        closeButton.setForeground(Color.WHITE);
    }
    
    // Arrange components on the window
    private void arrangeComponents() {
        setLayout(new BorderLayout());
        
        // Title at the top
        JLabel titleLabel = new JLabel("Medicines in " + pharmacy.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        
        // Top panel with search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table in the center
        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Medicines List"));
        
        // Add components to main window
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // Add actions to buttons
    private void addButtonActions() {
        // Refresh button - reload all medicines
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAllMedicines();
            }
        });
        
        // Search button - search for medicines
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchMedicines();
            }
        });
        
        // Allow Enter key in search field
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchMedicines();
            }
        });
        
        // Update button - update selected medicine
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSelectedMedicine();
            }
        });
        
        // Delete button - delete selected medicine
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedMedicine();
            }
        });
        
        // Close button - close window
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Double-click on table row to update
        medicinesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateSelectedMedicine();
                }
            }
        });
    }
    
    // Load all medicines into the table
    private void loadAllMedicines() {
        // Clear the table first
        tableModel.setRowCount(0);
        
        try {
            // Get all medicines for this pharmacy
            List<Medicine> medicines = medicineService.getAllMedicines(pharmacy.getId());
            
            // Add each medicine to the table
            for (Medicine medicine : medicines) {
                Object[] row = {
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getGenericName(),
                    medicine.getBrand(),
                    "$" + medicine.getPrice(),
                    medicine.getQuantity()
                };
                tableModel.addRow(row);
            }
            
            // Show message if no medicines found
            if (medicines.isEmpty()) {
                Object[] emptyRow = {"No medicines found", "", "", "", "", ""};
                tableModel.addRow(emptyRow);
            }
            
            // Update window title with count
            setTitle("All Medicines - " + pharmacy.getName() + " (" + medicines.size() + " medicines)");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading medicines: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Search for medicines
    private void searchMedicines() {
        String searchText = searchField.getText().trim();
        
        // If search is empty, load all medicines
        if (searchText.isEmpty()) {
            loadAllMedicines();
            return;
        }
        
        // Clear the table first
        tableModel.setRowCount(0);
        
        try {
            // Search for medicines
            List<Medicine> medicines = medicineService.searchMedicineByName(searchText);
            
            // Filter to only show medicines from this pharmacy
            int count = 0;
            for (Medicine medicine : medicines) {
                if (medicine.getPharmacyId() == pharmacy.getId()) {
                    Object[] row = {
                        medicine.getId(),
                        medicine.getName(),
                        medicine.getGenericName(),
                        medicine.getBrand(),
                        "$" + medicine.getPrice(),
                        medicine.getQuantity()
                    };
                    tableModel.addRow(row);
                    count++;
                }
            }
            
            // Show message if no medicines found
            if (count == 0) {
                Object[] emptyRow = {"No medicines found for: " + searchText, "", "", "", "", ""};
                tableModel.addRow(emptyRow);
            }
            
            // Update window title
            setTitle("Search Results - " + pharmacy.getName() + " (" + count + " medicines found)");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching medicines: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Update the selected medicine
    private void updateSelectedMedicine() {
        int selectedRow = medicinesTable.getSelectedRow();
        
        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a medicine to update.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get medicine ID from selected row
        Object idValue = tableModel.getValueAt(selectedRow, 0);
        if (!(idValue instanceof Integer)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot update this row.",
                "Invalid Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int medicineId = (Integer) idValue;
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Call the update method
        showUpdateDialog(medicineId, medicineName);
    }
    
    // Delete the selected medicine
    private void deleteSelectedMedicine() {
        int selectedRow = medicinesTable.getSelectedRow();
        
        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a medicine to delete.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get medicine ID from selected row
        Object idValue = tableModel.getValueAt(selectedRow, 0);
        if (!(idValue instanceof Integer)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot delete this row.",
                "Invalid Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int medicineId = (Integer) idValue;
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Ask for confirmation
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + medicineName + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Delete the medicine
                if (medicineService.deleteMedicine(medicineId)) {
                    JOptionPane.showMessageDialog(this,
                        "Medicine deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    loadAllMedicines();
                    
                    // Refresh parent dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete medicine.",
                        "Error",
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
    
    // Show dialog to update medicine details
    private void showUpdateDialog(int medicineId, String medicineName) {
        try {
            // Get current medicine details
            Medicine medicine = medicineService.getMedicineById(medicineId);
            if (medicine == null) {
                JOptionPane.showMessageDialog(this,
                    "Medicine not found. Please refresh the table.",
                    "Medicine Not Found",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create simple input dialog
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            JTextField nameField = new JTextField(medicine.getName());
            JTextField genericField = new JTextField(medicine.getGenericName());
            JTextField brandField = new JTextField(medicine.getBrand());
            JTextField priceField = new JTextField(String.valueOf(medicine.getPrice()));
            JTextField quantityField = new JTextField(String.valueOf(medicine.getQuantity()));
            JTextField expiryField = new JTextField(medicine.getExpiryDate());
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Generic Name:"));
            panel.add(genericField);
            panel.add(new JLabel("Brand:"));
            panel.add(brandField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
            panel.add(expiryField);
            
            // Show the dialog
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Update Medicine: " + medicineName, JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                // Get the entered values
                String name = nameField.getText().trim();
                String generic = genericField.getText().trim();
                String brand = brandField.getText().trim();
                String priceText = priceField.getText().trim();
                String quantityText = quantityField.getText().trim();
                String expiry = expiryField.getText().trim();
                // Simple validation
                if (name.isEmpty() || generic.isEmpty() || brand.isEmpty() || 
                    priceText.isEmpty() || quantityText.isEmpty() || expiry.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "All fields are required!", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // Convert price and quantity
                    double price = Double.parseDouble(priceText);
                    int quantity = Integer.parseInt(quantityText);
                    if (price < 0 || quantity < 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Price and quantity must be positive numbers!", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Create updated medicine (expiry editable)
                    Medicine updatedMedicine = new Medicine(medicineId, pharmacy.getId(), 
                        name, generic, brand, price, quantity, expiry);
                    // Update in database
                    medicineService.updateMedicine(updatedMedicine);
                    JOptionPane.showMessageDialog(this, 
                        "Medicine updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    // Refresh the table
                    loadAllMedicines();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter valid numbers for price and quantity!", 
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading medicine details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
