package it.unibz.inf.ontouml.vp.model;

/**
 * How much of one retrieved source is actually consumed before it is summarised.
 *
 * <p>{@link #MAP_REDUCE} is the mechanism this plugin has always used — see {@code
 * SourceSummarizer.summarise}: a source that fits one request is summarised whole, and a longer
 * one is split into parts, each summarised with the class in focus, then consolidated into one
 * digest. It costs a request per part but reads the whole source, tail included — which is where a
 * long norm's actual duties tend to sit.
 *
 * <p>{@link #TRUNCATE} is the cheaper alternative this settles for when that cost is not worth
 * paying: only the first {@code maxCharactersPerSource} characters are read, in one request, and
 * whatever comes after is simply not seen.
 */
public enum SourceReadMode {
  MAP_REDUCE("Read the whole source (map-reduce over long documents)"),
  TRUNCATE("Read only the first N characters");

  private final String label;

  SourceReadMode(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }

  public static SourceReadMode fromName(String name) {
    for (SourceReadMode mode : values()) {
      if (mode.name().equals(name)) {
        return mode;
      }
    }

    return MAP_REDUCE;
  }
}
