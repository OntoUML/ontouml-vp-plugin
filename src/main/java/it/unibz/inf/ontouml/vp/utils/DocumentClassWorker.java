package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController.SuggestionResult;
import it.unibz.inf.ontouml.vp.controllers.WebSearchController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.RelatedSource;
import it.unibz.inf.ontouml.vp.model.SearchMode;
import it.unibz.inf.ontouml.vp.model.SearchResult;
import it.unibz.inf.ontouml.vp.views.DocumentClassView;
import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingWorker;

/**
 * Produces the documentation of one class: gathers the class, its neighbourhood and — when research
 * is enabled — web search results, then asks the model to fill the documentation template.
 */
public class DocumentClassWorker extends SwingWorker<SuggestionResult, Void> {

  private static final String DEFAULT_ERROR_MESSAGE = "The documentation request failed.";

  /** One request per source, so the neighbourhood's reading list has to stay bounded. */
  private static final int MAX_SOURCE_SUMMARIES = 8;

  private final IClass targetClass;
  private final ProgressDialogHandler dialogHandler;

  /** The documentation after missing links were looked up; set on the background thread. */
  private String resolved;

  public DocumentClassWorker(IClass targetClass) {
    this.targetClass = targetClass;
    this.dialogHandler = new ProgressDialogHandler();
    this.dialogHandler.onCancel(e -> cancel(true));
  }

  @Override
  protected SuggestionResult doInBackground() throws Exception {
    dialogHandler.showDialog();

    final LlmConfigurations config = Configurations.getInstance().getLlmConfigurations();

    final String prompt =
        LlmPromptResources.getDocumentationPrompt()
            .replace("{class}", LlmPromptContextBuilder.buildDocumentationClassSummary(targetClass))
            .replace("{related}", LlmPromptContextBuilder.buildRelatedClassesSummary(targetClass))
            .replace("{relatedSources}", summariseRelatedSources(config))
            .replace("{search}", researchSection(config))
            .replace("{template}", LlmPromptResources.getDocumentationTemplate());

    final SuggestionResult result =
        LlmAccessController.requestDocumentation(config, targetClass.getName(), prompt);

    resolved =
        DocumentationTemplate.withoutEmptySourceSections(
            resolveMissingUrls(config, result.getExchange().getResponse()));

    return result;
  }

  /**
   * Gives a link to every source the model cited without one. A citation such as "Lei 14.133/2021,
   * art. 8º" does have a clear official page, and only a real search may supply it — asking the
   * model to remember the URL is what produces invented links. Normative citations only accept a
   * result from an official publisher.
   */
  private String resolveMissingUrls(LlmConfigurations config, String documentation) {
    if (documentation == null
        || config.getSearchMode() != SearchMode.SEARCH_API
        || !config.isSearchUsable()) {
      return documentation;
    }

    final DocumentationTemplate parsed = DocumentationTemplate.parse(documentation);
    String enriched = documentation;

    enriched = withResolvedUrls(config, enriched, parsed.getNormativeSources(), true);
    enriched = withResolvedUrls(config, enriched, parsed.getSources(), false);

    return enriched;
  }

  private String withResolvedUrls(
      LlmConfigurations config, String documentation, List<String> sources, boolean requireOfficial) {
    String enriched = documentation;

    for (String source : sources) {
      if (isCancelled() || DocumentationTemplate.urlOf(source) != null) {
        continue;
      }

      try {
        final String url =
            WebSearchController.resolveUrl(config, citationOf(source), requireOfficial);

        if (url != null) {
          // append the link to that very line, leaving any (não verificado) mark untouched:
          // finding the law's page does not confirm what the text claims about it
          enriched = enriched.replace(source, source + " — " + url);
        }
      } catch (Exception e) {
        // a citation we cannot resolve simply stays without a link
        e.printStackTrace();
      }
    }

    return enriched;
  }

  /** The citation without the unverified mark, which would only pollute the query. */
  private static String citationOf(String source) {
    return source.replace(DocumentationTemplate.UNVERIFIED_MARK, "").trim();
  }

