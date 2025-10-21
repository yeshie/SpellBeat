package com.wordheartschallenge.app.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {

    public static void init() {
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // User table
            String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                               "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                               "username TEXT UNIQUE NOT NULL," +
                               "password TEXT NOT NULL" +
                               ");";

            // GameHistory table
            String historyTable = "CREATE TABLE IF NOT EXISTS game_history (" +
                                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                  "user_id INTEGER," +
                                  "level INTEGER," +
                                  "score INTEGER," +
                                  "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                  "FOREIGN KEY(user_id) REFERENCES users(id)" +
                                  ");";

            stmt.execute(userTable);
            stmt.execute(historyTable);

            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
