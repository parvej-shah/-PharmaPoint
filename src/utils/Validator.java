package utils;
import models.Medicine;
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

    // Basic String Validations
    public static boolean isNotEmpty(String value) {  //returns true if the string is not null and not empty
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isNull(String value) {
        return value == null;
    }
    
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Numeric Validations
    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }

    
    public static boolean isValidNumber(String value) {
        if (isBlank(value)) return false;
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidInteger(String value) {
        if (isBlank(value)) return false;
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidPositiveNumber(String value) {
        if (!isValidNumber(value)) return false;
        try {
            double num = Double.parseDouble(value.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidPositiveInteger(String value) {
        if (!isValidInteger(value)) return false;
        try {
            int num = Integer.parseInt(value.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();       //returns true if the email matches the regex pattern
    }


    // Password Validations
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidDateFormat(String date) {
        if (isBlank(date)) return false;
        return DATE_PATTERN.matcher(date.trim()).matches();
    }

    public static boolean isValidRole(String role) {
        if (isBlank(role)) return false;
        String normalizedRole = role.trim().toLowerCase();
        return "pharmacist".equals(normalizedRole) || "patient".equals(normalizedRole) || "admin".equals(normalizedRole);
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
        
        return isPositiveInteger(pharmacy.getUserId()) &&
                isNotEmpty(pharmacy.getName()) &&
                isNotEmpty(pharmacy.getAddress()) &&
                isNotEmpty(pharmacy.getArea());
    }
    
    // Form Field Validations (for UI components)
    public static boolean validateMedicineForm(String name, String genericName, String brand, 
                                             String priceText, String quantityText, String expiryDate) {
        // Check if all fields are filled
        if (isBlank(name) || isBlank(genericName) || isBlank(brand) || 
            isBlank(priceText) || isBlank(quantityText) || isBlank(expiryDate)) {
            return false;
        }
        
        // Validate numeric fields
        if (!isValidPositiveNumber(priceText) || !isValidPositiveInteger(quantityText)) {
            return false;
        }
        
        // Validate date format
        return isValidDateFormat(expiryDate);
    }
    
    public static boolean validatePharmacyForm(String userId, String name, String address, String area) {
        if (isBlank(name) || isBlank(address) || isBlank(area) || isBlank(userId)) {
            return false;
        }
        
        return isValidPositiveInteger(userId);
    }

    
    public static boolean validateRegistrationForm(String name, String email, String password, 
                                                 String dateOfBirth, String role) {
        if (isBlank(name) || isBlank(email) || isBlank(password) || 
            isBlank(dateOfBirth) || isBlank(role)) {
            return false;
        }
        
        return isValidEmail(email) && 
               isValidPassword(password) && 
               isValidRole(role) &&
               isValidDateFormat(dateOfBirth);
    }
    
    public static String getEmailErrorMessage(String email) {
        if (isBlank(email)) return "Email is required.";
        if (!isValidEmail(email)) return "Please enter a valid email address.";
        return "";
    }
    
    public static String getPasswordErrorMessage(String password) {
        if (isBlank(password)) return "Password is required.";
        if (!isValidPassword(password)) return "Password must be at least 6 characters long.";
        return "";
    }

}
