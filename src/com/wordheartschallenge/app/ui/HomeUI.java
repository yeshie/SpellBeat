package com.wordheartschallenge.app.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;

public class HomeUI {

    // Create scrollable level selection area with arrows
    public Node createLevelSelectArea(Node... levelTiles) {
        VBox backgroundCard = new VBox();
        backgroundCard.getStyleClass().add("level-tile-background");
        backgroundCard.setAlignment(Pos.CENTER);

        HBox levelContainer = new HBox(30);
        levelContainer.setAlignment(Pos.CENTER);
        levelContainer.getChildren().addAll(levelTiles);

        backgroundCard.getChildren().add(levelContainer);

        ScrollPane scrollPane = new ScrollPane(backgroundCard);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Button leftArrow = new Button("<");
        leftArrow.getStyleClass().add("home-arrow-button");
        leftArrow.setOnAction(e -> scrollPane.setHvalue(Math.max(scrollPane.getHvalue() - 0.25, 0)));

        Button rightArrow = new Button(">");
        rightArrow.getStyleClass().add("home-arrow-button");
        rightArrow.setOnAction(e -> scrollPane.setHvalue(Math.min(scrollPane.getHvalue() + 0.25, 1)));

        HBox wrapper = new HBox(10, leftArrow, scrollPane, rightArrow);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("level-scroll-wrapper");

        return wrapper;
    }

    // Create individual level tile
    public VBox createLevelTile(String levelText, Node bottom, ImageView trophyIcon, HBox starsBox) {
        VBox tile = new VBox(12);
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("level-tile");

        Label title = new Label(levelText);
        title.getStyleClass().add("level-title");

        StackPane trophyWrapper = new StackPane(trophyIcon);
        trophyWrapper.setPrefSize(60, 60);
        trophyWrapper.setAlignment(Pos.CENTER);

        tile.getChildren().addAll(title, trophyWrapper, starsBox, bottom);
        return tile;
    }

    // Create stars box
    public HBox createStarsBox(ImageView starWrapper) {
        HBox starsBox = new HBox(6);
        starsBox.setAlignment(Pos.CENTER);
        starsBox.getChildren().add(starWrapper);
        return starsBox;
    }

    // Load ImageView from resources
    public ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = HomeUI.class.getResourceAsStream(path)) {
            if (stream != null) icon.setImage(new Image(stream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
