package it.unibz.inf.ontouml.vp.model;

/** How the documentation feature researches the web, if at all. */
public enum SearchMode {
  NONE("No research (model knowledge only)"),
  MODEL("Search via the model (provider must support it)"),
  SEARCH_API("Search API (the plugin searches)");

  private final String label;

  SearchMode(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }

  public static SearchMode fromName(String name) {
    for (SearchMode mode : values()) {
      if (mode.name().equals(name)) {
        return mode;
      }
    }

    return NONE;
  }
}
