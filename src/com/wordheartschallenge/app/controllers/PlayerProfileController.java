package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;

public class PlayerProfileController {

    private Scene scene;

    public Scene createScene(User user) {

        // ===== Background Layer (Gradient) =====
        StackPane backgroundLayer = new StackPane();
        backgroundLayer.getStyleClass().add("profile-root"); // gradient defined in CSS

        // ===== Centered Logo =====
        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        ImageView logo = new ImageView();
        if (logoStream != null) {
            logo.setImage(new Image(logoStream));
            logo.setFitWidth(450);
            logo.setPreserveRatio(true);
        } else {
            System.err.println("Logo image not found!");
        }
        StackPane.setAlignment(logo, Pos.CENTER);

        backgroundLayer.getChildren().add(logo);

        // ===== Transparent Form Card =====
        VBox formCard = new VBox(15);
        formCard.setAlignment(Pos.CENTER);
        formCard.getStyleClass().add("profile-card"); // CSS class

        // ===== Title =====
        Label title = new Label("Player Profile");
        title.getStyleClass().add("profile-title");

        // ===== Input Fields =====
        TextField usernameField = new TextField(user.getName());
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("profile-textfield");

        Label avatarLabel = new Label("Choose your avatar:");
        avatarLabel.getStyleClass().add("profile-label");

        // ===== Avatar Selection =====
        HBox avatarBox = new HBox(20);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.getStyleClass().add("avatar-box");

        ToggleGroup avatarGroup = new ToggleGroup();
        String[] avatars = {
                "/images/avatar1.png",
                "/images/avatar2.png",
                "/images/avatar3.png",
                "/images/avatar4.png"
        };

        for (String path : avatars) {
            InputStream avatarStream = getClass().getResourceAsStream(path);
            ImageView avatarImg = new ImageView();
            if (avatarStream != null) {
                avatarImg.setImage(new Image(avatarStream));
                avatarImg.setFitWidth(80);
                avatarImg.setFitHeight(80);
            } else {
                System.err.println("Avatar image not found: " + path);
            }

            RadioButton avatarButton = new RadioButton();
            avatarButton.setGraphic(avatarImg);
            avatarButton.setToggleGroup(avatarGroup);
            avatarBox.getChildren().add(avatarButton);
        }

        // ===== Save Button =====
        Button saveButton = new Button("Save Profile");
        saveButton.getStyleClass().add("profile-button");
        VBox.setMargin(saveButton, new Insets(40, 0, 0, 0)); // top, right, bottom, left

        saveButton.setOnAction(e -> {
            String username = usernameField.getText();

            RadioButton selectedAvatar = (RadioButton) avatarGroup.getSelectedToggle();
            if (username.isEmpty()) {
                showAlert("Error", "Please enter username.");
                return;
            }
            if (selectedAvatar == null) {
                showAlert("Error", "Please select an avatar.");
                return;
            }

            System.out.println("✅ Player Profile Saved:");
            System.out.println("Name: " + username);
            System.out.println("Avatar selected!");

            showAlert("Success", "Profile saved successfully!");
        });

        // ===== Add components to form card =====
        formCard.getChildren().addAll(title, usernameField, avatarLabel, avatarBox, saveButton);

        // ===== Stack layout =====
        StackPane root = new StackPane(backgroundLayer, formCard);
        root.setAlignment(Pos.CENTER);

        this.scene = new Scene(root, 900, 600);
        this.scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        return scene;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
