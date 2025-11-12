package com.wordheartschallenge.app.services;

import com.wordheartschallenge.app.database.DBConnection;
import com.wordheartschallenge.app.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class AuthService {

    // ===== Utility: Encrypt password (SHA-256) =====
    private static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    // ===== Register new user (with encrypted password + email) =====
    public static boolean register(String username, String email, String password, int age, String avatarPath) {
        String sql = "INSERT INTO users (username, email, password, age, avatar_path, heart_points, current_level) " +
                     "VALUES (?, ?, ?, ?, ?, 10, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, encryptPassword(password)); // 🔒 store encrypted password
            stmt.setInt(4, age);
            stmt.setString(5, avatarPath);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("❌ Registration Error: " + e.getMessage());
            return false;
        }
    }

    // ===== Login existing user (compare encrypted passwords) =====
    public static User login(String username, String password) {
        String encryptedPassword = encryptPassword(password); // 🔒 encrypt before checking
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, encryptedPassword);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAge(rs.getInt("age"));
                user.setAvatarPath(rs.getString("avatar_path"));
                user.setHearts(rs.getInt("heart_points"));
                user.setCurrentLevel(rs.getInt("current_level"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("❌ Login Error: " + e.getMessage());
        }
        return null;
    }

    // ===== Update heart points dynamically =====
    public static void updateHeartPoints(int userId, int newHearts) {
        String sql = "UPDATE users SET heart_points = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newHearts);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error updating heart points: " + e.getMessage());
        }
    }

    // ===== Update user level =====
    public static void updateCurrentLevel(int userId, int newLevel) {
        String sql = "UPDATE users SET current_level = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newLevel);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error updating level: " + e.getMessage());
        }
    }
    
 // ===== Update username =====
    public static boolean updateUsername(int userId, String newName) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating username: " + e.getMessage());
            return false;
        }
    }

    // ===== Update password =====
    public static boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, encryptPassword(newPassword));
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating password: " + e.getMessage());
            return false;
        }
    }

    // ===== Update avatar =====
    public static boolean updateAvatar(int userId, String avatarPath) {
        String sql = "UPDATE users SET avatar_path = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avatarPath);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating avatar: " + e.getMessage());
            return false;
        }
    }

}
