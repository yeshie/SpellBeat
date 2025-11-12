package com.wordheartschallenge.app.utils;

import javafx.scene.Scene;

import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;
   

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(Scene newScene) {
        if (primaryStage != null) {
            primaryStage.setScene(newScene);
            primaryStage.show();
        } else {
            System.err.println("Error: Primary stage is not set!");
        }
    }
}
