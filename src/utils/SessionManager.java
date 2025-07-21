package utils;

import models.User;

/**
 * SessionManager - Manages user session state across the application
 * Provides static methods to store and retrieve the currently logged-in user
 */
public class SessionManager {
    
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    public static void clearSession() {
        currentUser = null;
    }
    
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    public static boolean hasRole(String role) {
        return currentUser != null && role.equals(currentUser.getRole());
    }
}
