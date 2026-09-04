package it.unibz.inf.ontouml.vp.model;

/**
 * A reference taken from the References tab of a class related to the one being documented, with
 * the summary produced for it — a 50-word reading of that source through the lens of the class
 * under documentation.
 */
public class RelatedSource {

  private final String originClass;
  private final String caption;
  private final String url;

  private String summary;

  /**
   * Where the text actually came from. Normally the source's own URL, but a dead link sends the
   * search service after another copy, and the reader has to be told which page was read.
   */
  private String retrievedFrom;

  public RelatedSource(String originClass, String caption, String url) {
    this.originClass = originClass;
    this.caption = caption;
    this.url = url;
  }

  public String getOriginClass() {
    return originClass;
  }

  public String getCaption() {
    return caption;
  }

  public String getUrl() {
    return url;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getRetrievedFrom() {
    return retrievedFrom == null ? url : retrievedFrom;
  }

  public void setRetrievedFrom(String retrievedFrom) {
    this.retrievedFrom = retrievedFrom;
  }

  /** Whether the text was read somewhere other than the URL the source was cited with. */
  public boolean wasRetrievedElsewhere() {
    return retrievedFrom != null && !retrievedFrom.equals(url);
  }

  public boolean hasSummary() {
    return summary != null && !summary.trim().isEmpty();
  }
}
