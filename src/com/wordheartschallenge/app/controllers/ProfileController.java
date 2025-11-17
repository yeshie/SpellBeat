package com.wordheartschallenge.app.controllers;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.services.ProfileLogic;
import com.wordheartschallenge.app.ui.ProfileUI;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ProfileController {

    public static Scene createScene(User user, Stage stage, TopBarController topBarController) {

        Node topBar = topBarController.getView();

        BorderPane root = new BorderPane();
        root.setTop(topBar);

        ProfileUI ui = new ProfileUI(user);
        root.setCenter(ui.getCard());

        ProfileLogic logic = new ProfileLogic(user, topBarController);

        ui.getSaveBtn().setOnAction(e -> {
            String selectedAvatar = ui.getAvatarGroup().getSelectedToggle() != null ?
                    ui.getAvatarGroup().getSelectedToggle().getUserData().toString() :
                    user.getAvatarPath();

            String msg = logic.updateProfile(
                    ui.getUsernameField().getText(),
                    ui.getEmailField().getText(),
                    selectedAvatar,
                    ui.getPasswordField().getText(),
                    ui.getConfirmField().getText()
            );

            ui.getMessageLabel().setText(msg);
            ui.getMessageLabel().setStyle(msg.contains("successfully") ?
                    "-fx-text-fill: green;" : "-fx-text-fill: red;");
        });

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(ProfileUI.class.getResource("/css/style.css").toExternalForm());
        return scene;
    }
}
