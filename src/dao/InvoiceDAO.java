package dao;

import models.Invoice;
import models.SaleItem;

import java.sql.*;

public class InvoiceDAO {
    public boolean saveInvoice(Invoice invoice) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);        // Disables auto-save so you can manually control when changes are committed to the database.

            // Insert into invoices table
            String invoiceSQL = """
                INSERT INTO invoices (pharmacy_id, patient_name, patient_phone, total_amount, created_at)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            PreparedStatement invoiceStmt = connection.prepareStatement(invoiceSQL, Statement.RETURN_GENERATED_KEYS);       //statement.RETURN_GENERATED_KEYS allows you to retrieve the auto-generated keys (like ID) after insertion which will be needed to insert in that rows later
            invoiceStmt.setInt(1, invoice.getPharmacyId());
            invoiceStmt.setString(2, invoice.getPatientName());
            invoiceStmt.setString(3, invoice.getPatientPhone());
            invoiceStmt.setDouble(4, invoice.getTotalAmount());
            invoiceStmt.setTimestamp(5, Timestamp.valueOf(invoice.getCreatedAt()));
            
            int rowsAffected = invoiceStmt.executeUpdate();
            if (rowsAffected == 0) {
                connection.rollback();      //undo changes after last commit if couldnt execute the query
                return false;
            }
            
            // Get generated invoice ID
            ResultSet generatedKeys = invoiceStmt.getGeneratedKeys();  // generatedKeys is a table that contains the auto-generated keys
            int invoiceId;
            if (generatedKeys.next()) {
                invoiceId = generatedKeys.getInt(1);    //get the value of the first column 
                invoice.setId(invoiceId); // Set the generated ID back to the invoice
            } else {
                connection.rollback();
                return false;
            }
            
            // Insert invoice items
            String itemSQL = """
                INSERT INTO invoice_items (invoice_id, medicine_name, quantity, price, subtotal)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            PreparedStatement itemStmt = connection.prepareStatement(itemSQL);
            
            for (SaleItem item : invoice.getItems()) {
                itemStmt.setInt(1, invoiceId);
                itemStmt.setString(2, item.getMedicine().getName());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getMedicine().getPrice());
                itemStmt.setDouble(5, item.getSubtotal());
                itemStmt.addBatch();
            }
            
            int[] batchResults = itemStmt.executeBatch();
            
            // Check if all items were inserted
            for (int result : batchResults) {
                if (result == Statement.EXECUTE_FAILED) {
                    connection.rollback();
                    return false;
                }
            }
            
            connection.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error saving invoice: " + e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
