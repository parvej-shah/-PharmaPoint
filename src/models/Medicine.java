package models;

public class Medicine {
    private int id;
    private int pharmacyId;
    private String name;
    private String genericName;
    private String brand;
    private double price;
    private int quantity;
    private String expiryDate; //yyyy-MM-dd

    public Medicine(int id, int pharmacyId, String name, String genericName,
                    String brand, double price, int quantity, String expiryDate) {
        this.id = id;
        this.pharmacyId = pharmacyId;
        this.name = name;
        this.genericName = genericName;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Constructor without ID
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

    public int getId() { return id; }
    public int getPharmacyId() { return pharmacyId; }
    public String getName() { return name; }
    public String getGenericName() { return genericName; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
