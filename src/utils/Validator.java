package utils;
import models.Pharmacy;
import models.User;

import java.util.regex.Pattern;

public class Validator {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Date pattern for YYYY-MM-DD format
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    // Basic validation methods using method overloading
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isNotEmpty(int value) {
        return value != 0;
    }
    
    // Number validation using method overloading
    public static boolean isValidNumber(String value) {
        if (!isNotEmpty(value)) return false;
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidNumber(int value) {
        return isNotEmpty(value);
    }
    
    public static boolean isValidNumber(String value, boolean mustBePositive) {
        if (!isValidNumber(value)) return false;
        if (mustBePositive) {
            try {
                return Double.parseDouble(value.trim()) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidNumber(int value, boolean mustBePositive) {
        if (!isValidNumber(value)) return false;
        return !mustBePositive || value > 0;
    }


    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();       //returns true if the email matches the regex pattern
    }


    // Password Validations
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidDateFormat(String date) {
        if (isNotEmpty(date)) {
            return DATE_PATTERN.matcher(date.trim()).matches();
        }
        return false;
    }

    public static boolean isValidRole(String role) {
        if (isNotEmpty(role)) {
            String normalizedRole = role.trim().toLowerCase();
            return "pharmacist".equals(normalizedRole) || "patient".equals(normalizedRole) || "admin".equals(normalizedRole);
        }
        return false;
    }

    public static boolean validateUser(User user) {
        if (user == null) return false;
        
        return isNotEmpty(user.getName()) &&
                isValidEmail(user.getEmail()) &&
                isValidPassword(user.getPassword()) &&
                isNotEmpty(user.getDateOfBirth()) &&
                isValidRole(user.getRole());
    }
    
    public static boolean validateUserForRegistration(User user) {
        return validateUser(user);
    }

    public static boolean validatePharmacy(Pharmacy pharmacy) {
        if (pharmacy == null) return false;
        
        return isValidNumber(pharmacy.getUserId(), true) &&
                isNotEmpty(pharmacy.getName()) &&
                isNotEmpty(pharmacy.getAddress()) &&
                isNotEmpty(pharmacy.getArea());
    }
    
    // Form Field Validations (for UI components)
    public static boolean validateMedicineForm(String name, String genericName, String brand, 
                                             String priceText, String quantityText, String expiryDate) {
        // Check if all fields are filled
        if (isNotEmpty(name) && isNotEmpty(genericName) && isNotEmpty(brand) && 
            isNotEmpty(priceText) && isNotEmpty(quantityText) && isNotEmpty(expiryDate)) {
            
            // Validate numeric fields
            if (isValidNumber(priceText, true) && isValidNumber(quantityText, true)) {
                // Validate date format
                return isValidDateFormat(expiryDate);
            }
        }
        return false;
    }
    
    public static boolean validatePharmacyForm(String userId, String name, String address, String area) {
        if (isNotEmpty(name) && isNotEmpty(address) && isNotEmpty(area) && isNotEmpty(userId)) {
            return isValidNumber(userId, true);
        }
        return false;
    }

    
    public static boolean validateRegistrationForm(String name, String email, String password, 
                                                 String dateOfBirth, String role) {
        if (isNotEmpty(name) && isNotEmpty(email) && isNotEmpty(password) && 
            isNotEmpty(dateOfBirth) && isNotEmpty(role)) {
            
            return isValidEmail(email) && 
                   isValidPassword(password) && 
                   isValidRole(role) &&
                   isValidDateFormat(dateOfBirth);
        }
        return false;
    }
    
    public static String getEmailErrorMessage(String email) {
        if (isNotEmpty(email)) {
            if (!isValidEmail(email)) return "Please enter a valid email address.";
            return "";
        }
        return "Email is required.";
    }
    
    public static String getPasswordErrorMessage(String password) {
        if (isNotEmpty(password)) {
            if (!isValidPassword(password)) return "Password must be at least 6 characters long.";
            return "";
        }
        return "Password is required.";
    }

}
