package it.unibz.inf.ontouml.vp.model;

/**
 * Known providers of an OpenAI-compatible Chat Completions API, with the endpoint the plugin
 * should talk to and a sensible starting model.
 *
 * <p>Every provider listed here exposes the same <code>/chat/completions</code> contract and
 * authenticates with an <code>Authorization: Bearer &lt;key&gt;</code> header, which is why a
 * single client works for all of them — only the base URL changes. Providers that run locally need
 * no key at all. Anything else is covered by {@link #OTHER}, where the user types the URL.
 *
 * <p>Model names are only starting points: they change over time and the field stays editable.
 */
public enum LlmProvider {
  OPENAI("OpenAI", "https://api.openai.com/v1", "gpt-4o-mini", true),
  GEMINI(
      "Google Gemini",
      "https://generativelanguage.googleapis.com/v1beta/openai",
      "gemini-2.0-flash",
      true),
  OPENROUTER("OpenRouter", "https://openrouter.ai/api/v1", "openai/gpt-4o-mini", true),
  GROQ("Groq", "https://api.groq.com/openai/v1", "llama-3.3-70b-versatile", true),
  MISTRAL("Mistral", "https://api.mistral.ai/v1", "mistral-small-latest", true),
  DEEPSEEK("DeepSeek", "https://api.deepseek.com/v1", "deepseek-chat", true),
  OLLAMA("Ollama (local)", "http://localhost:11434/v1", "llama3.1", false),
  LM_STUDIO("LM Studio (local)", "http://localhost:1234/v1", "local-model", false),
  OTHER("Other (custom URL)", "", "", true);

  private final String label;
  private final String baseUrl;
  private final String defaultModel;
  private final boolean requiresApiKey;

  LlmProvider(String label, String baseUrl, String defaultModel, boolean requiresApiKey) {
    this.label = label;
    this.baseUrl = baseUrl;
    this.defaultModel = defaultModel;
    this.requiresApiKey = requiresApiKey;
  }

  public String getLabel() {
    return label;
  }

  /** The endpoint to call, empty for {@link #OTHER} where the user supplies it. */
  public String getBaseUrl() {
    return baseUrl;
  }

  public String getDefaultModel() {
    return defaultModel;
  }

  /** Local providers accept requests without credentials. */
  public boolean requiresApiKey() {
    return requiresApiKey;
  }

  /** Whether the base URL is typed by the user rather than taken from this enum. */
  public boolean hasCustomBaseUrl() {
    return this == OTHER;
  }

  @Override
  public String toString() {
    return label;
  }

  /** Resolves a persisted provider name, falling back to the default when it is unknown. */
  public static LlmProvider fromName(String name) {
    for (LlmProvider provider : values()) {
      if (provider.name().equals(name)) {
        return provider;
      }
    }

    return OPENAI;
  }

  /** True when the given URL is one of the built-in endpoints, i.e. not user-typed. */
  public static boolean isKnownBaseUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      return true;
    }

    for (LlmProvider provider : values()) {
      if (!provider.getBaseUrl().isEmpty() && provider.getBaseUrl().equals(url.trim())) {
        return true;
      }
    }

    return false;
  }

  /** True when the given model name is one of the built-in defaults, i.e. not user-chosen. */
  public static boolean isDefaultModel(String model) {
    if (model == null || model.trim().isEmpty()) {
      return true;
    }

    for (LlmProvider provider : values()) {
      if (provider.getDefaultModel().equals(model.trim())) {
        return true;
      }
    }

    return false;
  }
}
