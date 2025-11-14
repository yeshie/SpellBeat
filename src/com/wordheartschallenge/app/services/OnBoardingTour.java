package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.database.UserDAO;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OnBoardingTour {

    private User user;
    private int currentStep = 0;
    private Pane overlayPane;
    private VBox tourBox;
    private Polygon arrow;
    private Scene mainScene;
    
    // Tour steps data
    private static final TourStep[] TOUR_STEPS = {
        new TourStep("Welcome Aboard!", "Let's take a quick tour of WordHearts Challenge!\nReady to begin your journey?", null, "CENTER"),
        new TourStep("Your Profile", "Tap your avatar to open Profile & Settings.\nUpdate your name, avatar, and more!", "avatar-chip", "BELOW"),
        new TourStep("Hearts Counter", "These are your Hearts - your life strength!\nThey show how many tries you have left.", "hearts-chip", "BELOW"),
        new TourStep("Home Button ", "This Home button brings you back here anytime.\nYour safe space in the game!", "home-button", "BELOW"),
        new TourStep("Game Selection ", "Choose your game mode from these tiles.\nWord Game or Heart Hint Game - it's up to you!", "game-tiles", "ABOVE"),
        new TourStep("All Set! ", "You're ready to play!\nEnjoy your adventure and have fun!", null, "CENTER")
    };

    static class TourStep {
        String title, message, targetId, position;
        TourStep(String title, String message, String targetId, String position) {
            this.title = title;
            this.message = message;
            this.targetId = targetId;
            this.position = position;
        }
    }

    public OnBoardingTour(User user, Scene mainScene) {
        this.user = user;
        this.mainScene = mainScene;
    }

    public void start() {
        currentStep = 0;
        createOverlay();
        showTourStep();
    }

    public static void checkAndStart(User user, Scene mainScene) {
        if (!user.isTutorialCompleted()) {
            // Delay to ensure scene is fully rendered
            javafx.application.Platform.runLater(() -> {
                OnBoardingTour tour = new OnBoardingTour(user, mainScene);
                tour.start();
            });
        }
    }

    private void createOverlay() {
        // Get the root of the main scene
        Pane root = (Pane) mainScene.getRoot();
        
        // Create overlay pane
        overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlayPane.setPrefSize(1000, 650);
        overlayPane.setMaxSize(1000, 650);
        
        // Add overlay to the root
        root.getChildren().add(overlayPane);
    }

    private void showTourStep() {
        if (currentStep >= TOUR_STEPS.length) {
            completeTour();
            return;
        }

        overlayPane.getChildren().clear();

        TourStep step = TOUR_STEPS[currentStep];

        // Create tour box
        tourBox = new VBox(15);
        tourBox.setAlignment(Pos.CENTER);
        tourBox.getStyleClass().add("tour-popup-box");
        tourBox.setPrefWidth(380);
        tourBox.setMaxWidth(380);

        // Logo at top
        try {
            ImageView logo = new ImageView(new Image(
                OnBoardingTour.class.getResourceAsStream("/images/logo.png")
            ));
            logo.setFitWidth(100);
            logo.setPreserveRatio(true);
            tourBox.getChildren().add(logo);
        } catch (Exception e) {
            System.err.println("Logo not found for tour");
        }

        // Title
        Label titleLabel = new Label(step.title);
        titleLabel.getStyleClass().add("tour-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(340);

        // Message
        Label messageLabel = new Label(step.message);
        messageLabel.getStyleClass().add("tour-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);

        // Progress indicator
        Label progressLabel = new Label("Step " + (currentStep + 1) + " of " + TOUR_STEPS.length);
        progressLabel.getStyleClass().add("tour-progress");

        tourBox.getChildren().addAll(titleLabel, messageLabel, progressLabel);

        // Buttons
        HBox buttonsBox = new HBox(12);
        buttonsBox.setAlignment(Pos.CENTER);

        if (currentStep < TOUR_STEPS.length - 1) {
            Button skipButton = new Button("Skip Tour");
            skipButton.getStyleClass().add("tour-skip-button");
            skipButton.setOnAction(e -> skipTour());
            buttonsBox.getChildren().add(skipButton);
        }

        Button nextButton = new Button(currentStep == TOUR_STEPS.length - 1 ? "Finish! 🎉" : "Next →");
        nextButton.getStyleClass().add("tour-next-button");
        nextButton.setOnAction(e -> nextStep());
        buttonsBox.getChildren().add(nextButton);

        tourBox.getChildren().add(buttonsBox);

        // Position tourBox based on target element
        if (step.targetId != null) {
            positionNearElement(step.targetId, step.position);
        } else {
            // Center position
            tourBox.setLayoutX((1000 - 380) / 2);
            tourBox.setLayoutY((650 - 400) / 2);
            overlayPane.getChildren().add(tourBox);
        }

        animateEntrance(tourBox);
    }

    private void positionNearElement(String targetId, String position) {
        // Find target element
        Node targetNode = findNodeById(mainScene.getRoot(), targetId);
        
        if (targetNode == null) {
            System.err.println("Target element not found: " + targetId);
            // Fallback to center
            tourBox.setLayoutX((1000 - 380) / 2);
            tourBox.setLayoutY((650 - 400) / 2);
            overlayPane.getChildren().add(tourBox);
            return;
        }

        // Get bounds of target element relative to scene
        Bounds targetBounds = targetNode.localToScene(targetNode.getBoundsInLocal());
        
        System.out.println("Target: " + targetId + " at X=" + targetBounds.getMinX() + ", Y=" + targetBounds.getMinY());

        double tourX = 0;
        double tourY = 0;
        
        // Create arrow
        arrow = createArrow(position);

        switch (position) {
            case "ABOVE":
                // Position popup above the element
                tourX = targetBounds.getMinX() + (targetBounds.getWidth() / 2) - 190;
                tourY = targetBounds.getMinY() - 380; // tourBox height + spacing
                
                // Arrow points down, positioned at bottom of tourBox
                arrow.setLayoutX(190); // Center of box
                arrow.setLayoutY(tourBox.getPrefHeight() - 20);
                tourBox.getChildren().add(arrow);
                break;
                
            case "BELOW":
                // Position popup below the element
                tourX = targetBounds.getMinX() + (targetBounds.getWidth() / 2) - 190;
                tourY = targetBounds.getMaxY() + 30;
                
                // Arrow points up, positioned at top
                arrow.setLayoutX(190);
                arrow.setLayoutY(-15);
                tourBox.getChildren().add(0, arrow); // Add at start
                break;
                
            case "LEFT":
                // Position popup to the left
                tourX = targetBounds.getMinX() - 400;
                tourY = targetBounds.getMinY() + (targetBounds.getHeight() / 2) - 150;
                
                // Arrow points right
                arrow.setLayoutX(tourBox.getPrefWidth() - 10);
                arrow.setLayoutY(150);
                tourBox.getChildren().add(arrow);
                break;
                
            case "RIGHT":
                // Position popup to the right
                tourX = targetBounds.getMaxX() + 20;
                tourY = targetBounds.getMinY() + (targetBounds.getHeight() / 2) - 150;
                
                // Arrow points left
                arrow.setLayoutX(-15);
                arrow.setLayoutY(150);
                tourBox.getChildren().add(0, arrow);
                break;
        }

        // Make sure popup stays within screen bounds
        tourX = Math.max(10, Math.min(tourX, 1000 - 390));
        tourY = Math.max(10, Math.min(tourY, 650 - 400));

        tourBox.setLayoutX(tourX);
        tourBox.setLayoutY(tourY);
        
        overlayPane.getChildren().add(tourBox);
        
        System.out.println("Popup positioned at X=" + tourX + ", Y=" + tourY);
    }

    private Node findNodeById(Node root, String id) {
        if (root.getId() != null && root.getId().equals(id)) {
            return root;
        }
        if (root instanceof Parent) {
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                Node result = findNodeById(child, id);
                if (result != null) return result;
            }
        }
        return null;
    }

    private Polygon createArrow(String direction) {
        Polygon arrow = new Polygon();
        arrow.setFill(Color.web("#FFE5F0"));
        arrow.setStroke(Color.web("#FFB6C1"));
        arrow.setStrokeWidth(2);

        switch (direction) {
            case "ABOVE":
                // Arrow pointing down (at bottom of box)
                arrow.getPoints().addAll(
                    0.0, 0.0,
                    -15.0, -20.0,
                    15.0, -20.0
                );
                break;
            case "BELOW":
                // Arrow pointing up (at top of box)
                arrow.getPoints().addAll(
                    0.0, 0.0,
                    -15.0, 20.0,
                    15.0, 20.0
                );
                break;
            case "LEFT":
                // Arrow pointing right
                arrow.getPoints().addAll(
                    0.0, 0.0,
                    -20.0, -15.0,
                    -20.0, 15.0
                );
                break;
            case "RIGHT":
                // Arrow pointing left
                arrow.getPoints().addAll(
                    0.0, 0.0,
                    20.0, -15.0,
                    20.0, 15.0
                );
                break;
        }

        return arrow;
    }

    private void nextStep() {
        animateExit(() -> {
            currentStep++;
            showTourStep();
        });
    }

    private void skipTour() {
        animateExit(() -> {
            user.setTutorialCompleted(true);
            UserDAO.updateUserTutorial(user);
            removeOverlay();
        });
    }

    private void completeTour() {
        user.setTutorialCompleted(true);
        UserDAO.updateUserTutorial(user);
        removeOverlay();
    }

    private void removeOverlay() {
        if (overlayPane != null) {
            Pane root = (Pane) mainScene.getRoot();
            root.getChildren().remove(overlayPane);
        }
    }

    private void animateEntrance(VBox box) {
        box.setScaleX(0.7);
        box.setScaleY(0.7);
        box.setOpacity(0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), box);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), box);
        fadeIn.setToValue(1);

        ParallelTransition animation = new ParallelTransition(scaleIn, fadeIn);
        animation.play();
    }

    private void animateExit(Runnable onComplete) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), tourBox);
        fadeOut.setToValue(0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), tourBox);
        scaleOut.setToX(0.7);
        scaleOut.setToY(0.7);

        ParallelTransition animation = new ParallelTransition(fadeOut, scaleOut);
        animation.setOnFinished(e -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
        animation.play();
    }

    public static void restartTour(User user, Scene mainScene) {
        OnBoardingTour tour = new OnBoardingTour(user, mainScene);
        tour.start();
    }
}