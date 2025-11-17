package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.GameEngine;
import com.wordheartschallenge.app.ui.GameUI;
import com.wordheartschallenge.app.utils.AnimatedMessageUtil;
import com.wordheartschallenge.app.database.UserProgressDAO;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private static final String[] KEYBOARD_ROW_1 = {"A","B","C","D","E","F","G","H","I","J","K","L","M"};
    private static final String[] KEYBOARD_ROW_2 = {"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    private static GameEngine gameEngine;
    private static GameUI gameUI;
    private static Scene currentScene;

    private static List<Label> guessBoxes = new ArrayList<>();
    private static Label definitionLabel;
    private static Label hintLabel;
    private static HBox guessBoxContainer;

    private static User currentUser;
    private static Stage currentStage;
    private static TopBarController topBarController;

    public static Scene createScene(User user, int level, Stage stage) {
        currentUser = user;
        currentStage = stage;

        gameUI = new GameUI();

        // ✅ FIXED: Definition label with dynamic sizing
        definitionLabel = new Label("Loading definition...");
        definitionLabel.getStyleClass().add("definition-label");
        definitionLabel.setWrapText(true);
        definitionLabel.setAlignment(Pos.CENTER);
        // Allow the label to grow as needed
        definitionLabel.setMinWidth(400);
        definitionLabel.setMaxWidth(700);
        definitionLabel.setPrefWidth(600);
        definitionLabel.setMinHeight(60);
        definitionLabel.setMaxHeight(Double.MAX_VALUE);

        hintLabel = new Label("");
        hintLabel.getStyleClass().add("hint-label");
        hintLabel.setWrapText(true);
        hintLabel.setAlignment(Pos.CENTER);

        guessBoxContainer = new HBox(15);
        guessBoxContainer.setAlignment(Pos.CENTER);

        gameEngine = new GameEngine(user, definitionLabel, hintLabel, guessBoxContainer, guessBoxes);
        gameEngine.loadNewWord(level);

        // ✅ FIXED: Hint button shows only "Hint" text
        Button hintButton = new Button("Hint");
        hintButton.setGraphic(gameUI.createIconView("/images/hint.png", 25, 25));
        hintButton.setContentDisplay(ContentDisplay.LEFT); // Icon on left of text
        hintButton.setGraphicTextGap(10); // Adjust spacing between icon and text (smaller = closer)

        // Optional: adjust padding if needed
        hintButton.setPadding(new Insets(0, 5, 0, 0)); // top, right, bottom, left
        hintButton.setPrefSize(50, 40);
        hintButton.getStyleClass().add("game-hint-button");
        hintButton.setOnAction(e -> showHint());

        topBarController = new TopBarController(user, currentStage);

        gameUI.setTop(topBarController.getView());
        gameUI.setCenter(gameUI.createGameArea(definitionLabel, guessBoxContainer, hintLabel, hintButton));
        gameUI.setBottom(gameUI.createKeyboardArea(
                createKeyboardRow(KEYBOARD_ROW_1),
                createKeyboardRow(KEYBOARD_ROW_2),
                createSubmitButton(user, level),
                createBackspaceButton(),
                createRefreshButton()
        ));


        user.setLastScreen("Game");
        currentScene = gameUI.getScene();
        return currentScene;
    }

    private static List<Button> createKeyboardRow(String[] keys) {
        List<Button> row = new ArrayList<>();
        for (String key : keys) {
            Button keyButton = new Button(key);
            keyButton.getStyleClass().add("game-key");
            keyButton.setOnAction(e -> gameEngine.fillNextBox(key));
            row.add(keyButton);
        }
        return row;
    }

    private static Button createSubmitButton(User user, int level) {
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("game-play-button");
        submitButton.setOnAction(e -> checkWord(user, level));
        return submitButton;
    }
    private static Button createBackspaceButton() {
        Button backspace = new Button();
        backspace.getStyleClass().add("game-small-button"); // small button style
        backspace.setPrefSize(60, 40); // smaller than submit/hint

        // Icon scaled up
        backspace.setGraphic(gameUI.createIconView("/images/backspace.png", 40, 50));

        backspace.setOnAction(e -> gameEngine.removeLastLetter()); // remove last letter typed
        return backspace;
    }


    private static Button createRefreshButton() {
        Button refresh = new Button();
        refresh.getStyleClass().add("game-small-button"); // small button style
        refresh.setPrefSize(60, 40); // smaller than submit/hint

        // Icon scaled up
        refresh.setGraphic(gameUI.createIconView("/images/refresh.png", 30, 30));

        refresh.setOnAction(e -> {
            gameEngine.resetGuessBoxes();  // clear all letters
            hintLabel.setText("");          // clear hint
        });

        return refresh;
    }


    private static void checkWord(User user, int level) {
        if (gameEngine.getCurrentWord().isEmpty()) return;

        StringBuilder guess = new StringBuilder();
        for (Label box : guessBoxes) guess.append(box.getText());

        if (guess.toString().equalsIgnoreCase(gameEngine.getCurrentWord())) {
            hintLabel.setText("✅ Correct! The word was " + gameEngine.getCurrentWord());

            UserProgressDAO.saveCompletedLevel(user.getId(), level);

            int nextLevel = user.getCurrentLevel() + 1;
            user.setCurrentLevel(nextLevel);
            UserProgressDAO.updateLevel(user.getId(), nextLevel);

            int newHearts = Math.min(user.getHearts() + 1, 10);
            user.setHearts(newHearts);
            UserProgressDAO.updateHearts(user.getId(), newHearts);
            
            WinningController.createScene(user, level, newHearts, currentStage);

        } else {
            gameEngine.resetGuessBoxes();

            int currentHearts = user.getHearts();
            if (currentHearts > 0) {
                int newHearts = Math.max(0, currentHearts - 1);
                
                // ✅ Show animated heart deduction for wrong answer
                AnimatedMessageUtil.showHeartDeduction(currentScene, 1);
                
                topBarController.updateHearts(newHearts);
                user.setHearts(newHearts);
                UserProgressDAO.updateHearts(user.getId(), newHearts);

                hintLabel.setText("❌ Wrong! Try again.");

                if (newHearts == 0) {
                    openHeartMiniGame(user, level);
                }
            }
        }
    }

    private static void openHeartMiniGame(User user, int level) {
        Scene heartScene = HeartController.createScene(user, user.getHearts(), currentStage);
        currentStage.setScene(heartScene);

        currentStage.setOnCloseRequest(event -> {
            topBarController = new TopBarController(user, currentStage);
            gameUI.setTop(topBarController.getView());
            
            // ✅ Recreate hint button with just "Hint" text
            Button hintButton = new Button("Hint");
            hintButton.setGraphic(gameUI.createIconView("/images/hint.png", 30, 30));
            hintButton.setPrefSize(120, 40);
            hintButton.getStyleClass().add("game-hint-button");
            hintButton.setOnAction(e -> showHint());

            gameUI.setCenter(gameUI.createGameArea(definitionLabel, guessBoxContainer, hintLabel, hintButton));
            gameUI.setBottom(gameUI.createKeyboardArea(
                    createKeyboardRow(KEYBOARD_ROW_1),
                    createKeyboardRow(KEYBOARD_ROW_2),
                    createSubmitButton(user, level),
                    createBackspaceButton(),
                    createRefreshButton()
            ));

            currentStage.setScene(gameUI.getScene());
            currentScene = gameUI.getScene();
        });
    }

    /**
     * ✅ IMPROVED: Show hint with animated heart deduction
     * - Gets next unique hint from the hint pool
     * - Shows animated message box with -2 hearts
     * - No inline text about heart cost
     */
    private static void showHint() {
        if (gameEngine.getCurrentWord().isEmpty()) return;

        int currentHearts = currentUser.getHearts();
        
        // Check if user has enough hearts
        if (currentHearts < 2) {
            hintLabel.setText("⚠️ Not enough hearts for a hint! You need 2 ❤️");
            return;
        }

        // Get the next unique hint (cycles through different hints)
        String hint = gameEngine.getNextHint();
        
        // Deduct 2 hearts
        int newHearts = Math.max(0, currentHearts - 2);
        
        // ✅ Show animated message box with -2 hearts
        AnimatedMessageUtil.showHeartDeduction(currentScene, 2);
        
        // Update hearts in UI and database
        topBarController.updateHearts(newHearts);
        currentUser.setHearts(newHearts);
        UserProgressDAO.updateHearts(currentUser.getId(), newHearts);
        
        // Display the hint (no inline heart cost shown)
        hintLabel.setText("💡 " + hint);

        // If hearts reach 0, open mini game
        if (newHearts == 0) {
            openHeartMiniGame(currentUser, currentUser.getCurrentLevel());
        }
    }
}