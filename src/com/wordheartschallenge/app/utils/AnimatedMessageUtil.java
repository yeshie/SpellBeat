package com.wordheartschallenge.app.utils;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * ✅ ENHANCED: Animated message utility for heart deductions
 * Shows beautiful animated popups matching the game's aesthetic
 */
public class AnimatedMessageUtil {

    /**
     * Show animated message with fade-in → move up → fade-out
     * @param scene The current game scene
     * @param message Text to display (e.g., "-2")
     * @param iconPath Path to icon (e.g., "/images/heartLogo.png")
     */
    public static void showAnimatedMessage(Scene scene, String message, String iconPath) {
        // Get root pane - handle both Pane and StackPane
        Pane root;
        if (scene.getRoot() instanceof Pane) {
            root = (Pane) scene.getRoot();
        } else if (scene.getRoot() instanceof StackPane) {
            root = (StackPane) scene.getRoot();
        } else {
            System.err.println("Root is not a Pane or StackPane, cannot show animation");
            return;
        }

        // ✅ Create message box with custom styling
        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.getStyleClass().add("animated-hint-message");
        
        // Center the message box on screen
        double centerX = (scene.getWidth() / 2) - 75; // Approximate center
        double centerY = (scene.getHeight() / 2) - 50; // Slightly above center
        
        messageBox.setLayoutX(centerX);
        messageBox.setLayoutY(centerY);

        // ✅ Add heart icon
        try {
            ImageView icon = new ImageView(new Image(
                AnimatedMessageUtil.class.getResourceAsStream(iconPath)
            ));
            icon.setFitWidth(32);
            icon.setFitHeight(32);
            icon.setPreserveRatio(true);
            messageBox.getChildren().add(icon);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load icon: " + iconPath);
            // Continue without icon
        }

        // ✅ Create text label
        Label label = new Label(message);
        label.getStyleClass().add("animated-hint-text");
        messageBox.getChildren().add(label);

        // Add to root (on top of everything)
        root.getChildren().add(messageBox);

        // ✅ ANIMATION SEQUENCE
        messageBox.setOpacity(0);

        // 1. Fade In (300ms)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), messageBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // 2. Pause (500ms) - Let user see the message
        PauseTransition pause = new PauseTransition(Duration.millis(500));

        // 3. Move Upward + Fade Out (together, 800ms)
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(800), messageBox);
        moveUp.setByY(-100); // Move 100px upward

        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), messageBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ParallelTransition moveAndFade = new ParallelTransition(moveUp, fadeOut);

        // ✅ Full sequence: fade in → pause → move up while fading out
        SequentialTransition sequence = new SequentialTransition(
            fadeIn,
            pause,
            moveAndFade
        );

        // Remove from scene when done
        sequence.setOnFinished(e -> root.getChildren().remove(messageBox));

        // Play animation
        sequence.play();
    }

    /**
     * ✅ Show heart deduction message
     * @param scene Current game scene
     * @param heartsLost Number of hearts lost (1 or 2)
     */
    public static void showHeartDeduction(Scene scene, int heartsLost) {
        String message = "-" + heartsLost + " ❤️";
        showAnimatedMessage(scene, message, "/images/heartLogo.png");
    }

    /**
     * ✅ Show custom animated message (for other game events)
     * @param scene Current scene
     * @param message Custom message text
     */
    public static void showCustomMessage(Scene scene, String message) {
        showAnimatedMessage(scene, message, "/images/heartLogo.png");
    }
}