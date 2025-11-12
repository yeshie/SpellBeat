package com.wordheartschallenge.app.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class RegisterUI {

    private HBox root;
    private VBox leftPanel;
    private VBox rightPanel;
    private VBox formCard;

    // Expose input fields and button to Logic/Controller
    private TextField emailField;
    private TextField nameField;
    private TextField ageField;
    private PasswordField passwordField;
    private PasswordField confirmField;
    private Button createBtn;
    private Label loginLink; 


    public RegisterUI() {
        buildUI();
    }

    private void buildUI() {
        root = new HBox();

        // --- Left Panel ---
        leftPanel = new VBox(10);
        leftPanel.getStyleClass().add("left-panel");
        leftPanel.setPrefWidth(500);
        leftPanel.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image(RegisterUI.class.getResourceAsStream("/images/logo.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        Label slogan1 = new Label("Dive into the rhythm of words.");
        slogan1.setFont(Font.font("Serif", 18));
        slogan1.setStyle("-fx-text-fill: white;");

        Label slogan2 = new Label("Every word you spell keeps your beat alive.");
        slogan2.setFont(Font.font("Serif", 18));
        slogan2.setStyle("-fx-text-fill: brown;");

        Label slogan3 = new Label("Start your story today!");
        slogan3.setFont(Font.font("Serif", 18));
        slogan3.setStyle("-fx-text-fill: brown;");

        leftPanel.getChildren().addAll(logo, slogan1, slogan2, slogan3);
        leftPanel.setPadding(new Insets(40, 20, 40, 20));

        // --- Right Panel ---
        rightPanel = new VBox(15);
        rightPanel.getStyleClass().add("right-panel");
        rightPanel.setPrefWidth(500);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        formCard = new VBox(15);
        formCard.getStyleClass().add("form-card");
        formCard.setAlignment(Pos.CENTER);

        Label title = new Label("Create an account");
        title.getStyleClass().add("label-title");

        Label subtitle = new Label("Join SpellBeat and keep your beat alive!");
        subtitle.getStyleClass().add("label-subtitle");

        emailField = new TextField();
        emailField.setPromptText("Email Address");

        nameField = new TextField();
        nameField.setPromptText("Full Name");

        ageField = new TextField();
        ageField.setPromptText("Age");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");

        createBtn = new Button("Create an account");
        createBtn.getStyleClass().add("primary-button");
        createBtn.setMaxWidth(Double.MAX_VALUE);

   
     // Login Link
        HBox loginBox = new HBox(5);
        loginBox.setAlignment(Pos.CENTER);
        Label alreadyLabel = new Label("Already have an account?");
        loginLink = new Label("Login"); // assign to the field
        loginLink.getStyleClass().add("link-label");
        loginBox.getChildren().addAll(alreadyLabel, loginLink);


        formCard.getChildren().addAll(
                title, subtitle, emailField, nameField, ageField,
                passwordField, confirmField, createBtn, loginBox
        );
        rightPanel.getChildren().add(formCard);

        root.getChildren().addAll(leftPanel, rightPanel);
    }

    public Scene getScene() {
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(RegisterUI.class.getResource("/css/style.css").toExternalForm());
        return scene;
    }

    // ===== Getters for Logic/Controller =====
    public TextField getEmailField() { return emailField; }
    public TextField getNameField() { return nameField; }
    public TextField getAgeField() { return ageField; }
    public PasswordField getPasswordField() { return passwordField; }
    public PasswordField getConfirmField() { return confirmField; }
    public Button getCreateBtn() { return createBtn; }
    public Label getLoginLink() { return loginLink; }
}

