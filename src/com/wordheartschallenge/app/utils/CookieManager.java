package com.wordheartschallenge.app.utils;

import java.util.prefs.Preferences;

public class CookieManager {
    private static final String PREF_NODE = "com.wordheartschallenge.app";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    public static void saveCredentials(String email, String encryptedPassword) {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.put(EMAIL_KEY, email);
        prefs.put(PASSWORD_KEY, encryptedPassword);
    }

    public static String getEmail() {
        return Preferences.userRoot().node(PREF_NODE).get(EMAIL_KEY, null);
    }

    public static String getEncryptedPassword() {
        return Preferences.userRoot().node(PREF_NODE).get(PASSWORD_KEY, null);
    }

    public static void clearCredentials() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.remove(EMAIL_KEY);
        prefs.remove(PASSWORD_KEY);
    }
}
