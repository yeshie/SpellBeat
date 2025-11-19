package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.HomeLogic;
import com.wordheartschallenge.app.ui.HomeUI;
import com.wordheartschallenge.app.database.UserProgressDAO;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
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

        Set<Integer> completedLevels = new HashSet<>();
        for (int i = 1; i <= 12; i++) {
            if (UserProgressDAO.isLevelCompleted(user.getId(), i)) {
                completedLevels.add(i);
            }
        }

        int nextLevel = 1;
        for (int i = 1; i <= 12; i++) {
            if (!completedLevels.contains(i)) {
                nextLevel = i;
                break;
            }
        }

        for (int i = 1; i <= 12; i++) {
            boolean completed = completedLevels.contains(i);
            boolean playable = (i == nextLevel);
            boolean locked = (!completed && !playable);
            
            String levelText = "Level " + String.format("%02d", i);

            Label titleLabel = createTitleLabel(levelText);
            StackPane iconContainer = createIconContainer(homeUI);
            StackPane starsContainer = createStarsContainer(homeUI, completed);
            StackPane actionContainer = createActionContainer(homeUI, user, i, stage, completed, playable, locked);

            levelTiles[i - 1] = createUniformTile(titleLabel, iconContainer, starsContainer, actionContainer);
            levelTiles[i - 1].setId("level-tile-" + i);
            if (playable) {
            	 levelTiles[i - 1].setStyle(
            		        "-fx-effect: dropshadow(three-pass-box, rgba(255,139,186,0.9), 28, 0.3, 0, 0);"
            		    );
                applyGlowAndPulse(levelTiles[i - 1]);   // ⭐ Glow & Pulse Here
            }
        }

        Node levelSelectArea = homeUI.createLevelSelectArea((Node[]) levelTiles);

        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.getStyleClass().add("game-root");

        TopBarController topBar = new TopBarController(user, stage);
        root.setTop(topBar.getView());

        root.setCenter(levelSelectArea);
     // AUTO SCROLL to current level after scene is fully rendered
        final int scrollToLevel = nextLevel;
        Platform.runLater(() -> scrollToLevel(levelSelectArea, scrollToLevel));

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HomeController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        return scene;
    }
    private static void scrollToLevel(Node levelSelectArea, int levelNumber) {
        ScrollPane scrollPane = findScrollPane(levelSelectArea);

        if (scrollPane != null) {
            Node tile = scrollPane.getContent().lookup("#level-tile-" + levelNumber);

            if (tile != null) {
                double tileX = tile.getLayoutX();
                double containerWidth = scrollPane.getContent().getBoundsInLocal().getWidth();
                double viewportWidth = scrollPane.getViewportBounds().getWidth();

                double scrollPos = (tileX - (viewportWidth / 2) + (tile.getBoundsInLocal().getWidth() / 2))
                                   / (containerWidth - viewportWidth);
                scrollPos = Math.max(0, Math.min(1, scrollPos));

                scrollPane.setHvalue(scrollPos);
            }
        }
    }
    private static ScrollPane findScrollPane(Node node) {
        if (node instanceof ScrollPane) return (ScrollPane) node;

        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                ScrollPane result = findScrollPane(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static VBox createUniformTile(Label titleLabel, StackPane iconContainer, 
                                          StackPane starsContainer, StackPane actionContainer) {
        VBox tile = new VBox();
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("level-tile");
        tile.setPrefSize(180, 300);
        tile.setMinSize(180, 300);
        tile.setMaxSize(180, 300);

        // Spacer 1: Top padding 
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

        // Spacer 4: Stars to Action 
        Region spacer4 = new Region();
        spacer4.setPrefHeight(5);
        spacer4.setMinHeight(5);
        spacer4.setMaxHeight(5);

        // Spacer 5: Action to bottom
        Region spacer5 = new Region();
        spacer5.setPrefHeight(37);
        spacer5.setMinHeight(37);
        spacer5.setMaxHeight(37);

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

    private static StackPane createStarsContainer(HomeUI homeUI, boolean completed) {
        StackPane container = new StackPane();
        container.setPrefSize(60, 50);
        container.setMinSize(60, 50);
        container.setMaxSize(60, 50);
        container.setAlignment(Pos.CENTER);

        if (completed) {
            ImageView starIcon = homeUI.createIconView("/images/star.png", 100, 100);
            starIcon.setSmooth(true);
            starIcon.setPreserveRatio(true);
            container.getChildren().add(starIcon);
        } else {
            ImageView loadingStarIcon = homeUI.createIconView("/images/loadingStar.png", 100, 100);
            loadingStarIcon.setSmooth(true);
            loadingStarIcon.setPreserveRatio(true);
            container.getChildren().add(loadingStarIcon);
        }

        return container;
    }

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
            ImageView bannerImage = homeUI.createIconView("/images/banner.png", 130, 38);
            bannerImage.setSmooth(true);
            bannerImage.setPreserveRatio(false); 
            
            Label completedLabel = new Label("Completed");
            completedLabel.getStyleClass().add("level-completed-text");
            completedLabel.setAlignment(Pos.CENTER);
            completedLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            completedLabel.setTranslateY(-8);
            
           
            StackPane bannerWithText = new StackPane();
            bannerWithText.setPrefSize(130, 38);
            bannerWithText.setMinSize(130, 38);
            bannerWithText.setMaxSize(130, 38);
            bannerWithText.setAlignment(Pos.CENTER);
            bannerWithText.getChildren().addAll(bannerImage, completedLabel);
            
            content = bannerWithText;
            
        } else if (playable) {
        	
            Button playButton = new Button("Let's Play!");
            playButton.getStyleClass().add("level-play-button");
            playButton.setAlignment(Pos.CENTER);
            playButton.setPrefSize(130, 38);
            playButton.setMinSize(130, 38);
            playButton.setMaxSize(130, 38);
            HomeLogic.setupPlayButton(playButton, user, level, stage);
            content = playButton;
            
        } else { 
            ImageView lockIcon = homeUI.createIconView("/images/lock.png", 100, 100);
            lockIcon.setSmooth(true);
            lockIcon.setPreserveRatio(true);
            content = lockIcon;
        }

        container.getChildren().add(content);
        return container;
    }
    private static void applyGlowAndPulse(VBox tile) {
        // Glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(javafx.scene.paint.Color.web("#FF8BBA")); // pinkish glow
        glow.setRadius(35);
        glow.setSpread(0.3);
        tile.setEffect(glow);

        // Pulse animation
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.3), tile);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.06);
        pulse.setToY(1.06);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

}