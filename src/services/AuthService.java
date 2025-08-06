package services;

import dao.UserDAO;
import models.User;


public class AuthService {
    
    private UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    
    public User authenticateUser(String email, String password) {
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
}
