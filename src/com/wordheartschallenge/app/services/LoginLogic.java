package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.models.User;
import com.wordheartschallenge.app.utils.PasswordUtils;

import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;

import java.io.*;

public class LoginLogic {

    private static final String REMEMBER_FILE = "remember_me.dat";

    public static void saveRememberedUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REMEMBER_FILE))) {
            writer.write(username + "\n");
            writer.write(PasswordUtils.encodePassword(password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearRememberedUser() {
        File file = new File(REMEMBER_FILE);
        if (file.exists()) file.delete();
    }

    public static void loadRememberedUser(TextField userField, PasswordField passField, CheckBox rememberMe) {
        File file = new File(REMEMBER_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String username = reader.readLine();
            String encodedPassword = reader.readLine();

            if (username != null && encodedPassword != null) {
                userField.setText(username);
                passField.setText(PasswordUtils.decodePassword(encodedPassword));
                rememberMe.setSelected(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
