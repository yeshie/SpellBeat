package com.wordheartschallenge.app.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

public class GameUI {

    private BorderPane root;
    private VBox keyboardArea;
    private VBox gameArea;

    public GameUI() {
        root = new BorderPane();
        root.getStyleClass().add("game-root");
    }

    public void setTop(Node node) {
        root.setTop(node);
    }

    public void setCenter(Node node) {
        root.setCenter(node);
    }

    public void setBottom(Node node) {
        root.setBottom(node);
    }

    public Scene getScene() {
        Scene scene = new Scene(root,1000, 650);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        return scene;
    }

    // Game Area
    public Node createGameArea(Label definitionLabel, HBox guessBoxContainer, Label hintLabel, Button hintButton) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20, 20, 0, 20)); // Top, Right, Bottom, Left (bottom reduced)
        container.getStyleClass().add("game-center-card");

        VBox definitionBox = new VBox();
        definitionBox.setAlignment(Pos.CENTER);
        definitionBox.getStyleClass().add("definition-box");
        definitionBox.getChildren().add(definitionLabel);
        definitionBox.setPrefWidth(650);
    

        guessBoxContainer.setAlignment(Pos.CENTER);

        container.getChildren().addAll(definitionBox, guessBoxContainer, hintButton, hintLabel);
        return container;
    }

    // Keyboard Area
    public Node createKeyboardArea(List<Button> row1, List<Button> row2, Button submitButton,
            Button backspaceButton, Button refreshButton) {
        VBox keyboardArea = new VBox(20);
        keyboardArea.setAlignment(Pos.CENTER);
        keyboardArea.setPadding(new Insets(10, 20, 30, 20));
        keyboardArea.getStyleClass().add("keyboard-area");

        HBox rowBox1 = new HBox(10);
        rowBox1.setAlignment(Pos.CENTER);
        rowBox1.getChildren().addAll(row1);

        HBox rowBox2 = new HBox(10);
        rowBox2.setAlignment(Pos.CENTER);
        rowBox2.getChildren().addAll(row2);

        HBox submitRow = new HBox(15);
        submitRow.setAlignment(Pos.CENTER);
        submitRow.getChildren().addAll(backspaceButton, submitButton, refreshButton);

        keyboardArea.getChildren().addAll(rowBox1, rowBox2, submitRow);
        return keyboardArea;
    }

    public ImageView createIconView(String path, double width, double height) {
        ImageView icon = new ImageView(path);
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }
}
