
package com.wordheartschallenge.app.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HintAPIService {
	private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
	private static final Gson gson = new Gson();

	public static CompletableFuture<String> fetchDefinition(String word) {
		String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word.toLowerCase();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(8)).GET().build();

		return HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenApply(body -> {
					try {
						JsonArray root = gson.fromJson(body, JsonArray.class);
						if (root != null && root.size() > 0) {
							JsonElement meanings = root.get(0).getAsJsonObject().get("meanings");
							if (meanings != null && meanings.getAsJsonArray().size() > 0) {
								JsonElement defs = meanings.getAsJsonArray().get(0).getAsJsonObject()
										.get("definitions");
								if (defs != null && defs.getAsJsonArray().size() > 0) {
									String def = defs.getAsJsonArray().get(0).getAsJsonObject().get("definition")
											.getAsString();
									return def;
								}
							}
						}
					} catch (Exception ex) {
						// API may return 404 or different structure — ignore
					}
					return null;
				});
	}

	public static CompletableFuture<List<String>> fetchSynonyms(String word) {
		String url = "https://api.datamuse.com/words?rel_syn=" + word.toLowerCase();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(6)).GET().build();

		return HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenApply(body -> {
					List<String> synonyms = new ArrayList<>();
					try {
						JsonArray arr = gson.fromJson(body, JsonArray.class);
						for (JsonElement el : arr) {
							String w = el.getAsJsonObject().get("word").getAsString();
							synonyms.add(w);
						}
					} catch (Exception ex) {
						// ignore
					}
					return synonyms;
				});
	}
}
