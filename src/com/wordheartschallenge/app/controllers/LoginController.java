package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.AuthService;
import com.wordheartschallenge.app.utils.PasswordUtils; // ✅ Import new utility
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class LoginController {

    private static final String REMEMBER_FILE = "remember_me.dat";

    public static Scene createScene() {
        HBox root = new HBox();
        VBox leftPanel = new VBox();
        leftPanel.getStyleClass().add("left-panel");
        leftPanel.setPrefWidth(450);
        leftPanel.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image(LoginController.class.getResourceAsStream("/images/logo.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);
        leftPanel.getChildren().add(logo);

        VBox rightPanel = new VBox(15);
        rightPanel.getStyleClass().add("right-panel");
        rightPanel.setPrefWidth(450);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        VBox formCard = new VBox(15);
        formCard.getStyleClass().add("form-card");
        formCard.setAlignment(Pos.CENTER);

        Label title = new Label("Start your journey");
        title.getStyleClass().add("label-title");

        Label subtitle = new Label("Every word you spell keeps your beat alive!");
        subtitle.getStyleClass().add("label-subtitle");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.getStyleClass().add("text-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("password-field");

        CheckBox rememberMe = new CheckBox("Remember Me");
        rememberMe.setStyle("-fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-text-fill: #59504B;");

        // ✅ Load saved credentials
        loadRememberedUser(userField, passField, rememberMe);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        HBox registrationBox = new HBox(5);
        registrationBox.setAlignment(Pos.CENTER);
        Label notRegisteredLabel = new Label("Not Registered Yet?");
        Label createAccountLink = new Label("Create an account");
        createAccountLink.getStyleClass().add("link-label");
        createAccountLink.setOnMouseClicked(e -> {
            Stage stage = (Stage) createAccountLink.getScene().getWindow();
            stage.setScene(RegisterController.createScene());
        });
        registrationBox.getChildren().addAll(notRegisteredLabel, createAccountLink);

        formCard.getChildren().addAll(title, subtitle, userField, passField, rememberMe, loginButton, registrationBox);
        rightPanel.getChildren().add(formCard);
        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(LoginController.class.getResource("/css/style.css").toExternalForm());

        // ✅ Login Action
        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill all fields!");
                return;
            }

            if (password.length() < 4) {
                showAlert(Alert.AlertType.ERROR, "Password must be at least 4 characters long!");
                return;
            }

            // ✅ Encrypt password before verifying
            User user = AuthService.login(username, password);

            if (user != null) {
                if (rememberMe.isSelected()) {
                    saveRememberedUser(username, password);
                } else {
                    clearRememberedUser();
                }

                showAlert(Alert.AlertType.INFORMATION, "Login successful! Welcome " + user.getName());

                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene homeScene = HomeLandController.createScene(user, stage);
                stage.setScene(homeScene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
            }
        });

        return scene;
    }

    // ===== Utility: Show Alert =====
    private static void showAlert(Alert.AlertType type, String text) {
        Alert alert = new Alert(type, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // ✅ Use PasswordUtil for encoding/decoding
    private static void saveRememberedUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REMEMBER_FILE))) {
            writer.write(username + "\n");
            writer.write(PasswordUtils.encodePassword(password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadRememberedUser(TextField userField, PasswordField passField, CheckBox rememberMe) {
        File file = new File(REMEMBER_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String username = reader.readLine();
                String encodedPassword = reader.readLine();

                if (username != null && encodedPassword != null) {
                    userField.setText(username);
                    passField.setText(PasswordUtils.decodePassword(encodedPassword));
                    rememberMe.setSelected(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clearRememberedUser() {
        File file = new File(REMEMBER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
