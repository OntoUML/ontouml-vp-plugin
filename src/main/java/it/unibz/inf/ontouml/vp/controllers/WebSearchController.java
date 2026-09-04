package it.unibz.inf.ontouml.vp.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmConversationLog;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import it.unibz.inf.ontouml.vp.model.SearchProvider;
import it.unibz.inf.ontouml.vp.model.SearchResult;
import it.unibz.inf.ontouml.vp.utils.WebPageFetcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Runs web searches through a user-configured search service, so the documentation feature can cite
 * sources it has actually seen instead of ones the model remembers.
 *
 * <p>A search service is used for what it is good at: turning a term into candidate pages. It is
 * not used as a source of text — the snippet it returns is an advertisement for a page, written to
 * match the query. Once a hit is in hand, {@link #retrieve} downloads the page itself through
 * {@link WebPageFetcher}, and that text is what {@link #format} puts in front of the model.
 *
 * <p>Mirrors {@link LlmAccessController}'s idiom: {@code HttpURLConnection}, friendly
 * {@code USER_MESSAGE_*} errors, no credential ever leaving this class.
 */
public class WebSearchController {

  private static final int DEFAULT_MAX_RESULTS = 6;

  /** One request per page, so the reading list of a single run has to stay bounded. */
  private static final int DEFAULT_MAX_PAGES = 5;

  /** How much of each retrieved page reaches the prompt; the whole run has to fit in it. */
  private static final int MAX_PAGE_CHARACTERS = 2500;

  /** Candidate pages opened while resolving one citation, before falling back to the first hit. */
  private static final int MAX_RESOLUTION_ATTEMPTS = 3;

  /** Short enough that a stalled service does not hold the rotation up for long. */
  private static final int TIMEOUT_MILLIS = 20000;

  private static final String USER_MESSAGE_MISSING_KEY =
      "No search API key configured. Please set one in the OntoUML Plugin Settings.";
  private static final String USER_MESSAGE_UNAUTHORIZED =
      "The search API rejected the request (invalid key).";
  private static final String USER_MESSAGE_RATE_LIMITED =
      "The search API is rate-limiting requests. Please try again shortly.";
  private static final String USER_MESSAGE_CONNECTION_ERROR =
      "Unable to reach the configured search API.";
  private static final String USER_MESSAGE_UNKNOWN_ERROR_RESPONSE =
      "Unexpected response from the search API.";

  public static List<SearchResult> search(LlmConfigurations config, String query)
      throws IOException {
    return search(config, query, DEFAULT_MAX_RESULTS);
  }

  /**
   * Runs the query against the enabled services in order, moving on to the next whenever one fails,
   * times out or comes back empty. The exchange is recorded either way, so the user can see which
   * service answered and with what.
   */
  public static List<SearchResult> search(LlmConfigurations config, String query, int maxResults)
      throws IOException {
    final List<SearchProvider> providers = config.getUsableSearchProviders();

    if (providers.isEmpty()) {
      throw new IOException(USER_MESSAGE_MISSING_KEY);
    }

    final StringBuilder trail = new StringBuilder();
    IOException lastFailure = null;

    for (SearchProvider provider : providers) {
      final LlmExchange exchange =
          new LlmExchange(
              "Web search (" + provider.getLabel() + ")",
              config,
              endpointOf(provider, config),
              "Web search via " + provider.getLabel(),
              query);
      final long startedAt = System.currentTimeMillis();

      try {
        final List<SearchResult> results = searchWith(provider, config, query, maxResults);

        if (results.isEmpty()) {
          exchange.succeeded("(no results)", System.currentTimeMillis() - startedAt);
          LlmConversationLog.getInstance().record(exchange);
          trail.append(provider.getLabel()).append(": no results. ");
          continue;
        }

        exchange.succeeded(format(results), System.currentTimeMillis() - startedAt);
        LlmConversationLog.getInstance().record(exchange);

        return results;
      } catch (IOException e) {
        exchange.failed(e.getMessage(), System.currentTimeMillis() - startedAt);
        LlmConversationLog.getInstance().record(exchange);

        trail.append(provider.getLabel()).append(": ").append(e.getMessage()).append(" ");
        lastFailure = e;
      }
    }

    if (lastFailure != null) {
      throw new IOException("All search services failed — " + trail.toString().trim());
    }

    return new ArrayList<>();
  }

  private static String endpointOf(SearchProvider provider, LlmConfigurations config) {
    return provider == SearchProvider.SEARXNG
        ? config.getSearchCredential(provider)
        : provider.getEndpoint();
  }

  private static List<SearchResult> searchWith(
      SearchProvider provider, LlmConfigurations config, String query, int maxResults)
      throws IOException {
    final String credential = config.getSearchCredential(provider).trim();

    switch (provider) {
      case BRAVE:
        return searchBrave(credential, query, maxResults);
      case SEARXNG:
        return searchSearxng(credential, query, maxResults);
      default:
        return searchTavily(credential, query, maxResults);
    }
  }

  /**
   * A self-hosted SearXNG instance. Its JSON output is off by default, so a wrong instance answers
   * HTML and the parse fails — which the rotation then treats like any other failure.
   */
  private static List<SearchResult> searchSearxng(
      String instanceUrl, String query, int maxResults) throws IOException {
    final String base = instanceUrl.endsWith("/")
        ? instanceUrl.substring(0, instanceUrl.length() - 1)
        : instanceUrl;
    final String url =
        base + "/search?format=json&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name());

    final HttpURLConnection connection = open(url, "GET", null, null);
    connection.setRequestProperty("Accept", "application/json");

    final JsonObject response = readJson(connection);
    final List<SearchResult> results = new ArrayList<>();

    if (response.has("results")) {
      final JsonArray items = response.getAsJsonArray("results");

      for (JsonElement item : items) {
        if (results.size() >= maxResults) {
          break;
        }

        final JsonObject hit = item.getAsJsonObject();
        results.add(
            new SearchResult(string(hit, "title"), string(hit, "url"), string(hit, "content")));
      }
    }

    return results;
  }

  private static List<SearchResult> searchTavily(String apiKey, String query, int maxResults)
      throws IOException {
    final JsonObject body = new JsonObject();
    body.addProperty("api_key", apiKey);
    body.addProperty("query", query);
    body.addProperty("max_results", maxResults);
    body.addProperty("search_depth", "basic");

    final HttpURLConnection connection =
        open(SearchProvider.TAVILY.getEndpoint(), "POST", null, null);
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);

    try (OutputStream stream = connection.getOutputStream()) {
      stream.write(new Gson().toJson(body).getBytes(StandardCharsets.UTF_8));
    }

    final JsonObject response = readJson(connection);
    final List<SearchResult> results = new ArrayList<>();

    if (response.has("results")) {
      final JsonArray items = response.getAsJsonArray("results");

      for (JsonElement item : items) {
        final JsonObject hit = item.getAsJsonObject();
        results.add(
            new SearchResult(
                string(hit, "title"), string(hit, "url"), string(hit, "content")));
      }
    }

    return results;
  }

  private static List<SearchResult> searchBrave(String apiKey, String query, int maxResults)
      throws IOException {
    final String url =
        SearchProvider.BRAVE.getEndpoint()
            + "?count="
            + maxResults
            + "&q="
            + URLEncoder.encode(query, StandardCharsets.UTF_8.name());

    final HttpURLConnection connection = open(url, "GET", "X-Subscription-Token", apiKey);
    connection.setRequestProperty("Accept", "application/json");

    final JsonObject response = readJson(connection);
    final List<SearchResult> results = new ArrayList<>();

    if (response.has("web") && response.getAsJsonObject("web").has("results")) {
      final JsonArray items = response.getAsJsonObject("web").getAsJsonArray("results");

      for (JsonElement item : items) {
        final JsonObject hit = item.getAsJsonObject();
        results.add(
            new SearchResult(
                string(hit, "title"), string(hit, "url"), string(hit, "description")));
      }
    }

    return results;
  }

  private static HttpURLConnection open(
      String urlString, String method, String headerName, String headerValue) throws IOException {
    try {
      final HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();

      connection.setRequestMethod(method);
      connection.setConnectTimeout(TIMEOUT_MILLIS);
      connection.setReadTimeout(TIMEOUT_MILLIS);

      if (headerName != null) {
        connection.setRequestProperty(headerName, headerValue);
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
          throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
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
      throw new IOException(USER_MESSAGE_UNKNOWN_ERROR_RESPONSE);
    }
  }

  private static String string(JsonObject object, String member) {
    return object.has(member) && !object.get(member).isJsonNull()
        ? object.get(member).getAsString()
        : "";
  }

  /**
   * Whether a URL looks like it comes from an official publisher, used when resolving the link of a
   * normative citation: attaching a blog to "Lei 14.133/2021, art. 8º" would be worse than leaving
   * it without a link.
   */
  public static boolean isOfficial(String url) {
    if (url == null) {
      return false;
    }

    final String host = url.toLowerCase();

    return host.contains(".gov.br")
        || host.contains(".jus.br")
        || host.contains(".leg.br")
        || host.contains(".mp.br")
        || host.contains(".gov/")
        || host.contains(".gov.")
        || host.contains("planalto.")
        || host.contains("europa.eu")
        || host.contains(".un.org")
        || host.contains(".edu")
        || host.contains(".int/");
  }

  /**
   * Looks for the page of a citation that came without a link. Normative citations only accept a
   * result from an official publisher.
   *
   * <p>A candidate is only handed back as a link once its page has been retrieved: a hit whose page
   * cannot be opened is no better than the invented URL this whole step exists to avoid. When none
   * of the candidates can be read, the first acceptable hit is still returned — the search service
   * did find it — but only after the attempt was made.
   *
   * @return the URL found, or null when nothing suitable came back.
   */
  public static String resolveUrl(LlmConfigurations config, String citation, boolean requireOfficial)
      throws IOException {
    final String query = requireOfficial ? citation + " site oficial" : citation;
    String firstAcceptable = null;
    int attempted = 0;

    for (SearchResult result : search(config, query, 5)) {
      if (requireOfficial && !isOfficial(result.getUrl())) {
        continue;
      }

      if (firstAcceptable == null) {
        firstAcceptable = result.getUrl();
      }

      if (attempted++ >= MAX_RESOLUTION_ATTEMPTS) {
        break;
      }

      if (WebPageFetcher.fetch(result.getUrl(), 500) != null) {
        return result.getUrl();
      }
    }

    return firstAcceptable;
  }

  /**
   * Looks for a page about a term and retrieves it, for when the URL a source came with cannot be
   * read: a dead link to "Resolução CNJ 325/2020" does not make the resolution unavailable, it just
   * means the copy has to be found elsewhere.
   *
   * @return a hit whose page was retrieved, or null when none of the candidates could be read.
   */
  public static SearchResult findRetrievablePage(LlmConfigurations config, String term)
      throws IOException {
    int attempted = 0;

    for (SearchResult result : search(config, term, 5)) {
      if (attempted++ >= MAX_RESOLUTION_ATTEMPTS) {
        break;
      }

      final String content = WebPageFetcher.fetch(result.getUrl(), MAX_PAGE_CHARACTERS);

      if (content != null) {
        result.setContent(content);
        return result;
      }
    }

    return null;
  }

  /**
   * Drops repeated URLs, keeping the order the services returned them in. Several queries over the
   * same concept land on the same official page, and retrieving it twice buys nothing.
   */
  public static List<SearchResult> deduplicate(List<SearchResult> results) {
    final Map<String, SearchResult> byUrl = new LinkedHashMap<>();

    for (SearchResult result : results) {
      final String url = result.getUrl() == null ? "" : result.getUrl().trim();

      if (!url.isEmpty() && !byUrl.containsKey(url)) {
        byUrl.put(url, result);
      }
    }

    return new ArrayList<>(byUrl.values());
  }

  /** As {@link #retrieve(LlmConfigurations, List, int)}, for the usual number of pages. */
  public static int retrieve(LlmConfigurations config, List<SearchResult> results) {
    return retrieve(config, results, DEFAULT_MAX_PAGES);
  }

  /**
   * Downloads the pages behind the hits, so the prompt shows what the sources say rather than how a
   * search service chose to advertise them. Hits whose page cannot be retrieved keep only their
   * snippet, and {@link #format} says so on the entry.
   *
   * <p>The round is recorded in the conversation log: which pages were opened, and what came out of
   * each, is exactly what a user needs to check a citation the model then made.
   *
   * @return how many pages were read.
   */
  public static int retrieve(LlmConfigurations config, List<SearchResult> results, int maxPages) {
    final StringBuilder trail = new StringBuilder();
    final long startedAt = System.currentTimeMillis();
    int retrieved = 0;
    int attempted = 0;

    for (SearchResult result : results) {
      // a few failures are expected, but a run of them must not turn into a crawl of every hit
      if (retrieved >= maxPages || attempted >= maxPages + 3) {
        break;
      }

      if (result.hasContent()) {
        continue;
      }

      attempted++;
      final String content = WebPageFetcher.fetch(result.getUrl(), MAX_PAGE_CHARACTERS);

      if (content == null) {
        trail.append("NOT RETRIEVED: ").append(result.getUrl()).append("\n\n");
        continue;
      }

      result.setContent(content);
      retrieved++;
      trail
          .append("RETRIEVED: ")
          .append(result.getUrl())
          .append("\n")
          .append(content)
          .append("\n\n");
    }

    if (attempted == 0) {
      return 0;
    }

    final LlmExchange exchange =
        new LlmExchange(
            "Page retrieval (" + retrieved + " of " + attempted + " pages)",
            config,
            "(direct HTTP / curl)",
            "Retrieval of the pages found by the search services",
            urlsOf(results, attempted));

    exchange.succeeded(trail.toString().trim(), System.currentTimeMillis() - startedAt);
    LlmConversationLog.getInstance().record(exchange);

    return retrieved;
  }

  private static String urlsOf(List<SearchResult> results, int limit) {
    final StringBuilder text = new StringBuilder();
    int listed = 0;

    for (SearchResult result : results) {
      if (listed++ >= limit) {
        break;
      }

      text.append(result.getUrl()).append("\n");
    }

    return text.toString().trim();
  }

  /**
   * Renders hits for the prompt, numbered so the model can point at one. Each entry says plainly
   * whether its page was read or only listed — that distinction is what the prompt's
   * {@code (não verificado)} rule turns on.
   */
  public static String format(List<SearchResult> results) {
    if (results.isEmpty()) {
      return "(no search results)";
    }

    final StringBuilder text = new StringBuilder();
    int number = 0;

    for (SearchResult result : results) {
      number++;
      text.append(number).append(". ").append(result.getTitle()).append("\n");
      text.append("   URL: ").append(result.getUrl()).append("\n");

      if (result.hasContent()) {
        text.append("   [page retrieved — the text below is what it says]\n");
        text.append(indent(result.getContent()));
        continue;
      }

      text.append("   [page not retrieved — search snippet only, a lead rather than a source]\n");

      if (!result.getSnippet().isEmpty()) {
        text.append("   ").append(result.getSnippet().replaceAll("\\s+", " ").trim()).append("\n");
      }
    }

    return text.toString();
  }

  private static String indent(String text) {
    final StringBuilder indented = new StringBuilder();

    for (String line : text.split("\n")) {
      indented.append("   ").append(line).append("\n");
    }

    return indented.toString();
  }
}