  /**
   * Writes the documentation onto the class and checks that it stuck.
   *
   * <p>Visual Paradigm exposes {@code description} and {@code documentation} as two separate
   * properties on a model element, and which one the Specification window shows is not something
   * the Open API states. Writing only one of them left the window showing the previous text — or
   * nothing at all, for a class that had never been documented. So both are written, and both are
   * read back: a write that silently does nothing is worse than an error, because the user walks
   * away believing the class was documented.
   *
   * @return whether at least one of the two properties now holds the text.
   */
  private static boolean writeDescription(IClass _class, String documentation) {
    final boolean description = write(documentation, _class::setDescription, "setDescription");
    final boolean documentationField =
        write(documentation, _class::setDocumentation, "setDocumentation");

    // a class whose documentation was written in Visual Paradigm has it stored as HTML, and that
    // form is what the Specification window renders — writing only the plain text left the old
    // documentation on screen, while a class that had none picked the plain text up just fine
    final boolean htmlField =
        write(toHtml(documentation), _class::setHTMLDescription, "setHTMLDescription");

    final String storedDescription = read(_class::getDescription);
    final String storedDocumentation = read(_class::getDocumentation);

    final boolean written =
        matches(storedDescription, documentation) || matches(storedDocumentation, documentation);

    System.out.println(
        "Documentation write for \""
            + _class.getName()
            + "\": wrote "
            + documentation.length()
            + " chars"
            + " | setDescription accepted="
            + description
            + ", read back "
            + describe(storedDescription)
            + " | setDocumentation accepted="
            + documentationField
            + ", read back "
            + describe(storedDocumentation)
            + " | setHTMLDescription accepted="
            + htmlField
            + " | match="
            + written);

    if (!written) {
      ViewManagerUtils.simpleDialog(
          "Visual Paradigm did not accept the new documentation for \""
              + _class.getName()
              + "\".\n\nThe text was not lost — reopen the action, or copy it from the AI"
              + " Conversation Log. Please report this so it can be fixed.");
    }

    return written;
  }

  /**
   * The documentation as the HTML Visual Paradigm stores, mirroring the shape it writes itself:
   * a head with the default style and a body of paragraphs, one per line of the text.
   */
  private static String toHtml(String text) {
    final StringBuilder html = new StringBuilder();

    html.append("<head>\n")
        .append("    <style type=\"text/css\">\n")
        .append("      <!--\n")
        .append("        body { color: #000000; font-family: Dialog; font-size: 12px }\n")
        .append("      -->\n")
        .append("    </style>\n")
        .append("  </head>\n")
        .append("  <body>\n");

    for (String line : text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
      final String escaped =
          line.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").trim();

      // an empty line still deserves a paragraph, otherwise the text runs together
      html.append("    <p>").append(escaped.isEmpty() ? "&nbsp;" : escaped).append("</p>\n");
    }

    html.append("  </body>");

    return html.toString();
  }

  private static boolean write(String value, Consumer<String> setter, String label) {
    try {
      setter.accept(value);
      return true;
    } catch (UnsupportedOperationException e) {
      System.out.println(label + " is not supported by this element: " + e.getMessage());
    } catch (RuntimeException e) {
      e.printStackTrace();
    }

    return false;
  }

  private static String read(Supplier<String> getter) {
    try {
      return getter.get();
    } catch (RuntimeException e) {
      return null;
    }
  }

  private static boolean matches(String stored, String expected) {
    return stored != null && stored.trim().equals(expected.trim());
  }

  private static String describe(String value) {
    return value == null ? "null" : value.length() + " chars";
  }

