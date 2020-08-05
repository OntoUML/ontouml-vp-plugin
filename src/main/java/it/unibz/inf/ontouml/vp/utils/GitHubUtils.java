package it.unibz.inf.ontouml.vp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GitHubUtils {

	final public static String GITHUB_API = "https://api.github.com";
	final public static String REPOS = "/repos";
	final public static String RELEASES = "/releases";
	
	final public static String PROP_CREATED_AT = "created_at";
	final public static String PROP_PRERELEASE = "prerelease";
	
	static public JsonArray getReleases(String repository) throws IOException {
		final URL url = new URL(GITHUB_API + REPOS + "/" + repository + RELEASES);
		final HttpsURLConnection request = (HttpsURLConnection) url.openConnection();
		
		request.setRequestMethod("GET");
		request.setRequestProperty("Accept", "application/vnd.github.v3+json");
		request.setReadTimeout(60000);
		
		final int responseCode = request.getResponseCode();
		JsonArray releases = null;
		
		if(responseCode == HttpsURLConnection.HTTP_OK) {
			try(BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;
				
				while((line = in.readLine()) != null) {
					response.append(line);
				}
				
				releases = (JsonArray) new JsonParser().parse(response.toString()).getAsJsonArray();
			}
		}
		
		return releases;
	}
}
