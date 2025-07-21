package utils;

import models.User;

/**
 * SessionManager - Manages user session state across the application
 * Provides static methods to store and retrieve the currently logged-in user
 */
public class SessionManager {
    
    private static User currentUser;
    
    /**
     * Set the current logged-in user
     * @param user The user to set as current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Get the current logged-in user
     * @return The current user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Clear the current user session (logout)
     */
    public static void clearSession() {
        currentUser = null;
    }
    
    /**
     * Get the current user's role
     * @return The role of the current user, or null if no user is logged in
     */
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Check if the current user has a specific role
     * @param role The role to check
     * @return true if current user has the specified role, false otherwise
     */
    public static boolean hasRole(String role) {
        return currentUser != null && role.equals(currentUser.getRole());
    }
}
