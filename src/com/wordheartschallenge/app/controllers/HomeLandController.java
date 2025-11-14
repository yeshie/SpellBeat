package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.services.HomeLandLogic;
import com.wordheartschallenge.app.services.OnBoardingTour;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.HomeLandUI;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HomeLandController {
    
    public static Scene createScene(User user, Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root-hl");
        
        // Top Bar
        TopBarController topBar = new TopBarController(user, stage);
        root.setTop(topBar.getView());
        
        // UI
        HomeLandUI ui = new HomeLandUI();
        root.setCenter(ui.getView());
        
        // Set IDs for onboarding tour targeting
        ui.getView().setId("game-tiles");
        
        // Logic
        new HomeLandLogic(user, stage, ui);
        
        user.setLastScreen("HomeLand");
        
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HomeLandController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        
        // ✅ Start onboarding tour for new users (pass scene, not stage)
        OnBoardingTour.checkAndStart(user, scene);
        
        return scene;
    }
}