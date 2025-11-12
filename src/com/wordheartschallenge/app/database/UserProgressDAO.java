package com.wordheartschallenge.app.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProgressDAO {

    /** Update both heart points and current level */
    public static boolean updateProgress(int userId, int heartPoints, int currentLevel) {
        String sql = "UPDATE users SET heart_points = ?, current_level = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, heartPoints);
            stmt.setInt(2, currentLevel);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Update heart points only */
    public static boolean updateHearts(int userId, int heartPoints) {
        String sql = "UPDATE users SET heart_points = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, heartPoints);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Update level only */
    public static boolean updateLevel(int userId, int currentLevel) {
        String sql = "UPDATE users SET current_level = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentLevel);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Save completed level in history (only once) */
    public static void saveCompletedLevel(int userId, int level) {
        String query = """
            INSERT OR IGNORE INTO game_history (user_id, level, score)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, level);
            stmt.setInt(3, 0); // initial score 0
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Check if user has completed a level */
    public static boolean isLevelCompleted(int userId, int level) {
        String sql = "SELECT 1 FROM game_history WHERE user_id = ? AND level = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Get user's current hearts and level */
    public static int[] getProgress(int userId) {
        String sql = "SELECT heart_points, current_level FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new int[]{rs.getInt("heart_points"), rs.getInt("current_level")};
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}

