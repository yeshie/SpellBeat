package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.InputStream;

public class PlayerProfileController {

    private Scene scene;
    private Stage stage;

    /** ✅ Make it static to match pattern */
    public static Scene createScene(User user) {
        PlayerProfileController ctrl = new PlayerProfileController();
        return ctrl.buildScene(user);
    }

    private Scene buildScene(User user) {
        // Background
        StackPane backgroundLayer = new StackPane();
        backgroundLayer.getStyleClass().add("profile-root");

        // Logo
        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        ImageView logo = new ImageView();
        if (logoStream != null) {
            logo.setImage(new Image(logoStream));
            logo.setFitWidth(400);
            logo.setPreserveRatio(true);
        }
        StackPane.setAlignment(logo, Pos.CENTER);
        backgroundLayer.getChildren().add(logo);

        // Form Card
        VBox formCard = new VBox(15);
        formCard.setAlignment(Pos.CENTER);
        formCard.getStyleClass().add("profile-card");
        formCard.setMaxWidth(450);
        formCard.setPadding(new Insets(30));

        // Title
        Label title = new Label("Player Profile");
        title.getStyleClass().add("profile-title");

        // Username
        TextField usernameField = new TextField(user.getName());
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("profile-textfield");

        // Avatar Selection
        Label avatarLabel = new Label("Choose your avatar:");
        avatarLabel.getStyleClass().add("profile-label");

        HBox avatarBox = new HBox(20);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setStyle("-fx-padding:10;");
        avatarBox.setMaxWidth(400);

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
            }

            RadioButton avatarButton = new RadioButton();
            avatarButton.setGraphic(avatarImg);
            avatarButton.setToggleGroup(avatarGroup);
            avatarButton.setUserData(path);
            avatarBox.getChildren().add(avatarButton);
        }

        // Let's Play Button
        Button letsPlayButton = new Button("Let's Play");
        letsPlayButton.getStyleClass().add("profile-button");
        VBox.setMargin(letsPlayButton, new Insets(40, 0, 0, 0));

        letsPlayButton.setOnAction(e -> {
            String username = usernameField.getText();
            RadioButton selectedAvatar = (RadioButton) avatarGroup.getSelectedToggle();

            if (username.isEmpty()) {
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

            // Navigate to HomeController (defaults level=1, hearts=5)
            Stage stage = (Stage) letsPlayButton.getScene().getWindow();
            HomeController.createScene(user, 1, 5, stage);
        });

        formCard.getChildren().addAll(title, usernameField, avatarLabel, avatarBox, letsPlayButton);

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
