package utils;

import models.User;

public class Validator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    /*public static boolean isValidRole(String role) {
        return isNotEmpty(role) &&
                (role.equalsIgnoreCase("admin") ||
                        role.equalsIgnoreCase("pharmacist") ||
                        role.equalsIgnoreCase("patient"));
    }*/


    // ✅ Simple boolean validator for User
    public static boolean validateUser(User user) {
        return isNotEmpty(user.getName()) &&
                isNotEmpty(user.getEmail()) &&
                isValidPassword(user.getPassword()) &&
                isNotEmpty(user.getDateOfBirth()) &&
                isNotEmpty(user.getRole());
    }

}
