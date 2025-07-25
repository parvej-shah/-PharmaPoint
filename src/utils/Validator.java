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
    public static boolean isNotEmpty(String value) {
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
    
    public static boolean isNonNegativeNumber(double value) {
        return value >= 0;
    }

    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }
    
    public static boolean isNonNegativeInteger(int value) {
        return value >= 0;
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
    
    public static boolean isValidNonNegativeNumber(String value) {
        if (!isValidNumber(value)) return false;
        try {
            double num = Double.parseDouble(value.trim());
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Email Validations
    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        
        // Enhanced email validation using regex
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static boolean isBasicValidEmail(String email) {
        if (isBlank(email)) return false;
        
        // Basic email validation (fallback)
        String trimmedEmail = email.trim();
        return trimmedEmail.contains("@") && 
               trimmedEmail.contains(".") && 
               trimmedEmail.length() > 5 &&
               trimmedEmail.indexOf("@") > 0 &&
               trimmedEmail.lastIndexOf(".") > trimmedEmail.indexOf("@");
    }

    // Password Validations
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }
    
    public static boolean isBasicValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 4;
    }
    
    public static boolean isStrongPassword(String password) {
        if (isBlank(password) || password.length() < 8) return false;
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    // Date Validations
    public static boolean isValidDateFormat(String date) {
        if (isBlank(date)) return false;
        return DATE_PATTERN.matcher(date.trim()).matches();
    }
    
    public static boolean isValidDateRange(String date, int minYear, int maxYear) {
        if (!isValidDateFormat(date)) return false;
        
        try {
            String[] parts = date.trim().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            
            return year >= minYear && year <= maxYear &&
                   month >= 1 && month <= 12 &&
                   day >= 1 && day <= 31;
        } catch (Exception e) {
            return false;
        }
    }

    // Role Validations
    public static boolean isValidRole(String role) {
        if (isBlank(role)) return false;
        String normalizedRole = role.trim().toLowerCase();
        return "pharmacist".equals(normalizedRole) || "patient".equals(normalizedRole);
    }

    // Model Validations
    public static boolean validateUser(User user) {
        if (user == null) return false;
        
        return isNotEmpty(user.getName()) &&
                isValidEmail(user.getEmail()) &&
                isValidPassword(user.getPassword()) &&
                isNotEmpty(user.getDateOfBirth()) &&
                isValidRole(user.getRole());
    }
    
    public static boolean validateUserForRegistration(User user) {
        if (!validateUser(user)) return false;
        
        // Additional validation for registration
        return isValidDateFormat(user.getDateOfBirth()) &&
               isValidDateRange(user.getDateOfBirth(), 1900, 2010); // Reasonable age range
    }

    public static boolean validatePharmacy(Pharmacy pharmacy) {
        if (pharmacy == null) return false;
        
        return isPositiveInteger(pharmacy.getUserId()) &&
                isNotEmpty(pharmacy.getName()) &&
                isNotEmpty(pharmacy.getAddress()) &&
                isNotEmpty(pharmacy.getArea());
    }

    public static boolean validateMedicine(Medicine medicine) {
        if (medicine == null) return false;
        
        return isPositiveInteger(medicine.getPharmacyId()) &&
                isNotEmpty(medicine.getName()) &&
                isNotEmpty(medicine.getGenericName()) &&
                isNotEmpty(medicine.getBrand()) &&
                isPositiveNumber(medicine.getPrice()) &&
                isPositiveInteger(medicine.getQuantity()) &&
                isValidDateFormat(medicine.getExpiryDate());
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
    
    public static boolean validateLoginForm(String email, String password) {
        return isNotEmpty(email) && isNotEmpty(password);
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

    // Utility methods for error messages
    public static String getValidationErrorMessage(String fieldName, String value) {
        if (isBlank(value)) {
            return fieldName + " is required.";
        }
        return fieldName + " is invalid.";
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
    
    public static String getNumberErrorMessage(String fieldName, String value) {
        if (isBlank(value)) return fieldName + " is required.";
        if (!isValidNumber(value)) return fieldName + " must be a valid number.";
        if (!isValidPositiveNumber(value)) return fieldName + " must be positive.";
        return "";
    }
}
