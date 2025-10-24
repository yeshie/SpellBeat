package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;

public class HomeLandController {

    /**
     * Create the HomeLand (main menu) scene.
     * Adjusted to match LoginController and fix navigation signatures.
     */
    public static Scene createScene(User user, Stage stage) {
        // Default level and heart values
        int level = 1;
        int hearts = 10;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root-hl");

        // Top bar
        root.setTop(createTopBar(user, level, hearts, stage));

        // Center section
        root.setCenter(createGameSelectionArea(user, stage));

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HomeLandController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        return scene;
    }

    /**
     * Creates the top navigation bar (avatar, hearts, level, logout).
     */
    private static Node createTopBar(User user, int level, int hearts, Stage stage) {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("home-top-bar-hl");
        topBar.setAlignment(Pos.CENTER);

        // === Left: Avatar + Home ===
        HBox avatarChip = createInfoChip(user.getAvatarPath(), user.getName(), true);
        Button homeButton = createIconButton("/images/home.png");
        HBox leftBox = new HBox(12, avatarChip, homeButton);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // === Center: Logo ===
        ImageView logo = createIconView("/images/logo.png", 180, 100);
        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);

        // === Right: Hearts + Level + Logout ===
        HBox heartsChip = createInfoChip("/images/heartLogo.png", String.valueOf(hearts), false);
        HBox levelChip = createInfoChip("/images/level.png", "Level " + level, false);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button-hl");

        // ✅ FIXED: call LoginController.createScene() (no arguments)
        logoutButton.setOnAction(e -> {
            Scene loginScene = LoginController.createScene();
            stage.setScene(loginScene);
        });

        HBox rightBox = new HBox(12, heartsChip, levelChip, logoutButton);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        topBar.getChildren().addAll(leftBox, spacerLeft, logoBox, spacerRight, rightBox);
        return topBar;
    }

    /**
     * Info chip (used for avatar, heart count, and level).
     */
    private static HBox createInfoChip(String iconPath, String text, boolean wide) {
        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER);
        chip.getStyleClass().add("home-chip-hl");
        if (wide) chip.getStyleClass().add("wide");

        ImageView icon = createIconView(iconPath, 34, 34);
        Label label = new Label(text);
        label.getStyleClass().add("home-chip-label-hl");

        chip.getChildren().addAll(icon, label);
        return chip;
    }

    /**
     * Home icon button.
     */
    private static Button createIconButton(String iconPath) {
        ImageView icon = createIconView(iconPath, 34, 34);
        Button btn = new Button("", icon);
        btn.getStyleClass().add("home-button-hl");
        return btn;
    }

    /**
     * Center area with game tiles.
     */
    private static Node createGameSelectionArea(User user, Stage stage) {
        HBox gameContainer = new HBox(80);
        gameContainer.setAlignment(Pos.CENTER);

        // === Word Game Tile ===
        VBox wordGameTile = createGameTile(
                "/images/logo.png",
                "Word Game",
                "Let's Play!",
                e -> {
                    // Navigate to HomeController
                    Scene homeScene = HomeController.createScene(user, 1, 10, stage);
                    stage.setScene(homeScene);
                });

        // === Heart Hint Game Tile ===
        VBox heartGameTile = createGameTile(
                "/images/heartLogo.png",
                "Heart Hint Game",
                "Let's Play!",
                e -> {
                    // Navigate to HeartController
                    Scene heartScene = HeartController.createScene(user, 10, stage);
                    stage.setScene(heartScene);
                });

        gameContainer.getChildren().addAll(wordGameTile, heartGameTile);

        VBox wrapper = new VBox(gameContainer);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("game-center-wrapper-hl");
        return wrapper;
    }

    /**
     * Game tile with icon, title, and button.
     */
    private static VBox createGameTile(String iconPath, String title, String buttonText,
                                       javafx.event.EventHandler<javafx.event.ActionEvent> onPlay) {
        VBox tile = new VBox(16);
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("game-tile-hl");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("game-tile-title-hl");

        ImageView icon = createIconView(iconPath, 90, 90);

        Button playButton = new Button(buttonText);
        playButton.getStyleClass().add("game-play-button-hl");
        playButton.setOnAction(onPlay);

        tile.getChildren().addAll(titleLabel, icon, playButton);
        return tile;
    }

    /**
     * Utility for loading icons safely.
     */
    private static ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = HomeLandController.class.getResourceAsStream(path)) {
            if (stream != null) {
                icon.setImage(new Image(stream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
