package dao;

import models.Pharmacy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PharmacyDAO {

    public boolean addPharmacy(Pharmacy pharmacy) {
        String sql = "INSERT INTO pharmacies(user_id,name,address, area) Values (?,?,?,?)";
        try(Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try(PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setInt(1, pharmacy.getUserId());
                pstmt.setString(2, pharmacy.getName());
                pstmt.setString(3, pharmacy.getAddress());
                pstmt.setString(4, pharmacy.getArea());
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error Adding Pharmacy: "+e.getMessage());
            return false;
        }
    }

    public List<Pharmacy> getPharmacies() {
        List<Pharmacy> pharmacies = new ArrayList<>();
        String sql = "SELECT * FROM pharmacies";
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement();       //not prepared statement because we dont need to edit the query later
                 ResultSet result = stmt.executeQuery(sql)) {
                while (result.next()) {
                    pharmacies.add(new Pharmacy(
                        result.getInt("id"),
                        result.getInt("user_id"),
                        result.getString("name"),
                        result.getString("address"),
                        result.getString("area")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error Getting alL pharmacy: "+e.getMessage());
        }
        return  pharmacies;  // may need to refactor
    }

    public Pharmacy getPharmacyById(int id) {
        String sql = "SELECT * FROM pharmacies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Pharmacy(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("area")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pharmacy: " + e.getMessage());
        }
        return null;
    }
    
    public Pharmacy getPharmacyByUserId(int userId) {
        String sql = "SELECT * FROM pharmacies WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Pharmacy(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("area")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pharmacy by user ID: " + e.getMessage());
        }
        return null;
    }
}
