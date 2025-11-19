package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.services.HeartLogic;
import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.ui.HeartUI;
import com.wordheartschallenge.app.database.UserProgressDAO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * ✅ ENHANCED: HeartController with seamless heart refill feature
 * - Supports callback-based return to game
 * - Maintains game state when returning
 * - Awards hearts on mini-game completion
 * - Real-time heart updates in TopBar
 */
public class HeartController {
    
    private static User currentUser;
    private static Stage currentStage;
    private static HeartLogic logic;
    private static TopBarController topBarController;
    
    // ✅ Refill mode support
    private static Runnable refillCallback = null;
    private static User refillUser = null;
    private static Stage refillStage = null;
    private static boolean isRefillMode = false;

    /**
     * ✅ ORIGINAL: Create scene for normal heart game access
     */
    public static Scene createScene(User user, int hearts, Stage stage) {
        currentUser = user;
        currentStage = stage;
        isRefillMode = false;
        
        BorderPane root = new BorderPane();
        root.getStyleClass().add("miniheart-root");

        topBarController = new TopBarController(user, stage);
        root.setTop(topBarController.getView());

        HeartUI ui = new HeartUI();
        logic = new HeartLogic(user, ui.getFeedbackLabel(), ui.getPuzzleView(), topBarController);
        logic.loadNewPuzzle();

        for (int i = 0; i <= 9; i++) {
            int answer = i;
            ui.getNumberButtons()[i].setOnAction(e -> {
                logic.checkAnswer(answer, () -> handleWinNormalMode());
            });
        }

        ui.getBackButton().setOnAction(e -> handleBackButton());
        ui.getNextButton().setOnAction(e -> logic.loadNewPuzzle());

        root.setCenter(ui.getCenterBox());

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HeartController.class.getResource("/css/style.css").toExternalForm());
        
        currentStage.setScene(scene);
        currentStage.show();

        return scene;
    }

    /**
     * ✅ NEW: Create scene for heart refill with callback
     */
    public static void createSceneForRefill(User user, Stage stage, Runnable onComplete) {
        refillUser = user;
        refillStage = stage;
        refillCallback = onComplete;
        isRefillMode = true;
        
        System.out.println("✅ Heart Mini-Game opened in REFILL MODE");
        
        BorderPane root = new BorderPane();
        root.getStyleClass().add("miniheart-root");

        topBarController = new TopBarController(user, stage);
        root.setTop(topBarController.getView());

        HeartUI ui = new HeartUI();
        logic = new HeartLogic(user, ui.getFeedbackLabel(), ui.getPuzzleView(), topBarController);
        logic.loadNewPuzzle();

        for (int i = 0; i <= 9; i++) {
            int answer = i;
            ui.getNumberButtons()[i].setOnAction(e -> {
                logic.checkAnswer(answer, () -> handleWinRefillMode());
            });
        }

        ui.getBackButton().setOnAction(e -> handleLoseOrBack());
        ui.getNextButton().setOnAction(e -> logic.loadNewPuzzle());

        root.setCenter(ui.getCenterBox());

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HeartController.class.getResource("/css/style.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * ✅ Handle win in NORMAL mode
     */
    private static void handleWinNormalMode() {
        Platform.runLater(() -> {
            int newHearts = Math.min(currentUser.getHearts() + 2, 10);
            currentUser.setHearts(newHearts);
            UserProgressDAO.updateHearts(currentUser.getId(), newHearts);
            
            // ✅ Update TopBar immediately
            topBarController.updateHearts(newHearts);
            
            System.out.println("✅ Normal mode win! New hearts: " + newHearts);
            
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> {
                currentStage.setScene(HomeLandController.createScene(currentUser, currentStage));
            });
            pause.play();
        });
    }

    /**
     * ✅ Handle win in REFILL mode
     */
    private static void handleWinRefillMode() {
        if (refillUser != null) {
            int newHearts = Math.min(refillUser.getHearts() + 2, 10);
            refillUser.setHearts(newHearts);
            UserProgressDAO.updateHearts(refillUser.getId(), newHearts);
            
            // ✅ Update TopBar immediately in Heart Game
            topBarController.updateHearts(newHearts);
            
            System.out.println("✅ Refill mode win! New hearts: " + newHearts);
            
            if (refillCallback != null) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> {
                    Runnable callback = refillCallback;
                    
                    refillCallback = null;
                    refillUser = null;
                    refillStage = null;
                    isRefillMode = false;
                    
                    callback.run();
                });
                pause.play();
            }
        }
    }

    /**
     * ✅ Handle back button or lose in refill mode
     */
    private static void handleLoseOrBack() {
        if (isRefillMode && refillCallback != null) {
            System.out.println("⚠️ User left heart game without winning");
            
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                Runnable callback = refillCallback;
                
                refillCallback = null;
                refillUser = null;
                refillStage = null;
                isRefillMode = false;
                
                callback.run();
            });
            pause.play();
        } else {
            handleBackButton();
        }
    }

    /**
     * ✅ Normal back button handler
     */
    private static void handleBackButton() {
        if (currentUser.getLastScreen() != null && currentUser.getLastScreen().equals("Game")) {
            currentStage.setScene(
                GameController.createScene(currentUser, currentUser.getCurrentLevel(), currentStage)
            );
        } else {
            currentStage.setScene(HomeLandController.createScene(currentUser, currentStage));
        }
    }

    public static boolean isRefillMode() {
        return isRefillMode;
    }
}