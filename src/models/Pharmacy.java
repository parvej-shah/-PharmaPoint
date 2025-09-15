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
}
