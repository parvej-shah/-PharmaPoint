package ui;

import models.Medicine;
import models.Pharmacy;
import models.SaleItem;
import services.MedicineService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SellMedicineUI extends JFrame {
    
    private Pharmacy pharmacy;
    private PharmacyDashboard parentDashboard;
    private MedicineService medicineService;
    private List<Medicine> availableMedicines;
    private List<SaleItem> cartItems;
    
    // UI Components
    private JTextField medicineSearchField;
    private JList<Medicine> medicineList;
    private DefaultListModel<Medicine> medicineListModel;
    private JSpinner quantitySpinner;
    private JButton addToCartButton;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;
    private JButton removeFromCartButton;
    private JButton clearCartButton;
    private JButton processInvoiceButton;
    private JButton cancelButton;
    
    public SellMedicineUI(Pharmacy pharmacy, PharmacyDashboard parentDashboard) {
        this.pharmacy = pharmacy;
        this.parentDashboard = parentDashboard;
        this.medicineService = new MedicineService();
        this.cartItems = new ArrayList<>();
        
        initializeData();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame configuration
        setTitle("Sell Medicine - " + pharmacy.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(parentDashboard);
        setMinimumSize(new Dimension(900, 650));
    }
    
    private void initializeData() {
        availableMedicines = medicineService.getAllMedicines(pharmacy.getId());
        // Filter out medicines with zero quantity
        availableMedicines = availableMedicines.stream()
                .filter(medicine -> medicine.getQuantity() > 0)
                .collect(Collectors.toList());
    }
    
    private void initializeComponents() {
        // Medicine search components
        medicineSearchField = new JTextField();
        medicineSearchField.setFont(new Font("Arial", Font.PLAIN, 16));
        medicineSearchField.setPreferredSize(new Dimension(0, 35));
        
        medicineListModel = new DefaultListModel<>();
        updateMedicineList("");
        medicineList = new JList<>(medicineListModel);
        medicineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineList.setCellRenderer(new MedicineListCellRenderer());
        
        // Quantity spinner
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        quantitySpinner.setPreferredSize(new Dimension(90, 35));
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Buttons
        addToCartButton = new JButton("Add to Cart");
        removeFromCartButton = new JButton("Remove Selected");
        clearCartButton = new JButton("Clear Cart");
        processInvoiceButton = new JButton("Process Sale");
        cancelButton = new JButton("Cancel");
        
        styleButton(addToCartButton, new Color(34, 139, 34));
        styleButton(removeFromCartButton, new Color(255, 140, 0));
        styleButton(clearCartButton, new Color(220, 20, 60));
        styleButton(processInvoiceButton, new Color(70, 130, 180));
        styleButton(cancelButton, new Color(128, 128, 128));
        
        // Cart table
        String[] columnNames = {"Medicine", "Brand", "Unit Price", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 13));
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Total label
        totalLabel = new JLabel("Total: 0.00 BDT");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        updateButtonStates();
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left panel - Medicine selection
        JPanel leftPanel = createMedicineSelectionPanel();
        leftPanel.setPreferredSize(new Dimension(350, 0));
        
        // Right panel - Cart
        JPanel rightPanel = createCartPanel();
        
        // Bottom panel - Actions
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createMedicineSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Select Medicine", 
            0, 0, new Font("Arial", Font.BOLD, 16)));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Search Medicine:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(medicineSearchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        // Medicine list
        JScrollPane listScrollPane = new JScrollPane(medicineList);
        listScrollPane.setPreferredSize(new Dimension(0, 350));
        
        // Quantity panel
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        quantityPanel.add(qtyLabel);
        quantityPanel.add(quantitySpinner);
        quantityPanel.add(addToCartButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(listScrollPane, BorderLayout.CENTER);
        panel.add(quantityPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Shopping Cart", 
            0, 0, new Font("Arial", Font.BOLD, 16)));
        
        // Cart table
        JScrollPane tableScrollPane = new JScrollPane(cartTable);
        
        // Cart controls
        JPanel cartControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cartControlsPanel.add(removeFromCartButton);
        cartControlsPanel.add(clearCartButton);
        
        // Total panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        totalPanel.add(totalLabel, BorderLayout.EAST);
        
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(cartControlsPanel, BorderLayout.NORTH);
        panel.add(totalPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        panel.add(cancelButton);
        panel.add(processInvoiceButton);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Medicine search with autocomplete
        medicineSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = medicineSearchField.getText().toLowerCase();
                updateMedicineList(searchText);
            }
        });
        
        // Double-click on medicine to add to cart
        medicineList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSelectedMedicineToCart();
                }
            }
        });
        
        // Add to cart button
        addToCartButton.addActionListener(e -> addSelectedMedicineToCart());
        
        // Remove from cart button
        removeFromCartButton.addActionListener(e -> removeSelectedFromCart());
        
        // Clear cart button
        clearCartButton.addActionListener(e -> clearCart());
        
        // Process invoice button
        processInvoiceButton.addActionListener(e -> processInvoice());
        
        // Cancel button
        cancelButton.addActionListener(e -> dispose());
        
        // Update button states when cart table selection changes
        cartTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
    }
    
    private void updateMedicineList(String searchText) {
        medicineListModel.clear();
        
        List<Medicine> filteredMedicines = availableMedicines.stream()
                .filter(medicine -> 
                    medicine.getName().toLowerCase().contains(searchText) ||
                    medicine.getGenericName().toLowerCase().contains(searchText) ||
                    medicine.getBrand().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        
        for (Medicine medicine : filteredMedicines) {
            medicineListModel.addElement(medicine);
        }
    }
    
    private void addSelectedMedicineToCart() {
        Medicine selectedMedicine = medicineList.getSelectedValue();
        if (selectedMedicine == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine first.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity = (Integer) quantitySpinner.getValue();
        
        // Check if medicine already exists in cart
        SaleItem existingItem = cartItems.stream()
                .filter(item -> item.getMedicine().getId() == selectedMedicine.getId())
                .findFirst()
                .orElse(null);
        
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity > selectedMedicine.getQuantity()) {
                JOptionPane.showMessageDialog(this, 
                    "Total quantity (" + newQuantity + ") exceeds available stock (" + 
                    selectedMedicine.getQuantity() + ").", 
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }
            existingItem.setQuantity(newQuantity);
        } else {
            if (quantity > selectedMedicine.getQuantity()) {
                JOptionPane.showMessageDialog(this, 
                    "Requested quantity (" + quantity + ") exceeds available stock (" + 
                    selectedMedicine.getQuantity() + ").", 
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cartItems.add(new SaleItem(selectedMedicine, quantity));
        }
        
        updateCartTable();
        quantitySpinner.setValue(1); // Reset quantity
    }
    
    private void removeSelectedFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            cartItems.remove(selectedRow);
            updateCartTable();
        }
    }
    
    private void clearCart() {
        if (!cartItems.isEmpty()) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to clear the cart?", 
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                cartItems.clear();
                updateCartTable();
            }
        }
    }
    
    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        
        for (SaleItem item : cartItems) {
            Medicine medicine = item.getMedicine();
            Object[] row = {
                medicine.getName(),
                medicine.getBrand(),
                String.format("%.2f", medicine.getPrice()),
                item.getQuantity(),
                String.format("%.2f", item.getSubtotal())
            };
            cartTableModel.addRow(row);
        }
        
        updateTotal();
        updateButtonStates();
    }
    
    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
        totalLabel.setText(String.format("Total: %.2f BDT", total));
    }
    
    private void updateButtonStates() {
        boolean hasSelection = cartTable.getSelectedRow() != -1;
        boolean hasItems = !cartItems.isEmpty();
        
        removeFromCartButton.setEnabled(hasSelection);
        clearCartButton.setEnabled(hasItems);
        processInvoiceButton.setEnabled(hasItems);
    }
    
    private void processInvoice() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", 
                                        "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Open the final invoice form
        SwingUtilities.invokeLater(() -> {
            FinalInvoiceFormUI finalInvoiceForm = new FinalInvoiceFormUI(this, pharmacy, cartItems);
            finalInvoiceForm.setVisible(true);
        });
    }
    
    // Method to clear cart and refresh data after successful sale
    public void clearCartAndRefresh() {
        cartItems.clear();
        updateCartTable();
        initializeData(); // Refresh available medicines
        updateMedicineList(medicineSearchField.getText().toLowerCase());
        
        // Refresh parent dashboard
        if (parentDashboard != null) {
            parentDashboard.refreshDashboard();
        }
    }
    
    // Custom cell renderer for medicine list
    private class MedicineListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Medicine) {
                Medicine medicine = (Medicine) value;
                setText(String.format("<html><b>%s</b><br/>%s - Stock: %d - %.2f BDT</html>", 
                       medicine.getName(), 
                       medicine.getBrand(), 
                       medicine.getQuantity(), 
                       medicine.getPrice()));
            }
            
            return this;
        }
    }
}
