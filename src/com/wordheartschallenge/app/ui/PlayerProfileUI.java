package com.wordheartschallenge.app.ui;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.InputStream;

public class PlayerProfileUI {

    private Scene scene;
    private TextField usernameField;
    private ToggleGroup avatarGroup;
    private Button letsPlayButton;

    public PlayerProfileUI(User user) {
        buildUI(user);
    }

    private void buildUI(User user) {
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

        Label title = new Label("Player Profile");
        title.getStyleClass().add("profile-title");

        // Username
        usernameField = new TextField(user.getName());
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("profile-textfield");

        // Avatar Selection
        Label avatarLabel = new Label("Choose your avatar:");
        avatarLabel.getStyleClass().add("profile-label");

        HBox avatarBox = new HBox(20);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setStyle("-fx-padding:10;");
        avatarBox.setMaxWidth(400);

        avatarGroup = new ToggleGroup();
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

        letsPlayButton = new Button("Let's Play");
        letsPlayButton.getStyleClass().add("profile-button");
        VBox.setMargin(letsPlayButton, new Insets(40, 0, 0, 0));

        formCard.getChildren().addAll(title, usernameField, avatarLabel, avatarBox, letsPlayButton);

        StackPane root = new StackPane(backgroundLayer, formCard);
        root.setAlignment(Pos.CENTER);

        this.scene = new Scene(root, 900, 600);
        this.scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    }

 // In PlayerProfileUI.java
    public void setOnLetsPlay(Runnable handler) {
        letsPlayButton.setOnAction(e -> handler.run());
    }


    public Scene getScene() {
        return scene;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public Toggle getSelectedAvatar() {
        return avatarGroup.getSelectedToggle();
    }
}
