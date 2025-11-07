package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.HeartAPIService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HeartController {

    private static HeartAPIService.HeartQuestion currentQuestion;
    private static Label feedbackLabel;
    private static int sessionEarned = 0; // track hearts earned in this mini-game
    private static TopBarController topBarController;
    private static User currentUser;
    private static Stage currentStage;

    public static Scene createScene(User user, int hearts, Stage stage) {
        currentUser = user;
        currentStage = stage;
        sessionEarned = 0;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("miniheart-root");

        // ✅ Use reusable TopBarController
        topBarController = new TopBarController(user, stage);
        root.setTop(topBarController.getView());

        root.setCenter(createGameArea());

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HeartController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        return scene;
    }

    private static VBox createGameArea() {
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        VBox heartGameBox = new VBox(15);
        heartGameBox.setAlignment(Pos.CENTER);
        heartGameBox.getStyleClass().add("miniheart-game-box");

        ImageView puzzleView = new ImageView();
        puzzleView.setFitWidth(300);
        puzzleView.setPreserveRatio(true);

        feedbackLabel = new Label("");
        feedbackLabel.getStyleClass().add("miniheart-feedback");

        loadNewPuzzle(puzzleView);

        // Number buttons (0–9)
        HBox numbersBox = new HBox(10);
        numbersBox.setAlignment(Pos.CENTER);
        for (int i = 0; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.getStyleClass().add("miniheart-number-btn");
            int answer = i;
            numBtn.setOnAction(e -> checkAnswer(answer, puzzleView));
            numbersBox.getChildren().add(numBtn);
        }

        // Navigation (Back / Next)
        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("miniheart-nav-btn");
        backBtn.setOnAction(e -> {
            // Return to GameController if came from there, else Home
            if (currentUser.getLastScreen().equals("game")) {
                Scene gameScene = GameController.createScene(currentUser, currentUser.getCurrentLevel());
                currentStage.setScene(gameScene);
            } else {
                Scene homeland = HomeLandController.createScene(currentUser, currentStage);
                currentStage.setScene(homeland);
            }
        });


        Button nextBtn = new Button("Next");
        nextBtn.getStyleClass().add("miniheart-nav-btn");
        nextBtn.setOnAction(e -> loadNewPuzzle(puzzleView));

        navBox.getChildren().addAll(backBtn, nextBtn);

        heartGameBox.getChildren().addAll(puzzleView, feedbackLabel);
        centerBox.getChildren().addAll(heartGameBox, numbersBox, navBox);
        return centerBox;
    }

    private static void loadNewPuzzle(ImageView puzzleView) {
        feedbackLabel.setText("Loading puzzle...");
        new Thread(() -> {
            try {
                currentQuestion = HeartAPIService.fetchQuestion();
                Platform.runLater(() -> {
                    puzzleView.setImage(new Image(currentQuestion.imageUrl, true));
                    feedbackLabel.setText("How many hearts can you find?");
                });
            } catch (Exception e) {
                Platform.runLater(() -> feedbackLabel.setText("Failed to load puzzle."));
                e.printStackTrace();
            }
        }).start();
    }

    private static void checkAnswer(int answer, ImageView puzzleView) {
        if (currentQuestion == null) return;

        if (answer == currentQuestion.solution) {
            // ✅ +2 hearts immediately
            int newTotal = currentUser.getHearts() + 2;
            sessionEarned += 2;
            topBarController.updateHearts(newTotal);
            feedbackLabel.setText("✅ Correct! +2 Hearts (Total Earned: " + sessionEarned + "/10)");

            // ✅ If user earned +10 in this session → go home
            if (sessionEarned >= 10) {
                feedbackLabel.setText("🎉 You earned +10 hearts! Returning to Home...");
                Platform.runLater(() -> {
                    Scene homeland = HomeLandController.createScene(currentUser, currentStage);
                    currentStage.setScene(homeland);
                });
            } else {
                loadNewPuzzle(puzzleView);
            }
        } else {
            feedbackLabel.setText("❌ Wrong! Try again!");
        }
    }
}
