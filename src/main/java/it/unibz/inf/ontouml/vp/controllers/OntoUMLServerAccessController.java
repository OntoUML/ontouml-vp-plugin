package it.unibz.inf.ontouml.vp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GufoTransformationServiceResult;
import it.unibz.inf.ontouml.vp.model.ModularizationServiceResult;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.ServiceResult;
import it.unibz.inf.ontouml.vp.model.VerificationServiceResult;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Class responsible for making requests to the OntoUML Server based on standard end points and
 * configured server URL.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class OntoUMLServerAccessController {

  private static final String TRANSFORM_GUFO_SERVICE_ENDPOINT = "/v1/transform/gufo";
  private static final String VERIFICATION_SERVICE_ENDPOINT = "/v1/verify";
  private static final String MODULARIZATION_SERVICE_ENDPOINT = "/v1/modularize";
  private static final String USER_MESSAGE_BAD_REQUEST =
      "There was a internal plugin error and the service could not be completed.";
  private static final String USER_MESSAGE_NOT_FOUND = "Server not found.";
  private static final String USER_MESSAGE_CONNECTION_ERROR = "Unable to reach the server.";
  private static final String USER_MESSAGE_INTERNAL_ERROR = "Internal server error.";
  private static final String USER_MESSAGE_UNKNOWN_ERROR_RESPONSE =
      "Error receiving service response.";

  public static BufferedReader transformToGUFO(
      String project,
      String baseIRI,
      String format,
      String uriFormatBy,
      String inverse,
      String object,
      String analysis,
      String packages,
      String elementMapping,
      String packageMapping,
      IDialogHandler loading)
      throws Exception {
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
    bodyObj.add("project", new JsonParser().parse(project).getAsJsonObject());

    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.serializeNulls().setPrettyPrinting().create();
    final String body = gson.toJson(bodyObj);

    final ProjectConfigurations configurations =
        Configurations.getInstance().getProjectConfigurations();
    final String url;

    if (configurations.isCustomServerEnabled()) {
      url = configurations.getServerURL() + TRANSFORM_GUFO_SERVICE_ENDPOINT;
    } else {
      url = ProjectConfigurations.DEFAULT_SERVER_URL + TRANSFORM_GUFO_SERVICE_ENDPOINT;
    }

    loading.shown();

    try {
      final HttpURLConnection request = performRequest(url, body);
      final BufferedReader responseReader =
          request.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
              ? new BufferedReader(new InputStreamReader(request.getInputStream()))
              : new BufferedReader(new InputStreamReader(request.getErrorStream()));

      loading.canClosed();

      switch (request.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (!request.getContentType().equals("text/html")) {
            return responseReader;
          } else {
            if (ViewManagerUtils.exportToGUFOIssueDialogWithOption(
                "Server not found.", HttpURLConnection.HTTP_NOT_FOUND))
              return transformToGUFO(
                  project,
                  baseIRI,
                  format,
                  uriFormatBy,
                  inverse,
                  object,
                  analysis,
                  packages,
                  elementMapping,
                  packageMapping,
                  loading);

            System.out.println(responseReader.lines().collect(Collectors.joining()));
            new Exception("Server not found.").printStackTrace();
            return null;
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
          ViewManagerUtils.exportToGUFOIssueDialog(
              "Unable to transform the model due to an unexpected error.\n"
                  + "Please check the model for any syntactical errors.\n\n"
                  + "Warning: partially exporting models to gUFO may introduce syntactical"
                  + " errors.");
          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception(
                  "Unable to transform the model due to an unexpected error.\n"
                      + "Please check the model for any syntactical errors.\n\n"
                      + "Warning: partially exporting models to gUFO may introduce syntactical"
                      + " errors.")
              .printStackTrace();
          return null;
        case HttpURLConnection.HTTP_NOT_FOUND:
          if (ViewManagerUtils.exportToGUFOIssueDialogWithOption(
              "Server not found.", HttpURLConnection.HTTP_NOT_FOUND))
            return transformToGUFO(
                project,
                baseIRI,
                format,
                uriFormatBy,
                inverse,
                object,
                analysis,
                packages,
                elementMapping,
                packageMapping,
                loading);

          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server not found.").printStackTrace();
          return null;
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
          if (ViewManagerUtils.exportToGUFOIssueDialogWithOption(
              "Server error.", HttpURLConnection.HTTP_INTERNAL_ERROR))
            return transformToGUFO(
                project,
                baseIRI,
                format,
                uriFormatBy,
                inverse,
                object,
                analysis,
                packages,
                elementMapping,
                packageMapping,
                loading);

          System.out.println(responseReader.lines().collect(Collectors.joining()));
          new Exception("Server error.").printStackTrace();
          return null;
        default:
          ViewManagerUtils.exportToGUFOIssueDialog("Unexpected error.");
          throw new Exception("Unknown error");
      }
    } catch (MalformedURLException e) {
      ViewManagerUtils.exportToGUFOIssueDialog("Server error.");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private static String getServiceRequestBody(String project) {
    return "{\"options\": null, \"project\": " + project + "}";
  }

  private static String getServiceRequestBody(String project, String options) {
    return "{\"options\": " + options + ", \"project\": " + project + "}";
  }

  private static String getModularizationRequestUrl() {
    final ProjectConfigurations config = Configurations.getInstance().getProjectConfigurations();
    return config.isCustomServerEnabled()
        ? config.getServerURL() + MODULARIZATION_SERVICE_ENDPOINT
        : ProjectConfigurations.DEFAULT_SERVER_URL + MODULARIZATION_SERVICE_ENDPOINT;
  }

  private static String getVerificationRequestUrl() {
    final ProjectConfigurations config = Configurations.getInstance().getProjectConfigurations();
    return config.isCustomServerEnabled()
        ? config.getServerURL() + VERIFICATION_SERVICE_ENDPOINT
        : ProjectConfigurations.DEFAULT_SERVER_URL + VERIFICATION_SERVICE_ENDPOINT;
  }

  private static String getTransformationToGufoRequestUrl() {
    final ProjectConfigurations config = Configurations.getInstance().getProjectConfigurations();
    return config.isCustomServerEnabled()
        ? config.getServerURL() + TRANSFORM_GUFO_SERVICE_ENDPOINT
        : ProjectConfigurations.DEFAULT_SERVER_URL + TRANSFORM_GUFO_SERVICE_ENDPOINT;
  }

  private static <T extends ServiceResult<?>> T parseResponse(
      HttpURLConnection connection, Class<T> _class) throws IOException {
    if (connection.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
      return null;
    }

    final BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()));
    final String json = reader.lines().parallel().collect(Collectors.joining("\n"));
    return new ObjectMapper().readValue(json, _class);
  }

  private static boolean hasJsonContentType(HttpURLConnection connection) {
    return connection.getContentType().contains("application/json");
  }

  private static VerificationServiceResult caseVerificationResponseIsOk(
      HttpURLConnection connection, String project) throws IOException {
    if (hasJsonContentType(connection)) {
      return parseResponse(connection, VerificationServiceResult.class);
    }

    final boolean shouldRetry =
        ViewManagerUtils.verificationFailedDialogWithOption(
            USER_MESSAGE_NOT_FOUND, HttpURLConnection.HTTP_NOT_FOUND);

    return shouldRetry ? requestModelVerification(project) : null;
  }
  /*
  private static String caseVerificationResponseIsBadRequest(
      HttpURLConnection connection, String project) throws IOException {
    ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_BAD_REQUEST);
    return null;
  }

  private static String caseVerificationResponseIsNotFound(
      HttpURLConnection connection, String project) throws IOException {
    final boolean shouldRetry =
        ViewManagerUtils.verificationFailedDialogWithOption(
            USER_MESSAGE_NOT_FOUND, HttpURLConnection.HTTP_NOT_FOUND);

    return shouldRetry ? requestModelVerification(project) : null;
  }

  private static String caseVerificationResponseIsInternalError(
      HttpURLConnection connection, String project) throws IOException {
    final boolean shouldRetry =
        ViewManagerUtils.verificationFailedDialogWithOption(
            USER_MESSAGE_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);

    return shouldRetry ? requestModelVerification(project) : null;
  }

  private static String caseVerificationResponseIsDefault() throws IOException {
    ViewManagerUtils.verificationFailedDialog(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
    return null;
  }
  */

  public static ModularizationServiceResult requestProjectModularization(String project) {
    final String body = getServiceRequestBody(project);
    final String url = getModularizationRequestUrl();

    try {
      final HttpURLConnection connection = performRequest(url, body);

      switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (hasJsonContentType(connection)) {
            return parseResponse(connection, ModularizationServiceResult.class);
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
        case HttpURLConnection.HTTP_NOT_FOUND:
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
        default:
          System.err.println("Attention! Modularization request was not processed correctly");
          System.err.println("Status Code: " + connection.getResponseCode());
      }
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    return null;
  }

  public static VerificationServiceResult requestModelVerification(String project)
      throws IOException {
    try {
      final String url = getVerificationRequestUrl();
      final String body = getServiceRequestBody(project);

      final HttpURLConnection connection = performRequest(url, body);

      switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          return caseVerificationResponseIsOk(connection, project);
        case HttpURLConnection.HTTP_BAD_REQUEST:
          throw new IOException(USER_MESSAGE_BAD_REQUEST);
        case HttpURLConnection.HTTP_NOT_FOUND:
          throw new IOException(USER_MESSAGE_NOT_FOUND);
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
          throw new IOException(USER_MESSAGE_INTERNAL_ERROR);
        default:
          throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
      }
    } catch (SocketException e) {
      throw new IOException(USER_MESSAGE_CONNECTION_ERROR);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
    }
  }

  public static GufoTransformationServiceResult requestProjectTransformationToGufo(
      String project, String options) {
    final String body = getServiceRequestBody(project, options);
    final String url = getTransformationToGufoRequestUrl();

    try {
      final HttpURLConnection connection = performRequest(url, body);

      switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          if (hasJsonContentType(connection)) {
            return parseResponse(connection, GufoTransformationServiceResult.class);
          }
        case HttpURLConnection.HTTP_BAD_REQUEST:
        case HttpURLConnection.HTTP_NOT_FOUND:
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
        default:
          System.err.println("Attention! Transformation request was not processed correctly");
          System.err.println("Status Code: " + connection.getResponseCode());
      }
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    return null;
  }

  private static HttpURLConnection performRequest(String urlString, String body)
      throws IOException {
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
