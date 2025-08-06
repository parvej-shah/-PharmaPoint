package models;

public class SaleItem {
    private Medicine medicine;
    private int quantity;

    public SaleItem(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
    }

    // Getters
    public Medicine getMedicine() {
        return medicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return medicine.getPrice() * quantity;
    }

    // Setters
    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return medicine.getName() + " x " + quantity + " = " + getSubtotal() + " BDT";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SaleItem saleItem = (SaleItem) obj;
        return medicine.getId() == saleItem.medicine.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(medicine.getId());
    }
}
