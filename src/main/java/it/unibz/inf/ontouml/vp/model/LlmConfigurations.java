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

  /**
   * On by default: someone who already has a provider and a key configured from before this
   * switch existed should not have the feature silently turn off under them.
   */
  public static final boolean DEFAULT_LLM_INTEGRATION_ENABLED = true;

  @SerializedName("llmIntegrationEnabled")
  @Expose()
  private Boolean llmIntegrationEnabled;

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

  @SerializedName("customNodeSystemPrompt")
  @Expose()
  private String customNodeSystemPrompt;

  @SerializedName("customNodePrompt")
  @Expose()
  private String customNodePrompt;

  @SerializedName("customDecisionTree")
  @Expose()
  private String customDecisionTree;

  @SerializedName("customDocumentationSystemPrompt")
  @Expose()
  private String customDocumentationSystemPrompt;

  @SerializedName("customDocumentationPrompt")
  @Expose()
  private String customDocumentationPrompt;

  @SerializedName("customDocumentationTemplate")
  @Expose()
  private String customDocumentationTemplate;

  @SerializedName("customSourceSummaryPrompt")
  @Expose()
  private String customSourceSummaryPrompt;

  /**
   * The language the AI is told to write a class's documentation in — free text, sent into the
   * documentation prompt's {@code {language}} placeholder exactly as typed (e.g. "English (US)",
   * "Italiano", "日本語"). The template's own section headings are not affected by it: they stay in
   * English regardless, as a fixed structure {@link it.unibz.inf.ontouml.vp.utils.DocumentationTemplate}
   * reads back.
   */
  @SerializedName("documentationLanguage")
  @Expose()
  private String documentationLanguage;

  /**
   * How far to look for the sources cited around a class before documenting it — the class itself,
   * its diagrams, its neighbours, or none of it. Reading them costs a request per source, sometimes
   * more than one for a long one, so this is where that whole cost is scaled down, or switched off
   * outright, rather than merely tuned.
   */
  @SerializedName("sourceReadScope")
  @Expose()
  private String sourceReadScope;

  /** How many of the sources found are actually fetched and summarised, at most. */
  @SerializedName("maxSourcesToRead")
  @Expose()
  private Integer maxSourcesToRead;

  /** {@link SourceReadMode#MAP_REDUCE} (read everything) or {@link SourceReadMode#TRUNCATE}. */
  @SerializedName("sourceReadMode")
  @Expose()
  private String sourceReadMode;

  /** Only read under {@link SourceReadMode#TRUNCATE}: how much of one source is kept. */
  @SerializedName("maxCharactersPerSource")
  @Expose()
  private Integer maxCharactersPerSource;

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

  /**
   * Whether a reference pointing at a file on disk may be opened and read. On by default: the user
   * attached that file to the class themselves. Off is for whoever would rather nothing local
   * travel to the model.
   */
  @SerializedName("readLocalFileReferences")
  @Expose()
  private Boolean readLocalFileReferences;

  /**
   * Whether documents go through Docling. Off by default, deliberately: it is a Python program the
   * user has to install, and its first run downloads layout models of a few hundred megabytes.
   */
  @SerializedName("useDocling")
  @Expose()
  private Boolean useDocling;

  @SerializedName("doclingCommand")
  @Expose()
  private String doclingCommand;

  @SerializedName("pdftotextCommand")
  @Expose()
  private String pdftotextCommand;

  /** Generous, because converting a long scanned PDF is slow even on a warm install. */
  @SerializedName("documentConversionTimeoutSeconds")
  @Expose()
  private Integer documentConversionTimeoutSeconds;

  /**
   * Whether every exchange is kept and mirrored to disk. Off by default: a prompt carries the
   * user's model, and writing it to a file is a decision they should make on purpose.
   */
  @SerializedName("debugMode")
  @Expose()
  private Boolean debugMode;

  /** Empty means the default location beside this configuration file. */
  @SerializedName("debugLogDirectory")
  @Expose()
  private String debugLogDirectory;

  /**
   * Whether to pause between one call to the model and the next. Off by default: most providers
   * never need it. It is for the ones whose free tier allows a handful of requests a minute, where
   * documenting a class — a request per source, plus the documentation itself — is enough to hit
   * the limit on its own.
   */
  @SerializedName("delayBetweenCallsEnabled")
  @Expose()
  private Boolean delayBetweenCallsEnabled;

  @SerializedName("delayBetweenCallsSeconds")
  @Expose()
  private Integer delayBetweenCallsSeconds;

  public static final int DEFAULT_DELAY_BETWEEN_CALLS_SECONDS = 5;

  /** A pause longer than this is a mistake, not a setting: it would look like a hung dialog. */
  public static final int MAX_DELAY_BETWEEN_CALLS_SECONDS = 300;

  public static final String DEFAULT_DOCLING_COMMAND = "docling";
  public static final String DEFAULT_PDFTOTEXT_COMMAND = "pdftotext";
  public static final int DEFAULT_CONVERSION_TIMEOUT_SECONDS = 180;

  /** As wide as this used to be, unconditionally: reads the class, its diagrams and its neighbours. */
  public static final SourceReadScope DEFAULT_SOURCE_READ_SCOPE = SourceReadScope.ALL_NEIGHBOURS;

  /** Same number this used to be hard-coded to. */
  public static final int DEFAULT_MAX_SOURCES_TO_READ = 12;

  public static final int MAX_SOURCES_TO_READ_LIMIT = 50;

  /** {@link SourceReadMode#MAP_REDUCE} is what this used to do unconditionally. */
  public static final SourceReadMode DEFAULT_SOURCE_READ_MODE = SourceReadMode.MAP_REDUCE;

  /**
   * Only read under {@link SourceReadMode#TRUNCATE}. About 5,000 tokens: generous enough for an
   * ordinary page or article in one piece even though today's frontier models could take far more,
   * because {@link #DEFAULT_MAX_SOURCES_TO_READ} sources share one prompt's budget — twelve of them
   * at this size is already comfortably within even a 128k-token context window, with room left for
   * the rest of the prompt. A source long enough to be cut here is exactly the case
   * {@link SourceReadMode#MAP_REDUCE} exists for.
   */
  public static final int DEFAULT_MAX_CHARACTERS_PER_SOURCE = 20_000;

  public static final int MIN_CHARACTERS_PER_SOURCE = 1_000;
  public static final int MAX_CHARACTERS_PER_SOURCE_LIMIT = 2_000_000;

  public static final String DEFAULT_DOCUMENTATION_LANGUAGE =
      DocumentationLanguage.ENGLISH_US.getPromptValue();

  public LlmConfigurations() {
    setDefaultValues();
  }

  public void setDefaultValues() {
    this.llmIntegrationEnabled = DEFAULT_LLM_INTEGRATION_ENABLED;
    this.provider = DEFAULT_PROVIDER.name();
    this.apiKey = "";
    this.customBaseUrl = "";
    this.model = DEFAULT_PROVIDER.getDefaultModel();
    this.documentationLanguage = DEFAULT_DOCUMENTATION_LANGUAGE;
    this.sourceReadScope = DEFAULT_SOURCE_READ_SCOPE.name();
    this.maxSourcesToRead = DEFAULT_MAX_SOURCES_TO_READ;
    this.sourceReadMode = DEFAULT_SOURCE_READ_MODE.name();
    this.maxCharactersPerSource = DEFAULT_MAX_CHARACTERS_PER_SOURCE;
    this.searchMode = SearchMode.NONE.name();
    this.searchProviders = new ArrayList<>();
    this.searchCredentials = new LinkedHashMap<>();
    this.searchApiKey = "";
    this.readLocalFileReferences = Boolean.TRUE;
    this.useDocling = Boolean.FALSE;
    this.doclingCommand = DEFAULT_DOCLING_COMMAND;
    this.pdftotextCommand = DEFAULT_PDFTOTEXT_COMMAND;
    this.documentConversionTimeoutSeconds = DEFAULT_CONVERSION_TIMEOUT_SECONDS;
    this.debugMode = Boolean.FALSE;
    this.debugLogDirectory = "";
    this.delayBetweenCallsEnabled = Boolean.FALSE;
    this.delayBetweenCallsSeconds = DEFAULT_DELAY_BETWEEN_CALLS_SECONDS;
    restorePromptDefaults();
  }

  /** Read through defaults: a configuration written before this existed has it as null. */
  public boolean isDelayBetweenCalls() {
    return delayBetweenCallsEnabled != null && delayBetweenCallsEnabled;
  }

  public void setDelayBetweenCalls(boolean delayBetweenCallsEnabled) {
    this.delayBetweenCallsEnabled = delayBetweenCallsEnabled;
  }

  public int getDelayBetweenCallsSeconds() {
    if (delayBetweenCallsSeconds == null || delayBetweenCallsSeconds <= 0) {
      return DEFAULT_DELAY_BETWEEN_CALLS_SECONDS;
    }

    return Math.min(delayBetweenCallsSeconds, MAX_DELAY_BETWEEN_CALLS_SECONDS);
  }

  public void setDelayBetweenCallsSeconds(int delayBetweenCallsSeconds) {
    this.delayBetweenCallsSeconds = delayBetweenCallsSeconds;
  }

  public boolean isDebugMode() {
    return debugMode != null && debugMode;
  }

  public void setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }

  public String getDebugLogDirectory() {
    return debugLogDirectory == null ? "" : debugLogDirectory;
  }

  public void setDebugLogDirectory(String debugLogDirectory) {
    this.debugLogDirectory = debugLogDirectory;
  }

  /**
   * The document settings are boxed and read through defaults on purpose: a configuration file
   * written before they existed deserializes them as null, and must keep behaving as a fresh one.
   */
  public boolean isReadLocalFileReferences() {
    return readLocalFileReferences == null || readLocalFileReferences;
  }

  public void setReadLocalFileReferences(boolean readLocalFileReferences) {
    this.readLocalFileReferences = readLocalFileReferences;
  }

  /** How far to look for the sources cited around a class before documenting it. */
  public SourceReadScope getSourceReadScope() {
    return sourceReadScope == null ? DEFAULT_SOURCE_READ_SCOPE : SourceReadScope.fromName(sourceReadScope);
  }

  public void setSourceReadScope(SourceReadScope sourceReadScope) {
    this.sourceReadScope = sourceReadScope.name();
  }

  /** Whether the sources cited in the model are read at all while documenting a class. */
  public boolean isReadCitedSources() {
    return getSourceReadScope() != SourceReadScope.NONE;
  }

  public SourceReadMode getSourceReadMode() {
    return sourceReadMode == null ? DEFAULT_SOURCE_READ_MODE : SourceReadMode.fromName(sourceReadMode);
  }

  public void setSourceReadMode(SourceReadMode sourceReadMode) {
    this.sourceReadMode = sourceReadMode.name();
  }

  public int getMaxSourcesToRead() {
    if (maxSourcesToRead == null || maxSourcesToRead <= 0) {
      return DEFAULT_MAX_SOURCES_TO_READ;
    }

    return Math.min(maxSourcesToRead, MAX_SOURCES_TO_READ_LIMIT);
  }

  public void setMaxSourcesToRead(int maxSourcesToRead) {
    this.maxSourcesToRead = maxSourcesToRead;
  }

  public int getMaxCharactersPerSource() {
    if (maxCharactersPerSource == null || maxCharactersPerSource <= 0) {
      return DEFAULT_MAX_CHARACTERS_PER_SOURCE;
    }

    return Math.min(
        Math.max(maxCharactersPerSource, MIN_CHARACTERS_PER_SOURCE),
        MAX_CHARACTERS_PER_SOURCE_LIMIT);
  }

  public void setMaxCharactersPerSource(int maxCharactersPerSource) {
    this.maxCharactersPerSource = maxCharactersPerSource;
  }

  public boolean isUseDocling() {
    return useDocling != null && useDocling;
  }

  public void setUseDocling(boolean useDocling) {
    this.useDocling = useDocling;
  }

  public String getDoclingCommand() {
    return doclingCommand == null || doclingCommand.trim().isEmpty()
        ? DEFAULT_DOCLING_COMMAND
        : doclingCommand.trim();
  }

  public void setDoclingCommand(String doclingCommand) {
    this.doclingCommand = doclingCommand;
  }

  public String getPdftotextCommand() {
    return pdftotextCommand == null || pdftotextCommand.trim().isEmpty()
        ? DEFAULT_PDFTOTEXT_COMMAND
        : pdftotextCommand.trim();
  }

  public void setPdftotextCommand(String pdftotextCommand) {
    this.pdftotextCommand = pdftotextCommand;
  }

  public int getDocumentConversionTimeoutSeconds() {
    return documentConversionTimeoutSeconds == null || documentConversionTimeoutSeconds <= 0
        ? DEFAULT_CONVERSION_TIMEOUT_SECONDS
        : documentConversionTimeoutSeconds;
  }

  public void setDocumentConversionTimeoutSeconds(int documentConversionTimeoutSeconds) {
    this.documentConversionTimeoutSeconds = documentConversionTimeoutSeconds;
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
    this.customNodeSystemPrompt = "";
    this.customNodePrompt = "";
    this.customDecisionTree = "";
    this.customDocumentationSystemPrompt = "";
    this.customDocumentationPrompt = "";
    this.customDocumentationTemplate = "";
    this.customSourceSummaryPrompt = "";
  }

  public String getCustomNodeSystemPrompt() {
    return customNodeSystemPrompt == null ? "" : customNodeSystemPrompt;
  }

  public void setCustomNodeSystemPrompt(String customNodeSystemPrompt) {
    this.customNodeSystemPrompt = customNodeSystemPrompt;
  }

  public String getCustomDocumentationSystemPrompt() {
    return customDocumentationSystemPrompt == null ? "" : customDocumentationSystemPrompt;
  }

  public void setCustomDocumentationSystemPrompt(String customDocumentationSystemPrompt) {
    this.customDocumentationSystemPrompt = customDocumentationSystemPrompt;
  }

  public String getCustomSourceSummaryPrompt() {
    return customSourceSummaryPrompt == null ? "" : customSourceSummaryPrompt;
  }

  public void setCustomSourceSummaryPrompt(String customSourceSummaryPrompt) {
    this.customSourceSummaryPrompt = customSourceSummaryPrompt;
  }

  /** The language a class's documentation is written in; never blank. */
  public String getDocumentationLanguage() {
    return documentationLanguage == null || documentationLanguage.trim().isEmpty()
        ? DEFAULT_DOCUMENTATION_LANGUAGE
        : documentationLanguage.trim();
  }

  public void setDocumentationLanguage(String documentationLanguage) {
    this.documentationLanguage = documentationLanguage;
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

  /** The master switch: off hides every other AI setting and disables the AI menu actions. */
  public boolean isLlmIntegrationEnabled() {
    return llmIntegrationEnabled == null
        ? DEFAULT_LLM_INTEGRATION_ENABLED
        : llmIntegrationEnabled;
  }

  public void setLlmIntegrationEnabled(boolean llmIntegrationEnabled) {
    this.llmIntegrationEnabled = llmIntegrationEnabled;
  }

  /**
   * Whether enough is set up to attempt a request: the integration is switched on, and there is an
   * endpoint and, if required, a key.
   */
  public boolean isConfigured() {
    if (!isLlmIntegrationEnabled() || getEffectiveBaseUrl().isEmpty()) {
      return false;
    }

    return !getProvider().requiresApiKey() || !getApiKey().trim().isEmpty();
  }
}
