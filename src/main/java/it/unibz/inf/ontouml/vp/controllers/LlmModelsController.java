package it.unibz.inf.ontouml.vp.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lists the models a provider currently offers, from the same {@code GET /models} endpoint that
 * every provider in {@link it.unibz.inf.ontouml.vp.model.LlmProvider} documents itself as speaking
 * alongside {@code /chat/completions} — same base URL, same bearer key, one call ahead of the one
 * the plugin actually uses.
 *
 * <p>A custom endpoint ({@code Other}) may or may not support it, and is only ever asked because
 * the user pressed the button that calls this: same as any other setting here, a provider that
 * does not support it simply fails, the way a wrong model name would.
 */
public class LlmModelsController {

  private static final int TIMEOUT_MILLIS = 15000;

  private static final String USER_MESSAGE_NO_BASE_URL =
      "No base URL is set for this provider yet.";
  private static final String USER_MESSAGE_UNAUTHORIZED =
      "The provider rejected the API key while listing models.";
  private static final String USER_MESSAGE_RATE_LIMITED =
      "The provider is rate-limiting this request. Try again shortly.";
  private static final String USER_MESSAGE_CONNECTION_ERROR =
      "Could not reach the provider to list its models.";
  private static final String USER_MESSAGE_UNEXPECTED_RESPONSE =
      "This endpoint did not return a model list — this provider may not support it.";

  private LlmModelsController() {}

  /**
   * @return the model ids the provider reports, sorted, with no duplicates.
   * @throws IOException with a message meant to be shown to the user as it is.
   */
  public static List<String> listModels(LlmConfigurations config) throws IOException {
    final String baseUrl = config.getEffectiveBaseUrl().trim();

    if (baseUrl.isEmpty()) {
      throw new IOException(USER_MESSAGE_NO_BASE_URL);
    }

    final JsonObject response = readJson(open(baseUrl + "/models", config.getApiKey()));
    final List<String> models = new ArrayList<>(idsIn(response));

    if (models.isEmpty()) {
      throw new IOException(USER_MESSAGE_UNEXPECTED_RESPONSE);
    }

    Collections.sort(models);

    return models;
  }

  /**
   * The model ids in an OpenAI-shaped {@code {"data": [{"id": "..."}]}} response, or, failing
   * that, a native Gemini-shaped {@code {"models": [{"name": "models/..."}]}} one — the "models/"
   * prefix Gemini's own API uses is stripped, so the id reads the same as it would coming from the
   * OpenAI-compatible endpoint this class otherwise talks to.
   */
  private static Set<String> idsIn(JsonObject response) {
    final Set<String> ids = new LinkedHashSet<>();

    for (String member : new String[] {"data", "models"}) {
      final JsonElement list = response.get(member);

      if (list == null || !list.isJsonArray()) {
        continue;
      }

      for (JsonElement entry : list.getAsJsonArray()) {
        final String id = idOf(entry);

        if (id != null && !id.isEmpty()) {
          ids.add(id.startsWith("models/") ? id.substring("models/".length()) : id);
        }
      }
    }

    return ids;
  }

  private static String idOf(JsonElement entry) {
    if (!entry.isJsonObject()) {
      return entry.isJsonPrimitive() ? entry.getAsString() : null;
    }

    final JsonObject object = entry.getAsJsonObject();

    for (String field : new String[] {"id", "name"}) {
      if (object.has(field) && object.get(field).isJsonPrimitive()) {
        return object.get(field).getAsString();
      }
    }

    return null;
  }

  private static HttpURLConnection open(String urlString, String apiKey) throws IOException {
    try {
      final HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();

      connection.setRequestMethod("GET");
      connection.setConnectTimeout(TIMEOUT_MILLIS);
      connection.setReadTimeout(TIMEOUT_MILLIS);

      if (apiKey != null && !apiKey.trim().isEmpty()) {
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
      }

      return connection;
    } catch (IOException e) {
      throw new IOException(USER_MESSAGE_CONNECTION_ERROR);
    }
  }

  private static JsonObject readJson(HttpURLConnection connection) throws IOException {
    try {
      switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          break;
        case HttpURLConnection.HTTP_UNAUTHORIZED:
        case HttpURLConnection.HTTP_FORBIDDEN:
          throw new IOException(USER_MESSAGE_UNAUTHORIZED);
        case 429:
          throw new IOException(USER_MESSAGE_RATE_LIMITED);
        default:
          throw new IOException(USER_MESSAGE_UNEXPECTED_RESPONSE);
      }

      try (BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
        final String json = reader.lines().collect(Collectors.joining("\n"));
        return new JsonParser().parse(json).getAsJsonObject();
      }
    } catch (SocketTimeoutException | SocketException e) {
      throw new IOException(USER_MESSAGE_CONNECTION_ERROR);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(USER_MESSAGE_UNEXPECTED_RESPONSE);
    }
  }
}
