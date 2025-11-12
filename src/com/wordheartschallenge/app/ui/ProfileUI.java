package com.wordheartschallenge.app.ui;

import com.wordheartschallenge.app.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.InputStream;

public class ProfileUI {

    private VBox card;
    private Scene scene;
    private TextField usernameField, emailField;
    private PasswordField passwordField, confirmField;
    private ToggleGroup avatarGroup;
    private Button saveBtn;
    private Label messageLabel;

    public ProfileUI(User user) {
        buildUI(user);
    }

    private void buildUI(User user) {
        card = new VBox(18);
        card.getStyleClass().add("profile-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setMaxWidth(500);
        card.setMaxHeight(520);

        Label title = new Label("Profile Settings");
        title.getStyleClass().add("profile-title");

        usernameField = new TextField(user.getName());
        usernameField.setPromptText("Enter new username");
        usernameField.getStyleClass().add("profile-textfield");

        emailField = new TextField(user.getEmail());
        emailField.setPromptText("Enter new email");
        emailField.getStyleClass().add("profile-textfield");

        passwordField = new PasswordField();
        passwordField.setPromptText("New password");
        passwordField.getStyleClass().add("profile-textfield");

        confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");
        confirmField.getStyleClass().add("profile-textfield");

        Label avatarLabel = new Label("Choose Your Avatar");
        avatarLabel.getStyleClass().add("profile-label");
        avatarLabel.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(avatarLabel, new Insets(0,0,0,10));
        avatarLabel.setAlignment(Pos.CENTER_LEFT);

        HBox avatarBox = new HBox(15);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setPadding(new Insets(0,0,10,0));

        String[] avatarPaths = {
                "/images/avatar1.png",
                "/images/avatar2.png",
                "/images/avatar3.png",
                "/images/avatar4.png"
        };

        avatarGroup = new ToggleGroup();

        for (String path : avatarPaths) {
            try (InputStream stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    ImageView imgView = new ImageView(new Image(stream));
                    imgView.setFitWidth(70);
                    imgView.setFitHeight(70);
                    imgView.setPreserveRatio(true);

                    ToggleButton avatarButton = new ToggleButton();
                    avatarButton.setGraphic(imgView);
                    avatarButton.setUserData(path);
                    avatarButton.setToggleGroup(avatarGroup);
                    avatarButton.getStyleClass().add("avatar-button");

                    if (path.endsWith(user.getAvatarPath())) avatarButton.setSelected(true);

                    avatarBox.getChildren().add(avatarButton);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("profile-button");

        messageLabel = new Label();
        messageLabel.getStyleClass().add("profile-message");

        card.getChildren().addAll(
                title,
                usernameField, emailField,
                passwordField, confirmField,
                avatarLabel, avatarBox,
                saveBtn, messageLabel
        );

        StackPane centerPane = new StackPane(card);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(new Insets(40,20,40,20));

        scene = new Scene(centerPane, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    }

    public Scene getScene() { return scene; }

    public Button getSaveBtn() { return saveBtn; }
    public TextField getUsernameField() { return usernameField; }
    public TextField getEmailField() { return emailField; }
    public PasswordField getPasswordField() { return passwordField; }
    public PasswordField getConfirmField() { return confirmField; }
    public ToggleGroup getAvatarGroup() { return avatarGroup; }
    public Label getMessageLabel() { return messageLabel; }
    public VBox getCard() {
        return card;
    }

}
