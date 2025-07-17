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
        try (
                Connection conn = DBConnection.getConnection();
        ) {
            assert conn != null;
            try (Statement stmt = conn.createStatement();
                 ResultSet result = stmt.executeQuery(sql);
                    ){
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
}
