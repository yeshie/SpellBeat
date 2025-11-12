package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.controllers.GameController;
import com.wordheartschallenge.app.models.User;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeLogic {

    // Setup play button action for current level
    public static void setupPlayButton(Button playButton, User user, int levelNum, Stage stage) {
        playButton.setOnAction(e -> {
            stage.setScene(GameController.createScene(user, levelNum,stage));
        });
    }

    // Determine starting level for user
    public static int getStartLevel(User user) {
        return (user.getCurrentLevel() > 0) ? user.getCurrentLevel() : 1;
    }

    // Determine starting hearts for user
    public static int getStartHearts(User user) {
        return (user.getHearts() > 0) ? user.getHearts() : 10;
    }
}
