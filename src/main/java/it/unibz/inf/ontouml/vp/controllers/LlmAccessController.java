package it.unibz.inf.ontouml.vp.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmConversationLog;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import it.unibz.inf.ontouml.vp.model.LlmProvider;
import it.unibz.inf.ontouml.vp.model.MetaclassSuggestion;
import it.unibz.inf.ontouml.vp.model.SearchMode;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Class responsible for making requests to a user-configured OpenAI-compatible Chat
 * Completions endpoint in order to obtain an AI-suggested OntoUML class stereotype. Mirrors
 * the request/response/error-handling idiom already used by {@link
 * OntoUMLServerAccessController} for the OntoUML Server integration.
 */
public class LlmAccessController {

  private static final String CHAT_COMPLETIONS_ENDPOINT = "/chat/completions";

  private static final String USER_MESSAGE_MISSING_API_KEY =
      "The LLM provider is not fully configured. Please review it in the OntoUML Plugin"
          + " Settings.";
  private static final String USER_MESSAGE_UNAUTHORIZED =
      "The LLM API rejected the request (invalid API key).";
  private static final String USER_MESSAGE_RATE_LIMITED =
      "The LLM API is rate-limiting requests. Please try again shortly.";
  private static final String USER_MESSAGE_NOT_FOUND =
      "LLM API endpoint not found. Please check the configured base URL.";
  private static final String USER_MESSAGE_BAD_REQUEST =
      "The LLM API rejected the request as malformed.";
  private static final String USER_MESSAGE_SERVER_ERROR = "The LLM API returned a server error.";
  private static final String USER_MESSAGE_CONNECTION_ERROR =
      "Unable to reach the configured LLM API.";
  private static final String USER_MESSAGE_UNKNOWN_ERROR_RESPONSE =
      "Unexpected response from the LLM API.";
  private static final String USER_MESSAGE_UNPARSEABLE_SUGGESTION =
      "The AI response could not be understood as a metaclass suggestion.";
  private static final String USER_MESSAGE_INVALID_STEREOTYPE =
      "The AI suggested a stereotype that is not a valid OntoUML class stereotype.";

  private static final String USER_MESSAGE_UNPARSEABLE_ANSWER =
      "The AI did not answer the decision-tree question in the expected format.";
  private static final String NODE_SYSTEM_PROMPT =
      "You answer one question of the UFO decision tree at a time and reply only with the JSON"
          + " object the user asks for.";

  private static final String DOCUMENTATION_SYSTEM_PROMPT =
      "You document classes of an OntoUML model. You never invent laws, articles or URLs, and you"
          + " mark as (não verificado) every source you cannot back with the search results given"
          + " to you.";

  private static final String SUMMARY_SYSTEM_PROMPT =
      "You summarise a source in at most 50 words, focused on one given concept. You never invent"
          + " content: if the source text is missing or says nothing about the concept, you reply"
          + " exactly (conteúdo não recuperado).";

  /** Answered by the model when it has nothing legitimate to summarise. */
  public static final String NO_CONTENT_ANSWER = "(conteúdo não recuperado)";

  private static final String TEST_SYSTEM_PROMPT =
      "You are a connectivity probe. Answer with the single word OK.";
  private static final String TEST_USER_PROMPT = "Reply with the single word OK.";

  /**
   * The search probe needs its own system prompt: the connectivity one orders a bare "OK", and it
   * wins over the user message, so the probe always looked like a success without proving anything.
   */
  private static final String SEARCH_TEST_SYSTEM_PROMPT =
      "You have web search available. Answer only from pages you actually retrieved, and always"
          + " include the full URL of the page you used. If you cannot search, say so plainly"
          + " instead of answering from memory.";

