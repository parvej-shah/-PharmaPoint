package dao;

import models.User;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean save(User user) {
        String sql = "INSERT INTO users(name, email, password, dateOfBirth, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                // Hash the password before storing
                String hashed = hashPassword(user.getPassword());
                pstmt.setString(3, hashed);
                pstmt.setString(4, user.getDateOfBirth());
                pstmt.setString(5, user.getRole());

                pstmt.executeUpdate();
                return true;

            }
        } catch (SQLException e) {
            System.err.println("User save failed: " + e.getMessage());
            return false;
        }
    }

    public User findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);
                // First try with hashed password (for new users)
                pstmt.setString(2, hashPassword(password));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("dateOfBirth"),
                            rs.getString("role"));
                }
                
                // If not found, try with plain text password (for existing users)
                pstmt.setString(2, password);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    // Found with plain text password, return user
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("dateOfBirth"),
                            rs.getString("role"));             
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Find user failed: " + e.getMessage());
        }

        return null;
    }
    
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("dateOfBirth"),
                            rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find user by email failed: " + e.getMessage());
        }

        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("dateOfBirth"),
                            rs.getString("role")));
                }

            }
        } catch (SQLException e) {
            System.err.println("Fetch users failed: " + e.getMessage());
        }

        return users;
    }

    // Helper to hash passwords using SHA-256
    private static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
