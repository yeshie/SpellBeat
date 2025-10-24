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

        // Pass user and stage to the level selection area for button actions
        root.setCenter(createLevelSelectArea(user, stage));
        root.setTop(createTopBar(user, level, hearts));

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(HomeController.class.getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        return scene;
    }

    private static Node createTopBar(User user, int level, int hearts) {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("home-top-bar");
        topBar.setAlignment(Pos.CENTER);

        // ===== Left: Avatar + Home =====
        HBox avatarChip = createInfoChip(user.getAvatarPath(), user.getName(), true);
        Button homeButton = createIconButton("/images/home.png"); // same height as chips
        // NOTE: Add action for homeButton if needed (e.g., return to main menu)

        HBox leftBox = new HBox(12, avatarChip, homeButton);
        leftBox.getStyleClass().add("top-left-section");
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // ===== Center: Logo Image =====
        ImageView logo = createIconView("/images/logo.png", 220, 180);
        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.getStyleClass().add("top-logo-box");

        // ===== Right: Hearts + Level =====
        HBox heartsChip = createInfoChip("/images/heartLogo.png", String.valueOf(hearts), false);
        HBox levelChip = createInfoChip("/images/level.png", "Level " + level, true);

        HBox rightBox = new HBox(12, heartsChip, levelChip);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        // ===== Spacers =====
        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        topBar.getChildren().addAll(leftBox, spacerLeft, logoBox, spacerRight, rightBox);
        return topBar;
    }

    private static HBox createInfoChip(String iconPath, String text, boolean wide) {
        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER);
        chip.getStyleClass().add("home-chip");
        if (wide) chip.getStyleClass().add("wide");

        ImageView icon = createIconView(iconPath, 34, 34);
        Label label = new Label(text);
        label.getStyleClass().add("home-chip-label");

        chip.getChildren().addAll(icon, label);
        return chip;
    }

    private static Button createIconButton(String iconPath) {
        ImageView icon = createIconView(iconPath,34, 34);
        Button btn = new Button("", icon);
        btn.getStyleClass().add("home-button");
        return btn;
    }

    // Updated to accept User and Stage
    private static Node createLevelSelectArea(User user, Stage stage) {
        // Transparent background wrapper
        VBox backgroundCard = new VBox();
        backgroundCard.getStyleClass().add("level-tile-background");
        backgroundCard.setAlignment(Pos.CENTER);

        HBox levelContainer = new HBox(6);
        levelContainer.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 12; i++) {
            boolean completed = i <= 3;
            boolean playable = i == 4;
            // Pass user and stage to the tile creator
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

    // Updated to accept User and Stage
    private static VBox createLevelTile(User user, Stage stage, int levelNum, boolean completed, boolean playable) {
    	
	    VBox tile = new VBox(12); // VBox spacing 
	    tile.setAlignment(Pos.CENTER);
	    tile.getStyleClass().add("level-tile");

	    Label title = new Label("Level " + String.format("%02d", levelNum));
	    title.getStyleClass().add("level-title");

	    // FIX: Using 50x50 to allow CSS scaling
	    ImageView trophyIcon = createIconView("/images/level.png", 50, 50);
	    trophyIcon.getStyleClass().add("level-icon");

	    HBox starsBox = new HBox(6);
	    starsBox.setAlignment(Pos.CENTER);

	    // FIX: Using 50x40 to allow CSS scaling
	    ImageView star = createIconView("/images/star.png", 50, 40);
	    star.getStyleClass().add("level-star"); 
	    starsBox.getChildren().add(star);
    
    Node bottom;
    if (completed) {
        Label completedLabel = new Label("Completed");
        completedLabel.getStyleClass().add("level-completed-banner");
        bottom = completedLabel;
    } else if (playable) {
        Button playButton = new Button("Let's Play!");
        playButton.getStyleClass().add("level-play-button");
        
        // --- NAVIGATION LOGIC ADDED HERE ---
        playButton.setOnAction(e -> {
            // Switch scene to the GameController view for the selected level
            // user and stage are correctly in scope as method parameters
            Scene gameScene = GameController.createScene(user, levelNum);
            stage.setScene(gameScene);
        });
        // -----------------------------------
        
        bottom = playButton;
    } else {
        bottom = new Region();
        ((Region) bottom).setMinHeight(20);
    }

    tile.getChildren().addAll(title, trophyIcon, starsBox, bottom);
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
