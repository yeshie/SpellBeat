package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.controllers.HomeLandController;
import com.wordheartschallenge.app.database.UserDAO;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.PlayerProfileUI;
import com.wordheartschallenge.app.utils.AlertUtil;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class PlayerProfileLogic {
    
    private final User user;
    private final PlayerProfileUI ui;

    public PlayerProfileLogic(User user, PlayerProfileUI ui) {
        this.user = user;
        this.ui = ui;
    }

    public void handleLetsPlay() {
        Stage stage = (Stage) ui.getScene().getWindow();
        String username = ui.getUsername();
        RadioButton selectedAvatar = (RadioButton) ui.getSelectedAvatar();

        // Validation: Check username
        if (username == null || username.isEmpty()) {
            AlertUtil.showCustomAlert(
                "Username Required! 📝",
                "Please enter a username to continue.\nThis is how others will see you!"
            );
            return;
        }

        // Validation: Check if username contains only letters and spaces
        if (!username.matches("^[a-zA-Z\\s]+$")) {
            AlertUtil.showCustomAlert(
                "Invalid Username! ✨",
                "Username can only contain letters and spaces.\nNo numbers or special characters, please!"
            );
            return;
        }

        // Validation: Check avatar selection
        if (selectedAvatar == null) {
            AlertUtil.showCustomAlert(
                "Choose Your Avatar!",
                "Please select an avatar.\nPick one that represents you!"
            );
            return;
        }

        String avatarPath = (String) selectedAvatar.getUserData();
        
        // Update user data
        user.setName(username);
        user.setAvatarPath(avatarPath);
        user.setHearts(10);
        user.setCurrentLevel(1);

        // Save to database
        boolean success = UserDAO.updateUserProfile(user);
        
        if (!success) {
            AlertUtil.showCustomAlert(
                "Save Failed! 😞",
                "Failed to save your profile.\nPlease try again!"
            );
            return;
        }

        // Success! Navigate to HomeLandController
        HomeLandController.createScene(user, stage);
    }
}