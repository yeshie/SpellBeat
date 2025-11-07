package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.WordAPIService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private static final String[] KEYBOARD_ROW_1 = {"A","B","C","D","E","F","G","H","I","J","K","L","M"};
    private static final String[] KEYBOARD_ROW_2 = {"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    private static String currentWord = "";
    private static String currentDefinition = "";
    private static List<Label> guessBoxes = new ArrayList<>();
    private static Label hintLabel;
    private static Label definitionLabel;
    private static HBox guessBoxContainer;

    private static TopBarController topBarController;
    private static User currentUser;
    private static Stage currentStage;

    public static Scene createScene(User user, int level) {
        currentUser = user;
        Stage stage = new Stage();
        currentStage = stage;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");

        topBarController = new TopBarController(user, stage);
        root.setTop(topBarController.getView());

        root.setCenter(createGameArea());
        root.setBottom(createKeyboardArea(user, level, stage));

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(GameController.class.getResource("/css/style.css").toExternalForm());

        new Thread(GameController::loadNewWord).start();
        user.setLastScreen("Game");

        return scene;
    }

    // ==============================
    // Game Area
    // ==============================
    private static Node createGameArea() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("game-center-card");

        VBox definitionBox = new VBox();
        definitionBox.setAlignment(Pos.CENTER);
        definitionBox.getStyleClass().add("definition-box");

        definitionLabel = new Label("Loading definition...");
        definitionLabel.getStyleClass().add("definition-label");
        definitionLabel.setWrapText(true);
        definitionBox.getChildren().add(definitionLabel);

        guessBoxContainer = new HBox(15);
        guessBoxContainer.setAlignment(Pos.CENTER);

        hintLabel = new Label("");
        hintLabel.getStyleClass().add("hint-label");

        Button hintButton = new Button("Hint (-2 Hearts)");
        ImageView hintIcon = createIconView("/images/hint.png", 20, 20);
        hintIcon.setScaleX(2.4);
        hintIcon.setScaleY(2.4);
        hintButton.setGraphic(hintIcon);
        hintButton.setPrefSize(150, 40);
        hintButton.getStyleClass().add("game-hint-button");
        hintButton.setOnAction(e -> showHint());

        container.getChildren().addAll(definitionBox, guessBoxContainer, hintButton, hintLabel);
        return container;
    }

    // ==============================
    // Keyboard Area
    // ==============================
    private static Node createKeyboardArea(User user, int level, Stage stage) {
        VBox keyboardArea = new VBox(20);
        keyboardArea.setAlignment(Pos.CENTER);
        keyboardArea.setPadding(new Insets(10, 20, 30, 20));
        keyboardArea.getStyleClass().add("keyboard-area");

        HBox row1 = createKeyboardRow(KEYBOARD_ROW_1);
        HBox row2 = createKeyboardRow(KEYBOARD_ROW_2);

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("game-play-button");
        submitButton.setOnAction(e -> checkWord(user, level, stage));

        keyboardArea.getChildren().addAll(row1, row2, submitButton);
        return keyboardArea;
    }

    private static HBox createKeyboardRow(String[] keys) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        for (String key : keys) {
            Button keyButton = new Button(key);
            keyButton.getStyleClass().add("game-key");
            keyButton.setOnAction(e -> fillNextBox(key));
            row.getChildren().add(keyButton);
        }
        return row;
    }

    private static void fillNextBox(String letter) {
        for (Label box : guessBoxes) {
            if (box.getText().equals("_")) {
                box.setText(letter);
                break;
            }
        }
    }

    // ==============================
    // Game Logic
    // ==============================
    private static void checkWord(User user, int level, Stage stage) {
        if (currentWord.isEmpty()) return;

        StringBuilder guess = new StringBuilder();
        for (Label box : guessBoxes) guess.append(box.getText());

        if (guess.toString().equalsIgnoreCase(currentWord)) {
            hintLabel.setText("✅ Correct! The word was " + currentWord);
            topBarController.updateLevel(user.getCurrentLevel() + 1);
            new Thread(GameController::loadNewWord).start();
        } else {
            int currentHearts = user.getHearts();
            if (currentHearts > 0) {
                int newHearts = Math.max(0, currentHearts - 1);
                topBarController.updateHearts(newHearts);
                hintLabel.setText("❌ Wrong! -1 heart. Try again.");

                // ✅ If hearts reach 0 → open Heart mini-game
                if (newHearts == 0) {
                    openHeartMiniGame(user, level, stage);
                } else {
                    resetGuessBoxes();
                }
            }
        }
    }

    private static void openHeartMiniGame(User user, int level, Stage stage) {
        // open HeartController for refilling hearts
        Scene heartScene = HeartController.createScene(user, user.getHearts(), stage);

        // When mini-game closes (back button) → return to game with updated topbar
        stage.setOnCloseRequest(event -> {
            Scene gameScene = GameController.createScene(user, level);
            stage.setScene(gameScene);
        });
    }

    private static void resetGuessBoxes() {
        for (Label box : guessBoxes) {
            box.setText("_");
        }
    }

    private static void loadNewWord() {
        try {
            WordAPIService.WordData data = WordAPIService.fetchValidWord();
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
    }

    private static void showHint() {
        if (currentWord.isEmpty()) return;
        try {
            int currentHearts = currentUser.getHearts();
            if (currentHearts < 2) {
                hintLabel.setText("⚠️ Not enough hearts for a hint!");
                return;
            }

            String hint = WordAPIService.fetchHint(currentWord);
            int newHearts = Math.max(0, currentHearts - 2);
            topBarController.updateHearts(newHearts);
            hintLabel.setText("Hint: " + hint + " (-2 Hearts)");

            if (newHearts == 0) {
                openHeartMiniGame(currentUser, currentUser.getCurrentLevel(), currentStage);
            }
        } catch (Exception e) {
            hintLabel.setText("No hint available.");
        }
    }

    private static ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView(path);
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
