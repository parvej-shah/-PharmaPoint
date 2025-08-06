package services;

import dao.UserDAO;
import models.User;
import utils.Validator;


public class AuthService {
    
    private UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    
    public User authenticateUser(String email, String password) {
        
        if (!Validator.validateLoginForm(email, password)) {        //checks if email and password is empty
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
    
    
    public boolean isValidEmail(String email) {
        return Validator.isValidEmail(email);
    }
    
    
    public boolean isValidPassword(String password) {
        return Validator.isValidPassword(password);
    }
}
