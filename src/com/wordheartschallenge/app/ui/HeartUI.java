package com.wordheartschallenge.app.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

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

        VBox heartGameBox = new VBox(15);
        heartGameBox.setAlignment(Pos.CENTER);
        heartGameBox.getStyleClass().add("miniheart-game-box");

        puzzleView = new ImageView();
        puzzleView.setFitWidth(300);
        puzzleView.setPreserveRatio(true);

        feedbackLabel = new Label("");
        feedbackLabel.getStyleClass().add("miniheart-feedback");

        HBox numbersBox = new HBox(10);
        numbersBox.setAlignment(Pos.CENTER);
        numberButtons = new Button[10];
        for (int i = 0; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.getStyleClass().add("miniheart-number-btn");
            numberButtons[i] = numBtn;
            numbersBox.getChildren().add(numBtn);
        }

        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);

        backButton = new Button("Back");
        backButton.getStyleClass().add("miniheart-nav-btn");

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("miniheart-nav-btn");

        navBox.getChildren().addAll(backButton, nextButton);
        heartGameBox.getChildren().addAll(puzzleView, feedbackLabel);
        centerBox.getChildren().addAll(heartGameBox, numbersBox, navBox);

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
