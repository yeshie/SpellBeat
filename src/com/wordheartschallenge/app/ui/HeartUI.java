package com.wordheartschallenge.app.ui;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * ✅ ENHANCED: HeartUI with improved layout matching GameController style
 */
public class HeartUI {

    private VBox centerBox;
    private ImageView puzzleView;
    private Label feedbackLabel;
    private Button[] numberButtons;
    private Button backButton, nextButton;
    private Scene scene;

    public HeartUI() {
        centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        // ✅ Game area container matching GameController style
        VBox gameAreaContainer = new VBox(20);
        gameAreaContainer.setAlignment(Pos.CENTER);
        gameAreaContainer.getStyleClass().add("game-center-card");
        gameAreaContainer.setPrefWidth(650);
        gameAreaContainer.setMaxWidth(650);
        gameAreaContainer.setPadding(new Insets(30));

        // ✅ Feedback box styled like definition container
        VBox feedbackBox = new VBox();
        feedbackBox.setAlignment(Pos.CENTER);
        feedbackBox.getStyleClass().add("definition-box");
        feedbackBox.setPrefWidth(600);
        feedbackBox.setMinHeight(80);
        feedbackBox.setPadding(new Insets(20, 25, 20, 25));

        feedbackLabel = new Label("How many hearts can you find?");
        feedbackLabel.getStyleClass().add("definition-label");
        feedbackLabel.setWrapText(true);
        feedbackLabel.setAlignment(Pos.CENTER);
        feedbackLabel.setMaxWidth(550);
        
        feedbackBox.getChildren().add(feedbackLabel);

        // ✅ Puzzle image container
        StackPane puzzleContainer = new StackPane();
        puzzleContainer.setAlignment(Pos.CENTER);
        puzzleContainer.setPrefWidth(400);
        puzzleContainer.setPrefHeight(300);

        puzzleView = new ImageView();
        puzzleView.setFitWidth(350);
        puzzleView.setFitHeight(280);
        puzzleView.setPreserveRatio(true);
        
        puzzleContainer.getChildren().add(puzzleView);

        gameAreaContainer.getChildren().addAll(feedbackBox, puzzleContainer);

        // ✅ Number buttons styled like keyboard
        HBox numbersBox = new HBox(12);
        numbersBox.setAlignment(Pos.CENTER);
        numbersBox.setPadding(new Insets(20, 0, 10, 0));
        
        numberButtons = new Button[10];
        for (int i = 0; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.getStyleClass().add("game-key");
            numBtn.setPrefSize(50, 50);
            numberButtons[i] = numBtn;
            numbersBox.getChildren().add(numBtn);
        }

        // ✅ Navigation buttons
        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);
        navBox.setPadding(new Insets(10, 0, 20, 0));

        backButton = new Button("Back");
        backButton.getStyleClass().add("game-play-button");
        backButton.setPrefSize(120, 45);

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("game-play-button");
        nextButton.setPrefSize(120, 45);

        navBox.getChildren().addAll(backButton, nextButton);

        centerBox.getChildren().addAll(gameAreaContainer, numbersBox, navBox);

        scene = new Scene(centerBox, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    }

    // Getters
    public Scene getScene() { return scene; }
    public VBox getCenterBox() { return centerBox; }
    public ImageView getPuzzleView() { return puzzleView; }
    public Label getFeedbackLabel() { return feedbackLabel; }
    public Button[] getNumberButtons() { return numberButtons; }
    public Button getBackButton() { return backButton; }
    public Button getNextButton() { return nextButton; }
}