package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.models.User;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.List;

public class GameEngine {

    private String currentWord = "";
    private String currentDefinition = "";
    private User currentUser;
    private Label definitionLabel;
    private Label hintLabel;
    private HBox guessBoxContainer;
    private List<Label> guessBoxes;

    public GameEngine(User user, Label definitionLabel, Label hintLabel, HBox guessBoxContainer, List<Label> guessBoxes) {
        this.currentUser = user;
        this.definitionLabel = definitionLabel;
        this.hintLabel = hintLabel;
        this.guessBoxContainer = guessBoxContainer;
        this.guessBoxes = guessBoxes;
    }

    public void loadNewWord(int level) {
        new Thread(() -> {
            try {
                WordAPIService.WordData data = WordAPIService.fetchValidWord(level);  // ✅ Pass level
                currentWord = data.word.toUpperCase();
                currentDefinition = data.definition;
                Platform.runLater(() -> {
                    definitionLabel.setText("Definition: " + currentDefinition);
                    guessBoxes.clear();
                    guessBoxContainer.getChildren().clear();
                    for (int i = 0; i < currentWord.length(); i++) {
                        Label box = new Label("_");
                        box.getStyleClass().add("game-guess-box");
                        guessBoxes.add(box);
                    }
                    guessBoxContainer.getChildren().addAll(guessBoxes);
                    hintLabel.setText("");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> definitionLabel.setText("Error loading word."));
            }
        }).start();
    }

    public void fillNextBox(String letter) {
        for (Label box : guessBoxes) {
            if (box.getText().equals("_")) {
                box.setText(letter);
                break;
            }
        }
    }

    public void resetGuessBoxes() {
        for (Label box : guessBoxes) {
            box.setText("_");
        }
    }

    public void removeLastLetter() {
        for (int i = guessBoxes.size() - 1; i >= 0; i--) {
            if (!guessBoxes.get(i).getText().isEmpty()) {
                guessBoxes.get(i).setText("");
                break;
            }
        }
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public String getCurrentDefinition() {
        return currentDefinition;
    }
}
