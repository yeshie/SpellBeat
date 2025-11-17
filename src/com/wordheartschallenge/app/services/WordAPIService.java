package com.wordheartschallenge.app.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class WordAPIService {

    private static final String API_KEY = "0k0b67ybvc2fyfb8de03nk4gj8qasx0tgnh9uc5i0vgjb2aah";
    private static final Set<String> usedHints = new HashSet<>(); // Track used hints

    public static class WordData {
        public final String word;
        public final String definition;
        public final List<String> hints; // Multiple hints

        public WordData(String word, String definition, List<String> hints) {
            this.word = word;
            this.definition = definition;
            this.hints = hints;
        }
    }

    public static WordData fetchValidWord(int level) throws Exception {
        String word;
        String definition;
        List<String> hints;

        int attempts = 0;
        do {
            word = fetchRandomWord(level);
            definition = fetchDefinition(word);
            attempts++;
            if (attempts > 20) {  
                throw new Exception("Unable to find a valid word after 20 attempts.");
            }
        } while (isInvalidWord(word, definition, level));  

        hints = fetchMultipleHints(word);

        System.out.println("✅ Word for level " + level + ": " + word);
        System.out.println("   Definition: " + definition);
        System.out.println("   Available hints: " + hints.size());

        return new WordData(word, definition, hints);
    }

    private static boolean isInvalidWord(String word, String definition, int level) {
        if (word == null || definition == null) return true;

        // Only alphabetic
        if (!word.matches("^[a-zA-Z]+$")) return true;

        // Correct word length for the level
        if (word.length() != getExpectedLength(level)) return true;

        // Must contain at least 1 vowel for guessability
        int vowels = word.toLowerCase().replaceAll("[^aeiou]", "").length();
        if (vowels < 1) return true;

        // Definition quality check
        String defLower = definition.toLowerCase();
        if (defLower.contains("see ")
            || defLower.contains("prefix")
            || defLower.contains("suffix")
            || defLower.contains("root")
            || defLower.contains("combining form")
            || defLower.contains("obsolete")
            || defLower.contains("archaic")
            || defLower.contains("rare")
            || defLower.contains("etymology")) {
            return true;
        }

        // Must be 8–12 words
        int wordCount = definition.trim().split("\\s+").length;
        if (wordCount < 8 || wordCount > 12) return true;

        return false;
    }

    private static int getExpectedLength(int level) {
        if (level <= 5) return 4;
        else if (level <= 10) return 5;
        else return 6;
    }

    public static String fetchRandomWord(int level) throws Exception {
        int minLength, maxLength;
        if (level <= 5) {
            minLength = 4;
            maxLength = 4;
        } else if (level <= 10) {
            minLength = 5;
            maxLength = 5;
        } else {
            minLength = 6;
            maxLength = 6;
        }

        String apiUrl = "https://api.wordnik.com/v4/words.json/randomWord"
                + "?hasDictionaryDef=true"
                + "&minLength=" + minLength
                + "&maxLength=" + maxLength
                + "&api_key=" + API_KEY;

        Exception lastException = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
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
                Thread.sleep(1000);
            }
        }
        throw lastException;
    }

    public static String fetchDefinition(String word) {
        try {
            String apiUrl = "https://api.wordnik.com/v4/word.json/" + word
                    + "/definitions?limit=5&includeRelated=false&useCanonical=true&api_key=" + API_KEY;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            JSONArray arr = new JSONArray(sb.toString());

            for (int i = 0; i < arr.length(); i++) {
                String rawDefinition = arr.getJSONObject(i).getString("text");
                String cleanDef = stripXmlTags(rawDefinition).trim();

                // Count words
                int words = cleanDef.split("\\s+").length;

                // Short meaningful definition: 8–12 words
                if (words >= 8 && words <= 10) {
                    return cleanDef;
                }
            }

            return null; // none matched
        } catch (Exception e) {
            return null;
        }
    }


    private static String stripXmlTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]+>", "").trim();
    }

    /** ✅ NEW: Fetch multiple hints for variety */
    public static List<String> fetchMultipleHints(String word) {
        List<String> hints = new ArrayList<>();
        
        try {
            // Get synonyms
            String apiUrl = "https://api.wordnik.com/v4/word.json/" + word 
                + "/relatedWords?relationshipTypes=synonym&limitPerRelationshipType=5&api_key=" + API_KEY;

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
                for (int i = 0; i < wordsArr.length() && hints.size() < 5; i++) {
                    hints.add("Synonym: " + wordsArr.getString(i));
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        // Add letter-based hints
        hints.add("Starts with '" + word.charAt(0) + "'");
        hints.add("Ends with '" + word.charAt(word.length() - 1) + "'");
        hints.add("Contains " + word.length() + " letters");
        
        if (word.length() >= 3) {
            hints.add("First 2 letters: " + word.substring(0, 2));
        }

        return hints;
    }

    /** ✅ NEW: Get next unused hint */
    public static String getNextHint(List<String> availableHints, String word) {
        String key = word.toUpperCase();
        
        // Find unused hint
        for (String hint : availableHints) {
            String hintKey = key + ":" + hint;
            if (!usedHints.contains(hintKey)) {
                usedHints.add(hintKey);
                return hint;
            }
        }
        
        // All hints used, reset and return first
        usedHints.clear();
        return availableHints.isEmpty() ? "No more hints!" : availableHints.get(0);
    }

    /** ✅ Clear used hints for new word */
    public static void resetHints() {
        usedHints.clear();
    }
}