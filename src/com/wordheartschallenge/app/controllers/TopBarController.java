package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.AuthService;
import com.wordheartschallenge.app.controllers.ProfileController;

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

public class TopBarController {

    private final User user;
    private final Stage stage;
    private final Label heartsLabel;
    private final Label levelLabel;
    private ImageView avatarIcon; // Direct reference to the live avatar ImageView

    public TopBarController(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
        this.heartsLabel = new Label(String.valueOf(user.getHearts()));
        this.levelLabel = new Label("Level " + user.getCurrentLevel());
    }

 // Add these ID setters to your TopBarController getView() method

    public Node getView() {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("home-top-bar-hl");
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(10);

        // LEFT: Avatar + Home button
        HBox avatarChip = createInfoChip(ensureLeadingSlash(user.getAvatarPath()), user.getName(), true);
        avatarChip.setId("avatar-chip"); // ✅ Add ID for tour
        avatarChip.setOnMouseClicked(e -> {
            Scene profileScene = ProfileController.createScene(user, stage, this);
            stage.setScene(profileScene);
        });
        avatarIcon = (ImageView) avatarChip.getChildren().get(0);

        Button homeButton = createIconButton("/images/home.png");
        homeButton.setId("home-button"); // ✅ Add ID for tour
        homeButton.setOnAction(e -> {
            Scene homelandScene = HomeLandController.createScene(user, stage);
            stage.setScene(homelandScene);
        });

        HBox leftBox = new HBox(12, avatarChip, homeButton);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // CENTER: App logo
        ImageView logo = createIconView("/images/logo.png", 160, 80);
        logo.setId("logoIcon");
        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);

        // RIGHT: Hearts, Level, Logout
        HBox heartsChip = createInfoChip("/images/heartLogo.png", heartsLabel.getText(), false);
        heartsChip.setId("hearts-chip"); // ✅ Add ID for tour
        ((ImageView) heartsChip.getChildren().get(0)).setId("heartIcon");
        heartsChip.getChildren().set(1, heartsLabel);

        HBox levelChip = createInfoChip("/images/level.png", levelLabel.getText(), false);
        levelChip.setId("level-chip"); // ✅ Add ID for tour
        ((ImageView) levelChip.getChildren().get(0)).setId("levelIcon");
        levelChip.getChildren().set(1, levelLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button-hl");
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

    public void updateHearts(int newHearts) {
        user.setHearts(newHearts);
        heartsLabel.setText(String.valueOf(newHearts));
        AuthService.updateHeartPoints(user.getId(), newHearts);
    }

    public void updateLevel(int newLevel) {
        user.setCurrentLevel(newLevel);
        levelLabel.setText("Level " + newLevel);
        AuthService.updateCurrentLevel(user.getId(), newLevel);
    }
    
    public void updateAvatar(String newAvatarPath) {
        // Ensure the path starts with / but don't add it if it's already there
        String correctedPath = newAvatarPath.startsWith("/") ? newAvatarPath : "/" + newAvatarPath;
        user.setAvatarPath(correctedPath);
        System.out.println("DEBUG: Updating avatar to: " + correctedPath); // DEBUG
        if (avatarIcon != null) {
            try (InputStream stream = TopBarController.class.getResourceAsStream(correctedPath)) {  // ✅ Use correctedPath directly (no extra /)
                if (stream != null) {
                    Image newImage = new Image(stream);
                    avatarIcon.setImage(newImage);
                    System.out.println("DEBUG: Avatar image updated successfully."); // DEBUG
                } else {
                    System.err.println("ERROR: Avatar image not found: " + correctedPath);
                    setDefaultAvatar();
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to load avatar: " + e.getMessage());
                setDefaultAvatar();
            }
        } else {
            System.err.println("ERROR: avatarIcon is null!");
        }
    }

    private void setDefaultAvatar() {
        try (InputStream stream = TopBarController.class.getResourceAsStream("/images/avatar1.png")) {
            if (stream != null && avatarIcon != null) {
                avatarIcon.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.err.println("ERROR: Default avatar not found.");
        }
    }
    private static String ensureLeadingSlash(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }
    // Helper: info chip
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

    // Helper: icon button
    private static Button createIconButton(String iconPath) {
        ImageView icon = createIconView(iconPath, 32, 32);
        Button btn = new Button("", icon);
        btn.getStyleClass().add("home-button-hl");
        return btn;
    }

    // Helper: load icons safely
    private static ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = TopBarController.class.getResourceAsStream(path)) {
            if (stream != null) {
                icon.setImage(new Image(stream));
                System.out.println("DEBUG: Loaded icon: " + path); // DEBUG
            } else {
                System.err.println("ERROR: Icon not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load icon: " + path + " - " + e.getMessage());
        }
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
