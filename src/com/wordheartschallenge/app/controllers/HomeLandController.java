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
        
        TopBarController topBar = new TopBarController(user, stage);
        root.setTop(topBar.getView());
        
        HomeLandUI ui = new HomeLandUI();
        root.setCenter(ui.getView());
        
        ui.getView().setId("game-tiles");
        
        new HomeLandLogic(user, stage, ui);
        
        user.setLastScreen("HomeLand");
        
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HomeLandController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        
        OnBoardingTour.checkAndStart(user, scene);
        
        return scene;
    }
}