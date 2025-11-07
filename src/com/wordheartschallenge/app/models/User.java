package com.wordheartschallenge.app.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private int age;
    private String avatarPath; // added for profile image
    private int currentLevel;
    private int hearts;
    private String lastScreen; 

    public User() {}

    public User(String name, String email, String password, int age, String avatarPath, int currentLevel, int hearts) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.avatarPath = avatarPath;
        this.currentLevel = currentLevel;
        this.hearts = hearts;
        this.lastScreen=lastScreen;
    }


    // Getter
    public String getLastScreen() {
        return lastScreen;
    }

    // Setter
    public void setLastScreen(String lastScreen) {
        this.lastScreen = lastScreen;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public int getCurrentLevel() { return currentLevel; }
    public int getHearts() { return hearts; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    public void setHearts(int hearts) { this.hearts = hearts; }
}
