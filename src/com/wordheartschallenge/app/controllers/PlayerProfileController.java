package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.services.PlayerProfileLogic;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.PlayerProfileUI;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlayerProfileController {
    
    private Scene scene;

    public static Scene createScene(User user) {
        PlayerProfileController ctrl = new PlayerProfileController();
        return ctrl.buildScene(user);
    }

    private Scene buildScene(User user) {
        PlayerProfileUI ui = new PlayerProfileUI(user);
        PlayerProfileLogic logic = new PlayerProfileLogic(user, ui);
        
        // Set the "Let's Play" button action
        ui.setOnLetsPlay(() -> logic.handleLetsPlay());
        
        this.scene = ui.getScene();
        return scene;
    }

    public Scene getScene() {
        return scene;
    }
}