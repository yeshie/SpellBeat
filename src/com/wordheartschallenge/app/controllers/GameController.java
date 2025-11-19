package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.GameEngine;
import com.wordheartschallenge.app.ui.GameUI;
import com.wordheartschallenge.app.utils.AnimatedMessageUtil;
import com.wordheartschallenge.app.utils.AlertUtil;
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
    private static int currentLevel;
    private static Stage currentStage;
    private static TopBarController topBarController;

    public static Scene createScene(User user, int level, Stage stage) {
        currentUser = user;
        currentLevel = level;
        currentStage = stage;

        gameUI = new GameUI();

        definitionLabel = new Label("Loading definition...");
        definitionLabel.getStyleClass().add("definition-label");
        definitionLabel.setWrapText(true);
        definitionLabel.setAlignment(Pos.CENTER);
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

        Button hintButton = new Button("Hint");
        hintButton.setGraphic(gameUI.createIconView("/images/hint.png", 25, 25));
        hintButton.setContentDisplay(ContentDisplay.LEFT);
        hintButton.setGraphicTextGap(10);
        hintButton.setPadding(new Insets(0, 5, 0, 0));
        hintButton.setPrefSize(50, 40);
        hintButton.getStyleClass().add("game-hint-button");
        hintButton.setOnAction(e -> requestHint());

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
        backspace.getStyleClass().add("game-small-button");
        backspace.setPrefSize(60, 40);
        backspace.setGraphic(gameUI.createIconView("/images/backspace.png", 40, 50));
        backspace.setOnAction(e -> gameEngine.removeLastLetter());
        return backspace;
    }

    private static Button createRefreshButton() {
        Button refresh = new Button();
        refresh.getStyleClass().add("game-small-button");
        refresh.setPrefSize(60, 40);
        refresh.setGraphic(gameUI.createIconView("/images/refresh.png", 30, 30));
        refresh.setOnAction(e -> {
            gameEngine.resetGuessBoxes();
            hintLabel.setText("");
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
                
                AnimatedMessageUtil.showHeartDeduction(currentScene, 1);
                
                topBarController.updateHearts(newHearts);
                user.setHearts(newHearts);
                UserProgressDAO.updateHearts(user.getId(), newHearts);

                hintLabel.setText("❌ Wrong! Try again.");

                if (newHearts == 0) {
                    showForceHeartGameDialog();
                }
            }
        }
    }

    /** ✅ Request hint - check hearts and show custom alert if needed */
    private static void requestHint() {
        if (gameEngine.getCurrentWord().isEmpty()) return;

        int currentHearts = currentUser.getHearts();
        
        // ✅ Not enough hearts → Show custom alert with OK/Cancel
        if (currentHearts < 2) {
            showHeartMiniGameDialog();
            return;
        }

        // ✅ Enough hearts → give hint
        giveHint();
    }

    /** ✅ Give hint and deduct 2 hearts */
    private static void giveHint() {
        String hint = gameEngine.getNextHint();
        int newHearts = Math.max(0, currentUser.getHearts() - 2);
        
        AnimatedMessageUtil.showHeartDeduction(currentScene, 2);
        
        topBarController.updateHearts(newHearts);
        currentUser.setHearts(newHearts);
        UserProgressDAO.updateHearts(currentUser.getId(), newHearts);
        
        hintLabel.setText("💡 " + hint);

        if (newHearts == 0) {
            showForceHeartGameDialog();
        }
    }

    /** ✅ Show custom alert asking user to play heart mini-game (with choice) */
    private static void showHeartMiniGameDialog() {
        AlertUtil.showCustomAlertWithActions(
            "Not Enough Hearts! ❤️",
            "You need 2 hearts to get a hint.\nWould you like to play the Heart Mini-Game to earn more hearts?",
            () -> openHeartMiniGameForRefill(), // OK action
            () -> System.out.println("User cancelled heart game") // Cancel action
        );
    }

    /** ✅ Force user to play heart mini-game (no cancel option) */
    private static void showForceHeartGameDialog() {
        AlertUtil.showCustomAlert(
            "No Hearts Left! 💔",
            "You have 0 hearts!\nYou must play the Heart Mini-Game to continue."
        );
        
        openHeartMiniGameForRefill();
    }

    /** ✅ Open Heart Mini-Game for refill (preserves state) */
    private static void openHeartMiniGameForRefill() {
        System.out.println("\n=== OPENING HEART MINI-GAME FOR REFILL ===");
        System.out.println("Current word: " + gameEngine.getCurrentWord());
        
        // Save current guess state
        String savedGuessState = getCurrentGuessState();
        System.out.println("Saved guess state: " + savedGuessState);
        
        // Open Heart Mini-Game with callback
        HeartController.createSceneForRefill(
            currentUser,
            currentStage,
            () -> returnToGameAfterRefill(savedGuessState)
        );
    }

    /** ✅ Return to game after heart refill */
    private static void returnToGameAfterRefill(String savedGuessState) {
        System.out.println("\n=== RETURNING TO WORD GAME ===");
        System.out.println("Hearts after refill: " + currentUser.getHearts());
        System.out.println("Restoring guess state: " + savedGuessState);
        
        // Rebuild UI with updated hearts
        rebuildGameUI();
        
        // Restore guess state
        restoreGuessState(savedGuessState);
        
        hintLabel.setText("❤️ Hearts refilled! Continue playing.");
        
        currentStage.setScene(currentScene);
    }

    /** ✅ Get current guess state */
    private static String getCurrentGuessState() {
        StringBuilder state = new StringBuilder();
        for (Label box : guessBoxes) {
            state.append(box.getText());
        }
        return state.toString();
    }

    /** ✅ Restore guess state */
    private static void restoreGuessState(String state) {
        for (int i = 0; i < state.length() && i < guessBoxes.size(); i++) {
            guessBoxes.get(i).setText(String.valueOf(state.charAt(i)));
        }
    }

    /** ✅ Rebuild UI with updated hearts */
    private static void rebuildGameUI() {
        gameUI = new GameUI();

        Button hintButton = new Button("Hint");
        hintButton.setGraphic(gameUI.createIconView("/images/hint.png", 25, 25));
        hintButton.setContentDisplay(ContentDisplay.LEFT);
        hintButton.setGraphicTextGap(10);
        hintButton.setPadding(new Insets(0, 5, 0, 0));
        hintButton.setPrefSize(50, 40);
        hintButton.getStyleClass().add("game-hint-button");
        hintButton.setOnAction(e -> requestHint());

        topBarController = new TopBarController(currentUser, currentStage);

        gameUI.setTop(topBarController.getView());
        gameUI.setCenter(gameUI.createGameArea(definitionLabel, guessBoxContainer, hintLabel, hintButton));
        gameUI.setBottom(gameUI.createKeyboardArea(
                createKeyboardRow(KEYBOARD_ROW_1),
                createKeyboardRow(KEYBOARD_ROW_2),
                createSubmitButton(currentUser, currentLevel),
                createBackspaceButton(),
                createRefreshButton()
        ));

        currentScene = gameUI.getScene();
    }
}