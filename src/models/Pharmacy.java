package models;

public class Pharmacy {
    private int id;
    private int userId;
    private String name;
    private String address;
    private String area;

    public Pharmacy(int id, int userId, String name, String address, String area) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.area = area;
    }

    // Constructor without ID
    public Pharmacy(int userId, String name, String address, String area) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.area = area;
    }

    // Getters and setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getArea() { return area; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setArea(String area) { this.area = area; }
}
