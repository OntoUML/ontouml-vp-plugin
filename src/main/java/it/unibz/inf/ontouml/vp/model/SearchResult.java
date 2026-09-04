package it.unibz.inf.ontouml.vp.model;

/**
 * One hit returned by a web search, as handed to the model.
 *
 * <p>A hit arrives with the snippet the service chose to advertise it with, which is not the same
 * as what the page says. Once the page itself has been retrieved, {@link #setContent(String)}
 * carries the real text, and that is what the prompt should show.
 */
public class SearchResult {

  private final String title;
  private final String url;
  private final String snippet;

  /** The retrieved page text; null until the page has actually been fetched. */
  private String content;

  public SearchResult(String title, String url, String snippet) {
    this.title = title;
    this.url = url;
    this.snippet = snippet;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getSnippet() {
    return snippet;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  /** Whether the page behind this hit was read, rather than merely listed. */
  public boolean hasContent() {
    return content != null && !content.trim().isEmpty();
  }
}
