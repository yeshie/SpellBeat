package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.GameEngine;
import com.wordheartschallenge.app.services.WordAPIService;
import com.wordheartschallenge.app.ui.GameUI;
import com.wordheartschallenge.app.database.UserProgressDAO;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private static final String[] KEYBOARD_ROW_1 = {"A","B","C","D","E","F","G","H","I","J","K","L","M"};
    private static final String[] KEYBOARD_ROW_2 = {"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    private static GameEngine gameEngine;
    private static GameUI gameUI;

    private static List<Label> guessBoxes = new ArrayList<>();
    private static Label definitionLabel;
    private static Label hintLabel;
    private static HBox guessBoxContainer;

    private static User currentUser;
    private static Stage currentStage;
    private static TopBarController topBarController;

    public static Scene createScene(User user, int level,Stage stage) {
    	 currentUser = user;
    	    currentStage = stage;

        gameUI = new GameUI();

        definitionLabel = new Label("Loading definition...");
        definitionLabel.getStyleClass().add("definition-label");
        definitionLabel.setWrapText(true);

        hintLabel = new Label("");
        hintLabel.getStyleClass().add("hint-label");

        guessBoxContainer = new HBox(15);

        gameEngine = new GameEngine(user, definitionLabel, hintLabel, guessBoxContainer, guessBoxes);
        gameEngine.loadNewWord(level);

        Button hintButton = new Button("Hint (-2 Hearts)");
        hintButton.setGraphic(gameUI.createIconView("/images/hint.png", 20, 20));
        hintButton.setPrefSize(150, 40);
        hintButton.getStyleClass().add("game-hint-button");
        hintButton.setOnAction(e -> showHint());

        topBarController = new TopBarController(user, currentStage);

        gameUI.setTop(topBarController.getView());
        gameUI.setCenter(gameUI.createGameArea(definitionLabel, guessBoxContainer, hintLabel, hintButton));
        gameUI.setBottom(gameUI.createKeyboardArea(
                createKeyboardRow(KEYBOARD_ROW_1),
                createKeyboardRow(KEYBOARD_ROW_2),
                createSubmitButton(user, level)
        ));

        user.setLastScreen("Game");
        return gameUI.getScene();
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

    private static void checkWord(User user, int level) {
        if (gameEngine.getCurrentWord().isEmpty()) return;

        StringBuilder guess = new StringBuilder();
        for (Label box : guessBoxes) guess.append(box.getText());

        if (guess.toString().equalsIgnoreCase(gameEngine.getCurrentWord())) {
            hintLabel.setText("✅ Correct! The word was " + gameEngine.getCurrentWord());

            // ✅ Save completed level in history
            UserProgressDAO.saveCompletedLevel(user.getId(), level);

            // ✅ Update current level if next level is higher
            int nextLevel = user.getCurrentLevel() + 1;
            user.setCurrentLevel(nextLevel);
            UserProgressDAO.updateLevel(user.getId(), nextLevel);

            // ✅ Optionally update hearts (example: gain 1 heart)
            int newHearts = Math.min(user.getHearts() + 1, 10);
            user.setHearts(newHearts);
            UserProgressDAO.updateHearts(user.getId(), newHearts);

            // ✅ Navigate back to HomeController to show updated tiles
            HomeController.createScene(user, currentStage);

        } else {
            gameEngine.resetGuessBoxes();  

            int currentHearts = user.getHearts();
            if (currentHearts > 0) {
                int newHearts = Math.max(0, currentHearts - 1);
                topBarController.updateHearts(newHearts);
                user.setHearts(newHearts);
                UserProgressDAO.updateHearts(user.getId(), newHearts);

                hintLabel.setText("❌ Wrong! -1 heart. Try again.");

                if (newHearts == 0) {
                    openHeartMiniGame(user, level);
                } else {
                    gameEngine.resetGuessBoxes();
                }
            }
        }
    }

    private static void openHeartMiniGame(User user, int level) {
        Scene heartScene = HeartController.createScene(user, user.getHearts(), currentStage);

        currentStage.setScene(heartScene);

        currentStage.setOnCloseRequest(event -> {
            // Rebuild the same game scene after mini-game closes
            topBarController = new TopBarController(user, currentStage);
            gameUI.setTop(topBarController.getView());
            Button hintButton = new Button("Hint (-2 Hearts)");
            hintButton.setGraphic(gameUI.createIconView("/images/hint.png", 20, 20));
            hintButton.setPrefSize(150, 40);
            hintButton.getStyleClass().add("game-hint-button");
            hintButton.setOnAction(e -> showHint());

            gameUI.setCenter(gameUI.createGameArea(definitionLabel, guessBoxContainer, hintLabel, hintButton));
            gameUI.setBottom(gameUI.createKeyboardArea(
                    createKeyboardRow(KEYBOARD_ROW_1),
                    createKeyboardRow(KEYBOARD_ROW_2),
                    createSubmitButton(user, level)
            ));

            currentStage.setScene(gameUI.getScene());
        });
    }
  

    private static void showHint() {
        if (gameEngine.getCurrentWord().isEmpty()) return;

        int currentHearts = currentUser.getHearts();
        if (currentHearts < 2) {
            hintLabel.setText("⚠️ Not enough hearts for a hint!");
            return;
        }

        try {
            String hint = WordAPIService.fetchHint(gameEngine.getCurrentWord());
            int newHearts = Math.max(0, currentHearts - 2);
            topBarController.updateHearts(newHearts);
            currentUser.setHearts(newHearts);
            UserProgressDAO.updateHearts(currentUser.getId(), newHearts);
            hintLabel.setText("Hint: " + hint + " (-2 Hearts)");

            if (newHearts == 0) openHeartMiniGame(currentUser, currentUser.getCurrentLevel());
        } catch (Exception e) {
            hintLabel.setText("No hint available.");
        }
    }
}
