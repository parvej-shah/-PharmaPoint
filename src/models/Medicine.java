package models;

public class Medicine {
    private int id;
    private int pharmacyId;
    private String name;
    private String genericName;
    private String brand;
    private double price;
    private int quantity;
    private String expiryDate; // format: yyyy-MM-dd

    public Medicine(int id, int pharmacyId, String name, String genericName,
                    double price, int quantity, String expiryDate) {
        this.id = id;
        this.pharmacyId = pharmacyId;
        this.name = name;
        this.genericName = genericName;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Constructor without ID (for creation)
    public Medicine(int pharmacyId, String name, String genericName,
                    String brand, double price, int quantity, String expiryDate) {
        this.pharmacyId = pharmacyId;
        this.name = name;
        this.genericName = genericName;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Getters and setters
    public int getId() { return id; }
    public int getPharmacyId() { return pharmacyId; }
    public String getName() { return name; }
    public String getGenericName() { return genericName; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }

    public void setId(int id) { this.id = id; }
    public void setPharmacyId(int pharmacyId) { this.pharmacyId = pharmacyId; }
    public void setName(String name) { this.name = name; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
