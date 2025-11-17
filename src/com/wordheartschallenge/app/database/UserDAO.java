package com.wordheartschallenge.app.database;

import com.wordheartschallenge.app.models.User;
import java.sql.*;

public class UserDAO {
    
    public static boolean insertUser(User user) {
        String sql = """
            INSERT INTO users (username, email, password, age, heart_points, current_level, tutorial_completed)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getAge());
            stmt.setInt(5, user.getHearts());
            stmt.setInt(6, user.getCurrentLevel());
            stmt.setBoolean(7, false); 
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    user.setTutorialCompleted(false); 
                }
                System.out.println("✅ New user created: " + user.getName() + " | Tutorial Completed: FALSE");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET username = ?, avatar_path = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAvatarPath());
            stmt.setInt(3, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static User getUserByCredentials(String username, String password) {
        String sql = "SELECT id, username, email, heart_points, current_level, avatar_path, tutorial_completed FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setHearts(rs.getInt("heart_points"));
                user.setCurrentLevel(rs.getInt("current_level"));
                user.setAvatarPath(rs.getString("avatar_path")); 
                
                Object tutorialObj = rs.getObject("tutorial_completed");
                if (tutorialObj != null) {
                    user.setTutorialCompleted(rs.getBoolean("tutorial_completed"));
                } else {
                    user.setTutorialCompleted(false); 
                }
                
                System.out.println("✅ User loaded: " + user.getName() + " | Tutorial Completed: " + user.isTutorialCompleted());
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean updateUserTutorial(User user) {
        String sql = "UPDATE users SET tutorial_completed = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, user.isTutorialCompleted());
            stmt.setInt(2, user.getId());
            
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Tutorial status updated: " + user.getName() + " = " + user.isTutorialCompleted());
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}