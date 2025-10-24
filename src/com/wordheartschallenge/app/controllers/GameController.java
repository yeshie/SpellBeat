package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;

import java.io.InputStream;

public class GameController {

    private static final String[] KEYBOARD_ROW_1 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
    private static final String[] KEYBOARD_ROW_2 = {"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * Creates the game scene with consistent UI styling (static method for consistency).
     */
    public static Scene createScene(User user, int level) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");

        root.setTop(createTopBar(user, level));
        root.setCenter(createGameArea());
        root.setBottom(createKeyboardArea());

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(GameController.class.getResource("/css/style.css").toExternalForm());
        return scene;
    }

    private static Node createTopBar(User user, int level) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setSpacing(20);
        topBar.getStyleClass().add("game-top-bar");

        // Left side
        HBox leftBox = new HBox(15);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        HBox profileChip = createInfoChip(user.getAvatarPath(), user.getName());
        profileChip.getStyleClass().add("game-avatar-chip");

        Button homeButton = new Button();
        homeButton.setGraphic(createIconView("/images/home.png", 30));
        homeButton.getStyleClass().add("game-home-button");

        leftBox.getChildren().addAll(profileChip, homeButton);

        // Center logo
        VBox centerBox = new VBox(3);
        centerBox.setAlignment(Pos.CENTER);
        ImageView logo = createIconView("/images/logo.png", 50);
        Label title = new Label("Word Hearts Challenge");
        title.getStyleClass().add("game-title");
        centerBox.getChildren().addAll(logo, title);

        // Right side
        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        HBox heartInfo = createInfoChip("/images/heartLogo.png", "10");
        HBox levelInfo = createInfoChip("/images/level.png", "Level " + level);

        // 💖 Navigate to HeartController when clicking heart chip
        heartInfo.setOnMouseClicked(event -> {
        	javafx.stage.Stage stage = (javafx.stage.Stage) ((Node) event.getSource()).getScene().getWindow();
        	Scene heartScene = HeartController.createScene(user, level, stage);
        	stage.setScene(heartScene);

        });

        rightBox.getChildren().addAll(heartInfo, levelInfo);

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        topBar.getChildren().addAll(leftBox, spacerLeft, centerBox, spacerRight, rightBox);
        return topBar;
    }

    private static Node createGameArea() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("game-center-card");

        HBox guessBoxContainer = new HBox(15);
        guessBoxContainer.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            Label guessBox = new Label("_");
            guessBox.getStyleClass().add("game-guess-box");
            guessBoxContainer.getChildren().add(guessBox);
        }

        Button hintButton = new Button("Use Hint (0 remaining)");
        hintButton.setGraphic(createIconView("/images/hint.png", 20));
        hintButton.getStyleClass().add("game-hint-button");

        container.getChildren().addAll(guessBoxContainer, hintButton);
        return container;
    }

    private static Node createKeyboardArea() {
        VBox keyboardArea = new VBox(20);
        keyboardArea.setAlignment(Pos.CENTER);
        keyboardArea.setPadding(new Insets(10, 20, 30, 20));
        keyboardArea.getStyleClass().add("keyboard-area");

        HBox row1 = createKeyboardRow(KEYBOARD_ROW_1);
        HBox row2 = createKeyboardRow(KEYBOARD_ROW_2);

        Button playButton = new Button("Let's Play!");
        playButton.getStyleClass().add("game-play-button");

        keyboardArea.getChildren().addAll(row1, row2, playButton);
        return keyboardArea;
    }

    private static HBox createKeyboardRow(String[] keys) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        for (String key : keys) {
            Button keyButton = new Button(key);
            keyButton.getStyleClass().add("game-key");
            row.getChildren().add(keyButton);
        }
        return row;
    }

    private static HBox createInfoChip(String iconPath, String text) {
        HBox chip = new HBox(8);
        chip.setAlignment(Pos.CENTER);
        chip.getStyleClass().add("game-info-chip");

        ImageView iconView = createIconView(iconPath, 28);
        Label label = new Label(text);
        label.getStyleClass().add("game-info-label");

        chip.getChildren().addAll(iconView, label);
        return chip;
    }

    private static ImageView createIconView(String path, double size) {
        ImageView iconView = new ImageView();
        try (InputStream stream = GameController.class.getResourceAsStream(path)) {
            if (stream != null) {
                iconView.setImage(new Image(stream));
            } else {
                System.err.println("Icon not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
        }
        iconView.setFitWidth(size);
        iconView.setFitHeight(size);
        iconView.setPreserveRatio(true);
        return iconView;
    }
}
