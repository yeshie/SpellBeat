package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Pos;
import javafx.scene.Node; // ✅ FIX: import Node
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;

public class HomeLandController {

    public static Scene createScene(User user, Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root-hl");

        // ✅ Use TopBarController
        TopBarController topBar = new TopBarController(user, stage);
        root.setTop(topBar.getView());

        // Center section
        root.setCenter(createGameSelectionArea(user, stage));

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HomeLandController.class.getResource("/css/style.css").toExternalForm());
        user.setLastScreen("HomeLand");

        return scene;
    }

    private static Node createGameSelectionArea(User user, Stage stage) {
        HBox gameContainer = new HBox(80);
        gameContainer.setAlignment(Pos.CENTER);

        VBox wordGameTile = createGameTile(
                "/images/logo.png",
                "Word Game",
                "Let's Play!",
                e -> {
                    Scene homeScene = HomeController.createScene(user, 1, user.getHearts(), stage);
                    stage.setScene(homeScene);
                });

        VBox heartGameTile = createGameTile(
                "/images/heartLogo.png",
                "Heart Hint Game",
                "Let's Play!",
                e -> {
                    Scene heartScene = HeartController.createScene(user, user.getHearts(), stage);
                    stage.setScene(heartScene);
                });

        gameContainer.getChildren().addAll(wordGameTile, heartGameTile);

        VBox wrapper = new VBox(gameContainer);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("game-center-wrapper-hl");
        return wrapper;
    }

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
