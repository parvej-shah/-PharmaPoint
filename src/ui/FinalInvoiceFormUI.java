package ui;

import models.Invoice;
import models.Pharmacy;
import models.SaleItem;
import services.InvoiceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FinalInvoiceFormUI extends JDialog {
    
    private Pharmacy pharmacy;
    private List<SaleItem> saleItems;
    private SellMedicineUI parentWindow;
    private InvoiceService invoiceService;
    
    // UI Components
    private JTextField patientNameField;
    private JTextField patientPhoneField;
    private JTable medicinesTable;
    private DefaultTableModel tableModel;
    private JLabel totalAmountLabel;
    private JLabel pharmacyInfoLabel;
    private JButton confirmSaleButton;
    private JButton cancelButton;
    
    public FinalInvoiceFormUI(SellMedicineUI parentWindow, Pharmacy pharmacy, List<SaleItem> saleItems) {
        super(parentWindow, "Finalize Invoice - " + pharmacy.getName(), true);
        this.pharmacy = pharmacy;
        this.saleItems = saleItems;
        this.parentWindow = parentWindow;
        this.invoiceService = new InvoiceService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateMedicinesTable();
        
        // Frame configuration
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(parentWindow);
        setMinimumSize(new Dimension(700, 550));
    }
    
    private void initializeComponents() {
        // Patient information fields
        patientNameField = new JTextField();
        patientNameField.setFont(new Font("Arial", Font.PLAIN, 16));
        patientNameField.setPreferredSize(new Dimension(0, 35));
        
        patientPhoneField = new JTextField();
        patientPhoneField.setFont(new Font("Arial", Font.PLAIN, 16));
        patientPhoneField.setPreferredSize(new Dimension(0, 35));
        
        // Medicines table (non-editable)
        String[] columnNames = {"Medicine", "Brand", "Quantity", "Unit Price", "Subtotal"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        medicinesTable = new JTable(tableModel);
        medicinesTable.setRowHeight(30);
        medicinesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        medicinesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        medicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Total amount label
        double totalAmount = saleItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
        totalAmountLabel = new JLabel("Total Amount: " + String.format("%.2f BDT", totalAmount));
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Pharmacy info label
        pharmacyInfoLabel = new JLabel(String.format("<html><b>%s</b><br/>%s<br/>Area: %s</html>", 
                                                    pharmacy.getName(), 
                                                    pharmacy.getAddress(), 
                                                    pharmacy.getArea()));
        pharmacyInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pharmacyInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Buttons
        confirmSaleButton = new JButton("Confirm Sale");
        cancelButton = new JButton("Cancel");
        
        styleButton(confirmSaleButton, new Color(34, 139, 34)); // Green
        styleButton(cancelButton, new Color(220, 20, 60)); // Red
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(160, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel - Pharmacy info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Pharmacy Information", 
            0, 0, new Font("Arial", Font.BOLD, 16)));
        topPanel.add(pharmacyInfoLabel, BorderLayout.CENTER);
        
        // Center panel - Patient info and medicines
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Patient info panel
        JPanel patientPanel = createPatientInfoPanel();
        
        // Medicines panel
        JPanel medicinesPanel = createMedicinesPanel();
        
        centerPanel.add(patientPanel, BorderLayout.NORTH);
        centerPanel.add(medicinesPanel, BorderLayout.CENTER);
        
        // Bottom panel - Total and buttons
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPatientInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Patient Information", 
            0, 0, new Font("Arial", Font.BOLD, 16)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        
        // Patient Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Patient Name: *");
        nameLabel.setFont(labelFont);
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(patientNameField, gbc);
        
        // Patient Phone
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(labelFont);
        panel.add(phoneLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(patientPhoneField, gbc);
        
        return panel;
    }
    
    private JPanel createMedicinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Medicines", 
            0, 0, new Font("Arial", Font.BOLD, 16)));
        
        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalAmountLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmSaleButton);
        
        panel.add(totalPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void populateMedicinesTable() {
        tableModel.setRowCount(0);
        
        for (SaleItem item : saleItems) {
            Object[] row = {
                item.getMedicine().getName(),
                item.getMedicine().getBrand(),
                item.getQuantity(),
                String.format("%.2f", item.getMedicine().getPrice()),
                String.format("%.2f", item.getSubtotal())
            };
            tableModel.addRow(row);
        }
    }
    
    private void setupEventHandlers() {
        confirmSaleButton.addActionListener(e -> confirmSale());
        cancelButton.addActionListener(e -> dispose());
        
        // Add Enter key listener to confirm sale when in name field
        patientNameField.addActionListener(e -> confirmSale());
        patientPhoneField.addActionListener(e -> confirmSale());
    }
    
    private String validatePatientInfo(String patientName, String patientPhone) {
        // Just check if patient name is not empty
        if (patientName == null || patientName.isEmpty()) {
            return "Patient name is required.";
        }
        if (patientPhone == null || patientPhone.isEmpty()) {
            return "Patient phone number is required.";
        }
        
        return ""; // No validation errors
    }
    
    private void confirmSale() {
        String patientName = patientNameField.getText().trim();
        String patientPhone = patientPhoneField.getText().trim();
        
        // Validate patient information directly in UI
        String validationError = validatePatientInfo(patientName, patientPhone);
        if (!validationError.isEmpty()) {
            JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            if (validationError.contains("name")) {
                patientNameField.requestFocus();
            } else {
                patientPhoneField.requestFocus();
            }
            return;
        }
        
        // Create invoice
        Invoice invoice = invoiceService.createInvoice(
            pharmacy.getId(),
            patientName,
            patientPhone.isEmpty() ? null : patientPhone,
            saleItems,
            pharmacy
        );
        
        // Show confirmation dialog
        int result = JOptionPane.showConfirmDialog(this,
            String.format("Confirm sale to %s?\n\nTotal: %.2f BDT\nItems: %d",
                         patientName,
                         invoice.getTotalAmount(),
                         invoice.getTotalItems()),
            "Confirm Sale",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            processSaleAsync(invoice);
        }
    }
    
    private void processSaleAsync(Invoice invoice) {
        // Disable button to prevent double-clicking
        confirmSaleButton.setEnabled(false);
        confirmSaleButton.setText("Processing...");
        
        // Process sale in a separate thread to prevent UI freezing
        SwingWorker<InvoiceService.SaveInvoiceResult, Void> worker = new SwingWorker<InvoiceService.SaveInvoiceResult, Void>() {
            @Override
            protected InvoiceService.SaveInvoiceResult doInBackground() throws Exception {
                return invoiceService.saveInvoiceWithPDF(invoice);
            }
            
            @Override
            protected void done() {
                try {
                    InvoiceService.SaveInvoiceResult result = get();
                    
                    if (result.success()) {
                        handleSaleSuccess(invoice, result);
                    } else {
                        handleSaleFailure(result.message());
                    }
                    
                } catch (Exception e) {
                    handleSaleException(e);
                }
            }
        };
        
        worker.execute();
    }
    
    private void handleSaleSuccess(Invoice invoice, InvoiceService.SaveInvoiceResult result) {
        // Show success message with invoice ID and PDF info
        String successMessage = String.format(
            "Sale processed successfully!\n\nInvoice ID: %d\nPatient: %s\nTotal: %.2f BDT",
            invoice.getId(),
            invoice.getPatientName(),
            invoice.getTotalAmount()
        );
        
        if (result.hasPdf()) {
            successMessage += "\n\n✓ Invoice file generated successfully!";
        } else {
            successMessage += "\n\n⚠ Invoice file generation failed, but sale was completed.";
        }
        
        JOptionPane.showMessageDialog(this,
            successMessage,
            "Sale Successful",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Show full invoice
        showFullInvoice(invoice);
        
        // Offer to open invoice file if generated
        if (result.hasPdf()) {
            offerToOpenInvoiceFile(result.pdfPath());
        }
        
        // Close this window and refresh parent
        dispose();
        if (parentWindow != null) {
            parentWindow.clearCartAndRefresh();
        }
    }
    
    private void handleSaleFailure(String errorMessage) {
        JOptionPane.showMessageDialog(this,
            "Failed to process sale:\n" + errorMessage + 
            "\n\nPlease check:\n" +
            "1. Medicine stock availability\n" +
            "2. Patient information\n" +
            "3. Network connection\n\n" +
            "Please try again.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        
        // Re-enable button
        confirmSaleButton.setEnabled(true);
        confirmSaleButton.setText("Confirm Sale");
    }
    
    private void handleSaleException(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "An error occurred while processing the sale:\n" + e.getMessage() +
            "\n\nPlease try again or contact support.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        
        // Re-enable button
        confirmSaleButton.setEnabled(true);
        confirmSaleButton.setText("Confirm Sale");
    }
    
    private void showFullInvoice(Invoice invoice) {
        JDialog invoiceDialog = new JDialog(this, "Invoice - ID: " + invoice.getId(), true);
        invoiceDialog.setSize(600, 500);
        invoiceDialog.setLocationRelativeTo(this);
        
        JTextArea invoiceTextArea = new JTextArea(invoice.toString());
        invoiceTextArea.setEditable(false);
        invoiceTextArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(invoiceTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> invoiceDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        
        invoiceDialog.add(scrollPane, BorderLayout.CENTER);
        invoiceDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        invoiceDialog.setVisible(true);
    }
    
    private void offerToOpenInvoiceFile(String invoicePath) {
        int choice = JOptionPane.showConfirmDialog(this,
            "Invoice file has been saved to:\n" + invoicePath + 
            "\n\nWould you like to open it now?",
            "Invoice Generated",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            utils.PDFGenerator.openInvoiceFile(invoicePath);
        }
    }
}
