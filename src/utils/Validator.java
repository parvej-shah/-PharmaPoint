package utils;

import models.User;
import models.Medicine;
import models.Pharmacy;

public class Validator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }

    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    public static boolean validateUser(User user) {
        return isNotEmpty(user.getName()) &&
                isNotEmpty(user.getEmail()) &&
                isValidPassword(user.getPassword()) &&
                isNotEmpty(user.getDateOfBirth()) &&
                isNotEmpty(user.getRole());
    }

    public static boolean validatePharmacy(Pharmacy pharmacy) {
        return isPositiveInteger(pharmacy.getUserId()) &&
                isNotEmpty(pharmacy.getName()) &&
                isNotEmpty(pharmacy.getAddress()) &&
                isNotEmpty(pharmacy.getArea());
    }

    public static boolean validateMedicine(Medicine medicine) {
        return isPositiveInteger(medicine.getPharmacyId()) &&
                isNotEmpty(medicine.getName()) &&
                isNotEmpty(medicine.getGenericName()) &&
                isNotEmpty(medicine.getBrand()) &&
                isPositiveNumber(medicine.getPrice()) &&
                isPositiveInteger(medicine.getQuantity()) &&
                isNotEmpty(medicine.getExpiryDate());
    }
}
