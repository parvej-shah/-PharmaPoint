package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Medicine;

public class MedicineDAO {

    public boolean addMedicine(Medicine medicine){
        String sql = "INSERT INTO medicines(pharmacy_id, name, generic_name, brand, price, quantity, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

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

    public List<Medicine> getAllMedicines(int pharmacyId){
        List <Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE pharmacy_id = ?";
        try( Connection conn = DBConnection.getConnection()
        ) {
            assert conn != null;
            try(PreparedStatement pstmt = conn.prepareStatement(sql)
                    ) {
                pstmt.setInt(1, pharmacyId);
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()){
                    medicines.add(new Medicine(
                            rs.getInt("id"),
                            rs.getInt("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getInt("quantity"),
                            rs.getInt("price"),
                            rs.getString("expiryDate")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return medicines;
    }
}
