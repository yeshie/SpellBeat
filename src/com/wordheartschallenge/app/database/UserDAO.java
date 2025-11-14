package com.wordheartschallenge.app.database;

import com.wordheartschallenge.app.models.User;
import java.sql.*;

public class UserDAO {

    /** ✅ Insert new user (on Register) */
    public static boolean insertUser(User user) {
        String sql = """
            INSERT INTO users (username, email, password, age, heart_points, current_level)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getAge());
            stmt.setInt(5, user.getHearts());
            stmt.setInt(6, user.getCurrentLevel());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** ✅ Update username + avatar (on Player Profile) */
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

    /** ✅ Get user by username + password (for login) */
    public static User getUserByCredentials(String username, String password) {
        String sql = "SELECT id, username, email, heart_points, current_level FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 👇 Only load what’s needed after login
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setHearts(rs.getInt("heart_points"));
                user.setAvatarPath(rs.getString("avatar_path")); // ✅ add this
                user.setCurrentLevel(rs.getInt("current_level"));
                user.setTutorialCompleted(rs.getBoolean("tutorial_completed"));
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
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
