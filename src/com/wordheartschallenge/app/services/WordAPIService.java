package com.wordheartschallenge.app.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WordAPIService {

    private static final String API_KEY = "0k0b67ybvc2fyfb8de03nk4gj8qasx0tgnh9uc5i0vgjb2aah";

    // ✅ Inner static data holder
    public static class WordData {
        public final String word;
        public final String definition;
        public final String hint;

        public WordData(String word, String definition, String hint) {
            this.word = word;
            this.definition = definition;
            this.hint = hint;
        }
    }

 // ✅ Fetch a valid word with definition and hint, based on level
    public static WordData fetchValidWord(int level) throws Exception {
        String word;
        String definition;
        String hint;

        int attempts = 0;
        do {
            word = fetchRandomWord(level);
            definition = fetchDefinition(word);
            attempts++;
            if (attempts > 15) {  // Increased limit to find better words
                throw new Exception("Unable to find a valid word after 15 attempts.");
            }
        } while (isInvalidWord(word, definition, level));  // ✅ New validation method

        hint = fetchHint(word);

        // ✅ Console log the chosen word
        System.out.println("Chosen word for level " + level + ": " + word + " | Definition: " + definition);

        return new WordData(word, definition, hint);
    }

    // ✅ Helper to check if word/definition is invalid
    private static boolean isInvalidWord(String word, String definition, int level) {
        if (word == null || definition == null) return true;
        if (definition.length() < 10) return true;  // Too short
        if (definition.toLowerCase().contains("see ") || 
            definition.toLowerCase().contains("prefix") || 
            definition.toLowerCase().contains("suffix") || 
            definition.toLowerCase().contains("root") || 
            definition.toLowerCase().contains("combining form")) return true;  // Reject non-full words
        if (!word.matches("[a-zA-Z]+")) return true;  // Reject hyphens, numbers, etc.
        if (word.length() != getExpectedLength(level)) return true;  // Exact length
        return false;
    }

    // ✅ Helper to get expected word length for level
    private static int getExpectedLength(int level) {
        if (level <= 5) return 4;
        else if (level <= 10) return 5;
        else return 6;
    }


 // ✅ Random word from Wordnik with filters for practical words, adjusted by level
    public static String fetchRandomWord(int level) throws Exception {
        int minLength, maxLength;
        if (level <= 5) {
            minLength = 4;
            maxLength = 4;  // 4-letter words for levels 1-5
        } else if (level <= 10) {
            minLength = 5;
            maxLength = 5;  // 5-letter words for levels 6-10
        } else {
            minLength = 6;
            maxLength = 8;  // Extend as needed, e.g., 6-letter for higher levels
        }

        // ✅ Removed minCorpusCount (not supported in free plan, causes 404)
        String apiUrl = "https://api.wordnik.com/v4/words.json/randomWord"
                + "?hasDictionaryDef=true"
                + "&minLength=" + minLength
                + "&maxLength=" + maxLength
                + "&api_key=" + API_KEY;

        Exception lastException = null;
        for (int attempt = 1; attempt <= 3; attempt++) {  // ✅ Retry up to 3 times
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                reader.close();

                JSONObject obj = new JSONObject(response);
                return obj.getString("word");
            } catch (Exception e) {
                lastException = e;
                System.out.println("Wordnik attempt " + attempt + " failed: " + e.getMessage());
                Thread.sleep(1000);  // Wait 1 second before retry
            }
        }
        throw lastException;  // If all retries fail, throw the last exception
    }


    // ✅ Definition from Wordnik (unchanged)
    public static String fetchDefinition(String word) {
        try {
            String apiUrl = "https://api.wordnik.com/v4/word.json/" + word + "/definitions?limit=1&includeRelated=false&useCanonical=true&api_key=" + API_KEY;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            JSONArray arr = new JSONArray(sb.toString());
            if (arr.length() > 0) {
                String rawDefinition = arr.getJSONObject(0).getString("text");
                return stripXmlTags(rawDefinition);  // ✅ Strip tags
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    // ✅ Utility to strip XML/HTML tags from text
    private static String stripXmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]+>", "").trim();  // Removes <tag> and </tag>
    }

    // ✅ Hint generator using synonyms from Wordnik (unchanged)
    public static String fetchHint(String word) {
        try {
            String apiUrl = "https://api.wordnik.com/v4/word.json/" + word + "/relatedWords?relationshipTypes=synonym&limitPerRelationshipType=1&api_key=" + API_KEY;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONArray arr = new JSONArray(sb.toString());
            if (arr.length() > 0) {
                JSONArray wordsArr = arr.getJSONObject(0).getJSONArray("words");
                if (wordsArr.length() > 0) return wordsArr.getString(0);
            }

        } catch (Exception e) {
            // ignore
        }

        // fallback hint if no synonym found
        return "Starts with '" + word.charAt(0) + "'";
    }
}