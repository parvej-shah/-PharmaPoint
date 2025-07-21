package services;

import dao.UserDAO;
import models.User;
import utils.Validator;

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
        // Input validation using centralized Validator
        if (!Validator.validateLoginForm(email, password)) {
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
     * Validate email format using centralized Validator
     * @param email Email to validate
     * @return true if email format is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        return Validator.isValidEmail(email);
    }
    
    /**
     * Validate password using centralized Validator
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    public boolean isValidPassword(String password) {
        return Validator.isValidPassword(password);
    }
}
