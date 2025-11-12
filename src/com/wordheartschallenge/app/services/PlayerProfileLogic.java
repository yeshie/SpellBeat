package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.controllers.HomeController;
import com.wordheartschallenge.app.database.UserDAO;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.PlayerProfileUI;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class PlayerProfileLogic {

    private final User user;
    private final PlayerProfileUI ui;

    public PlayerProfileLogic(User user, PlayerProfileUI ui) {
        this.user = user;
        this.ui = ui; // keep UI reference so we can get input values
    }

    public void handleLetsPlay() {
        Stage stage = (Stage) ui.getScene().getWindow();

        String username = ui.getUsername();
        RadioButton selectedAvatar = (RadioButton) ui.getSelectedAvatar();

        if (username == null || username.isEmpty()) {
            showAlert("Error", "Please enter a username.");
            return;
        }

        if (selectedAvatar == null) {
            showAlert("Error", "Please select an avatar.");
            return;
        }

        String avatarPath = (String) selectedAvatar.getUserData();
        user.setName(username);
        user.setAvatarPath(avatarPath);
        user.setHearts(10);
        user.setCurrentLevel(1);

        boolean success = UserDAO.updateUserProfile(user);
        if (!success) {
            showAlert("Error", "Failed to save user profile.");
            return;
        }

        HomeController.createScene(user, 1, 10, stage);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
