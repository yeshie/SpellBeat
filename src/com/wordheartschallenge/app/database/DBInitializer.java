package com.wordheartschallenge.app.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {

    public static void init() {
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement()) {
            String users = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    email TEXT UNIQUE,
                    age INTEGER,
                    avatar_path TEXT,
                    heart_points INTEGER DEFAULT 10,
                    current_level INTEGER DEFAULT 1
                );
            """;

            String historyTable = """
                CREATE TABLE IF NOT EXISTS game_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    level INTEGER,
                    score INTEGER,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                );
            """;

            stmt.execute(users);
            stmt.execute(historyTable);

            System.out.println("✅ Database initialized successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
