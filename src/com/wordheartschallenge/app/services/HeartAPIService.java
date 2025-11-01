package com.wordheartschallenge.app.services;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartAPIService {

    public static class HeartQuestion {
        public String imageUrl;
        public int solution;

        public HeartQuestion(String imageUrl, int solution) {
            this.imageUrl = imageUrl;
            this.solution = solution;
        }
    }

    public static HeartQuestion fetchQuestion() throws Exception {
        String API_URL = "https://marcconrad.com/uob/heart/api.php?out=json";
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString().trim();

            // Debug print
            System.out.println("❤️ API Response: " + json);

            // Ensure it’s valid JSON
            if (!json.startsWith("{")) {
                throw new RuntimeException("Invalid response: " + json);
            }

            JSONObject obj = new JSONObject(json);
            String imageUrl = obj.getString("question");
            int solution = obj.getInt("solution");
            return new HeartQuestion(imageUrl, solution);
        }
    }
}
