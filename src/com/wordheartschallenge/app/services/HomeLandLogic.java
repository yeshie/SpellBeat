package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.controllers.HomeController;
import com.wordheartschallenge.app.controllers.HeartController;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.HomeLandUI;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeLandLogic {

    private final User user;
    private final Stage stage;
    private final HomeLandUI ui;

    public HomeLandLogic(User user, Stage stage, HomeLandUI ui) {
        this.user = user;
        this.stage = stage;
        this.ui = ui;
        setupActions();
    }

    private void setupActions() {
        ui.getWordGameButton().setOnAction(e -> {
            Scene homeScene = HomeController.createScene(user, 1, user.getHearts(), stage);
            stage.setScene(homeScene);
        });

        ui.getHeartGameButton().setOnAction(e -> {
            Scene heartScene = HeartController.createScene(user, user.getHearts(), stage);
            stage.setScene(heartScene);
        });
    }
}
