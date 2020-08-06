package it.unibz.inf.ontouml.vp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class GitHubUtils {

	final public static String GITHUB_API = "https://api.github.com";
	final public static String REPOS = "/repos";
	final public static String RELEASES = "/releases";

	final public static String PROP_CREATED_AT = "created_at";
	final public static String PROP_PRERELEASE = "prerelease";
	final public static String PROP_TAG_NAME = "tag_name";
	final public static String PROP_ASSETS = "assets";
	final public static String PROP_NAME = "name";
	final public static String PROP_BROWSER_DOWNLOAD_URL = "browser_download_url";
	
	private static File downloadedFile;

	static public JsonArray getReleases() throws IOException {
		final URL url = new URL(GITHUB_API + REPOS + "/" + OntoUMLPlugin.PLUGIN_REPO_OWNER + "/"
				+ OntoUMLPlugin.PLUGIN_REPO_NAME + RELEASES);
		final HttpsURLConnection request = (HttpsURLConnection) url.openConnection();

		request.setRequestMethod("GET");
		request.setRequestProperty("Accept", "application/vnd.github.v3+json");
		request.setReadTimeout(60000);

		final int responseCode = request.getResponseCode();
		JsonArray releases = null;

		if (responseCode == HttpsURLConnection.HTTP_OK) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					response.append(line);
				}

				releases = (JsonArray) new JsonParser().parse(response.toString()).getAsJsonArray();
			}
		}

		return releases;
	}

	static public File downloadRelease(JsonObject release) {
		JsonArray assets;
		
		if(release.get(PROP_ASSETS) == null) {
			return null;
		} else {
			assets = release.get(PROP_ASSETS).getAsJsonArray();
			downloadedFile = null;
		}
		
		assets.forEach(asset -> {
			
			try {
				String assetName = asset.getAsJsonObject().get(PROP_NAME).getAsString();
				final String link = asset.getAsJsonObject().get(PROP_BROWSER_DOWNLOAD_URL).getAsString();
				final URL downloadURL = assetName.contains("ontouml-vp-plugin") && assetName.endsWith(".zip")
						? new URL(link) : null;
						
				if(downloadURL != null) {
					final ReadableByteChannel rbc = Channels.newChannel(downloadURL.openStream());
					
					assetName = assetName.substring(0, assetName.lastIndexOf("."));
					
					File tempFile = File.createTempFile(assetName, ".zip");
					FileOutputStream fos = new FileOutputStream(tempFile);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);	
					fos.close();
					
					downloadedFile = tempFile;
				}
						
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return downloadedFile;
	}
}
