package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Invoice {
    private int id;
    private int pharmacyId;
    private String patientName;
    private String patientPhone;
    private double totalAmount;
    private List<SaleItem> items;
    private LocalDateTime createdAt;
    private String pharmacyName;
    private String pharmacyArea;

    // Constructor (before saving invoice to DB)
    public Invoice(int pharmacyId, String patientName, String patientPhone, 
                   List<SaleItem> items, String pharmacyName, String pharmacyArea) {
        this.pharmacyId = pharmacyId;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.items = items;
        this.totalAmount = calculateTotalAmount();
        this.createdAt = LocalDateTime.now();
        this.pharmacyName = pharmacyName;
        this.pharmacyArea = pharmacyArea;
    }

    // Constructor for invoices loaded from DB(after searching by ID)
    public Invoice(int id, int pharmacyId, String patientName, String patientPhone, 
                   double totalAmount, LocalDateTime createdAt, List<SaleItem> items) {
        this.id = id;
        this.pharmacyId = pharmacyId;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

    // Legacy constructor for backward compatibility
    public Invoice(List<SaleItem> items, String pharmacistName, String pharmacyName) {
        this.items = items;
        this.pharmacyName = pharmacyName;
        this.totalAmount = calculateTotalAmount();
        this.createdAt = LocalDateTime.now();
    }

    private double calculateTotalAmount() {
        // return items != null ? items.stream().mapToDouble(SaleItem::getSubtotal).sum() : 0.0;
        double total = 0.0;
        if (items != null) {
            for (SaleItem item : items) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPharmacyId() {
        return pharmacyId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public String getPharmacyArea() {
        return pharmacyArea;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getTotalItems() {
        return items != null ? items.stream().mapToInt(SaleItem::getQuantity).sum() : 0;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("================== INVOICE ==================\n");
        
        if (pharmacyName != null) {
            sb.append("Pharmacy: ").append(pharmacyName);
            if (pharmacyArea != null) {
                sb.append(" (").append(pharmacyArea).append(")");
            }
            sb.append("\n");
        }
        
        if (patientName != null) {
            sb.append("Patient: ").append(patientName);
            if (patientPhone != null) {
                sb.append(" (").append(patientPhone).append(")");
            }
            sb.append("\n");
        }
        
        if (createdAt != null) {
            sb.append("Date & Time: ").append(createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        }
        
        if (id > 0) {
            sb.append("Invoice ID: ").append(id).append("\n");
        }
        
        sb.append("==============================================\n\n");
        
        sb.append(String.format("%-25s %-8s %-10s %-12s\n", "Medicine", "Qty", "Unit Price", "Subtotal"));
        sb.append("--------------------------------------------------------------\n");
        
        if (items != null) {
            for (SaleItem item : items) {
                sb.append(String.format("%-25s %-8d %-10.2f %-12.2f\n",
                        item.getMedicine().getName(),
                        item.getQuantity(),
                        item.getMedicine().getPrice(),
                        item.getSubtotal()));
            }
        }
        
        sb.append("--------------------------------------------------------------\n");
        sb.append(String.format("Total Items: %d\n", getTotalItems()));
        sb.append(String.format("TOTAL AMOUNT: %.2f BDT\n", getTotalAmount()));
        sb.append("==============================================");
        
        return sb.toString();
    }

}
