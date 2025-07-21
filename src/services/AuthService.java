package services;

import dao.UserDAO;
import models.User;

/**
 * AuthService - Handles user authentication and login logic
 */
public class AuthService {
    
    private UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Authenticate a user with email and password
     * @param email User's email
     * @param password User's password
     * @return User object if authentication succeeds, null otherwise
     */
    public User authenticateUser(String email, String password) {
        // Input validation
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Find user by email and password
            User user = userDAO.findByEmailAndPassword(email.trim(), password);
            
            if (user != null) {
                System.out.println("Authentication successful for user: " + user.getName());
                return user;
            } else {
                System.out.println("Authentication failed for email: " + email);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate email format (basic validation)
     * @param email Email to validate
     * @return true if email format is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    /**
     * Validate password (basic validation)
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Basic password validation - at least 4 characters for now
        return password.length() >= 4;
    }
}
