package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Captures user preferences for the LLM-based metaclass suggestion feature. Persisted as part of
 * the workspace-level {@link Configurations} file, never inside a shared project file, since it
 * holds an API key.
 */
public class LlmConfigurations {

  public static final LlmProvider DEFAULT_PROVIDER = LlmProvider.OPENAI;

  @SerializedName("provider")
  @Expose()
  private String provider;

  @SerializedName("apiKey")
  @Expose()
  private String apiKey;

  /** Only meaningful for {@link LlmProvider#OTHER}; known providers take their URL from code. */
  @SerializedName("customBaseUrl")
  @Expose()
  private String customBaseUrl;

  @SerializedName("model")
  @Expose()
  private String model;

  /** Empty means "use the text bundled with the plugin". */
  @SerializedName("customSystemPrompt")
  @Expose()
  private String customSystemPrompt;

  @SerializedName("customNodePrompt")
  @Expose()
  private String customNodePrompt;

  @SerializedName("customDecisionTree")
  @Expose()
  private String customDecisionTree;

  @SerializedName("customDocumentationPrompt")
  @Expose()
  private String customDocumentationPrompt;

  @SerializedName("customDocumentationTemplate")
  @Expose()
  private String customDocumentationTemplate;

  @SerializedName("searchMode")
  @Expose()
  private String searchMode;

  /** Enabled search services, in the order they should be tried. */
  @SerializedName("searchProviders")
  @Expose()
  private List<String> searchProviders;

  /** Credential per service: an API key, or the instance URL for a self-hosted one. */
  @SerializedName("searchCredentials")
  @Expose()
  private Map<String, String> searchCredentials;

  /** Kept so a configuration written before multi-provider support is not lost. */
  @SerializedName("searchApiKey")
  @Expose()
  private String searchApiKey;

  public LlmConfigurations() {
    setDefaultValues();
  }

  public void setDefaultValues() {
    this.provider = DEFAULT_PROVIDER.name();
    this.apiKey = "";
    this.customBaseUrl = "";
    this.model = DEFAULT_PROVIDER.getDefaultModel();
    this.searchMode = SearchMode.NONE.name();
    this.searchProviders = new ArrayList<>();
    this.searchCredentials = new LinkedHashMap<>();
    this.searchApiKey = "";
    restorePromptDefaults();
  }

  public SearchMode getSearchMode() {
    return SearchMode.fromName(searchMode);
  }

  public void setSearchMode(SearchMode searchMode) {
    this.searchMode = searchMode.name();
  }

  /**
   * The services to try, in order. A configuration saved before several could be enabled is carried
   * over here, so an existing Tavily key keeps working.
   */
  public List<SearchProvider> getSearchProviders() {
    final List<SearchProvider> providers = new ArrayList<>();

    if (searchProviders != null) {
      for (String name : searchProviders) {
        providers.add(SearchProvider.fromName(name));
      }
    }

    if (providers.isEmpty() && searchApiKey != null && !searchApiKey.trim().isEmpty()) {
      providers.add(SearchProvider.TAVILY);
    }

    return providers;
  }

  public void setSearchProviders(List<SearchProvider> providers) {
    this.searchProviders = new ArrayList<>();

    for (SearchProvider provider : providers) {
      this.searchProviders.add(provider.name());
    }
  }

  /** The API key or instance URL of a service. */
  public String getSearchCredential(SearchProvider provider) {
    if (searchCredentials != null && searchCredentials.containsKey(provider.name())) {
      return searchCredentials.get(provider.name());
    }

    // the single-provider era stored Tavily's key on its own field
    return provider == SearchProvider.TAVILY && searchApiKey != null ? searchApiKey : "";
  }

  public void setSearchCredential(SearchProvider provider, String credential) {
    if (searchCredentials == null) {
      searchCredentials = new LinkedHashMap<>();
    }

    searchCredentials.put(provider.name(), credential == null ? "" : credential);

    if (provider == SearchProvider.TAVILY) {
      searchApiKey = credential;
    }
  }

  /** Services that are both enabled and have their credential filled in. */
  public List<SearchProvider> getUsableSearchProviders() {
    final List<SearchProvider> usable = new ArrayList<>();

    for (SearchProvider provider : getSearchProviders()) {
      if (!getSearchCredential(provider).trim().isEmpty()) {
        usable.add(provider);
      }
    }

    return usable;
  }

  /** Whether research is on and usable, i.e. the search-API mode has at least one usable service. */
  public boolean isSearchUsable() {
    if (getSearchMode() == SearchMode.SEARCH_API) {
      return !getUsableSearchProviders().isEmpty();
    }

    return getSearchMode() == SearchMode.MODEL;
  }

  /** Drops every customisation, so the bundled prompts and decision tree are used again. */
  public void restorePromptDefaults() {
    this.customSystemPrompt = "";
    this.customNodePrompt = "";
    this.customDecisionTree = "";
    this.customDocumentationPrompt = "";
    this.customDocumentationTemplate = "";
  }

  public String getCustomDocumentationPrompt() {
    return customDocumentationPrompt == null ? "" : customDocumentationPrompt;
  }

  public void setCustomDocumentationPrompt(String customDocumentationPrompt) {
    this.customDocumentationPrompt = customDocumentationPrompt;
  }

  public String getCustomDocumentationTemplate() {
    return customDocumentationTemplate == null ? "" : customDocumentationTemplate;
  }

  public void setCustomDocumentationTemplate(String customDocumentationTemplate) {
    this.customDocumentationTemplate = customDocumentationTemplate;
  }

  public String getCustomSystemPrompt() {
    return customSystemPrompt == null ? "" : customSystemPrompt;
  }

  public void setCustomSystemPrompt(String customSystemPrompt) {
    this.customSystemPrompt = customSystemPrompt;
  }

  public String getCustomNodePrompt() {
    return customNodePrompt == null ? "" : customNodePrompt;
  }

  public void setCustomNodePrompt(String customNodePrompt) {
    this.customNodePrompt = customNodePrompt;
  }

  public String getCustomDecisionTree() {
    return customDecisionTree == null ? "" : customDecisionTree;
  }

  public void setCustomDecisionTree(String customDecisionTree) {
    this.customDecisionTree = customDecisionTree;
  }

  public LlmProvider getProvider() {
    return LlmProvider.fromName(provider);
  }

  public void setProvider(LlmProvider provider) {
    this.provider = provider.name();
  }

  public String getApiKey() {
    return apiKey == null ? "" : apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getCustomBaseUrl() {
    return customBaseUrl == null ? "" : customBaseUrl;
  }

  public void setCustomBaseUrl(String customBaseUrl) {
    this.customBaseUrl = customBaseUrl;
  }

  /**
   * The endpoint to call: taken from the selected provider, or from the user-typed URL when the
   * provider is {@link LlmProvider#OTHER}. Any trailing slash is dropped so the endpoint path can
   * be appended safely.
   */
  public String getEffectiveBaseUrl() {
    final LlmProvider selected = getProvider();
    final String url =
        selected.hasCustomBaseUrl() ? getCustomBaseUrl().trim() : selected.getBaseUrl();

    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }

  public String getModel() {
    return model == null ? "" : model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  /** Whether enough is set up to attempt a request: an endpoint and, if required, a key. */
  public boolean isConfigured() {
    if (getEffectiveBaseUrl().isEmpty()) {
      return false;
    }

    return !getProvider().requiresApiKey() || !getApiKey().trim().isEmpty();
  }
}
