package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.database.UserDAO;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.controllers.TopBarController;

public class ProfileLogic {

    private final User user;
    private final TopBarController topBarController;

    public ProfileLogic(User user, TopBarController topBarController) {
        this.user = user;
        this.topBarController = topBarController;
    }

    public String updateProfile(String newUsername, String newEmail, String newAvatar,
                                String newPassword, String confirmPassword) {

        boolean changed = false;

        // Username
        if (newUsername != null && !newUsername.equals(user.getName()) && !newUsername.isBlank()) {
            user.setName(newUsername.trim());
            changed = true;
        }

        // Email
        if (newEmail != null && !newEmail.equals(user.getEmail()) && newEmail.contains("@")) {
            user.setEmail(newEmail.trim());
            changed = true;
        }

        // Avatar
        if (newAvatar != null && !newAvatar.equals(user.getAvatarPath())) {
            user.setAvatarPath(newAvatar);
            // ✅ Immediately update visible top bar
            topBarController.updateAvatar(newAvatar);
            changed = true;
        }

        // Password
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) return "Passwords do not match.";
            user.setPassword(newPassword);
            changed = true;
        }

        if (!changed) return "No changes made.";

        // Save to DB
        boolean success = UserDAO.updateUserProfile(user);
        return success ? "Profile updated successfully!" : "Update failed. Try again.";
    }
}
