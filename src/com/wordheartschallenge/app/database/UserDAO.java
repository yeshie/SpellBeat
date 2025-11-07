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
                    user.setId(rs.getInt(1)); // store the new ID for later
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** ✅ Update username + avatar (on Player Profile) */
    public static boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET username = ?, avatar_path = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAvatarPath());
            stmt.setInt(3, user.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
