package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.models.User;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;

public class GameEngine {
    
    private String currentWord = "";
    private String currentDefinition = "";
    private List<String> currentHints;
    private User currentUser;
    private Label definitionLabel;
    private Label hintLabel;
    private HBox guessBoxContainer;
    private List<Label> guessBoxes;

    public GameEngine(User user, Label definitionLabel, Label hintLabel, 
                     HBox guessBoxContainer, List<Label> guessBoxes) {
        this.currentUser = user;
        this.definitionLabel = definitionLabel;
        this.hintLabel = hintLabel;
        this.guessBoxContainer = guessBoxContainer;
        this.guessBoxes = guessBoxes;
    }

    /**
     * ✅ IMPROVED: Load word with dynamic definition sizing
     * - Fetches word data from API
     * - Configures definition label to expand based on content
     * - Ensures full text visibility
     */
    public void loadNewWord(int level) {
        new Thread(() -> {
            try {
                WordAPIService.resetHints(); // Clear previous hints
                WordAPIService.WordData data = WordAPIService.fetchValidWord(level);
                
                currentWord = data.word.toUpperCase();
                currentDefinition = data.definition;
                currentHints = data.hints;

                Platform.runLater(() -> {
                    // ✅ FIXED: Configure definition label for dynamic sizing
                    definitionLabel.setText(currentDefinition);
                    definitionLabel.setWrapText(true);
                    definitionLabel.setAlignment(Pos.CENTER);
                    
                    // Set flexible width constraints
                    definitionLabel.setMinWidth(400);
                    definitionLabel.setMaxWidth(700);
                    definitionLabel.setPrefWidth(600);
                    
                    // ✅ KEY FIX: Allow unlimited height growth
                    definitionLabel.setMinHeight(Label.USE_COMPUTED_SIZE);
                    definitionLabel.setPrefHeight(Label.USE_COMPUTED_SIZE);
                    definitionLabel.setMaxHeight(Double.MAX_VALUE);
                    
                    // Ensure proper text layout
                    definitionLabel.autosize();
                    
                    // Setup guess boxes
                    guessBoxes.clear();
                    guessBoxContainer.getChildren().clear();
                    
                    for (int i = 0; i < currentWord.length(); i++) {
                        Label box = new Label("_");
                        box.getStyleClass().add("game-guess-box");
                        guessBoxes.add(box);
                    }
                    
                    guessBoxContainer.getChildren().addAll(guessBoxes);
                    guessBoxContainer.setAlignment(Pos.CENTER);
                    
                    // Clear hint label for new word
                    hintLabel.setText("");
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    definitionLabel.setText("Error loading word. Please try again.");
                    definitionLabel.setWrapText(true);
                    definitionLabel.setAlignment(Pos.CENTER);
                    definitionLabel.setMaxHeight(Double.MAX_VALUE);
                });
            }
        }).start();
    }

    /**
     * Fill the next empty guess box with a letter
     */
    public void fillNextBox(String letter) {
        for (Label box : guessBoxes) {
            if (box.getText().equals("_")) {
                box.setText(letter);
                break;
            }
        }
    }

    /**
     * Reset all guess boxes to empty state
     */
    public void resetGuessBoxes() {
        for (Label box : guessBoxes) {
            box.setText("_");
        }
    }

    /**
     * Remove the last filled letter
     */
    public void removeLastLetter() {
        for (int i = guessBoxes.size() - 1; i >= 0; i--) {
            if (!guessBoxes.get(i).getText().equals("_")) {
                guessBoxes.get(i).setText("_");
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

    /**
     * ✅ Get next unique hint from the hint pool
     * - Cycles through different hints each time
     * - Never repeats the same hint for the current word
     * - Returns variety of hint types (synonyms, letter clues, etc.)
     */
    public String getNextHint() {
        if (currentHints == null || currentHints.isEmpty()) {
            return "No hints available";
        }
        return WordAPIService.getNextHint(currentHints, currentWord);
    }
}