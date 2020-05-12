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
import com.vp.plugin.view.IDialogHandler;

/**
 * 
 * Class responsible for making requests to the OntoUML Server based on standard
 * end points and configured server URL.
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
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

	public static BufferedReader transformToGUFO(String model, String baseIRI, String format, String uriFormatBy,
			String inverse, String object, String analysis, String packages, String elementMapping,
			String packageMapping, IDialogHandler loading) throws Exception {
		final JsonObject optionsObj = new JsonObject();

		boolean createObjectProperty = !Boolean.parseBoolean(object);
		boolean createInverses = Boolean.parseBoolean(inverse);
		boolean preAnalysis = Boolean.parseBoolean(analysis);
		boolean prefixPackages = Boolean.parseBoolean(packages);

		optionsObj.addProperty("baseIRI", baseIRI);
		optionsObj.addProperty("format", format);
		optionsObj.addProperty("uriFormatBy", uriFormatBy);
		optionsObj.addProperty("createInverses", createInverses);
		optionsObj.addProperty("createObjectProperty", createObjectProperty);
		optionsObj.addProperty("preAnalysis", preAnalysis);
		optionsObj.addProperty("prefixPackages", prefixPackages);
		optionsObj.add("customElementMapping", new Gson().fromJson(elementMapping, JsonObject.class));
		optionsObj.add("customPackageMapping", new Gson().fromJson(packageMapping, JsonObject.class));

		final JsonObject bodyObj = new JsonObject();
		bodyObj.add("options", optionsObj);
		bodyObj.add("model", new JsonParser().parse(model).getAsJsonObject());

		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.serializeNulls().setPrettyPrinting().create();
		final String body = gson.toJson(bodyObj);

		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();
		final String url;

		if (configurations.isCustomServerEnabled()) {
			url = configurations.getServerURL() + TRANSFORM_GUFO_SERVICE_ENDPOINT;
		} else {
			url = ProjectConfigurations.DEFAULT_SERVER_URL + TRANSFORM_GUFO_SERVICE_ENDPOINT;
		}

		loading.shown();

		try {
			final HttpURLConnection request = request(url, body);
			final BufferedReader responseReader = request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
					? new BufferedReader(new InputStreamReader(request.getInputStream()))
					: new BufferedReader(new InputStreamReader(request.getErrorStream()));

			loading.canClosed();

			switch (request.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				if (!request.getContentType().equals("text/html")) {
					return responseReader;
				} else {
					if (ViewUtils.exportToGUFOIssueDialogWithOption("Server not found.",
							HttpURLConnection.HTTP_NOT_FOUND))
						return transformToGUFO(model, baseIRI, format, uriFormatBy, inverse, object, analysis, packages,
								elementMapping, packageMapping, loading);

					System.out.println(responseReader.lines().collect(Collectors.joining()));
					new Exception("Server not found.").printStackTrace();
					return null;
				}
			case HttpURLConnection.HTTP_BAD_REQUEST:
				ViewUtils.exportToGUFOIssueDialog("Unable to transform the model due to an unexpected error."
						+ "\nPlease check the model for any syntactical errors.");
				System.out.println(responseReader.lines().collect(Collectors.joining()));
				new Exception("Unable to transform the model due to an unexpected error."
						+ "\nPlease check the model for any syntactical errors.").printStackTrace();
				return null;
			case HttpURLConnection.HTTP_NOT_FOUND:
				if (ViewUtils.exportToGUFOIssueDialogWithOption("Server not found.", HttpURLConnection.HTTP_NOT_FOUND))
					return transformToGUFO(model, baseIRI, format, uriFormatBy, inverse, object, analysis, packages,
							elementMapping, packageMapping, loading);

				System.out.println(responseReader.lines().collect(Collectors.joining()));
				new Exception("Server not found.").printStackTrace();
				return null;
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				if (ViewUtils.exportToGUFOIssueDialogWithOption("Server error.", HttpURLConnection.HTTP_INTERNAL_ERROR))
					return transformToGUFO(model, baseIRI, format, uriFormatBy, inverse, object, analysis, packages,
							elementMapping, packageMapping, loading);

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

	public static String requestModelVerification(String serializedModel, IDialogHandler loading) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();
		final String url;

		if (configurations.isCustomServerEnabled()) {
			url = configurations.getServerURL() + VERIFICATION_SERVICE_ENDPOINT;
		} else {
			url = ProjectConfigurations.DEFAULT_SERVER_URL + VERIFICATION_SERVICE_ENDPOINT;
		}

		loading.shown();

		try {

			final HttpURLConnection request = request(url, serializedModel);
			final StringBuilder response = new StringBuilder();
			final BufferedReader reader = request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
					? new BufferedReader(new InputStreamReader(request.getInputStream()))
					: new BufferedReader(new InputStreamReader(request.getErrorStream()));

			String line = null;

			while ((line = reader.readLine()) != null) {
				response.append(line.trim());
			}

			reader.close();

			loading.canClosed();

			switch (request.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				if (!request.getContentType().equals("text/html")) {
					return response.toString();
				} else {
					if (ViewUtils.verificationFailedDialogWithOption(USER_MESSAGE_NOT_FOUND,
							HttpURLConnection.HTTP_NOT_FOUND))
						return requestModelVerification(serializedModel, loading);
				}
			case HttpURLConnection.HTTP_BAD_REQUEST:
				ViewUtils.verificationFailedDialog(USER_MESSAGE_BAD_REQUEST);
				return null;
			case HttpURLConnection.HTTP_NOT_FOUND:
				if (ViewUtils.verificationFailedDialogWithOption(USER_MESSAGE_NOT_FOUND,
						HttpURLConnection.HTTP_NOT_FOUND))
					return requestModelVerification(serializedModel, loading);

				return null;
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				if (ViewUtils.verificationFailedDialogWithOption(USER_MESSAGE_INTERNAL_ERROR,
						HttpURLConnection.HTTP_INTERNAL_ERROR))
					return requestModelVerification(serializedModel, loading);

				return null;
			default:
				ViewUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
				return null;
			}

		} catch (SocketException e) {
			loading.canClosed();
			ViewUtils.verificationFailedDialog(USER_MESSAGE_NOT_FOUND);
			e.printStackTrace();
		} catch (IOException e) {
			loading.canClosed();
			ViewUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
			e.printStackTrace();
		} catch (Exception e) {
			loading.canClosed();
			ViewUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_REQUEST);
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

}
