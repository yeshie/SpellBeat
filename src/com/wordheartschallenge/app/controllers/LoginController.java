package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.database.UserDAO;
import com.wordheartschallenge.app.utils.PasswordUtils;
import com.wordheartschallenge.app.services.LoginLogic;
import com.wordheartschallenge.app.ui.LoginUI;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LoginController {
    
    public static Scene createScene() {
        LoginUI loginUI = new LoginUI();
        
        LoginLogic.loadRememberedUser(loginUI.getUserField(), loginUI.getPassField(), loginUI.getRememberMe());
        
        loginUI.getLoginButton().setOnAction(e -> {
            String username = loginUI.getUserField().getText().trim();
            String password = loginUI.getPassField().getText().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill all fields!");
                return;
            }
            
            if (password.length() < 4) {
                showAlert(Alert.AlertType.ERROR, "Password must be at least 4 characters long!");
                return;
            }
            
            String encryptedPassword = PasswordUtils.encryptPassword(password);
            User user = UserDAO.getUserByCredentials(username, encryptedPassword);
            
            if (user != null) {
                if (loginUI.getRememberMe().isSelected()) {
                    LoginLogic.saveRememberedUser(username, password);
                } else {
                    LoginLogic.clearRememberedUser();
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Login successful! Welcome " + user.getName());
                
                Stage stage = (Stage) loginUI.getLoginButton().getScene().getWindow();
                stage.setScene(HomeLandController.createScene(user, stage));
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
            }
        });
        
        loginUI.getCreateAccountLink().setOnMouseClicked(e -> {
            Stage stage = (Stage) loginUI.getCreateAccountLink().getScene().getWindow();
            stage.setScene(RegisterController.createScene());
        });
        
        return loginUI.getScene();
    }
    
    private static void showAlert(Alert.AlertType type, String text) {
        Alert alert = new Alert(type, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}