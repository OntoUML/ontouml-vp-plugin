package it.unibz.inf.ontouml.vp.utils;

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
	private static final String USER_MESSAGE_BAD_REQUEST = "There was a internal plugin error and the verification could not be completed.";
	private static final String USER_MESSAGE_NOT_FOUND = "Service not found.";
	private static final String USER_MESSAGE_INTERNAL_ERROR = "Internal server error.";
	private static final String USER_MESSAGE_UNKNOWN_ERROR_REQUEST = "Error sending model verification to the server.";
	private static final String USER_MESSAGE_UNKNOWN_ERROR_RESPONSE = "Error receiving model verification response.";

	public static String requestModelVerification(String serializedModel) {

		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();
		final URL url;

		try {
			if (configurations.isCustomServerEnabled()) {
				url = new URL(configurations.getServerURL() + VERIFICATION_SERVICE_ENDPOINT);
			} else {
				url = new URL(ProjectConfigurations.DEFAULT_SERVER_URL + VERIFICATION_SERVICE_ENDPOINT);
			}
		} catch (MalformedURLException e) {
			return USER_MESSAGE_UNKNOWN_ERROR_REQUEST;
		}

		try {

			final HttpURLConnection request = (HttpURLConnection) url.openConnection();

			request.setRequestMethod("POST");
			request.setRequestProperty("Content-Type", "application/json");
			request.setReadTimeout(60000);
			request.setDoOutput(true);

			final OutputStream requestStream = request.getOutputStream();
			final byte[] requestBody = serializedModel.getBytes();

			final StringBuilder response = new StringBuilder();
			final BufferedReader reader;

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

			switch (request.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				return response.toString();
			case HttpURLConnection.HTTP_BAD_REQUEST:
				return USER_MESSAGE_BAD_REQUEST;
			case HttpURLConnection.HTTP_NOT_FOUND:
				return USER_MESSAGE_NOT_FOUND;
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				return USER_MESSAGE_INTERNAL_ERROR;
			default:
				return USER_MESSAGE_UNKNOWN_ERROR_RESPONSE;
			}

		} catch (IOException e) {
			System.err.println("Error occurred during model verification request.");
			e.printStackTrace();

			return USER_MESSAGE_UNKNOWN_ERROR_RESPONSE;

		} catch (Exception e) {
			return USER_MESSAGE_UNKNOWN_ERROR_REQUEST;
		}

	}

}