  /**
   * Performs a minimal chat completion against the configured endpoint so that the API key, base
   * URL and model can be validated without running a full metaclass suggestion. Reuses the same
   * error mapping as {@link #requestMetaclassSuggestion}, so failures surface as an {@link
   * IOException} carrying a user-facing message.
   *
   * @return the model's reply, trimmed (empty when the model answered with nothing).
   */
  public static String testConnection(LlmConfigurations config) throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body = buildRequestBody(config.getModel(), TEST_SYSTEM_PROMPT, TEST_USER_PROMPT);
    final LlmExchange exchange =
        new LlmExchange("Connection test", config, url, TEST_SYSTEM_PROMPT, TEST_USER_PROMPT);
    final long startedAt = System.currentTimeMillis();

    try {
      final HttpURLConnection connection = request(url, body, config.getApiKey());
      final String content = readMessageContent(connection);

      exchange.succeeded(content, System.currentTimeMillis() - startedAt);
      return content == null ? "" : content.trim();
    } catch (IOException e) {
      exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
      throw e;
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  /** A suggestion together with the conversation that produced it, so the user can inspect it. */
  public static class SuggestionResult {

    private final MetaclassSuggestion suggestion;
    private final LlmExchange exchange;

    private SuggestionResult(MetaclassSuggestion suggestion, LlmExchange exchange) {
      this.suggestion = suggestion;
      this.exchange = exchange;
    }

    public MetaclassSuggestion getSuggestion() {
      return suggestion;
    }

    public LlmExchange getExchange() {
      return exchange;
    }
  }

  public static SuggestionResult requestMetaclassSuggestion(
      LlmConfigurations config, String className, String systemPrompt, String userPrompt)
      throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body = buildRequestBody(config.getModel(), systemPrompt, userPrompt);
    final LlmExchange exchange =
        new LlmExchange(
            "Metaclass suggestion for \"" + className + "\"", config, url, systemPrompt, userPrompt);
    final long startedAt = System.currentTimeMillis();
    String rawContent = null;

    try {
      final HttpURLConnection connection = request(url, body, config.getApiKey());
      rawContent = readMessageContent(connection);

      final MetaclassSuggestion suggestion = parseSuggestion(rawContent);

      if (suggestion.getStereotype() == null
          || !Stereotype.getOntoUMLClassStereotypeNames().contains(suggestion.getStereotype())) {
        throw new IOException(USER_MESSAGE_INVALID_STEREOTYPE);
      }

      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      return new SuggestionResult(suggestion, exchange);
    } catch (IOException e) {
      // keep whatever the model did answer: it is what explains a parsing or validation failure
      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
      throw e;
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  /** The model's answer to one decision-tree question, plus the exchange that produced it. */
  public static class NodeAnswer {

    private final String answer;
    private final String justification;
    private final LlmExchange exchange;

    private NodeAnswer(String answer, String justification, LlmExchange exchange) {
      this.answer = answer;
      this.justification = justification;
      this.exchange = exchange;
    }

    public String getAnswer() {
      return answer;
    }

    public String getJustification() {
      return justification;
    }

    public LlmExchange getExchange() {
      return exchange;
    }
  }

  /**
   * Asks a single question of the decision tree. Used by the detailed, node-by-node strategy, where
   * each node of the tree is one request, so the user can follow and audit every decision.
   */
  public static NodeAnswer askDecisionNode(
      LlmConfigurations config, String label, String prompt) throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body = buildRequestBody(config.getModel(), NODE_SYSTEM_PROMPT, prompt);
    final LlmExchange exchange = new LlmExchange(label, config, url, NODE_SYSTEM_PROMPT, prompt);
    final long startedAt = System.currentTimeMillis();
    String rawContent = null;

    try {
      final HttpURLConnection connection = request(url, body, config.getApiKey());
      rawContent = readMessageContent(connection);

      final JsonObject answerObject =
          new JsonParser().parse(stripCodeFence(rawContent)).getAsJsonObject();
      final String answer =
          answerObject.has("answer") ? answerObject.get("answer").getAsString() : null;
      final String justification =
          answerObject.has("justification") ? answerObject.get("justification").getAsString() : "";

      if (answer == null || answer.trim().isEmpty()) {
        throw new IOException(USER_MESSAGE_UNPARSEABLE_ANSWER);
      }

      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      return new NodeAnswer(answer, justification, exchange);
    } catch (IOException e) {
      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
      throw e;
    } catch (Exception e) {
      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      exchange.failed(USER_MESSAGE_UNPARSEABLE_ANSWER, System.currentTimeMillis() - startedAt);
      throw new IOException(USER_MESSAGE_UNPARSEABLE_ANSWER);
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  /**
   * The model name to send. In {@link SearchMode#MODEL} the provider is asked to search: OpenRouter
   * documents a ":online" suffix for that, while Perplexity/Sonar models search inherently and need
   * nothing. Any other provider simply ignores this and answers from its own knowledge — which is
   * why the settings say plainly that this mode depends on the provider.
   */
  private static String searchAwareModel(LlmConfigurations config) {
    final String model = config.getModel();

    if (config.getSearchMode() != SearchMode.MODEL) {
      return model;
    }

    final boolean isOpenRouter = config.getProvider() == LlmProvider.OPENROUTER;

    return isOpenRouter && !model.endsWith(":online") ? model + ":online" : model;
  }

  /** Fills the documentation template for one class, optionally grounded in search results. */
  public static SuggestionResult requestDocumentation(
      LlmConfigurations config, String className, String userPrompt) throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body =
        buildRequestBody(searchAwareModel(config), DOCUMENTATION_SYSTEM_PROMPT, userPrompt);
    final LlmExchange exchange =
        new LlmExchange(
            "Documentation for \"" + className + "\"",
            config,
            url,
            DOCUMENTATION_SYSTEM_PROMPT,
            userPrompt);
    final long startedAt = System.currentTimeMillis();
    String rawContent = null;

    try {
      final HttpURLConnection connection = request(url, body, config.getApiKey());
      rawContent = readMessageContent(connection);

      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      return new SuggestionResult(null, exchange);
    } catch (IOException e) {
      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
      throw e;
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  /**
   * Summarises one source in at most 50 words, read through the lens of the concept being
   * documented. Used for the sources cited by neighbouring classes, so their content can feed the
   * documentation of this one.
   *
   * @param content the page text, or null when it could not be retrieved.
   * @return the summary, or null when there was nothing legitimate to summarise.
   */
  public static String requestSourceSummary(
      LlmConfigurations config, String concept, String caption, String sourceUrl, String content)
      throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String prompt =
        "Concept being documented: "
            + concept
            + "\n\nSource: "
            + caption
            + "\nURL: "
            + sourceUrl
            + "\n\nSource text:\n"
            + (content == null || content.trim().isEmpty() ? "(not retrieved)" : content)
            + "\n\nIn at most 50 words, summarise what this source says that is relevant to \""
            + concept
            + "\". Reply with the summary only, in the language of the source.";

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body =
        buildRequestBody(searchAwareModel(config), SUMMARY_SYSTEM_PROMPT, prompt);
    final LlmExchange exchange =
        new LlmExchange(
            "Source summary for \"" + concept + "\" — " + caption,
            config,
            url,
            SUMMARY_SYSTEM_PROMPT,
            prompt);
    final long startedAt = System.currentTimeMillis();

    try {
      final HttpURLConnection connection = request(url, body, config.getApiKey());
      final String summary = readMessageContent(connection);

      exchange.succeeded(summary, System.currentTimeMillis() - startedAt);

      if (summary == null || summary.trim().isEmpty() || summary.contains(NO_CONTENT_ANSWER)) {
        return null;
      }

      return summary.trim();
    } catch (IOException e) {
      exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
      throw e;
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  /** Probes provider-side search by asking for something only a fresh lookup can answer well. */
  public static String testModelSearch(LlmConfigurations config) throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String prompt =
        "Search the web and reply with the official title and the URL of Brazilian Lei nº 14.133/2021"
            + " on planalto.gov.br. Reply in one line: TITLE — URL.";
    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body =
        buildRequestBody(searchAwareModel(config), SEARCH_TEST_SYSTEM_PROMPT, prompt);
    final LlmExchange exchange =
        new LlmExchange("Search test (via model)", config, url, SEARCH_TEST_SYSTEM_PROMPT, prompt);
    final long startedAt = System.currentTimeMillis();

    try {
      final HttpURLConnection connection = request(url, body, config.getApiKey());
      final String content = readMessageContent(connection);

      exchange.succeeded(content, System.currentTimeMillis() - startedAt);
      return content == null ? "" : content.trim();
    } catch (IOException e) {
      exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
      throw e;
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  private static String buildRequestBody(String model, String systemPrompt, String userPrompt) {
    final JsonObject systemMessage = new JsonObject();
    systemMessage.addProperty("role", "system");
    systemMessage.addProperty("content", systemPrompt);

    final JsonObject userMessage = new JsonObject();
    userMessage.addProperty("role", "user");
    userMessage.addProperty("content", userPrompt);

    final JsonArray messages = new JsonArray();
    messages.add(systemMessage);
    messages.add(userMessage);

    final JsonObject body = new JsonObject();
    body.addProperty("model", model);
    body.addProperty("temperature", 0);
    body.add("messages", messages);

    return new Gson().toJson(body);
  }

  private static HttpURLConnection request(String urlString, String body, String apiKey)
      throws IOException {
    try {
      final URL url = new URL(urlString);
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      // local providers (Ollama, LM Studio) accept unauthenticated requests
      if (apiKey != null && !apiKey.trim().isEmpty()) {
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
      }
      connection.setConnectTimeout(60000);
      connection.setReadTimeout(60000);
      connection.setDoOutput(true);

      try (OutputStream requestStream = connection.getOutputStream()) {
        requestStream.write(body.getBytes(StandardCharsets.UTF_8));
      }

      switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          return connection;
        case HttpURLConnection.HTTP_UNAUTHORIZED:
        case HttpURLConnection.HTTP_FORBIDDEN:
          throw new IOException(USER_MESSAGE_UNAUTHORIZED);
        case 429:
          throw new IOException(USER_MESSAGE_RATE_LIMITED);
        case HttpURLConnection.HTTP_NOT_FOUND:
          throw new IOException(USER_MESSAGE_NOT_FOUND);
        case HttpURLConnection.HTTP_BAD_REQUEST:
          throw new IOException(USER_MESSAGE_BAD_REQUEST);
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
        case HttpURLConnection.HTTP_UNAVAILABLE:
          throw new IOException(USER_MESSAGE_SERVER_ERROR);
        default:
          throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
      }
    } catch (SocketTimeoutException | SocketException e) {
      throw new IOException(USER_MESSAGE_CONNECTION_ERROR);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
    }
  }

  private static String readMessageContent(HttpURLConnection connection) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
      final String json = reader.lines().collect(Collectors.joining("\n"));
      final JsonObject responseObject = new JsonParser().parse(json).getAsJsonObject();

      return responseObject
          .getAsJsonArray("choices")
          .get(0)
          .getAsJsonObject()
          .getAsJsonObject("message")
          .get("content")
          .getAsString();
    } catch (Exception e) {
      throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
    }
  }

  private static MetaclassSuggestion parseSuggestion(String rawContent) throws IOException {
    try {
      final String cleaned = stripCodeFence(rawContent);
      final MetaclassSuggestion suggestion =
          new Gson().fromJson(cleaned, MetaclassSuggestion.class);

      if (suggestion == null) {
        throw new IOException(USER_MESSAGE_UNPARSEABLE_SUGGESTION);
      }

      return suggestion;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(USER_MESSAGE_UNPARSEABLE_SUGGESTION);
    }
  }

  private static String stripCodeFence(String content) {
    String trimmed = content.trim();

    if (trimmed.startsWith("```")) {
      trimmed = trimmed.substring(trimmed.indexOf('\n') + 1);
      final int closingFence = trimmed.lastIndexOf("```");
      if (closingFence >= 0) {
        trimmed = trimmed.substring(0, closingFence);
      }
    }

    return trimmed.trim();
  }
}
