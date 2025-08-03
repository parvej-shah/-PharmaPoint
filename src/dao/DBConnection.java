package dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_DIR = "src/db";
    private static final String DB_FILE = "pharma.db";
    private static final String DB_URL;
    
    static {
        // Create absolute path for database
        File currentDir = new File(System.getProperty("user.dir"));
        File dbDir = new File(currentDir, DB_DIR);
        File dbFile = new File(dbDir, DB_FILE);
        DB_URL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public static Connection getConnection() {
        try {
            // Ensure the database directory exists
            File dbDir = new File(System.getProperty("user.dir"), DB_DIR);
            if (!dbDir.exists()) {
                boolean created = dbDir.mkdirs();   //mkdirs() creates the any necessary files and directories
                if (created) {
                    System.out.println("Created database directory: " + dbDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create database directory: " + dbDir.getAbsolutePath());
                    throw new SQLException("Could not create database directory");
                }
            }

            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");  //org.sqlite.JDBC is a class inside the SQLite JDBC .jar file.
            
            // Create connection
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established successfully to: " + DB_URL);
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
