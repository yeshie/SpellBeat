package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.HeartAPIService;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HeartLogic {

    private User user;
    private Label feedbackLabel;
    private ImageView puzzleView;
    private int sessionEarned = 0;
    private HeartAPIService.HeartQuestion currentQuestion;

    public HeartLogic(User user, Label feedbackLabel, ImageView puzzleView) {
        this.user = user;
        this.feedbackLabel = feedbackLabel;
        this.puzzleView = puzzleView;
    }

    public void loadNewPuzzle() {
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

    public void checkAnswer(int answer, Runnable onMaxHearts) {
        if (currentQuestion == null) return;

        if (answer == currentQuestion.solution) {
            int newTotal = user.getHearts() + 2;
            sessionEarned += 2;
            user.setHearts(newTotal);
            feedbackLabel.setText("✅ Correct! +2 Hearts (Total Earned: " + sessionEarned + "/10)");

            if (sessionEarned >= 10) {
                feedbackLabel.setText("🎉 You earned +10 hearts! Returning to Home...");
                Platform.runLater(onMaxHearts);
            } else {
                loadNewPuzzle();
            }
        } else {
            feedbackLabel.setText("❌ Wrong! Try again!");
        }
    }

    public int getSessionEarned() { return sessionEarned; }
}
