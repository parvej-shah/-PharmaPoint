package dao;

import models.Invoice;
import models.SaleItem;
import models.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    /**
     * Saves an invoice to the database
     * @param invoice The invoice to save
     * @return true if successful, false otherwise
     */
    public boolean saveInvoice(Invoice invoice) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction
            
            // Insert into invoices table
            String invoiceSQL = """
                INSERT INTO invoices (pharmacy_id, patient_name, patient_phone, total_amount, created_at)
                VALUES (?, ?, ?, ?, ?)
            """;
            
            PreparedStatement invoiceStmt = connection.prepareStatement(invoiceSQL, Statement.RETURN_GENERATED_KEYS);
            invoiceStmt.setInt(1, invoice.getPharmacyId());
            invoiceStmt.setString(2, invoice.getPatientName());
            invoiceStmt.setString(3, invoice.getPatientPhone());
            invoiceStmt.setDouble(4, invoice.getTotalAmount());
            invoiceStmt.setTimestamp(5, Timestamp.valueOf(invoice.getCreatedAt()));
            
            int rowsAffected = invoiceStmt.executeUpdate();
            if (rowsAffected == 0) {
                connection.rollback();
                return false;
            }
            
            // Get generated invoice ID
            ResultSet generatedKeys = invoiceStmt.getGeneratedKeys();
            int invoiceId;
            if (generatedKeys.next()) {
                invoiceId = generatedKeys.getInt(1);
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

    /**
     * Retrieves an invoice by ID
     * @param invoiceId The ID of the invoice to retrieve
     * @return Invoice object or null if not found
     */
    public Invoice getInvoiceById(int invoiceId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            
            // Get invoice details
            String invoiceSQL = """
                SELECT i.*, p.name as pharmacy_name, p.area as pharmacy_area
                FROM invoices i
                LEFT JOIN pharmacies p ON i.pharmacy_id = p.id
                WHERE i.id = ?
            """;
            
            PreparedStatement invoiceStmt = connection.prepareStatement(invoiceSQL);
            invoiceStmt.setInt(1, invoiceId);
            ResultSet invoiceRs = invoiceStmt.executeQuery();
            
            if (!invoiceRs.next()) {
                return null; // Invoice not found
            }
            
            // Create invoice object
            Invoice invoice = new Invoice(
                invoiceRs.getInt("id"),
                invoiceRs.getInt("pharmacy_id"),
                invoiceRs.getString("patient_name"),
                invoiceRs.getString("patient_phone"),
                invoiceRs.getDouble("total_amount"),
                invoiceRs.getTimestamp("created_at").toLocalDateTime(),
                new ArrayList<>()
            );
            
            invoice.setPharmacyName(invoiceRs.getString("pharmacy_name"));
            invoice.setPharmacyArea(invoiceRs.getString("pharmacy_area"));
            
            // Get invoice items
            String itemsSQL = """
                SELECT * FROM invoice_items WHERE invoice_id = ?
            """;
            
            PreparedStatement itemsStmt = connection.prepareStatement(itemsSQL);
            itemsStmt.setInt(1, invoiceId);
            ResultSet itemsRs = itemsStmt.executeQuery();
            
            List<SaleItem> items = new ArrayList<>();
            while (itemsRs.next()) {
                // Create a mock medicine object for the sale item
                Medicine medicine = new Medicine(
                    0, // ID not needed for display
                    invoice.getPharmacyId(),
                    itemsRs.getString("medicine_name"),
                    "", // Generic name not stored in invoice_items
                    "", // Brand not stored in invoice_items
                    itemsRs.getDouble("price"),
                    0, // Quantity not relevant here
                    "" // Expiry date not stored in invoice_items
                );
                
                SaleItem item = new SaleItem(medicine, itemsRs.getInt("quantity"));
                items.add(item);
            }
            
            invoice.setItems(items);
            return invoice;
            
        } catch (SQLException e) {
            System.err.println("Error retrieving invoice: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves all invoices for a specific pharmacy
     * @param pharmacyId The ID of the pharmacy
     * @return List of invoices
     */
    public List<Invoice> getInvoicesByPharmacyId(int pharmacyId) {
        List<Invoice> invoices = new ArrayList<>();
        Connection connection = null;
        
        try {
            connection = DBConnection.getConnection();
            
            String sql = """
                SELECT i.*, p.name as pharmacy_name, p.area as pharmacy_area
                FROM invoices i
                LEFT JOIN pharmacies p ON i.pharmacy_id = p.id
                WHERE i.pharmacy_id = ?
                ORDER BY i.created_at DESC
            """;
            
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, pharmacyId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Invoice invoice = new Invoice(
                    rs.getInt("id"),
                    rs.getInt("pharmacy_id"),
                    rs.getString("patient_name"),
                    rs.getString("patient_phone"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    new ArrayList<>() // Items loaded separately if needed
                );
                
                invoice.setPharmacyName(rs.getString("pharmacy_name"));
                invoice.setPharmacyArea(rs.getString("pharmacy_area"));
                
                invoices.add(invoice);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving invoices: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
        
        return invoices;
    }

    /**
     * Deletes an invoice and its items
     * @param invoiceId The ID of the invoice to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteInvoice(int invoiceId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            
            // Delete invoice items first (foreign key constraint)
            String deleteItemsSQL = "DELETE FROM invoice_items WHERE invoice_id = ?";
            PreparedStatement deleteItemsStmt = connection.prepareStatement(deleteItemsSQL);
            deleteItemsStmt.setInt(1, invoiceId);
            deleteItemsStmt.executeUpdate();
            
            // Delete invoice
            String deleteInvoiceSQL = "DELETE FROM invoices WHERE id = ?";
            PreparedStatement deleteInvoiceStmt = connection.prepareStatement(deleteInvoiceSQL);
            deleteInvoiceStmt.setInt(1, invoiceId);
            int rowsAffected = deleteInvoiceStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting invoice: " + e.getMessage());
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
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
