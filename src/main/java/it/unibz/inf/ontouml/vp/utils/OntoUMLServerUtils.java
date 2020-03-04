package it.unibz.inf.ontouml.vp.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * Class responsible for making requests to the OntoUML Server based on standard
 * end points and configured server URL.
 * 
 * @author Claudenir Fonseca
 *
 */
public class OntoUMLServerUtils {

	private static final String VERIFICATION_SERVICE_ENDPOINT = "/v1/verify";

	public static String requestModelVerification(String serializedModel) throws MalformedURLException, IOException {

		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();
		final URL url;

		if (configurations.isCustomServerEnabled()) {
			url = new URL(configurations.getServerURL());
		} else {
			url = new URL(ProjectConfigurations.DEFAULT_SERVER_URL + VERIFICATION_SERVICE_ENDPOINT);
		}

		final HttpURLConnection request = (HttpURLConnection) url.openConnection();

		request.setRequestMethod("POST");
		request.setRequestProperty("Content-Type", "application/json");
		request.setReadTimeout(60000);
		request.setDoOutput(true);

		final OutputStream requestStream = request.getOutputStream();
		final byte[] requestBody = serializedModel.getBytes();

		final StringBuilder response = new StringBuilder();
		final BufferedReader reader;

		try {
			ViewUtils.log("Sending model to the server: " + url, ViewUtils.SCOPE_PLUGIN);
			ViewUtils.log("Please wait. This might take a while.", ViewUtils.SCOPE_PLUGIN);

			requestStream.write(requestBody, 0, requestBody.length);
			requestStream.flush();
			requestStream.close();

			reader = request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
					? new BufferedReader(new InputStreamReader(request.getInputStream()))
					: new BufferedReader(new InputStreamReader(request.getErrorStream()));

			String line = null;
			while ((line = reader.readLine()) != null) {
				response.append(line.trim());
			}
			reader.close();

		} catch (IOException e) {
			ViewUtils.log("Error receiving model verification response.", ViewUtils.SCOPE_PLUGIN);
			System.err.println("Error occurred during model verification request.");
			e.printStackTrace();
		}

		return response.toString();
	}

}
