package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.HomeLogic;
import com.wordheartschallenge.app.ui.HomeUI;
import com.wordheartschallenge.app.database.UserProgressDAO;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.geometry.Pos;
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

        // Build uniform level tiles
        for (int i = 1; i <= 12; i++) {
            boolean completed = completedLevels.contains(i);
            boolean playable = (i == nextLevel);
            boolean locked = (!completed && !playable);
            
            String levelText = "Level " + String.format("%02d", i);

            // Create uniform components with fixed dimensions
            Label titleLabel = createTitleLabel(levelText);
            StackPane iconContainer = createIconContainer(homeUI);
            StackPane starsContainer = createStarsContainer(homeUI, completed);
            StackPane actionContainer = createActionContainer(homeUI, user, i, stage, completed, playable, locked);

            // Create uniform tile structure
            levelTiles[i - 1] = createUniformTile(titleLabel, iconContainer, starsContainer, actionContainer);
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

    // Create uniform tile with fixed spacers for perfect alignment
    private static VBox createUniformTile(Label titleLabel, StackPane iconContainer, 
                                          StackPane starsContainer, StackPane actionContainer) {
        VBox tile = new VBox();
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("level-tile");
        
        // Fixed dimensions for uniform tiles
        tile.setPrefSize(180, 300);
        tile.setMinSize(180, 300);
        tile.setMaxSize(180, 300);

        // Spacer 1: Top padding (title to top)
        Region spacer1 = new Region();
        spacer1.setPrefHeight(15);
        spacer1.setMinHeight(15);
        spacer1.setMaxHeight(15);

        // Spacer 2: Title to Icon
        Region spacer2 = new Region();
        spacer2.setPrefHeight(20);
        spacer2.setMinHeight(20);
        spacer2.setMaxHeight(20);

        // Spacer 3: Icon to Stars
        Region spacer3 = new Region();
        spacer3.setPrefHeight(15);
        spacer3.setMinHeight(15);
        spacer3.setMaxHeight(15);

        // Spacer 4: Stars to Action (move completed banner more upward)
        // Tile height: 300
        // Used so far: 15 (sp1) + 30 (title) + 20 (sp2) + 90 (icon) + 15 (sp3) + 50 (stars) = 220
        // Remaining: 300 - 220 - 38 (action) = 42
        // For completed: 5 above, 37 below (moves banner significantly up)
        Region spacer4 = new Region();
        spacer4.setPrefHeight(5);
        spacer4.setMinHeight(5);
        spacer4.setMaxHeight(5);

        // Spacer 5: Action to bottom
        Region spacer5 = new Region();
        spacer5.setPrefHeight(37);
        spacer5.setMinHeight(37);
        spacer5.setMaxHeight(37);

        // Add components with fixed spacing
        tile.getChildren().addAll(
            spacer1,
            titleLabel,
            spacer2,
            iconContainer,
            spacer3,
            starsContainer,
            spacer4,
            actionContainer,
            spacer5
        );

        return tile;
    }

    // Create uniform title label with fixed dimensions
    private static Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("level-title");
        label.setAlignment(Pos.CENTER);
        label.setPrefHeight(30);
        label.setMinHeight(30);
        label.setMaxHeight(30);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    // Create uniform icon container with fixed dimensions
    private static StackPane createIconContainer(HomeUI homeUI) {
        ImageView trophyIcon = homeUI.createIconView("/images/level.png", 130, 130);
        trophyIcon.setSmooth(true);
        trophyIcon.setPreserveRatio(true);
        
        StackPane container = new StackPane(trophyIcon);
        container.setPrefSize(90, 90);
        container.setMinSize(90, 90);
        container.setMaxSize(90, 90);
        container.setAlignment(Pos.CENTER);
        
        return container;
    }

    // Create uniform stars container with fixed dimensions
    private static StackPane createStarsContainer(HomeUI homeUI, boolean completed) {
        StackPane container = new StackPane();
        container.setPrefSize(60, 50);
        container.setMinSize(60, 50);
        container.setMaxSize(60, 50);
        container.setAlignment(Pos.CENTER);

        if (completed) {
            // Show completed star
            ImageView starIcon = homeUI.createIconView("/images/star.png", 100, 100);
            starIcon.setSmooth(true);
            starIcon.setPreserveRatio(true);
            container.getChildren().add(starIcon);
        } else {
            // Show loading star for playable and locked levels
            ImageView loadingStarIcon = homeUI.createIconView("/images/loadingStar.png", 100, 100);
            loadingStarIcon.setSmooth(true);
            loadingStarIcon.setPreserveRatio(true);
            container.getChildren().add(loadingStarIcon);
        }

        return container;
    }

    // Create uniform action container with fixed dimensions and centered content
    private static StackPane createActionContainer(HomeUI homeUI, User user, int level, 
                                                    Stage stage, boolean completed, 
                                                    boolean playable, boolean locked) {
        StackPane container = new StackPane();
        container.setPrefSize(130, 38);
        container.setMinSize(130, 38);
        container.setMaxSize(130, 38);
        container.setAlignment(Pos.CENTER);

        Node content;
        
        if (completed) {
            // Banner image with centered "Completed" text overlay
            ImageView bannerImage = homeUI.createIconView("/images/banner.png", 130, 38);
            bannerImage.setSmooth(true);
            bannerImage.setPreserveRatio(false); // Stretch to fit exact dimensions
            
            Label completedLabel = new Label("Completed");
            completedLabel.getStyleClass().add("level-completed-text");
            completedLabel.setAlignment(Pos.CENTER);
            completedLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            completedLabel.setTranslateY(-8);
            
            // Stack text on top of banner image
            StackPane bannerWithText = new StackPane();
            bannerWithText.setPrefSize(130, 38);
            bannerWithText.setMinSize(130, 38);
            bannerWithText.setMaxSize(130, 38);
            bannerWithText.setAlignment(Pos.CENTER);
            bannerWithText.getChildren().addAll(bannerImage, completedLabel);
            
            content = bannerWithText;
            
        } else if (playable) {
            // Play button centered in action area
            Button playButton = new Button("Let's Play!");
            playButton.getStyleClass().add("level-play-button");
            playButton.setAlignment(Pos.CENTER);
            playButton.setPrefSize(130, 38);
            playButton.setMinSize(130, 38);
            playButton.setMaxSize(130, 38);
            HomeLogic.setupPlayButton(playButton, user, level, stage);
            content = playButton;
            
        } else { // locked
            // Lock icon centered in action area (same space as button would occupy)
            ImageView lockIcon = homeUI.createIconView("/images/lock.png", 100, 100);
            lockIcon.setSmooth(true);
            lockIcon.setPreserveRatio(true);
            // Lock icon inherits container's center alignment
            content = lockIcon;
        }

        container.getChildren().add(content);
        return container;
    }
}