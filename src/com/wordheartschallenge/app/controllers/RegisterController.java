package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.ui.RegisterUI;
import com.wordheartschallenge.app.services.RegisterLogic;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.utils.SceneManager;
import javafx.scene.Scene;

public class RegisterController {
    
    public static Scene createScene() {
        RegisterUI ui = new RegisterUI();
        
        // Login link action
        ui.getLoginLink().setOnMouseClicked(e -> 
            SceneManager.switchScene(LoginController.createScene())
        );
        
        // Create account button with custom alert validation
        ui.getCreateBtn().setOnAction(e -> {
            User user = RegisterLogic.createUser(
                    ui.getEmailField().getText().trim(),
                    ui.getNameField().getText().trim(),
                    ui.getAgeField().getText().trim(),
                    ui.getPasswordField().getText().trim(),
                    ui.getConfirmField().getText().trim()
            );
            
            if (user != null) {
                // Success - Pass user to PlayerProfileController
                SceneManager.switchScene(PlayerProfileController.createScene(user));
            }
            // If validation failed, custom alert is already shown
        });
        
        return ui.getScene();
    }
}