package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.services.AuthService;
import com.wordheartschallenge.app.utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class RegisterController {

    public static Scene createScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        HBox topBox = new HBox(12);
        topBox.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image(RegisterController.class.getResourceAsStream("/images/logo.png")));
        logo.setFitHeight(44);
        logo.setPreserveRatio(true);
        Label title = new Label("Create account");
        title.setFont(Font.font("Inter", 20));
        topBox.getChildren().addAll(logo, title);
        root.setTop(topBox);

        VBox form = new VBox(10);
        form.setPadding(new Insets(30));
        form.setAlignment(Pos.CENTER);

        TextField nameField = new TextField(); nameField.setPromptText("Full name");
        TextField emailField = new TextField(); emailField.setPromptText("Email (gmail)");
        PasswordField passwordField = new PasswordField(); passwordField.setPromptText("Password");
        PasswordField confirmField = new PasswordField(); confirmField.setPromptText("Confirm password");
        TextField ageField = new TextField(); ageField.setPromptText("Age");

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("primary-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Back to Login");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        form.getChildren().addAll(nameField, emailField, passwordField, confirmField, ageField, registerBtn, backBtn);
        root.setCenter(form);

        // events
        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passwordField.getText().trim();
            String confirm = confirmField.getText().trim();
            String ageText = ageField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty() || ageText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill all fields.");
                return;
            }
            if (!pass.equals(confirm)) {
                showAlert(Alert.AlertType.ERROR, "Passwords do not match.");
                return;
            }
            int age;
            try { age = Integer.parseInt(ageText); }
            catch (NumberFormatException ex) { showAlert(Alert.AlertType.ERROR, "Age must be a number."); return; }

            boolean ok = AuthService.register(name, email, pass, age);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Registration successful. Please login.");
                SceneManager.switchScene(LoginController.createScene());
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration failed. Email may already exist.");
            }
        });

        backBtn.setOnAction(e -> SceneManager.switchScene(LoginController.createScene()));

        Scene scene = new Scene(root, 780, 520);
        scene.getStylesheets().add(RegisterController.class.getResource("/css/style.css").toExternalForm());
        return scene;
    }

    private static void showAlert(Alert.AlertType type, String text) {
        Alert a = new Alert(type, text);
        a.showAndWait();
    }
}
