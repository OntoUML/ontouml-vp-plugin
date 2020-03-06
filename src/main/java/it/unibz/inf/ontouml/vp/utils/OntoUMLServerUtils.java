package it.unibz.inf.ontouml.vp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * Class responsible for making requests to the OntoUML Server based on standard
 * end points and configured server URL.
 * 
 * @author Claudenir Fonseca
 *
 */
public class OntoUMLServerUtils {

	private static final String TRANSFORM_GUFO_SERVICE_ENDPOINT = "/v1/transform/gufo";
	private static final String VERIFICATION_SERVICE_ENDPOINT = "/v1/verify";
	private static final String USER_MESSAGE_BAD_REQUEST = "There was a internal plugin error and the verification could not be completed.";
	private static final String USER_MESSAGE_NOT_FOUND = "Unable to reach the server.";
	private static final String USER_MESSAGE_INTERNAL_ERROR = "Internal server error.";
	private static final String USER_MESSAGE_UNKNOWN_ERROR_REQUEST = "Error sending model verification to the server.";
	private static final String USER_MESSAGE_UNKNOWN_ERROR_RESPONSE = "Error receiving model verification response.";

	public static BufferedReader transformToGUFO(String model, String baseIRI, String format, String uriFormatBy) throws Exception {
		final JsonObject optionsObj = new JsonObject();
		optionsObj.addProperty("baseIRI", baseIRI);
		optionsObj.addProperty("format", format);
		optionsObj.addProperty("uriFormatBy", uriFormatBy);
		
		final JsonObject bodyObj = new JsonObject();
		bodyObj.add("options", optionsObj);
		bodyObj.add("model", new JsonParser().parse(model).getAsJsonObject());

		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.serializeNulls().setPrettyPrinting().create();
		final String body = gson.toJson(bodyObj);

		try {
			final HttpURLConnection request = request(
					Configurations.getInstance().getProjectConfigurations().getServerURL()
							+ TRANSFORM_GUFO_SERVICE_ENDPOINT,
							body);
			final BufferedReader responseReader = request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
					? new BufferedReader(new InputStreamReader(request.getInputStream()))
					: new BufferedReader(new InputStreamReader(request.getErrorStream()));

			switch (request.getResponseCode()) {
				case HttpURLConnection.HTTP_OK:
					return responseReader;
				case HttpURLConnection.HTTP_BAD_REQUEST:
					ViewUtils.exportToGUFOIssueDialog("Unable to transform model due to unexpected error."
							+ "\nPlease check the model for nay syntactical errors.");
					System.out.println(responseReader.lines().collect(Collectors.joining()));
					new Exception("Unable to transform model due to unexpected error."
							+ "\nPlease check the model for nay syntactical errors.").printStackTrace();
					return null;
				case HttpURLConnection.HTTP_NOT_FOUND:
					ViewUtils.exportToGUFOIssueDialog("Server not found.");
					System.out.println(responseReader.lines().collect(Collectors.joining()));
					new Exception("Server not found.").printStackTrace();
					return null;
				case HttpURLConnection.HTTP_INTERNAL_ERROR:
					ViewUtils.exportToGUFOIssueDialog("Server error.");
					System.out.println(responseReader.lines().collect(Collectors.joining()));
					new Exception("Server error.").printStackTrace();
					return null;
				default:
					ViewUtils.exportToGUFOIssueDialog("Unexpected error.");
					throw new Exception("Unknown error");
			}
		} catch (MalformedURLException e) {
			ViewUtils.exportToGUFOIssueDialog("Server error.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static HttpURLConnection request(String urlString, String body) throws MalformedURLException, IOException {
		final URL url = new URL(urlString);
		final HttpURLConnection request = (HttpURLConnection) url.openConnection();
		
		request.setRequestMethod("POST");
		request.setRequestProperty("Content-Type", "application/json");
		request.setReadTimeout(60000);
		request.setDoOutput(true);

		final OutputStream requestStream = request.getOutputStream();
		final byte[] requestBody = body.getBytes();

		requestStream.write(requestBody, 0, requestBody.length);
		requestStream.flush();
		requestStream.close();

		return request;
	}

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
			ViewUtils.verificationFailedDialog(USER_MESSAGE_NOT_FOUND);
			e.printStackTrace();
			return null;
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
				ViewUtils.verificationFailedDialog(USER_MESSAGE_BAD_REQUEST);
				return null;
			case HttpURLConnection.HTTP_NOT_FOUND:
				ViewUtils.verificationFailedDialog(USER_MESSAGE_NOT_FOUND);
				return null;
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				ViewUtils.verificationFailedDialog(USER_MESSAGE_INTERNAL_ERROR);
				return null;
			default:
				ViewUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
				return null;
			}

		} catch(SocketException e) {
			ViewUtils.verificationFailedDialog(USER_MESSAGE_NOT_FOUND);
			e.printStackTrace();
		} catch (IOException e) {
			ViewUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
			e.printStackTrace();
		} catch (Exception e) {
			ViewUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_REQUEST);
			e.printStackTrace();
		}

		return null;
	}

}
