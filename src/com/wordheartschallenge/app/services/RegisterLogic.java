package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.utils.PasswordUtils;
import com.wordheartschallenge.app.utils.AlertUtil;
import com.wordheartschallenge.app.database.UserDAO;

public class RegisterLogic {

    public static User createUser(String email, String name, String ageText, String password, String confirm) {
        
        // 1. Check if all fields are filled
        if (email.isEmpty() || name.isEmpty() || ageText.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            AlertUtil.showEmptyFieldsError();
            return null;
        }

        // 2. Email Validation
        if (!isValidEmail(email)) {
            AlertUtil.showEmailError();
            return null;
        }

        // 3. Name Validation (letters and spaces only)
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            AlertUtil.showNameError();
            return null;
        }

        // 4. Age Validation (exactly 2 digits, 10-99)
        if (!ageText.matches("^\\d{2}$")) {
            AlertUtil.showAgeError();
            return null;
        }
        
        int age = Integer.parseInt(ageText);
        if (age < 10 || age > 99) {
            AlertUtil.showAgeError();
            return null;
        }

        // 5. Password Validation (at least 4 characters)
        if (password.length() < 4) {
            AlertUtil.showPasswordError();
            return null;
        }

        // 6. Confirm Password Validation
        if (!password.equals(confirm)) {
            AlertUtil.showPasswordMismatchError();
            return null;
        }

        // All validation passed - create user
        String encryptedPassword = PasswordUtils.encryptPassword(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encryptedPassword);
        user.setAge(age);
        user.setHearts(10);
        user.setCurrentLevel(1);

        boolean success = UserDAO.insertUser(user);
        if (!success) {
            AlertUtil.showAccountExistsError();
            return null;
        }

        return user;
    }

    private static boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}