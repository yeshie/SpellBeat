package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.Random;

public class WinningController {

    public static Scene createScene(User user, int currentLevel, int hearts, Stage stage) {
        StackPane root = new StackPane();
        root.getStyleClass().add("winning-root");

        Pane animationLayer = createAnimationLayer();
        
        VBox contentCard = createContentCard(user, currentLevel, hearts, stage);

        root.getChildren().addAll(animationLayer, contentCard);

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(WinningController.class.getResource("/css/style.css").toExternalForm());
        
        // Start animations
        startFireworksAnimation(animationLayer);
        startCardAnimation(contentCard);
        
        stage.setScene(scene);
        stage.show();

        return scene;
    }

    private static Pane createAnimationLayer() {
        Pane layer = new Pane();
        layer.setMouseTransparent(true);
        
        ImageView leftBlast = loadImage("/images/ink.png", 400, 400);
        ImageView rightBlast = loadImage("/images/ink.png", 400, 400);
        
        if (leftBlast != null) {
            leftBlast.setLayoutX(-50);
            leftBlast.setLayoutY(100);
            leftBlast.setOpacity(0);
            layer.getChildren().add(leftBlast);
            animateInkBlast(leftBlast, true);
        }
        
        if (rightBlast != null) {
            rightBlast.setLayoutX(650);
            rightBlast.setLayoutY(100);
            rightBlast.setOpacity(0);
            layer.getChildren().add(rightBlast);
            animateInkBlast(rightBlast, false);
        }
        
        return layer;
    }

