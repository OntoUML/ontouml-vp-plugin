package it.unibz.inf.ontouml.vp.model;

/**
 * A reference reachable from the class being documented — its own, a neighbour's, one on a diagram
 * it appears in, or one on a package above it — together with what came of reading it: a summary of
 * that source through the lens of this class, or the reason there is none.
 */
public class RelatedSource {

  private final SourceScope scope;

  /** The element the reference was found on, named for the prompt: "classe X", "diagrama Y". */
  private final String origin;

  private final String caption;

  /** A URL, a file path, or empty for a reference that names a source without pointing at one. */
  private final String locator;

  /** The reference's own description, worth using as a search query when there is no locator. */
  private final String description;

  private String summary;

  /**
   * Why there is no summary, for a source that could not be read: "PDF não convertido (Docling
   * desligado…)", "arquivo não encontrado: …". It goes into the prompt so the model still knows the
   * class cites this source, and knows it was not verified.
   */
  private String unreadReason;

  /**
   * Where the text actually came from. Normally the source's own locator, but a dead link sends the
   * search service after another copy, and the reader has to be told which page was read.
   */
  private String retrievedFrom;

  public RelatedSource(
      SourceScope scope, String origin, String caption, String locator, String description) {
    this.scope = scope;
    this.origin = origin;
    this.caption = caption;
    this.locator = locator == null ? "" : locator.trim();
    this.description = description == null ? "" : description.trim();
  }

  public SourceScope getScope() {
    return scope;
  }

  /** How to name the origin in the prompt: "a própria classe", "classe Contrato", "diagrama X". */
  public String getOrigin() {
    return scope.describe(origin);
  }

  public String getCaption() {
    return caption;
  }

  public String getLocator() {
    return locator;
  }

  public boolean hasLocator() {
    return !locator.isEmpty();
  }

  public String getDescription() {
    return description;
  }

  /** What to ask a search service for, when the locator is missing or led nowhere. */
  public String getSearchTerm() {
    if (description.isEmpty() || description.equals(caption)) {
      return caption;
    }

    return caption + " " + description;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public boolean hasSummary() {
    return summary != null && !summary.trim().isEmpty();
  }

  /** Never null once a read failed: "[not read] null" in a prompt would be worse than vague. */
  public String getUnreadReason() {
    return unreadReason == null || unreadReason.trim().isEmpty()
        ? "não foi possível ler esta fonte"
        : unreadReason.trim();
  }

  public void setUnreadReason(String unreadReason) {
    this.unreadReason = unreadReason;
  }

  public String getRetrievedFrom() {
    return retrievedFrom == null ? locator : retrievedFrom;
  }

  public void setRetrievedFrom(String retrievedFrom) {
    this.retrievedFrom = retrievedFrom;
  }

  /** Whether the text was read somewhere other than the locator the source was cited with. */
  public boolean wasRetrievedElsewhere() {
    return retrievedFrom != null && !retrievedFrom.equals(locator);
  }
}
