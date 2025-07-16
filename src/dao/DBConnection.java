package dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:db/pharma.db";

    public static Connection getConnection() {
        try {
            // Ensure the database directory exists
           /* File dbDir = new File("db");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
                System.out.println("Created database directory: " + dbDir.getAbsolutePath());
            }*/

            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established successfully.");
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Database connection failed: SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
