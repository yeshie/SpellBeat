package com.wordheartschallenge.app.services;


import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.utils.PasswordUtils;
import com.wordheartschallenge.app.database.UserDAO;

import javafx.scene.control.Alert;

public class RegisterLogic {

    public static User createUser(String email, String name, String ageText, String password, String confirm) {
        if (email.isEmpty() || name.isEmpty() || ageText.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields!");
            return null;
        }

        if (password.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Password must be at least 4 characters long!");
            return null;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid age!");
            return null;
        }

        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match!");
            return null;
        }

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
            showAlert(Alert.AlertType.ERROR, "Failed to create account. Try again!");
            return null;
        }

        return user;
    }

    private static void showAlert(Alert.AlertType type, String text) {
        Alert alert = new Alert(type, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

