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
import it.unibz.inf.ontouml.vp.utils.LlmRequestPacing;
import it.unibz.inf.ontouml.vp.utils.ModelCitations;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
  /**
   * How much of a provider's error body is worth putting in a dialog. Enough for the sentence that
   * names the cause, short of pasting a stack trace into a pop-up.
   */
  private static final int MAX_ERROR_DETAIL = 400;

  /** Width the dialog text is wrapped to; JOptionPane does not wrap a long line on its own. */
  private static final int DIALOG_WIDTH = 90;

  private static final String LOG_HINT =
      "The whole exchange — prompt included — is in the AI Conversation Log, under the OntoUML"
          + " Plugin menu.";

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

  /**
   * Whether an answer is the model declining to summarise, rather than a summary.
   *
   * <p>Matching {@link #NO_CONTENT_ANSWER} literally is not enough, and the cost of getting this
   * wrong is high: a small model asked for "exactly (conteúdo não recuperado)" answered
   * <em>conteúdo não recuperado</em> without the parentheses, the literal check missed it, and the
   * refusal went into the prompt sitting where a summary goes — telling the model that a source
   * nobody could open had been read, and throwing away the real reason it was not.
   *
   * <p>So the comparison is made on normalised text, and only over a short answer: a genuine
   * summary that happens to mention the phrase is a summary, not a refusal.
   */
  public static boolean isNoContentAnswer(String answer) {
    if (answer == null || answer.trim().isEmpty()) {
      return true;
    }

    final String normalised = normalise(answer);

    // a refusal is a sentence; anything longer is a summary that merely mentions one
    return normalised.length() <= 60 && normalised.contains(normalise(NO_CONTENT_ANSWER));
  }

  /** Lower case, unaccented, free of punctuation and of runs of whitespace. */
  private static String normalise(String text) {
    return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(java.util.Locale.ROOT)
        .replaceAll("[^a-z0-9]+", " ")
        .trim();
  }

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
      final HttpURLConnection connection = sendOnce(url, body, config.getApiKey());
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
      final HttpURLConnection connection = request(config, url, body);
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

  /**
   * One answer to a decision-tree question, in the shape the node prompt asks the model for:
   * an answer, a justification and a confidence.
   *
   * <p>It is the single internal format for an answer whatever its origin, so the walk, the prompts
   * and the log treat an answer the modeller gave exactly like one the model produced. Only the
   * display says which is which, through {@link #isTaughtByUser()}.
   */
  public static class NodeAnswer {

    private final String answer;
    private final String justification;
    private final double confidence;
    private final LlmExchange exchange;

    private NodeAnswer(
        String answer, String justification, double confidence, LlmExchange exchange) {
      this.answer = answer;
      this.justification = justification;
      this.confidence = confidence;
      this.exchange = exchange;
    }

    /** The answer as the model gave it, carrying the exchange that produced it. */
    public static NodeAnswer fromModel(
        String answer, String justification, double confidence, LlmExchange exchange) {
      return new NodeAnswer(answer, justification, confidence, exchange);
    }

    /**
     * The answer the modeller decided on. There is no round trip behind it, hence no exchange, and
     * it is taken as certain: the walk must follow it rather than weigh it.
     */
    public static NodeAnswer taughtByUser(String answer, String justification) {
      return new NodeAnswer(answer, justification, 1.0, null);
    }

    public String getAnswer() {
      return answer;
    }

    public String getJustification() {
      return justification;
    }

    /** Between 0.0 and 1.0; 0.0 when the model did not report one. */
    public double getConfidence() {
      return confidence;
    }

    /** Null for an answer taught by the modeller, since nothing was asked of the model. */
    public LlmExchange getExchange() {
      return exchange;
    }

    public boolean isTaughtByUser() {
      return exchange == null;
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
      final HttpURLConnection connection = request(config, url, body);
      rawContent = readMessageContent(connection);

      final JsonObject answerObject =
          new JsonParser().parse(stripCodeFence(rawContent)).getAsJsonObject();
      final String answer =
          answerObject.has("answer") ? answerObject.get("answer").getAsString() : null;
      final String justification =
          answerObject.has("justification") ? answerObject.get("justification").getAsString() : "";
      final double confidence = readConfidence(answerObject);

      if (answer == null || answer.trim().isEmpty()) {
        throw new IOException(USER_MESSAGE_UNPARSEABLE_ANSWER);
      }

      exchange.succeeded(rawContent, System.currentTimeMillis() - startedAt);
      return NodeAnswer.fromModel(answer, justification, confidence, exchange);
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
   * The confidence the node prompt asks for. It is informative only, so a missing or non-numeric
   * value costs nothing: the answer still stands, reported as an unknown confidence of 0.
   */
  private static double readConfidence(JsonObject answerObject) {
    if (!answerObject.has("confidence")) {
      return 0.0;
    }

    try {
      return answerObject.get("confidence").getAsDouble();
    } catch (Exception e) {
      return 0.0;
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
      final HttpURLConnection connection = request(config, url, body);
      final Reply reply = readReply(connection);
      rawContent = reply.content;

      exchange.setCitations(reply.citations);
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
    return requestSourceSummary(config, concept, caption, sourceUrl, content, null);
  }

  /**
   * As {@link #requestSourceSummary(LlmConfigurations, String, String, String, String)}, for one
   * part of a source too long to send at once.
   *
   * @param part how this piece sits in the whole, such as "parte 2 de 5", or null for a whole
   *     source. It is told to the model so it does not treat a mid-document excerpt as if the
   *     document began and ended there.
   */
  public static String requestSourceSummary(
      LlmConfigurations config,
      String concept,
      String caption,
      String sourceUrl,
      String content,
      String part)
      throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final String prompt =
        "Concept being documented: "
            + concept
            + "\n\nSource: "
            + caption
            + (part == null ? "" : " (" + part + ")")
            + "\nURL: "
            + sourceUrl
            + "\n\nSource text:\n"
            + (content == null || content.trim().isEmpty() ? "(not retrieved)" : content)
            + (part == null
                ? ""
                : "\n\nThis is one excerpt of a longer document; do not assume it begins or ends"
                    + " the document.")
            + "\n\nIn at most 50 words, summarise what this source says that is relevant to \""
            + concept
            + "\". Reply with the summary only, in the language of the source.";

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body =
        buildRequestBody(searchAwareModel(config), SUMMARY_SYSTEM_PROMPT, prompt);
    final LlmExchange exchange =
        new LlmExchange(
            "Source summary for \""
                + concept
                + "\" — "
                + caption
                + (part == null ? "" : " (" + part + ")"),
            config,
            url,
            SUMMARY_SYSTEM_PROMPT,
            prompt);
    final long startedAt = System.currentTimeMillis();

    try {
      final HttpURLConnection connection = request(config, url, body);
      final String summary = readMessageContent(connection);

      exchange.succeeded(summary, System.currentTimeMillis() - startedAt);

      if (isNoContentAnswer(summary)) {
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

  /**
   * Consolidates the partial summaries of a long document into one summary of the whole.
   *
   * <p>The reduce half of reading a document too large to send at once. It is given the parts in
   * order and nothing else: a part that said nothing about the concept contributes nothing, and the
   * model is told not to fill the gap from what it remembers about the document.
   *
   * @return the consolidated summary, or null when the parts said nothing worth keeping.
   */
  public static String requestSourceDigest(
      LlmConfigurations config, String concept, String caption, String sourceUrl, List<String> parts)
      throws IOException {
    if (!config.isConfigured()) {
      throw new IOException(USER_MESSAGE_MISSING_API_KEY);
    }

    final StringBuilder numbered = new StringBuilder();

    for (int i = 0; i < parts.size(); i++) {
      numbered.append(i + 1).append(". ").append(parts.get(i).trim()).append("\n");
    }

    final String prompt =
        "Concept being documented: "
            + concept
            + "\n\nSource: "
            + caption
            + "\nURL: "
            + sourceUrl
            + "\n\nThe source was too long to read at once, so it was read in parts. These are the"
            + " summaries of those parts, in order:\n\n"
            + numbered
            + "\nIn at most 80 words, consolidate them into a single summary of what this source"
            + " says that is relevant to \""
            + concept
            + "\". Use only what the parts state — add nothing from your own knowledge of the"
            + " document. Reply with the summary only, in the language of the source.";

    final String url = config.getEffectiveBaseUrl() + CHAT_COMPLETIONS_ENDPOINT;
    final String body = buildRequestBody(searchAwareModel(config), SUMMARY_SYSTEM_PROMPT, prompt);
    final LlmExchange exchange =
        new LlmExchange(
            "Source digest for \"" + concept + "\" — " + caption + " (" + parts.size() + " parts)",
            config,
            url,
            SUMMARY_SYSTEM_PROMPT,
            prompt);
    final long startedAt = System.currentTimeMillis();

    try {
      final HttpURLConnection connection = request(config, url, body);
      final String summary = readMessageContent(connection);

      exchange.succeeded(summary, System.currentTimeMillis() - startedAt);

      if (isNoContentAnswer(summary)) {
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

  /** What the probe of provider-side search found out. */
  public static class ModelSearchProbe {

    private final String reply;
    private final List<String> citations;

    private ModelSearchProbe(String reply, List<String> citations) {
      this.reply = reply;
      this.citations = citations;
    }

    /** What the model answered. */
    public String getReply() {
      return reply;
    }

    /** The pages the provider says it consulted. */
    public List<String> getCitations() {
      return citations;
    }

    /**
     * Whether the provider searched. Only the pages it returns alongside the answer prove it: the
     * text may well name the right page from memory.
     */
    public boolean isSearching() {
      return !citations.isEmpty();
    }
  }

  /**
   * Probes provider-side search. The verdict does not rest on what the model wrote, which a model
   * without search can get right from memory, but on whether the provider returned the pages it
   * used; see {@link ModelCitations}.
   */
  public static ModelSearchProbe testModelSearch(LlmConfigurations config) throws IOException {
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
      final HttpURLConnection connection = sendOnce(url, body, config.getApiKey());
      final Reply reply = readReply(connection);

      exchange.setCitations(reply.citations);
      exchange.succeeded(reply.content, System.currentTimeMillis() - startedAt);
      return new ModelSearchProbe(reply.content == null ? "" : reply.content.trim(), reply.citations);
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

  /**
   * Sends a request, pausing before it if the user asked for a delay between calls, and waiting and
   * retrying when the provider answers HTTP 429.
   *
   * <p>A refusal for going too fast is not a failure of the request, only of its timing, and a run
   * that has already read a dozen sources should not be thrown away because the thirteenth call
   * arrived a second too early. So a 429 is waited out — {@value
   * LlmRequestPacing#RATE_LIMIT_WAIT_SECONDS} seconds, or longer when the provider says so — and
   * the same request is sent again, up to {@value LlmRequestPacing#MAX_RATE_LIMIT_RETRIES} times.
   * Every other error is reported at once: retrying a wrong key or a wrong model only delays the
   * message.
   */
  private static HttpURLConnection request(LlmConfigurations config, String url, String body)
      throws IOException {
    int retries = 0;

    while (true) {
      LlmRequestPacing.beforeCall(config);

      try {
        return sendOnce(url, body, config.getApiKey());
      } catch (RateLimitedException e) {
        final int wait = LlmRequestPacing.rateLimitWaitSeconds(e.getRetryAfter());

        if (wait < 0) {
          throw new IOException(
              e.getMessage()
                  + "\n\n"
                  + wrap(
                      "The provider asked to wait "
                          + e.getRetryAfter()
                          + " seconds, which is longer than is worth waiting here: this looks like"
                          + " a spent quota rather than a pace that is too fast."));
        }

        if (retries >= LlmRequestPacing.MAX_RATE_LIMIT_RETRIES) {
          throw new IOException(
              e.getMessage()
                  + "\n\n"
                  + wrap(
                      "The plugin waited "
                          + wait
                          + " seconds and tried again "
                          + retries
                          + " times, and the provider kept refusing. The limit may be a daily quota,"
                          + " or a per-minute one that this run keeps exceeding — the delay"
                          + " between calls in the OntoUML Plugin Settings can be raised."));
        }

        retries++;
        LlmRequestPacing.waitAfterRateLimit(
            config, url, retries, LlmRequestPacing.MAX_RATE_LIMIT_RETRIES, wait);
      } finally {
        LlmRequestPacing.afterCall(config);
      }
    }
  }

  /** A 429, kept apart from other errors so {@link #request} can wait it out. */
  private static final class RateLimitedException extends IOException {

    private static final long serialVersionUID = 1L;

    private final String retryAfter;

    private RateLimitedException(String message, String retryAfter) {
      super(message);
      this.retryAfter = retryAfter;
    }

    /** The {@code Retry-After} header, or null. */
    private String getRetryAfter() {
      return retryAfter;
    }
  }

  /** One attempt, with no pause and no retry. */
  private static HttpURLConnection sendOnce(String urlString, String body, String apiKey)
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

      final int status = connection.getResponseCode();

      if (status == HttpURLConnection.HTTP_OK) {
        return connection;
      }

      if (status == 429) {
        throw new RateLimitedException(
            errorMessage(connection, status), connection.getHeaderField("Retry-After"));
      }

      throw new IOException(errorMessage(connection, status));
    } catch (SocketTimeoutException | SocketException e) {
      throw new IOException(USER_MESSAGE_CONNECTION_ERROR);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
    }
  }

  /**
   * The text the user is shown when the API refuses the request.
   *
   * <p>The friendly summary alone was not enough to act on: "The LLM API returned a server error"
   * says nothing about whether the model name is wrong, the context window was exceeded or the
   * provider's upstream is simply down. Providers put that in the body of the error response, so
   * the body is read and quoted here, next to the status code and a line on what to do about it.
   */
  private static String errorMessage(HttpURLConnection connection, int status) {
    final StringBuilder message = new StringBuilder();

    final String summary = summaryOf(status);

    // the status belongs inside the sentence, not tacked on after its full stop
    message
        .append(summary.endsWith(".") ? summary.substring(0, summary.length() - 1) : summary)
        .append(" (HTTP ")
        .append(status)
        .append(").");

    final String detail = errorDetail(connection);

    if (detail != null) {
      message.append("\n\nThe provider answered:\n").append(wrap(detail));
    }

    message.append("\n\nEndpoint: ").append(connection.getURL());
    message.append("\n\n").append(wrap(adviceFor(status)));
    message.append("\n\n").append(wrap(LOG_HINT));

    return message.toString();
  }

  private static String summaryOf(int status) {
    switch (status) {
      case HttpURLConnection.HTTP_UNAUTHORIZED:
      case HttpURLConnection.HTTP_FORBIDDEN:
        return USER_MESSAGE_UNAUTHORIZED;
      case 429:
        return USER_MESSAGE_RATE_LIMITED;
      case HttpURLConnection.HTTP_NOT_FOUND:
        return USER_MESSAGE_NOT_FOUND;
      case HttpURLConnection.HTTP_BAD_REQUEST:
        return USER_MESSAGE_BAD_REQUEST;
      case HttpURLConnection.HTTP_INTERNAL_ERROR:
      case HttpURLConnection.HTTP_UNAVAILABLE:
        return USER_MESSAGE_SERVER_ERROR;
      default:
        return USER_MESSAGE_UNKNOWN_ERROR_RESPONSE;
    }
  }

  /** What the user can actually do about this status, which is the point of showing it at all. */
  private static String adviceFor(int status) {
    switch (status) {
      case HttpURLConnection.HTTP_UNAUTHORIZED:
      case HttpURLConnection.HTTP_FORBIDDEN:
        return "Check the API key in the OntoUML Plugin Settings, and that it is valid for this"
            + " provider.";
      case 429:
        return "You have reached the provider's rate or quota limit. If it keeps happening, check"
            + " the credit on your account, or add a delay between calls in the OntoUML Plugin"
            + " Settings.";
      case HttpURLConnection.HTTP_NOT_FOUND:
        return "Check the base URL and the model name in the OntoUML Plugin Settings — a model the"
            + " provider does not serve answers 404 just like a wrong URL.";
      case HttpURLConnection.HTTP_BAD_REQUEST:
        return "Usually the model name is wrong for this provider, or the prompt exceeded the"
            + " model's context window. A class with many neighbours and many sources makes a long"
            + " prompt; try a model with a larger context.";
      case HttpURLConnection.HTTP_INTERNAL_ERROR:
      case HttpURLConnection.HTTP_UNAVAILABLE:
        return "This is a fault on the provider's side, not in your model or your settings. Try"
            + " again in a moment; if it persists, switch to another model or provider in the"
            + " OntoUML Plugin Settings.";
      default:
        return "Please report this if it keeps happening.";
    }
  }

  /**
   * The provider's own account of what went wrong. Bodies compatible with the OpenAI API carry it
   * at {@code error.message}; anything else is shown as it came, trimmed.
   *
   * @return the description, or null when the response carried none.
   */
  private static String errorDetail(HttpURLConnection connection) {
    final String body = errorBody(connection);

    if (body == null || body.trim().isEmpty()) {
      return null;
    }

    try {
      final JsonObject parsed = new JsonParser().parse(body).getAsJsonObject();
      final JsonObject error =
          parsed.has("error") && parsed.get("error").isJsonObject()
              ? parsed.getAsJsonObject("error")
              : parsed;

      for (String member : new String[] {"message", "detail", "error"}) {
        if (error.has(member) && error.get(member).isJsonPrimitive()) {
          return truncate(error.get(member).getAsString().trim());
        }
      }
    } catch (Exception e) {
      // not JSON, or not the shape we know: the raw body is still better than nothing
    }

    return truncate(body.replaceAll("\\s+", " ").trim());
  }

  private static String errorBody(HttpURLConnection connection) {
    final InputStream stream = connection.getErrorStream();

    if (stream == null) {
      return null;
    }

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (Exception e) {
      return null;
    }
  }

  private static String truncate(String text) {
    return text.length() <= MAX_ERROR_DETAIL
        ? text
        : text.substring(0, MAX_ERROR_DETAIL) + "…";
  }

  /** JOptionPane shows a plain string as-is, so a long line has to be broken by hand. */
  private static String wrap(String text) {
    final StringBuilder wrapped = new StringBuilder();
    int lineLength = 0;

    for (String word : text.split(" ")) {
      if (lineLength > 0 && lineLength + 1 + word.length() > DIALOG_WIDTH) {
        wrapped.append("\n");
        lineLength = 0;
      } else if (lineLength > 0) {
        wrapped.append(" ");
        lineLength++;
      }

      wrapped.append(word);
      lineLength += word.length();
    }

    return wrapped.toString();
  }

  private static String readMessageContent(HttpURLConnection connection) throws IOException {
    return readReply(connection).content;
  }

  /** What a chat completion carried: the answer, and the pages the provider says it used. */
  private static final class Reply {
    private final String content;
    private final List<String> citations;

    private Reply(String content, List<String> citations) {
      this.content = content;
      this.citations = citations;
    }
  }

  private static Reply readReply(HttpURLConnection connection) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
      final String json = reader.lines().collect(Collectors.joining("\n"));
      final JsonObject responseObject = new JsonParser().parse(json).getAsJsonObject();

      final String content =
          responseObject
              .getAsJsonArray("choices")
              .get(0)
              .getAsJsonObject()
              .getAsJsonObject("message")
              .get("content")
              .getAsString();

      return new Reply(content, ModelCitations.in(responseObject));
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
