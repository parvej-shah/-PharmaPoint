package models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password; 
    private String dateOfBirth;
    private String role; // patient or pharmacy

    // Constructor
    public User(int id, String name, String email, String password, String dob,String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dob;
        this.role = role;
    }

    // Overloaded constructor without ID (for new user before DB insertion)
    public User(String name, String email, String password, String dob,String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dob;
        this.role = role;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