  /**
   * Reads the sources cited by the neighbouring classes through the lens of this one: each page is
   * retrieved first and summarised in at most 50 words focused on the class being documented, and
   * the summaries go into the prompt as further input. Nothing here is guaranteed to be used — it
   * is offered as material, the way a retrieval step feeds a generation step.
   *
   * <p>Retrieval comes first and search only after it: when the cited link is dead, the search
   * service is asked for another page carrying the same reference, and that one is retrieved in
   * turn. What is summarised is always text that was actually read.
   */
  private String summariseRelatedSources(LlmConfigurations config) {
    final List<RelatedSource> sources = LlmPromptContextBuilder.collectRelatedSources(targetClass);

    if (sources.isEmpty()) {
      return "(the related classes cite no references)";
    }

    final StringBuilder text = new StringBuilder();
    int summarised = 0;

    for (RelatedSource source : sources) {
      if (isCancelled() || summarised >= MAX_SOURCE_SUMMARIES) {
        break;
      }

      String content = WebPageFetcher.fetch(source.getUrl());
      source.setRetrievedFrom(content != null ? source.getUrl() : null);

      // the link died, or the publisher moved the document: the search service is there to find
      // where else the same reference lives, and that page is then read the same way
      if (content == null
          && config.getSearchMode() == SearchMode.SEARCH_API
          && config.isSearchUsable()) {
        content = fromAnotherPage(config, source);
      }

      // with no content and no provider-side search there is nothing to summarise from: asking
      // anyway would just get us the model's recollection of the URL
      if (content == null && config.getSearchMode() != SearchMode.MODEL) {
        continue;
      }

      try {
        source.setSummary(
            LlmAccessController.requestSourceSummary(
                config,
                targetClass.getName(),
                source.getCaption(),
                source.getRetrievedFrom(),
                content));
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (!source.hasSummary()) {
        continue;
      }

      summarised++;
      text.append("- ")
          .append(source.getCaption())
          .append("  (cited by ")
          .append(source.getOriginClass())
          .append(")\n")
          .append("  URL: ")
          .append(source.getUrl())
          .append("\n");

      if (source.wasRetrievedElsewhere()) {
        text.append("  read from: ").append(source.getRetrievedFrom()).append("\n");
      }

      text.append("  ").append(source.getSummary().replaceAll("\\s+", " ").trim()).append("\n");
    }

    return summarised == 0
        ? "(none of the references cited by the related classes could be read)"
        : text.toString();
  }

  /**
   * Asks the search service where else the same reference can be read, and retrieves that page.
   *
   * @return the text of the replacement page, or null when none could be read.
   */
  private String fromAnotherPage(LlmConfigurations config, RelatedSource source) {
    try {
      final SearchResult alternative =
          WebSearchController.findRetrievablePage(config, source.getCaption());

      if (alternative != null) {
        source.setRetrievedFrom(alternative.getUrl());
        return alternative.getContent();
      }
    } catch (Exception e) {
      // no replacement found: the source is simply left unread
      e.printStackTrace();
    }

    return null;
  }

  /**
   * What goes under "Search results". Only the search-API mode can put real hits here; with search
   * done by the provider the model has them itself, and with research off the prompt must say so,
   * because that is what forces every source to be flagged as unverified.
   *
   * <p>The two steps are kept apart on purpose. The search services turn the terms of the class
   * into candidate pages — that is all they are asked for. Each candidate is then retrieved, and it
   * is the page's own text that goes into the prompt; a hit that could not be opened stays in the
   * list with its snippet, marked as the lead it is.
   */
  private String researchSection(LlmConfigurations config) {
    if (config.getSearchMode() == SearchMode.MODEL) {
      return "You have web search available. Search for official sources about this class before"
          + " answering, and cite only pages you actually retrieved.";
    }

    if (config.getSearchMode() != SearchMode.SEARCH_API || !config.isSearchUsable()) {
      return "(no research was performed — no search results are available, so every source you"
          + " list must be marked (não verificado))";
    }

    final List<SearchResult> found = new ArrayList<>();

    for (String query : queries()) {
      if (isCancelled()) {
        return "(research cancelled)";
      }

      try {
        found.addAll(WebSearchController.search(config, query));
      } catch (Exception e) {
        // a failed query must not sink the whole documentation: carry on with what we have
        e.printStackTrace();
      }
    }

    final List<SearchResult> results = WebSearchController.deduplicate(found);

    if (results.isEmpty()) {
      return "(the search returned nothing — every source you list must be marked"
          + " (não verificado))";
    }

    if (isCancelled()) {
      return "(research cancelled)";
    }

    final int pagesRead = WebSearchController.retrieve(config, results);

    if (pagesRead == 0) {
      return "(the search found pages, but none of them could be retrieved — what follows are"
          + " search snippets, not sources, so every source you list must be marked"
          + " (não verificado))\n\n"
          + WebSearchController.format(results);
    }

    return WebSearchController.format(results);
  }

  /** One query for the concept, one biased towards the official/normative material. */
  private List<String> queries() {
    final String name = targetClass.getName();
    final String project = ApplicationManager.instance().getProjectManager().getProject().getName();

    final List<String> queries = new ArrayList<>();
    queries.add(name + " " + project);
    queries.add(name + " " + project + " lei norma resolução site oficial");

    return queries;
  }

  @Override
  protected void done() {
    dialogHandler.closeDialog();

    if (isCancelled()) {
      return;
    }

    final SuggestionResult result;

    try {
      result = get();
    } catch (InterruptedException | ExecutionException e) {
      final Throwable cause = e.getCause() != null ? e.getCause() : e;
      ViewManagerUtils.simpleDialog(
          cause.getMessage() == null ? DEFAULT_ERROR_MESSAGE : cause.getMessage());
      return;
    }

    final String proposal = resolved != null ? resolved : result.getExchange().getResponse();

    if (proposal == null || proposal.trim().isEmpty()) {
      ViewManagerUtils.simpleDialog("The AI returned no documentation.");
      return;
    }

    final DocumentClassView view =
        new DocumentClassView(targetClass, proposal.trim(), result.getExchange());
    ApplicationManager.instance().getViewManager().showDialog(view);

    System.out.println(
        "Documentation dialog closed for \"" + targetClass.getName() + "\": applied=" + view.wasApplied());

    if (!view.wasApplied()) {
      return;
    }

    final String documentation = view.getDocumentation();

    if (!writeDescription(targetClass, documentation)) {
      return;
    }

    if (view.shouldAddReferences()) {
      final int added = ClassReferences.addFrom(targetClass, documentation);
      final int linkable = ClassReferences.countLinkable(documentation);

      if (added < linkable) {
        ViewManagerUtils.log(
            "Added " + added + " of " + linkable + " cited links to the References tab of \""
                + targetClass.getName()
                + "\" (the rest were already there).");
      }
    }
  }
}
