package com.wordheartschallenge.app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.wordheartschallenge.app.database.DBInitializer;
import com.wordheartschallenge.app.controllers.LoginController;
import com.wordheartschallenge.app.utils.SceneManager;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);

        // Initialize Database
        try {
            DBInitializer.init();
            System.out.println("✅ Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
        }

        // Set App Logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(logo);
        } catch (Exception e) {
            System.err.println("⚠️ Logo not found! Please check /resources/images/logo.png");
        }

        // Set Title and Scene
        primaryStage.setTitle("Word Hearts Challenge");
        primaryStage.setScene(LoginController.createScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
