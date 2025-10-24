package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.AuthService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginController {

    public static Scene createScene() {
        HBox root = new HBox();

        // ===== Left Panel (Gradient + Logo) =====
        VBox leftPanel = new VBox();
        leftPanel.getStyleClass().add("left-panel");
        leftPanel.setPrefWidth(450);
        leftPanel.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image(LoginController.class.getResourceAsStream("/images/logo.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);
        leftPanel.getChildren().add(logo);

        // ===== Right Panel (Login Form) =====
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

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        // ===== Registration Link =====
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

        // Add components to formCard
        formCard.getChildren().addAll(title, subtitle, userField, passField, loginButton, registrationBox);
        rightPanel.getChildren().add(formCard);

        root.getChildren().addAll(leftPanel, rightPanel);

        // ===== Scene Setup =====
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(LoginController.class.getResource("/css/style.css").toExternalForm());

        // ===== Login Action =====
        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill all fields!");
                return;
            }

            User user = AuthService.login(username, password);
            if (user != null) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful! Welcome " + user.getName());

                // Navigate to HomeLandController
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene homeScene = HomeLandController.createScene(user, stage);
                stage.setScene(homeScene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
            }
        });

        return scene;
    }

    // ===== Helper Alert Method =====
    private static void showAlert(Alert.AlertType type, String text) {
        Alert alert = new Alert(type, text);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
