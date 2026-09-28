package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The pages a model says it consulted, read from the chat completion it answered with.
 *
 * <p>This is the only evidence that a provider searched the web when it was asked to. The text of
 * an answer proves nothing: a model that cannot search will still name a page, from memory, and
 * for a famous law it will even name the right one. A provider that does search returns, next to
 * the text, the pages it used — as {@code url_citation} annotations on the message (OpenAI,
 * OpenRouter) or as a {@code citations} or {@code search_results} list (Perplexity). No such list
 * means no search happened, whatever the answer looks like.
 */
public class ModelCitations {

  private ModelCitations() {}

  /** The distinct URLs cited by the first choice of the response, in order; empty when none. */
  public static List<String> in(JsonObject response) {
    final Set<String> urls = new LinkedHashSet<>();

    if (response == null) {
      return new ArrayList<>();
    }

    final JsonObject message = firstMessage(response);

    if (message != null) {
      collect(message.get("annotations"), urls);
      collect(message.get("citations"), urls);
    }

    collect(response.get("citations"), urls);
    collect(response.get("search_results"), urls);

    return new ArrayList<>(urls);
  }

  private static JsonObject firstMessage(JsonObject response) {
    final JsonElement choices = response.get("choices");

    if (choices == null || !choices.isJsonArray() || choices.getAsJsonArray().size() == 0) {
      return null;
    }

    final JsonElement first = choices.getAsJsonArray().get(0);

    if (!first.isJsonObject() || !first.getAsJsonObject().has("message")) {
      return null;
    }

    final JsonElement message = first.getAsJsonObject().get("message");

    return message.isJsonObject() ? message.getAsJsonObject() : null;
  }

  /** Accepts a list of URL strings, or of objects that carry one, directly or as url_citation. */
  private static void collect(JsonElement list, Set<String> urls) {
    if (list == null || !list.isJsonArray()) {
      return;
    }

    final JsonArray entries = list.getAsJsonArray();

    for (JsonElement entry : entries) {
      final String url = urlOf(entry);

      if (url != null) {
        urls.add(url);
      }
    }
  }

  private static String urlOf(JsonElement entry) {
    if (entry.isJsonPrimitive()) {
      return isUrl(entry.getAsString()) ? entry.getAsString().trim() : null;
    }

    if (!entry.isJsonObject()) {
      return null;
    }

    final JsonObject object = entry.getAsJsonObject();

    if (object.has("url_citation") && object.get("url_citation").isJsonObject()) {
      return urlOf(object.getAsJsonObject("url_citation"));
    }

    final JsonElement url = object.get("url");

    return url != null && url.isJsonPrimitive() && isUrl(url.getAsString())
        ? url.getAsString().trim()
        : null;
  }

  private static boolean isUrl(String text) {
    final String trimmed = text == null ? "" : text.trim();

    return trimmed.startsWith("http://") || trimmed.startsWith("https://");
  }
}
