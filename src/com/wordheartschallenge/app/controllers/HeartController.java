package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.services.HeartLogic;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.HeartUI;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HeartController {

    private static User currentUser;
    private static Stage currentStage;
    private static HeartLogic logic;
    private static TopBarController topBarController;

    public static Scene createScene(User user, int hearts, Stage stage) {
        currentUser = user;
        currentStage = stage;

        BorderPane root = new BorderPane();
        root.getStyleClass().add("miniheart-root");

        topBarController = new TopBarController(user, stage);
        root.setTop(topBarController.getView());

        HeartUI ui = new HeartUI();
        logic = new HeartLogic(user, ui.getFeedbackLabel(), ui.getPuzzleView());
        logic.loadNewPuzzle();

        // Number buttons
        for (int i = 0; i <= 9; i++) {
            int answer = i;
            ui.getNumberButtons()[i].setOnAction(e -> logic.checkAnswer(answer, () -> {
                Platform.runLater(() -> {
                    currentStage.setScene(HomeLandController.createScene(currentUser, currentStage));
                });
            }));
        }

        ui.getBackButton().setOnAction(e -> {
            if (currentUser.getLastScreen().equals("game")) {
                currentStage.setScene(GameController.createScene(currentUser, currentUser.getCurrentLevel(),currentStage));
            } else {
                currentStage.setScene(HomeLandController.createScene(currentUser, currentStage));
            }
        });

        ui.getNextButton().setOnAction(e -> logic.loadNewPuzzle());

        root.setCenter(ui.getCenterBox());
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HeartController.class.getResource("/css/style.css").toExternalForm());

        currentStage.setScene(scene);
        currentStage.show();
        return scene;
    }
}
