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
    private static final Set<String> usedHints = new HashSet<>();

    public static class WordData {
        public final String word;
        public final String definition;
        public final List<String> hints;

        public WordData(String word, String definition, List<String> hints) {
            this.word = word;
            this.definition = definition;
            this.hints = hints;
        }
    }

    public static WordData fetchValidWord(int level) throws Exception {
        String word, definition;
        List<String> hints;

        int attempts = 0;
        do {
            word = fetchRandomWord(level);
            definition = fetchDefinition(word);
            attempts++;
            if (attempts > 20) throw new Exception("❌ Failed to find valid word after 20 tries");
        } while (isInvalidWord(word, definition, level));

        hints = fetchMultipleHints(word);

        System.out.println("🎉 Word: " + word);
        System.out.println("📚 Definition: " + definition);

        return new WordData(word, definition, hints);
    }

    private static boolean isInvalidWord(String word, String definition, int level) {
        if (word == null || definition == null) return true;
        if (!word.matches("^[a-zA-Z]+$")) return true;
        if (word.length() != getExpectedLength(level)) return true;
        if (word.toLowerCase().replaceAll("[^aeiou]", "").length() < 1) return true;

        String check = definition.toLowerCase();
        if (check.contains("see ") || check.contains("obsolete") ||
                check.contains("archaic") || check.contains("rare") ||
                check.contains("prefix") || check.contains("suffix"))
            return true;

        int words = definition.trim().split("\\s+").length;
        return words < 8 || words > 12;
    }

    private static int getExpectedLength(int level) {
        return (level <= 5) ? 4 : (level <= 10) ? 5 : 6;
    }

    public static String fetchRandomWord(int level) throws Exception {
        int len = getExpectedLength(level);
        String apiUrl = "https://random-word-api.vercel.app/api?words=1&length=" + len;

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.readLine();
        reader.close();

        return response.replace("[", "").replace("]", "").replace("\"", "");
    }

    public static String fetchDefinition(String word) {
        try {
            String apiUrl = "https://api.wordnik.com/v4/word.json/" + word
                    + "/definitions?limit=200&includeRelated=false&api_key=" + API_KEY;

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            r.close();

            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                String def = stripXml(arr.getJSONObject(i).getString("text")).trim();
                int count = def.split("\\s+").length;
                if (count >= 8 && count <= 12) return def;
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static String stripXml(String text) {
        return text == null ? "" : text.replaceAll("<[^>]+>", "").trim();
    }

    /** ✨ Improved Hint Builder with Natural Language */
    public static List<String> fetchMultipleHints(String word) {
        List<String> hints = new ArrayList<>();
        List<String> synonyms = fetchSynonymsFromDatamuse(word);

        if (!synonyms.isEmpty()) {
            String s = synonyms.get(0);
            hints.add("It is related to **" + s + "**.");
            hints.add("It means something similar to **" + s + "**.");
            hints.add("You might use this word instead of **" + s + "**.");
        }

        hints.add("It starts with the letter '" + word.charAt(0) + "'.");
        hints.add("It ends with the letter '" + word.charAt(word.length() - 1) + "'.");
        hints.add("It contains **" + word.length() + "** letters.");

        if (word.length() >= 3)
            hints.add("The first part looks like **" + word.substring(0, 2) + "**.");

        // Example sentence hint
        try {
            String api = "https://api.wordnik.com/v4/word.json/" + word + "/topExample?api_key=" + API_KEY;
            HttpURLConnection conn = (HttpURLConnection) new URL(api).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject obj = new JSONObject(r.readLine());
            r.close();

            String example = obj.getString("text").replace(word, "_____");
            hints.add("Example sentence: " + example);

        } catch (Exception ignored) {}

        return hints;
    }

    /** 🎤 Datamuse Synonyms API */
    public static List<String> fetchSynonymsFromDatamuse(String word) {
        List<String> list = new ArrayList<>();
        try {
            String apiUrl = "https://api.datamuse.com/words?ml=" + word;

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);

            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length() && i < 5; i++) {
                list.add(arr.getJSONObject(i).getString("word"));
            }
        } catch (Exception ignored) {}

        return list;
    }

    public static String getNextHint(List<String> availableHints, String word) {
        String key = word.toUpperCase();
        for (String hint : availableHints) {
            String hv = key + ":" + hint;
            if (!usedHints.contains(hv)) {
                usedHints.add(hv);
                return hint;
            }
        }
        usedHints.clear();
        return availableHints.isEmpty() ? "No hints available" : availableHints.get(0);
    }

    public static void resetHints() {
        usedHints.clear();
    }
}
