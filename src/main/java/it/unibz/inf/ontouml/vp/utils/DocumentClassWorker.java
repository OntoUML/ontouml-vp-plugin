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

  /**
   * At least one request per source, so the reading list has to stay bounded. Raised from 8 when
   * the pool grew from "the neighbours' references" to four origins; the collection order is what
   * guarantees the class' own references are never the ones left out.
   */
  private static final int MAX_SOURCE_SUMMARIES = 12;

  /**
   * How many sources are even attempted. A source that cannot be read still costs a retrieval and
   * often a search, and it does not count towards the summary cap — so without this, a model whose
   * references have all rotted would keep the dialog busy for as long as it had references.
   */
  private static final int MAX_SOURCES_ATTEMPTED = 20;

  private final IClass targetClass;
  private final ProgressDialogHandler dialogHandler;

  /** What the progress window shows: the steps below, and the one that is running. */
  private final TaskProgress progress;

  private final TaskProgress.Step contextStep;
  private final TaskProgress.Step sourcesStep;
  private final TaskProgress.Step researchStep;
  private final TaskProgress.Step generationStep;
  private final TaskProgress.Step linksStep;

  /** The documentation after missing links were looked up; set on the background thread. */
  private String resolved;

  public DocumentClassWorker(IClass targetClass) {
    this.targetClass = targetClass;

    // declared in the order doInBackground runs them, so the list reads as the story of the run
    this.progress = new TaskProgress("Documentando \"" + targetClass.getName() + "\"");
    this.contextStep = progress.step("Contexto do modelo");
    this.sourcesStep = progress.step("Leitura das fontes citadas no modelo");
    this.researchStep = progress.step("Pesquisa na web");
    this.generationStep = progress.step("Geração da documentação pelo modelo de IA");
    this.linksStep = progress.step("Links das fontes citadas sem endereço");

    this.dialogHandler = new ProgressDialogHandler(progress);
    this.dialogHandler.onCancel(e -> cancel(true));
  }

  @Override
  protected SuggestionResult doInBackground() throws Exception {
    dialogHandler.showDialog();

    // waits for the provider's rate limit are counted down in this task's window
    LlmRequestPacing.bind(progress);

    try {
      final LlmConfigurations config = Configurations.getInstance().getLlmConfigurations();

      progress.start(contextStep);
      progress.activity("Lendo a classe e as classes relacionadas");
      final String classSummary = LlmPromptContextBuilder.buildDocumentationClassSummary(targetClass);
      final String relatedSummary = LlmPromptContextBuilder.buildRelatedClassesSummary(targetClass);
      progress.finish(contextStep);

      final String sources = summariseRelatedSources(config);
      final String search = researchSection(config);

      final String prompt =
          PromptTemplate.fill(
              LlmPromptResources.getDocumentationPrompt(),
              "class", classSummary,
              "related", relatedSummary,
              "relatedSources", sources,
              "search", search,
              "template", LlmPromptResources.getDocumentationTemplate());

      progress.start(generationStep);
      progress.activity(
          "Aguardando "
              + config.getModel()
              + " — o prompt tem "
              + prompt.length()
              + " caracteres");

      final SuggestionResult result =
          LlmAccessController.requestDocumentation(config, targetClass.getName(), prompt);

      progress.finish(generationStep);

      if (config.getSearchMode() == SearchMode.MODEL
          && result.getExchange().getCitations().isEmpty()) {
        // the prompt said a search was available; without citations there is no sign one happened
        final String warning =
            "A pesquisa via modelo não está funcionando: "
                + config.getModel()
                + " respondeu sem citar nenhuma página, então as fontes da documentação vêm da"
                + " memória do modelo e não foram verificadas. Use um modelo com busca ou a"
                + " pesquisa por API.";

        progress.activity(warning);
        ViewManagerUtils.log(warning);
      }

      resolved =
          DocumentationTemplate.withoutEmptySourceSections(
              resolveMissingUrls(config, result.getExchange().getResponse()));

      return result;
    } catch (Exception e) {
      progress.failRunning(e.getMessage());
      throw e;
    } finally {
      LlmRequestPacing.unbind();
    }
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
      progress.skip(linksStep, "só é feito com a pesquisa por API ligada");
      return documentation;
    }

    final DocumentationTemplate parsed = DocumentationTemplate.parse(documentation);
    String enriched = documentation;

    progress.start(linksStep);
    enriched = withResolvedUrls(config, enriched, parsed.getNormativeSources(), true);
    enriched = withResolvedUrls(config, enriched, parsed.getSources(), false);
    progress.finish(linksStep);

    return enriched;
  }

  private String withResolvedUrls(
      LlmConfigurations config, String documentation, List<String> sources, boolean requireOfficial) {
    String enriched = documentation;

    for (String source : sources) {
      if (isCancelled() || DocumentationTemplate.urlOf(source) != null) {
        continue;
      }

      progress.activity("Procurando o endereço de \"" + citationOf(source) + "\"");

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
   * Reads every source the model itself points at — the class' own references first, then its
   * neighbours', the diagrams it is drawn on and the packages above it — through the lens of this
   * class. Each one is retrieved and summarised in at most 50 words, and the summaries go into the
   * prompt as further input. Nothing here is guaranteed to be used: it is offered as material, the
   * way a retrieval step feeds a generation step.
   *
   * <p>A source that could not be read is listed all the same, with the reason. It used to be
   * dropped, which left the model unaware that the class cites that norm at all — and a citation
   * the model cannot see is one it cannot carry over, however clearly the author put it there.
   */
  private String summariseRelatedSources(LlmConfigurations config) {
    progress.start(sourcesStep);
    progress.activity("Procurando referências na classe, nas vizinhas, nos diagramas e nos pacotes");

    final List<RelatedSource> sources = LlmPromptContextBuilder.collectSources(targetClass);

    if (sources.isEmpty()) {
      progress.skip(sourcesStep, "nem a classe nem o que a cerca cita referências");

      return "(neither this class nor its surroundings cite any reference)";
    }

    final SourceSummarizer summarizer =
        new SourceSummarizer(config, targetClass.getName(), this::isCancelled, progress);
    final int total = Math.min(sources.size(), MAX_SOURCES_ATTEMPTED);
    final StringBuilder text = new StringBuilder();
    int summarised = 0;
    int declared = 0;

    progress.count(sourcesStep, 0, total);

    for (RelatedSource source : sources) {
      if (isCancelled()
          || summarised >= MAX_SOURCE_SUMMARIES
          || summarised + declared >= MAX_SOURCES_ATTEMPTED) {
        break;
      }

      // the counter says which source is being read now, so it moves before the read, not after
      progress.count(sourcesStep, summarised + declared + 1, total);
      progress.activity("Lendo \"" + source.getCaption() + "\" (" + source.getOrigin() + ")");

      final boolean read = summarizer.read(source);

      if (read) {
        summarised++;
      } else {
        declared++;
      }

      text.append("- ")
          .append(source.getCaption())
          .append("  (origem: ")
          .append(source.getOrigin())
          .append(")\n");

      if (source.hasLocator()) {
        text.append("  ").append(locatorLabel(source)).append(": ").append(source.getLocator())
            .append("\n");
      }

      if (source.wasRetrievedElsewhere()) {
        text.append("  read from: ").append(source.getRetrievedFrom()).append("\n");
      }

      if (read) {
        text.append("  ").append(source.getSummary().replaceAll("\\s+", " ").trim()).append("\n");
      } else {
        text.append("  [not read] ").append(source.getUnreadReason()).append("\n");
      }
    }

    // a finished step shows how many sources it went through, not how many it meant to
    progress.count(sourcesStep, summarised + declared, summarised + declared);
    progress.finish(sourcesStep);

    System.out.println(
        "Sources for \""
            + targetClass.getName()
            + "\": "
            + sources.size()
            + " found, "
            + summarised
            + " summarised, "
            + declared
            + " declared unread, "
            + Math.max(0, sources.size() - summarised - declared)
            + " not attempted.");

    return text.toString();
  }

  /** A file reference and a URL reference are cited differently, and the model needs to know which. */
  private static String locatorLabel(RelatedSource source) {
    return LocalFileFetcher.isFileLocator(source.getLocator()) ? "Arquivo" : "URL";
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
      progress.skip(researchStep, "feita pelo próprio provedor do modelo, durante a geração");

      return "You have web search available. Search for official sources about this class before"
          + " answering, and cite only pages you actually retrieved.";
    }

    if (config.getSearchMode() != SearchMode.SEARCH_API || !config.isSearchUsable()) {
      progress.skip(researchStep, "desligada nas configurações");

      return "(no research was performed — no search results are available, so every source you"
          + " list must be marked (não verificado))";
    }

    final List<SearchResult> found = new ArrayList<>();
    final List<String> queries = queries();

    progress.start(researchStep);

    for (int i = 0; i < queries.size(); i++) {
      if (isCancelled()) {
        return "(research cancelled)";
      }

      progress.count(researchStep, i, queries.size());
      progress.activity("Pesquisando: " + queries.get(i));

      try {
        found.addAll(WebSearchController.search(config, queries.get(i)));
      } catch (Exception e) {
        // a failed query must not sink the whole documentation: carry on with what we have
        e.printStackTrace();
      }
    }

    final List<SearchResult> results = WebSearchController.deduplicate(found);

    if (results.isEmpty()) {
      progress.finish(researchStep);

      return "(the search returned nothing — every source you list must be marked"
          + " (não verificado))";
    }

    if (isCancelled()) {
      return "(research cancelled)";
    }

    progress.activity("Lendo as páginas encontradas (" + results.size() + " candidatas)");

    final int pagesRead = WebSearchController.retrieve(config, results);
    progress.finish(researchStep);

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
    dialogHandler.closeDialog(isCancelled());

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
