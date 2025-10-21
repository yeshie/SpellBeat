package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
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
        HBox root = new HBox();

        // --- Left Panel (Gradient + Logo + Slogan) ---
        VBox leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("left-panel");
        leftPanel.setPrefWidth(450);
        leftPanel.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image(RegisterController.class.getResourceAsStream("/images/logo.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        Label slogan1 = new Label("Dive into the rhythm of words.");
        slogan1.setFont(Font.font("Serif", 18));
        slogan1.setStyle("-fx-text-fill: white;");

        Label slogan2 = new Label("Every word you spell keeps your beat alive.");
        slogan2.setFont(Font.font("Serif", 18));
        slogan2.setStyle("-fx-text-fill: brown;");

        Label slogan3 = new Label("Start your story today!");
        slogan3.setFont(Font.font("Serif", 18));
        slogan3.setStyle("-fx-text-fill: brown;");

        leftPanel.getChildren().addAll(logo, slogan1, slogan2, slogan3);
        leftPanel.setPadding(new Insets(40, 20, 40, 20));

        // --- Right Panel (Register Form) ---
        VBox rightPanel = new VBox(15);
        rightPanel.getStyleClass().add("right-panel");
        rightPanel.setPrefWidth(450);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        VBox formCard = new VBox(15);
        formCard.getStyleClass().add("form-card");
        formCard.setAlignment(Pos.CENTER);

        Label title = new Label("Create an account");
        title.getStyleClass().add("label-title");

        Label subtitle = new Label("Join SpellBeat and keep your beat alive!");
        subtitle.getStyleClass().add("label-subtitle");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField ageField = new TextField();
        ageField.setPromptText("Age"); // New age field

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");

        Button createBtn = new Button("Create an account");
        createBtn.getStyleClass().add("primary-button");
        createBtn.setMaxWidth(Double.MAX_VALUE);

        // --- Login Link ---
        HBox loginBox = new HBox(5);
        loginBox.setAlignment(Pos.CENTER);
        Label alreadyLabel = new Label("Already have an account?");
        Label loginLink = new Label("Login");
        loginLink.getStyleClass().add("link-label");
        loginLink.setOnMouseClicked(e -> SceneManager.switchScene(LoginController.createScene()));
        loginBox.getChildren().addAll(alreadyLabel, loginLink);

        formCard.getChildren().addAll(title, subtitle, emailField, nameField, ageField,
                passwordField, confirmField, createBtn, loginBox);
        rightPanel.getChildren().add(formCard);

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(RegisterController.class.getResource("/css/style.css").toExternalForm());

        // --- Button Action ---
        createBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String name = nameField.getText().trim();
            String ageText = ageField.getText().trim();
            String password = passwordField.getText().trim();
            String confirm = confirmField.getText().trim();

            if(email.isEmpty() || name.isEmpty() || ageText.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill all fields!");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch(NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Please enter a valid age!");
                return;
            }

            if(!password.equals(confirm)) {
                showAlert(Alert.AlertType.ERROR, "Passwords do not match!");
                return;
            }

            // Create user and pass to PlayerProfile
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setAge(age);

            PlayerProfileController profileUI = new PlayerProfileController();
            SceneManager.switchScene(profileUI.createScene(user));

        });

        return scene;
    }

    private static void showAlert(Alert.AlertType type, String text) {
        Alert a = new Alert(type, text);
        a.showAndWait();
    }
}
