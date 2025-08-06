package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Medicine;
import models.Pharmacy;

public class MedicineDAO {

    public boolean addMedicine(Medicine medicine){
        String sql = "INSERT INTO medicines(pharmacy_id, name, generic_name, brand, price, quantity, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;       //if connection is null, it will throw an Error
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

    public List<Medicine> getAllMedicines(int pharmacyId){      //returns an ArrayList of Medicine objects
        List <Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE pharmacy_id = ?";
        try( Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, pharmacyId);

                ResultSet rs = pstmt.executeQuery();        //rs is a table with data that we want 
                while(rs.next()){
                    medicines.add(new Medicine(
                            rs.getInt("id"),                // Get the data from the column "id"
                            rs.getInt("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("generic_name"),
                            rs.getString("brand"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("expiry_date")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return medicines;
    }
    public List<Medicine> getMedicinesByPharmacyId(int pharmacyId) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE pharmacy_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, pharmacyId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    medicines.add(new Medicine(
                            rs.getInt("id"),
                            rs.getInt("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("generic_name"),
                            rs.getString("brand"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("expiry_date")
                    ));
                }

            }
        } catch (SQLException e) {
            System.err.println("Error fetching medicines: " + e.getMessage());
        }
        return medicines;
    }
    public boolean deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, medicineId);
                return stmt.executeUpdate() > 0;

            }
        } catch (SQLException e) {
            System.err.println("Error deleting medicine: " + e.getMessage());
            return false;
        }
    }
    public List<Medicine> searchMedicineByName(String keyword) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE name LIKE ? OR generic_name LIKE ?";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                String search = "%" + keyword + "%";
                pstmt.setString(1, search);
                pstmt.setString(2, search);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(new Medicine(
                            rs.getInt("id"),
                            rs.getInt("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("generic_name"),
                            rs.getString("brand"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("expiry_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching medicines: " + e.getMessage());
        }
        return list;
    }
    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name = ?, generic_name = ?, brand = ?, price = ?, quantity = ?, expiry_date = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, medicine.getName());
                pstmt.setString(2, medicine.getGenericName());
                pstmt.setString(3, medicine.getBrand());
                pstmt.setDouble(4, medicine.getPrice());
                pstmt.setInt(5, medicine.getQuantity());
                pstmt.setString(6, medicine.getExpiryDate());
                pstmt.setInt(7, medicine.getId());
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
            return false;
        }
    }

    public Medicine getMedicineById(int medicineId) {
        String sql = "SELECT * FROM medicines WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, medicineId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new Medicine(
                            rs.getInt("id"),
                            rs.getInt("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("generic_name"),
                            rs.getString("brand"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("expiry_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medicine by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Pharmacy> findPharmaciesWithMedicine(String keyword) {
        List<Pharmacy> result = new ArrayList<>();

        String sql = """
        SELECT p.id, p.user_id, p.name, p.address, p.area
        FROM medicines m
        JOIN pharmacies p ON m.pharmacy_id = p.id           
        WHERE (m.name LIKE ? OR m.generic_name LIKE ?)
        AND m.quantity > 0
        GROUP BY p.id
    """;                                                    //starting from the medicines table, joining it with pharmacies, and returning details of each pharmacy that has at least one matching, in-stock medicine.
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                String search = "%" + keyword + "%";
                stmt.setString(1, search);
                stmt.setString(2, search);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Pharmacy pharmacy = new Pharmacy(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("area")
                    );
                    result.add(pharmacy);
                }

            }
        } catch (SQLException e) {
            System.err.println("Error finding pharmacies with medicine: " + e.getMessage());
        }

        return result;
    }

    public Map<Pharmacy, Map<String, Object>> findPharmaciesWithMedicines(List<String> medicineNames) {
        Map<Pharmacy, Map<String, Object>> result = new HashMap<>();

        if (medicineNames.isEmpty()) {
            return result;
        }
        
        // Build dynamic SQL with placeholders
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.user_id, p.name, p.address, p.area, ");
        sql.append("m.name as medicine_name, m.generic_name, m.brand, m.price, m.quantity ");
        sql.append("FROM pharmacies p ");
        sql.append("LEFT JOIN medicines m ON p.id = m.pharmacy_id ");
        sql.append("WHERE m.quantity > 0 AND (");
        
        for (int i = 0; i < medicineNames.size(); i++) {
            if (i > 0) sql.append(" OR ");
            sql.append("m.name LIKE ? OR m.generic_name LIKE ?");
        }
        sql.append(") ORDER BY p.name ASC");
        
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                
                // Set parameters
                int paramIndex = 1;
                for (String medicineName : medicineNames) {
                    String search = "%" + medicineName.trim() + "%";
                    stmt.setString(paramIndex++, search);
                    stmt.setString(paramIndex++, search);
                }
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    // Create pharmacy object
                    Pharmacy pharmacy = new Pharmacy(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("area")
                    );
                    
                    // Group by pharmacy to avoid duplicates
                    Pharmacy existingPharmacy = null;
                    for (Pharmacy p : result.keySet()) {
                        if (p.getId() == pharmacy.getId()) {
                            existingPharmacy = p;
                            break;
                        }
                    }
                    
                    if (existingPharmacy == null) {
                        // Create new pharmacy entry
                        java.util.Map<String, Object> data = new java.util.HashMap<>();
                        data.put("availableMedicines", new java.util.ArrayList<Medicine>());
                        data.put("count", 0);
                        result.put(pharmacy, data);
                        existingPharmacy = pharmacy;
                    }
                    
                    // Add medicine to pharmacy's available list (avoid duplicates)
                    @SuppressWarnings("unchecked")
                    List<Medicine> medicines = (List<Medicine>) result.get(existingPharmacy).get("availableMedicines");
                    
                    Medicine medicine = new Medicine(
                            0, // id not needed for this use case
                            rs.getInt("id"),
                            rs.getString("medicine_name"),
                            rs.getString("generic_name"),
                            rs.getString("brand"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            "" // expiry not needed for this use case
                    );
                    
                    // Check if medicine already exists (avoid duplicates)
                    boolean exists = false;
                    for (Medicine m : medicines) {
                        if (m.getName().equals(medicine.getName()) && m.getBrand().equals(medicine.getBrand())) {
                            exists = true;
                            break;
                        }
                    }
                    
                    if (!exists) {
                        medicines.add(medicine);
                        result.get(existingPharmacy).put("count", medicines.size());
                    }
                }
                
            }
        } catch (SQLException e) {
            System.err.println("Error finding pharmacies with medicines: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get all medicines with pharmacy information, ordered by quantity descending
     * @return List of all medicines sorted by quantity
     */
    public List<Medicine> getAllMedicinesWithPharmacyInfo() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT m.*, p.name as pharmacy_name " +
                    "FROM medicines m " +
                    "LEFT JOIN pharmacies p ON m.pharmacy_id = p.id " +
                    "ORDER BY m.quantity DESC";
        
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Medicine medicine = new Medicine(
                        rs.getInt("id"),
                        rs.getInt("pharmacy_id"),
                        rs.getString("name"),
                        rs.getString("generic_name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("expiry_date")
                    );
                    medicines.add(medicine);
                }
                
            }
        } catch (SQLException e) {
            System.err.println("Error getting all medicines: " + e.getMessage());
        }
        
        return medicines;
    }

}
