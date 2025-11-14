package com.wordheartschallenge.app.utils;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AlertUtil {

    /**
     * Shows a custom themed alert popup
     * @param title The playful title (e.g., "Oops! Try Again 😅")
     * @param message The error description
     */
    public static void showCustomAlert(String title, String message) {
        Stage alertStage = new Stage();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.initModality(Modality.APPLICATION_MODAL);

        // Main container
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        // Alert box
        VBox alertBox = new VBox(15);
        alertBox.setAlignment(Pos.CENTER);
        alertBox.getStyleClass().add("custom-alert-box");
        alertBox.setPrefWidth(400);
        alertBox.setMaxWidth(400);

        // Logo at top
        try {
            ImageView logo = new ImageView(new Image(
                AlertUtil.class.getResourceAsStream("/images/logo.png")
            ));
            logo.setFitWidth(120);
            logo.setPreserveRatio(true);
            alertBox.getChildren().add(logo);
        } catch (Exception e) {
            // If logo fails to load, continue without it
            System.err.println("Logo not found for alert");
        }

        // Title label
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("alert-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(350);

        // Message label
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("alert-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);

        // OK Button
        Button okButton = new Button("OK");
        okButton.getStyleClass().add("alert-button");
        okButton.setOnAction(e -> {
            // Fade out animation before closing
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> alertStage.close());
            fadeOut.play();
        });

        alertBox.getChildren().addAll(titleLabel, messageLabel, okButton);
        root.getChildren().add(alertBox);

        Scene scene = new Scene(root, 450, 400);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(AlertUtil.class.getResource("/css/style.css").toExternalForm());

        alertStage.setScene(scene);

        // Scale-in animation when appearing
        root.setScaleX(0);
        root.setScaleY(0);
        root.setOpacity(0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), root);
        scaleIn.setFromX(0.5);
        scaleIn.setFromY(0.5);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition animation = new ParallelTransition(scaleIn, fadeIn);
        
        alertStage.show();
        animation.play();
    }

    // Convenience methods for common alerts
    public static void showEmailError() {
        showCustomAlert(
            "Oops! Try Again 😅",
            "Please enter a valid email address.\nExample: name@gmail.com"
        );
    }

    public static void showNameError() {
        showCustomAlert(
            "Hold on! ✨",
            "Name can contain letters only.\nNo numbers or symbols, please!"
        );
    }

    public static void showAgeError() {
        showCustomAlert(
            "Age Check! 🎈",
            "Age must be exactly two digits (10–99).\nPlease enter a valid age!"
        );
    }

    public static void showPasswordError() {
        showCustomAlert(
            "Password Too Short! 🔒",
            "Password must be at least 4 characters long.\nMake it a bit stronger!"
        );
    }

    public static void showPasswordMismatchError() {
        showCustomAlert(
            "Passwords Don't Match! 🔒",
            "The passwords you entered don't match.\nPlease try again!"
        );
    }

    public static void showEmptyFieldsError() {
        showCustomAlert(
            "Missing Information! 📝",
            "Please fill in all fields.\nEvery detail matters!"
        );
    }

    public static void showAccountExistsError() {
        showCustomAlert(
            "Account Already Exists! 💫",
            "This email is already registered.\nTry logging in instead!"
        );
    }
}