    private static void animateInkBlast(ImageView blast, boolean isLeft) {
        FadeTransition fade = new FadeTransition(Duration.millis(800), blast);
        fade.setFromValue(0);
        fade.setToValue(0.7);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(800), blast);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.2);
        scale.setToY(1.2);
        
        RotateTransition rotate = new RotateTransition(Duration.millis(800), blast);
        rotate.setByAngle(isLeft ? -15 : 15);
        
        ParallelTransition parallel = new ParallelTransition(fade, scale, rotate);
        parallel.setDelay(Duration.millis(200));
        parallel.play();
    }

    private static VBox createContentCard(User user, int currentLevel, int hearts, Stage stage) {
        VBox mainWrapper = new VBox(0);
        mainWrapper.setAlignment(Pos.CENTER);

        StackPane layeredStack = new StackPane();
        layeredStack.setAlignment(Pos.CENTER);
        layeredStack.setPrefWidth(650);
        layeredStack.setPrefHeight(420);

        VBox pinkCard = new VBox(15);
        pinkCard.setAlignment(Pos.BOTTOM_CENTER);
        pinkCard.getStyleClass().add("winning-pink-card");
        pinkCard.setPrefWidth(650);
        pinkCard.setPrefHeight(380);
        pinkCard.setMaxWidth(650);
        pinkCard.setMaxHeight(380);

        HBox buttonsBox = createButtonsBox(user, currentLevel, hearts, stage);
        pinkCard.getChildren().add(buttonsBox);

        VBox yellowBox = new VBox(15);
        yellowBox.setAlignment(Pos.CENTER);
        yellowBox.getStyleClass().add("winning-yellow-box");
        yellowBox.setPrefWidth(550);
        yellowBox.setPrefHeight(250);
        yellowBox.setMaxWidth(550);
        yellowBox.setMaxHeight(250);


        Label levelCompleted = new Label("Level Completed");
        levelCompleted.getStyleClass().add("winning-title");
        
        ImageView starPng = loadImage("/images/star.png", 150, 200);
        if (starPng != null) {
            starPng.setOpacity(0);
            animateSingleStarPopup(starPng);
            yellowBox.getChildren().addAll(levelCompleted, starPng);
        } else {
            yellowBox.getChildren().add(levelCompleted);
        }

        StackPane.setAlignment(yellowBox, Pos.CENTER);
        yellowBox.setTranslateY(-40);

        StackPane bannerStack = new StackPane();
        bannerStack.setAlignment(Pos.CENTER);
        
        ImageView banner = loadImage("/images/banner.png", 550, 100);
        if (banner != null) {
            bannerStack.getChildren().add(banner);
        }
        
        Label congratsText = new Label("Congratulations");
        congratsText.getStyleClass().add("winning-congrats-text");
        congratsText.setTranslateY(-10);
        bannerStack.getChildren().add(congratsText);

        StackPane.setAlignment(bannerStack, Pos.TOP_CENTER);
        bannerStack.setTranslateY(-120);

        layeredStack.getChildren().addAll(pinkCard, yellowBox, bannerStack);

        mainWrapper.getChildren().add(layeredStack);
        
        return mainWrapper;
    }

    private static void animateSingleStarPopup(ImageView star) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), star);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), star);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        ParallelTransition popup = new ParallelTransition(fade, scale);
        popup.setDelay(Duration.millis(500));
        popup.play();
    }

    private static HBox createButtonsBox(User user, int currentLevel, int hearts, Stage stage) {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new javafx.geometry.Insets(0, 0, 25, 0));

        Button settingsBtn = new Button("Settings");
        settingsBtn.getStyleClass().add("winning-button");
        settingsBtn.setOnAction(e -> {
            TopBarController topBarController = new TopBarController(user, stage);
            Scene profileScene = ProfileController.createScene(user, stage, topBarController);
            stage.setScene(profileScene);
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("winning-button");
        logoutBtn.setOnAction(e -> {
            stage.setScene(LoginController.createScene());
        });

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("winning-button-primary");
        continueBtn.setOnAction(e -> {
            HomeController.createScene(user, stage);
        });

        box.getChildren().addAll(settingsBtn, logoutBtn, continueBtn);
        return box;
    }

    private static void startFireworksAnimation(Pane layer) {
        Random random = new Random();
        
        Timeline fireworks = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 650;
            createFirework(layer, x, y);
        }));
        
        fireworks.setCycleCount(12);
        fireworks.play();
    }

    private static void createFirework(Pane layer, double x, double y) {
        Random random = new Random();
        Color[] colors = {
            Color.web("#FF8ABC"), Color.web("#FFE59E"), 
            Color.web("#FF6B81"), Color.web("#FFD3C1"),
            Color.web("#A8E6CF"), Color.web("#C3CFE2")
        };
        
        Color color = colors[random.nextInt(colors.length)];
        
        for (int i = 0; i < 12; i++) {
            Circle particle = new Circle(3, color);
            particle.setLayoutX(x);
            particle.setLayoutY(y);
            layer.getChildren().add(particle);
            
            double angle = random.nextDouble() * 360;
            double distance = 50 + random.nextDouble() * 100;
            double endX = x + Math.cos(Math.toRadians(angle)) * distance;
            double endY = y + Math.sin(Math.toRadians(angle)) * distance;
            
            TranslateTransition move = new TranslateTransition(Duration.millis(800), particle);
            move.setToX(endX - x);
            move.setToY(endY - y);
            
            FadeTransition fade = new FadeTransition(Duration.millis(800), particle);
            fade.setFromValue(1);
            fade.setToValue(0);
            
            ParallelTransition anim = new ParallelTransition(move, fade);
            anim.setOnFinished(e -> layer.getChildren().remove(particle));
            anim.play();
        }
    }

    private static void startCardAnimation(VBox card) {
        card.setScaleX(0);
        card.setScaleY(0);
        card.setOpacity(0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), card);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1);
        scale.setToY(1);
        
        ParallelTransition anim = new ParallelTransition(fade, scale);
        anim.setDelay(Duration.millis(400));
        anim.play();
    }

    private static ImageView loadImage(String path, double width, double height) {
        try (InputStream stream = WinningController.class.getResourceAsStream(path)) {
            if (stream != null) {
                ImageView imageView = new ImageView(new Image(stream));
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(true);
                return imageView;
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
        }
        return null;
    }
}