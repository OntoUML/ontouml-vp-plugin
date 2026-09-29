package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One round trip to the LLM: what was asked and what came back. Kept so the user can inspect the
 * exact conversation behind a suggestion.
 *
 * <p>The API key is deliberately not part of this record — only the endpoint, provider and model
 * are, so the log can be read and shared without leaking credentials.
 */
public class LlmExchange {

  private static final DateTimeFormatter TIMESTAMP_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final LocalDateTime timestamp = LocalDateTime.now();
  private final String label;
  private final String provider;
  private final String model;
  private final String endpoint;
  private final String systemPrompt;
  private final String userPrompt;

  private String response;
  private String error;
  private long durationMillis;
  private List<String> citations = new ArrayList<>();

  public LlmExchange(
      String label,
      LlmConfigurations config,
      String endpoint,
      String systemPrompt,
      String userPrompt) {
    this.label = label;
    this.provider = config.getProvider().getLabel();
    this.model = config.getModel();
    this.endpoint = endpoint;
    this.systemPrompt = systemPrompt;
    this.userPrompt = userPrompt;
  }

  public void succeeded(String response, long durationMillis) {
    this.response = response;
    this.durationMillis = durationMillis;
  }

  /** The pages the provider says it consulted; empty when it returned none. */
  public void setCitations(List<String> citations) {
    this.citations = citations == null ? new ArrayList<>() : new ArrayList<>(citations);
  }

  public List<String> getCitations() {
    return Collections.unmodifiableList(citations);
  }

  public void failed(String error, long durationMillis) {
    this.error = error;
    this.durationMillis = durationMillis;
  }

  public boolean hasFailed() {
    return error != null;
  }

  public String getTimestamp() {
    return timestamp.format(TIMESTAMP_FORMAT);
  }

  public String getSystemPrompt() {
    return systemPrompt;
  }

  public String getUserPrompt() {
    return userPrompt;
  }

  public String getResponse() {
    return response;
  }

  /** Single-line entry for the list of conversations. */
  @Override
  public String toString() {
    return getTimestamp() + "  —  " + label + (hasFailed() ? "  (failed)" : "");
  }

  /** The whole exchange as readable text, for the detail pane and for copying out. */
  public String toDetailedText() {
    final StringBuilder text = new StringBuilder();

    text.append("Timestamp: ").append(getTimestamp()).append("\n");
    text.append("Action:    ").append(label).append("\n");
    text.append("Provider:  ").append(provider).append("\n");
    text.append("Model:     ").append(model).append("\n");
    text.append("Endpoint:  ").append(endpoint).append("\n");
    text.append("Duration:  ").append(durationMillis).append(" ms\n");
    text.append("Outcome:   ").append(hasFailed() ? "FAILED — " + error : "success").append("\n");

    text.append("\n===================== SYSTEM PROMPT =====================\n");
    text.append(systemPrompt == null ? "(none)" : systemPrompt);

    text.append("\n\n====================== USER PROMPT ======================\n");
    text.append(userPrompt == null ? "(none)" : userPrompt);

    text.append("\n\n======================= RESPONSE ========================\n");
    text.append(response == null ? "(no response received)" : response);
    text.append("\n");

    return text.toString();
  }
}
