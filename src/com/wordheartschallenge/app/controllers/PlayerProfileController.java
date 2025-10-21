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

import java.io.InputStream;

public class PlayerProfileController {

    public static Scene createScene(User user) {
        HBox root = new HBox();

        // --- Left Panel (Gradient + Logo) ---
        VBox leftPanel = new VBox(20);
        leftPanel.getStyleClass().add("left-panel");
        leftPanel.setPrefWidth(450);
        leftPanel.setAlignment(Pos.CENTER);

        Image logoImg = loadImage("/images/logo.png", 300);
        ImageView logo = new ImageView(logoImg);
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        leftPanel.getChildren().add(logo);

        // --- Right Panel (Profile Form) ---
        VBox rightPanel = new VBox(15);
        rightPanel.getStyleClass().add("right-panel");
        rightPanel.setPrefWidth(450);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        VBox profileCard = new VBox(15);
        profileCard.getStyleClass().add("form-card");
        profileCard.setAlignment(Pos.CENTER);

        Label title = new Label("Create Your Player Profile");
        title.setFont(Font.font("Serif", 28));

        Label nameLabel = new Label("Choose Your Player Name");
        nameLabel.setFont(Font.font(16));

        TextField nameField = new TextField(user.getName()); // Pre-fill with registered name
        nameField.setFont(Font.font(16));

        Label avatarLabel = new Label("Choose Your Avatar");
        avatarLabel.setFont(Font.font(16));

        HBox avatarBox = new HBox(15);
        avatarBox.setAlignment(Pos.CENTER);

        ToggleGroup avatarGroup = new ToggleGroup();
        for (int i = 1; i <= 4; i++) {
            Image avatarImg = loadImage("/images/avatar" + i + ".png", 80);
            ImageView avatarView = new ImageView(avatarImg);
            avatarView.setFitWidth(80);
            avatarView.setPreserveRatio(true);

            RadioButton rb = new RadioButton();
            rb.setGraphic(avatarView);
            rb.setToggleGroup(avatarGroup);
            avatarBox.getChildren().add(rb);
        }

        Button playButton = new Button("Let's Play!");
        playButton.getStyleClass().add("primary-button");
        playButton.setMaxWidth(Double.MAX_VALUE);

        profileCard.getChildren().addAll(title, nameLabel, nameField, avatarLabel, avatarBox, playButton);
        rightPanel.getChildren().add(profileCard);

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(PlayerProfileController.class.getResource("/css/style.css").toExternalForm());

        playButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile created! Ready to play, " + nameField.getText());
            alert.showAndWait();
            // TODO: Switch to main game scene
        });

        return scene;
    }

    /**
     * Utility method to load an image safely.
     * If the image is missing, uses a placeholder.
     */
    private static Image loadImage(String path, double width) {
        InputStream stream = PlayerProfileController.class.getResourceAsStream(path);
        if (stream != null) {
            return new Image(stream, width, 0, true, true);
        } else {
            System.err.println("Image not found: " + path);
            return new Image("https://via.placeholder.com/" + (int) width); // temporary placeholder
        }
    }
}
