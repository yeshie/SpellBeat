package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.InputStream;

public class HeartController {

    public static Scene createScene(User user, int hearts, Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("miniheart-root");

        root.setTop(createTopBar(user, hearts));
        root.setCenter(createGameArea(stage));

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(HeartController.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        return scene;
    }

    private static HBox createTopBar(User user, int hearts) {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("miniheart-top-bar");

        // Left: Avatar + Home Button
        ImageView avatar = createIconView(user.getAvatarPath(), 34, 34);
        Label name = new Label(user.getName());
        name.getStyleClass().add("miniheart-chip-label");

        HBox avatarChip = new HBox(avatar, name);
        avatarChip.getStyleClass().add("miniheart-chip");

        ImageView homeIcon = createIconView("/images/home.png", 32, 32);
        Button homeButton = new Button("", homeIcon);
        homeButton.getStyleClass().add("home-button");

        HBox leftBox = new HBox(12, avatarChip, homeButton);
        leftBox.getStyleClass().add("miniheart-top-left");

        // Center Title
        Label title = new Label("How Many Hearts Can You Find");
        title.getStyleClass().add("miniheart-title");

        // Right: Heart Counter
        ImageView heartIcon = createIconView("/images/heartLogo.png", 28, 28);
        Label heartLabel = new Label(String.valueOf(hearts));
        heartLabel.getStyleClass().add("miniheart-chip-label");

        HBox heartChip = new HBox(heartIcon, heartLabel);
        heartChip.getStyleClass().add("miniheart-heart-chip");

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        topBar.getChildren().addAll(leftBox, spacerLeft, title, spacerRight, heartChip);
        return topBar;
    }

    private static VBox createGameArea(Stage stage) {
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        VBox heartGameBox = new VBox();
        heartGameBox.getStyleClass().add("miniheart-game-box");

        HBox numbersBox = new HBox(10);
        numbersBox.setAlignment(Pos.CENTER);
        for (int i = 0; i <= 9; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.getStyleClass().add("miniheart-number-btn");
            numbersBox.getChildren().add(numBtn);
        }

        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("miniheart-nav-btn");
        Button nextBtn = new Button("Next");
        nextBtn.getStyleClass().add("miniheart-nav-btn");

        navBox.getChildren().addAll(backBtn, nextBtn);
        centerBox.getChildren().addAll(heartGameBox, numbersBox, navBox);
        return centerBox;
    }

    private static ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView();
        try (InputStream stream = HeartController.class.getResourceAsStream(path)) {
            if (stream != null) icon.setImage(new Image(stream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
