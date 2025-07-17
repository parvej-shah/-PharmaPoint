package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import models.Medicine;

public class MedicineDAO {
    public boolean addMedicine(Medicine medicine){
        String sql = "INSERT INTO medicines(pharmacyId, name, genericName, brand, price, quantity, expiryDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, medicine.getPharmacyId());
                pstmt.setString(2, medicine.getName());
                pstmt.setString(3, medicine.getGenericName());
                pstmt.setString(4, medicine.getBrand());
                pstmt.setDouble(5, medicine.getPrice());
                pstmt.setInt(6, medicine.getQuantity());
                pstmt.setDate(7, java.sql.Date.valueOf(medicine.getExpiryDate()));

                pstmt.executeUpdate();
                return true;

            }
        } catch (SQLException e) {
            System.err.println("Add medicine failed: " + e.getMessage());
            return false;
        }
    }
}
