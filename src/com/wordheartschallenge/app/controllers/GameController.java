package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.WordAPIService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.InputStream;
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
    private static int hearts = 10;

    public static Scene createScene(User user, int level) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");

        Stage stage = new Stage();
        root.setTop(createTopBar(user, level, hearts, stage));
        root.setCenter(createGameArea());
        root.setBottom(createKeyboardArea(user, level, stage));

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(GameController.class.getResource("/css/style.css").toExternalForm());

        new Thread(GameController::loadNewWord).start();
        return scene;
    }

    // ================= TOP BAR =================
    private static Node createTopBar(User user, int level, int hearts, Stage stage) {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("home-top-bar");
        topBar.setAlignment(Pos.CENTER);

        HBox avatarChip = createInfoChip(user.getAvatarPath(), user.getName(), true);
        Button homeButton = createIconButton("/images/home.png");
        homeButton.setOnAction(e -> {
            Scene homeScene = HomeController.createScene(user, level, hearts, stage);
            stage.setScene(homeScene);
        });

        HBox leftBox = new HBox(12, avatarChip, homeButton);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = createIconView("/images/logo.png", 220, 180);
        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER);

        HBox heartsChip = createInfoChip("/images/heartLogo.png", String.valueOf(hearts), false);
        heartsChip.setOnMouseClicked(event -> {
            Scene heartScene = HeartController.createScene(user, level, stage);
            stage.setScene(heartScene);
        });

        HBox levelChip = createInfoChip("/images/level.png", "Level " + level, true);
        HBox rightBox = new HBox(12, heartsChip, levelChip);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        topBar.getChildren().addAll(leftBox, spacerLeft, logoBox, spacerRight, rightBox);
        return topBar;
    }

    private static HBox createInfoChip(String iconPath, String text, boolean wide) {
        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER);
        chip.getStyleClass().add("home-chip");
        if (wide) chip.getStyleClass().add("wide");

        ImageView icon = createIconView(iconPath, 34, 34);
        Label label = new Label(text);
        label.getStyleClass().add("home-chip-label");

        chip.getChildren().addAll(icon, label);
        return chip;
    }

    private static Button createIconButton(String iconPath) {
        ImageView icon = createIconView(iconPath, 34, 34);
        Button btn = new Button("", icon);
        btn.getStyleClass().add("home-button");
        return btn;
    }

    private static ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = GameController.class.getResourceAsStream(path)) {
            if (stream != null) icon.setImage(new Image(stream));
            else System.err.println("Icon not found: " + path);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
        }
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }

    //  Game area 
    private static Node createGameArea() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("game-center-card");

        definitionLabel = new Label("Loading definition...");
        definitionLabel.getStyleClass().add("definition-label");

        guessBoxContainer = new HBox(15);
        guessBoxContainer.setAlignment(Pos.CENTER);

        hintLabel = new Label("");
        hintLabel.getStyleClass().add("hint-label");

        Button hintButton = new Button("Hint (-2 Hearts)");
        hintButton.setGraphic(createIconView("/images/hint.png", 20, 20));
        hintButton.getStyleClass().add("game-hint-button");
        hintButton.setOnAction(e -> showHint());

        container.getChildren().addAll(definitionLabel, guessBoxContainer, hintButton, hintLabel);
        return container;
    }

    // Keyboard area
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

    // Game logic
    private static void checkWord(User user, int level, Stage stage) {
        if (currentWord.isEmpty()) return;

        StringBuilder guess = new StringBuilder();
        for (Label box : guessBoxes) guess.append(box.getText());

        if (guess.toString().equalsIgnoreCase(currentWord)) {
            hintLabel.setText("✅ Correct! The word was " + currentWord);
            System.out.println("✅ User guessed correctly: " + currentWord);
            new Thread(GameController::loadNewWord).start();
        } else {
            hearts--;
            hintLabel.setText("❌ Wrong! -1 heart. Try again.");
            System.out.println("❌ Wrong guess: " + guess + " | Correct word: " + currentWord);
            updateHeartsChip(stage, user, level);

            if (hearts <= 0) {
                Scene heartScene = HeartController.createScene(user, level, stage);
                stage.setScene(heartScene);
            } else {
                resetGuessBoxes();
            }
        }
    }

    private static void resetGuessBoxes() {
        for (Label box : guessBoxes) {
            box.setText("_");
        }
    }

    private static void updateHeartsChip(Stage stage, User user, int level) {
        Scene currentScene = stage.getScene();
        if (currentScene != null) {
            HBox topBar = (HBox) ((BorderPane) currentScene.getRoot()).getTop();
            HBox rightBox = (HBox) topBar.getChildren().get(topBar.getChildren().size() - 1);
            HBox heartChip = (HBox) rightBox.getChildren().get(0);
            Label label = (Label) heartChip.getChildren().get(1);
            label.setText(String.valueOf(hearts));
        }
    }

    private static void loadNewWord() {
        try {
            WordAPIService.WordData data = WordAPIService.fetchValidWord();
            currentWord = data.word.toUpperCase();
            currentDefinition = data.definition;

            System.out.println("=== NEW WORD LOADED ===");
            System.out.println("Word: " + currentWord);
            System.out.println("Definition: " + currentDefinition);

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
            String hint = WordAPIService.fetchHint(currentWord);
            hearts -= 2;
            System.out.println("Hint fetched for " + currentWord + ": " + hint);
            hintLabel.setText("Hint: " + hint + " (-2 Hearts)");
        } catch (Exception e) {
            hintLabel.setText("No hint available.");
        }
    }
}
