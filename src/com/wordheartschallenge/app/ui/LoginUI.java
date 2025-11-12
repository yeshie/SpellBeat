package com.wordheartschallenge.app.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class LoginUI {

    private HBox root;
    private VBox leftPanel;
    private VBox rightPanel;
    private VBox formCard;

    private TextField userField;
    private PasswordField passField;
    private CheckBox rememberMe;
    private Button loginButton;
    private Label createAccountLink;

    public LoginUI() {
        buildUI();
    }

    private void buildUI() {
        root = new HBox();

        // --- Left Panel ---
        leftPanel = new VBox();
        leftPanel.getStyleClass().add("left-panel");
        leftPanel.setPrefWidth(500);
        leftPanel.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image(LoginUI.class.getResourceAsStream("/images/logo.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);
        leftPanel.getChildren().add(logo);

        // --- Right Panel ---
        rightPanel = new VBox(15);
        rightPanel.getStyleClass().add("right-panel");
        rightPanel.setPrefWidth(500);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        formCard = new VBox(15);
        formCard.getStyleClass().add("form-card");
        formCard.setAlignment(Pos.CENTER);

        Label title = new Label("Start your journey");
        title.getStyleClass().add("label-title");

        Label subtitle = new Label("Every word you spell keeps your beat alive!");
        subtitle.getStyleClass().add("label-subtitle");

        userField = new TextField();
        userField.setPromptText("Username");
        userField.getStyleClass().add("text-field");

        passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("password-field");

        rememberMe = new CheckBox("Remember Me");
        rememberMe.setStyle("-fx-font-size: 14px; -fx-font-family: 'Serif'; -fx-text-fill: #59504B;");

        loginButton = new Button("Login");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        HBox registrationBox = new HBox(5);
        registrationBox.setAlignment(Pos.CENTER);
        Label notRegisteredLabel = new Label("Not Registered Yet?");
        createAccountLink = new Label("Create an account");
        createAccountLink.getStyleClass().add("link-label");
        registrationBox.getChildren().addAll(notRegisteredLabel, createAccountLink);

        formCard.getChildren().addAll(title, subtitle, userField, passField, rememberMe, loginButton, registrationBox);
        rightPanel.getChildren().add(formCard);
        root.getChildren().addAll(leftPanel, rightPanel);
    }

    public Scene getScene() {
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(LoginUI.class.getResource("/css/style.css").toExternalForm());
        return scene;
    }

    // ===== Getters for Controller =====
    public TextField getUserField() { return userField; }
    public PasswordField getPassField() { return passField; }
    public CheckBox getRememberMe() { return rememberMe; }
    public Button getLoginButton() { return loginButton; }
    public Label getCreateAccountLink() { return createAccountLink; }
}
