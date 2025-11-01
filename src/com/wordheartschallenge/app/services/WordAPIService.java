package com.wordheartschallenge.app.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WordAPIService {

    // ✅ Inner static data holder
    public static class WordData {
        public final String word;
        public final String definition;

        public WordData(String word, String definition) {
            this.word = word;
            this.definition = definition;
        }
    }

    // ✅ Returns both word and definition now
    public static WordData fetchValidWord() throws Exception {
        String word;
        String definition;
        do {
            word = fetchRandomWord();
            definition = fetchDefinition(word);
        } while (definition == null);

        return new WordData(word, definition);
    }

    public static String fetchRandomWord() throws Exception {
        URL url = new URL("https://random-word-api.vercel.app/api?words=1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.readLine();
        reader.close();

        return response.replaceAll("[\\[\\]\"]", "");
    }

    public static String fetchDefinition(String word) {
        try {
            URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONArray arr = new JSONArray(sb.toString());
            JSONObject obj = arr.getJSONObject(0)
                    .getJSONArray("meanings").getJSONObject(0)
                    .getJSONArray("definitions").getJSONObject(0);
            return obj.getString("definition");
        } catch (Exception e) {
            return null;
        }
    }

    public static String fetchHint(String word) throws Exception {
        URL url = new URL("https://api.datamuse.com/words?ml=" + word);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();

        JSONArray arr = new JSONArray(sb.toString());
        if (arr.length() > 0) {
            return arr.getJSONObject(0).getString("word");
        }
        return "No hints found.";
    }
}
