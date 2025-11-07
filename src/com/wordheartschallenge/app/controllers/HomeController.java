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
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.InputStream;

public class HomeController {

    public static Scene createScene(User user, int level, int hearts, Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");

        // ✅ Use TopBarController instead of createTopBar()
        TopBarController topBar = new TopBarController(user, stage);
        root.setTop(topBar.getView());

        // ✅ Level selection area
        root.setCenter(createLevelSelectArea(user, stage));

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(HomeController.class.getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        return scene;
    }

    // Updated to accept User and Stage
    private static Node createLevelSelectArea(User user, Stage stage) {
        VBox backgroundCard = new VBox();
        backgroundCard.getStyleClass().add("level-tile-background");
        backgroundCard.setAlignment(Pos.CENTER);

        HBox levelContainer = new HBox(30);
        levelContainer.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 12; i++) {
            boolean completed = i <= 3;
            boolean playable = i == 4;
            levelContainer.getChildren().add(createLevelTile(user, stage, i, completed, playable));
        }

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

    private static VBox createLevelTile(User user, Stage stage, int levelNum, boolean completed, boolean playable) {
        VBox tile = new VBox(12);
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("level-tile");

        Label title = new Label("Level " + String.format("%02d", levelNum));
        title.getStyleClass().add("level-title");

        ImageView trophyIcon = createIconView("/images/level.png", 50, 50);
        trophyIcon.getStyleClass().add("level-icon");

        StackPane trophyWrapper = new StackPane(trophyIcon);
        trophyWrapper.setPrefSize(60, 60);
        trophyWrapper.setAlignment(Pos.CENTER);

        trophyIcon.setScaleX(1.6);
        trophyIcon.setScaleY(1.6);

        HBox starsBox = new HBox(6);
        starsBox.setAlignment(Pos.CENTER);

        ImageView star = createIconView("/images/star.png", 50, 40);
        star.getStyleClass().add("level-star");

        StackPane starWrapper = new StackPane(star);
        starWrapper.setPrefSize(60, 50);
        starWrapper.setAlignment(Pos.CENTER);

        star.setScaleX(2.5);
        star.setScaleY(2.5);

        starsBox.getChildren().add(starWrapper);

        Node bottom;
        if (completed) {
            Label completedLabel = new Label("Completed");
            completedLabel.getStyleClass().add("level-completed-banner");
            bottom = completedLabel;
        } else if (playable) {
            Button playButton = new Button("Let's Play!");
            playButton.getStyleClass().add("level-play-button");

            playButton.setOnAction(e -> {
                Scene gameScene = GameController.createScene(user, levelNum);
                stage.setScene(gameScene);
            });
            bottom = playButton;
        } else {
            bottom = new Region();
            ((Region) bottom).setMinHeight(20);
        }

        tile.getChildren().addAll(title, trophyWrapper, starsBox, bottom);
        return tile;
    }

    private static ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = HomeController.class.getResourceAsStream(path)) {
            if (stream != null) icon.setImage(new Image(stream));
        } catch (Exception e) { e.printStackTrace(); }
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
