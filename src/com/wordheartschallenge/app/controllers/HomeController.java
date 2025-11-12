package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.HomeLogic;
import com.wordheartschallenge.app.ui.HomeUI;
import com.wordheartschallenge.app.database.UserProgressDAO;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Set;
import java.util.HashSet;

public class HomeController {

    public static Scene createScene(User user, Stage stage) {
        int startLevel = HomeLogic.getStartLevel(user);
        int hearts = HomeLogic.getStartHearts(user);

        return createScene(user, startLevel, hearts, stage);
    }

    public static Scene createScene(User user, int startLevel, int hearts, Stage stage) {
        HomeUI homeUI = new HomeUI();
        VBox[] levelTiles = new VBox[12];

        // Load completed levels from DB
        Set<Integer> completedLevels = new HashSet<>();
        for (int i = 1; i <= 12; i++) {
            if (UserProgressDAO.isLevelCompleted(user.getId(), i)) {
                completedLevels.add(i);
            }
        }

        // Determine next playable level
        int nextLevel = 1;
        for (int i = 1; i <= 12; i++) {
            if (!completedLevels.contains(i)) {
                nextLevel = i;
                break;
            }
        }

        // Build level tiles
        for (int i = 1; i <= 12; i++) {
            boolean completed = completedLevels.contains(i);
            boolean playable = (i == nextLevel);
            boolean locked = (!completed && !playable);

            String levelText = "Level " + String.format("%02d", i);

            // Trophy icon
            ImageView trophyIcon = homeUI.createIconView("/images/level.png", 50, 50);
            trophyIcon.setScaleX(1.6);
            trophyIcon.setScaleY(1.6);

            // Star icon → only show if completed
            HBox starsBox = new HBox(0);
            if (completed) {
                ImageView starIcon = homeUI.createIconView("/images/star.png", 50, 40);
                starIcon.setScaleX(2.5);
                starIcon.setScaleY(2.5);
                starsBox = homeUI.createStarsBox(starIcon);
            }

            // Bottom node
            Node bottom;
            if (completed) {
                bottom = new javafx.scene.control.Label("Completed");
                bottom.getStyleClass().add("level-completed-banner");
            } else if (playable) {
                Button playButton = new Button("Let's Play!");
                playButton.getStyleClass().add("level-play-button");
                HomeLogic.setupPlayButton(playButton, user, i, stage);
                bottom = playButton;
            } else { // locked
                ImageView lockIcon = homeUI.createIconView("/images/lock.png", 40, 40);
                lockIcon.setScaleX(2.5); // scale X
                lockIcon.setScaleY(2.5); // scale Y
                bottom = lockIcon;
            }

            // Create level tile
            levelTiles[i - 1] = homeUI.createLevelTile(levelText, bottom, trophyIcon, starsBox);
        }

        // Scrollable level area
        Node levelSelectArea = homeUI.createLevelSelectArea((Node[]) levelTiles);

        // Build root BorderPane
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.getStyleClass().add("game-root");

        // Top bar
        TopBarController topBar = new TopBarController(user, stage);
        root.setTop(topBar.getView());

        // Center
        root.setCenter(levelSelectArea);

        // Scene
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HomeController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        return scene;
    }
}
