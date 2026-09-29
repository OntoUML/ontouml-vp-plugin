package it.unibz.inf.ontouml.vp.model;

/**
 * Search services the plugin can call itself. Several can be enabled at once: the searcher walks
 * them in order and moves on to the next when one fails or times out.
 */
public enum SearchProvider {
  TAVILY("Tavily", "https://api.tavily.com/search", "API key", true),
  BRAVE("Brave", "https://api.search.brave.com/res/v1/web/search", "API key", true),
  /**
   * A self-hosted SearXNG instance. It takes the instance URL instead of a key, and that instance
   * must have the JSON format enabled ({@code formats: [json]} in its settings.yml) — it is off by
   * default and the search will fail with a parsing error until it is turned on.
   */
  SEARXNG("SearXNG (self-hosted)", "", "Instance URL", false);

  private final String label;
  private final String endpoint;
  private final String credentialLabel;
  private final boolean credentialIsSecret;

  SearchProvider(String label, String endpoint, String credentialLabel, boolean credentialIsSecret) {
    this.label = label;
    this.endpoint = endpoint;
    this.credentialLabel = credentialLabel;
    this.credentialIsSecret = credentialIsSecret;
  }

  public String getLabel() {
    return label;
  }

  public String getEndpoint() {
    return endpoint;
  }

  /** What the credential field holds: an API key, or the instance URL for a self-hosted service. */
  public String getCredentialLabel() {
    return credentialLabel;
  }

  /** Whether the credential should be masked in the UI. */
  public boolean isCredentialSecret() {
    return credentialIsSecret;
  }

  @Override
  public String toString() {
    return label;
  }

  public static SearchProvider fromName(String name) {
    for (SearchProvider provider : values()) {
      if (provider.name().equals(name)) {
        return provider;
      }
    }

    return TAVILY;
  }
}
