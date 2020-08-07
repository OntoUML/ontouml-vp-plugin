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
import java.nio.file.Files;
import java.nio.file.Path;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class GitHubUtils {

	final public static String GITHUB_API = "https://api.github.com";
	final public static String REPOS = "/repos";
	final public static String RELEASES = "/releases";
	
	public static void lookupUpdates() throws IOException {
		JsonArray releases = GitHubUtils.getReleases();
		Configurations config = Configurations.getInstance();
		config.setReleases(releases);
		config.save();
	}

	public static JsonArray getReleases() throws IOException {
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

	public static File downloadReleaseAsset(GitHubReleaseAsset asset) throws MalformedURLException, IOException {
		final URL downloadURL = new URL(asset.getDownloadUrl());
		final ReadableByteChannel rbc = Channels.newChannel(downloadURL.openStream());
		final Path pluginDownloadDir = Files.createTempDirectory("pluginDownloadDir");
		final File downloadFile = new File(pluginDownloadDir.toFile(), asset.getName());
		final FileOutputStream fos = new FileOutputStream(downloadFile);

		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);	
		fos.close();
		
		return downloadFile;
	}
}
