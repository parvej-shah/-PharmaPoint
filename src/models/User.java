package models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password; // must be hashed
    private String dateOfBirth;
    private String role; // "patient" or "pharmacy"

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

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
