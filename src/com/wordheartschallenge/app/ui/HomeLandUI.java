package com.wordheartschallenge.app.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;

public class HomeLandUI {

    private VBox wrapper;
    private Button wordGameButton;
    private Button heartGameButton;

    public HomeLandUI() {
        buildUI();
    }

    private void buildUI() {
        HBox gameContainer = new HBox(80);
        gameContainer.setAlignment(Pos.CENTER);

        // Word Game Tile
        VBox wordGameTile = createGameTile("/images/logo.png", "Word Game", "Let's Play!");
        wordGameButton = (Button) wordGameTile.getChildren().get(2);

        // Heart Game Tile
        VBox heartGameTile = createGameTile("/images/heartLogo.png", "Heart Hint Game", "Let's Play!");
        heartGameButton = (Button) heartGameTile.getChildren().get(2);

        gameContainer.getChildren().addAll(wordGameTile, heartGameTile);

        wrapper = new VBox(gameContainer);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("game-center-wrapper-hl");
    }

    private VBox createGameTile(String iconPath, String title, String buttonText) {
        VBox tile = new VBox(16);
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("game-tile-hl");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("game-tile-title-hl");

        ImageView icon = createIconView(iconPath, 90, 90);

        Button playButton = new Button(buttonText);
        playButton.getStyleClass().add("game-play-button-hl");

        tile.getChildren().addAll(titleLabel, icon, playButton);
        return tile;
    }

    private ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = getClass().getResourceAsStream(path)) {
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

    public Node getView() {
        return wrapper;
    }

    public Button getWordGameButton() {
        return wordGameButton;
    }

    public Button getHeartGameButton() {
        return heartGameButton;
    }
}
