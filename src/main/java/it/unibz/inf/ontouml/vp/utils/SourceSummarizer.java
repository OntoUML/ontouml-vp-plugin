package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.controllers.WebSearchController;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmConversationLog;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import it.unibz.inf.ontouml.vp.model.RelatedSource;
import it.unibz.inf.ontouml.vp.model.SearchMode;
import it.unibz.inf.ontouml.vp.model.SearchResult;
import it.unibz.inf.ontouml.vp.utils.DocumentExtractor.Extraction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Reads the sources cited around a class and summarises each one through the lens of that class.
 *
 * <p>Retrieval comes first and the model only after it: what is summarised is always text that was
 * actually read — a page downloaded, a file opened, a document converted. A search service is asked
 * for a page only when the citation leads nowhere by itself, and what it returns is retrieved in
 * turn.
 *
 * <p>A source that resists all of that is still reported. Dropping it, as this code used to, left
 * the model unaware that the class cites a norm at all; naming it as unread lets the citation
 * survive, with the unverified mark it deserves.
 */
public class SourceSummarizer {

  /**
   * How much of one source is read. A norm runs long, and its tail is often where the duties are:
   * Lei 14.133 is 277.000 characters, and the article on "inviabilidade de competição" sits at
   * 125.000 — 45% in. Reading less than the whole thing is how a source gets called irrelevant.
   */
  private static final int MAX_SOURCE_CHARACTERS = 300000;

  /** What goes into a single summary request, comfortably inside any provider's context. */
  private static final int MAX_CHARACTERS_PER_PART = 12000;

  /** How many parts are summarised. Each one is a request, so this is the cost of a long source. */
  private static final int MAX_PARTS = 8;

  /** Terms shorter than this say nothing about relevance; "de" matches every part of every law. */
  private static final int MIN_TERM_LENGTH = 4;

  /**
   * How much of a term has to match. Portuguese inflects: the class is
   * "InviabilidadeDeCompeticao" and the law says "inviável a competição", which share a stem and
   * not much else. Comparing the first few letters finds that; comparing whole words does not.
   */
  private static final int STEM_LENGTH = 5;

  private final LlmConfigurations config;
  private final String concept;

  /** Asked between sources and between parts, so cancelling the dialog stops the work promptly. */
  private final BooleanSupplier cancelled;

  /** Told what is being done, for the progress window. */
  private final TaskProgress progress;

  public SourceSummarizer(
      LlmConfigurations config,
      String concept,
      BooleanSupplier cancelled,
      TaskProgress progress) {
    this.config = config;
    this.concept = concept;
    this.cancelled = cancelled;
    this.progress = progress;
  }

  /**
   * Reads and summarises one source, filling its summary or its reason for being unread.
   *
   * @return whether the source ended up with a summary.
   */
  public boolean read(RelatedSource source) {
    final long startedAt = System.currentTimeMillis();
    final Extraction retrieved = retrieve(source);
    String content = retrieved.succeeded() ? retrieved.getText() : null;

    if (content == null) {
      content = fromAnotherPage(source);

      if (content != null) {
        retrieved.noting("busca: lida outra cópia em " + source.getRetrievedFrom());
      }
    }

    // Nothing retrieved means nothing to summarise, in every search mode. Asking the model anyway
    // — which this used to do under provider-side search — can only produce a refusal or an
    // invention, and a refusal that slips past the sentinel lands in the prompt looking exactly
    // like a summary of a source nobody opened.
    if (content == null) {
      source.setUnreadReason(retrieved.getReason());
      recordRetrieval(source, retrieved, "não lida: " + retrieved.getReason(), startedAt);
      return false;
    }

    try {
      source.setSummary(summarise(source, content));
    } catch (Exception e) {
      e.printStackTrace();
      source.setUnreadReason("a geração do resumo falhou: " + e.getMessage());
      recordRetrieval(source, retrieved, "resumo falhou: " + e.getMessage(), startedAt);
      return false;
    }

    if (!source.hasSummary()) {
      source.setUnreadReason("o modelo não encontrou nada sobre esta classe no texto lido");
      recordRetrieval(
          source, retrieved, "lida, mas sem nada sobre a classe: " + content.length() + " caracteres",
          startedAt);
      return false;
    }

    recordRetrieval(source, retrieved, "lida: " + content.length() + " caracteres", startedAt);
    return true;
  }

  /**
   * Puts the retrieval of one source in the AI Conversation Log, next to the model exchanges.
   *
   * <p>Only the LLM round trips used to be recorded, so when a source came back unread there was
   * nothing to inspect: whether curl ran at all, what the server answered, whether a converter was
   * even found. Those are exactly the questions a failure raises, and the difference between a page
   * that is gone and a tool that is missing from this process.
   */
  private void recordRetrieval(
      RelatedSource source, Extraction extraction, String outcome, long startedAt) {
    final StringBuilder request = new StringBuilder();
    request
        .append("origem:      ")
        .append(source.getOrigin())
        .append("\nlocalizador: ")
        .append(source.hasLocator() ? source.getLocator() : "(a referência não indica um endereço)");

    if (source.wasRetrievedElsewhere()) {
      request.append("\nlida em:     ").append(source.getRetrievedFrom());
    }

    final StringBuilder response = new StringBuilder();

    for (String line : extraction.getTrail()) {
      response.append(line).append("\n");
    }

    response.append("\nresultado: ").append(outcome);

    final LlmExchange exchange =
        new LlmExchange(
            "Source retrieval — " + source.getCaption(),
            config,
            "(curl / HTTP / arquivo / conversor)",
            "Retrieval of a source cited in the model",
            request.toString());

    exchange.succeeded(response.toString().trim(), System.currentTimeMillis() - startedAt);
    LlmConversationLog.getInstance().record(exchange);
  }

  /** Opens the source where it says it lives: a web page, or a file on this machine. */
  private Extraction retrieve(RelatedSource source) {
    if (!source.hasLocator()) {
      return Extraction.failed("a referência não indica um endereço");
    }

    if (LocalFileFetcher.isFileLocator(source.getLocator())) {
      if (!config.isReadLocalFileReferences()) {
        return Extraction.failed(
            "leitura de arquivos locais desligada nas configurações: " + source.getLocator());
      }

      final Extraction extraction =
          LocalFileFetcher.fetch(config, progress, source.getLocator(), MAX_SOURCE_CHARACTERS);

      if (extraction.succeeded()) {
        source.setRetrievedFrom(source.getLocator());
      } else {
        // the user attached this file on purpose; if it cannot be opened they should be told where
        // to look, and a log that is unavailable must not cost us the run
        System.out.println("Reference not read — " + extraction.getReason());

        try {
          ViewManagerUtils.log("Reference not read — " + extraction.getReason());
        } catch (RuntimeException | NoClassDefFoundError e) {
          // no view manager to log to
        }
      }

      return extraction;
    }

    final Extraction extraction =
        WebPageFetcher.retrieve(config, progress, source.getLocator(), MAX_SOURCE_CHARACTERS);

    if (extraction.succeeded()) {
      source.setRetrievedFrom(source.getLocator());
    }

    return extraction;
  }

  /**
   * Asks the search service where else the same reference can be read, and retrieves that page.
   *
   * <p>This runs whenever the citation itself led nowhere — a dead link, a file that moved, a PDF
   * nothing installed can convert, or a reference that never carried an address at all. A dead link
   * to "Resolução CNJ 325/2020" does not make the resolution unavailable; it means the copy has to
   * be found elsewhere.
   *
   * @return the text of the replacement page, or null when none could be read.
   */
  private String fromAnotherPage(RelatedSource source) {
    if (config.getSearchMode() != SearchMode.SEARCH_API || !config.isSearchUsable()) {
      return null;
    }

    progress.activity("Procurando outra cópia de \"" + source.getCaption() + "\" na pesquisa web");

    try {
      final SearchResult alternative =
          WebSearchController.findRetrievablePage(config, source.getSearchTerm());

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
   * One request for a source that fits, and a map-reduce for one that does not: each part is
   * summarised with the class in focus, then the partial summaries are consolidated into one.
   * Truncating instead, as this used to, threw away everything past the first few thousand
   * characters — in a norm, usually the articles that actually bind.
   */
  private String summarise(RelatedSource source, String content) throws Exception {
    if (content == null || content.length() <= MAX_CHARACTERS_PER_PART) {
      progress.activity("Resumindo \"" + source.getCaption() + "\" com o modelo de IA");

      return LlmAccessController.requestSourceSummary(
          config, concept, source.getCaption(), source.getRetrievedFrom(), content);
    }

    final List<String> parts = split(content);
    final List<Integer> chosen = selectParts(parts, concept);
    final List<String> summaries = new ArrayList<>();

    for (int index : chosen) {
      if (cancelled.getAsBoolean()) {
        break;
      }

      progress.activity(
          "Resumindo \""
              + source.getCaption()
              + "\" — trecho "
              + (index + 1)
              + " de "
              + parts.size()
              + " ("
              + (summaries.size() + 1)
              + " de "
              + chosen.size()
              + " escolhidos por relevância)");

      final String summary =
          LlmAccessController.requestSourceSummary(
              config,
              concept,
              source.getCaption(),
              source.getRetrievedFrom(),
              parts.get(index),
              "trecho " + (index + 1) + " de " + parts.size());

      if (summary != null && !summary.trim().isEmpty()) {
        summaries.add(summary);
      }
    }

    if (summaries.isEmpty()) {
      return null;
    }

    // one part that had something to say needs no consolidating round
    if (summaries.size() == 1) {
      return summaries.get(0);
    }

    progress.activity("Consolidando os resumos de \"" + source.getCaption() + "\"");

    return LlmAccessController.requestSourceDigest(
        config, concept, source.getCaption(), source.getRetrievedFrom(), summaries);
  }

  /**
   * Cuts the text into parts at paragraph boundaries, so no part starts mid-sentence. A paragraph
   * longer than a whole part is cut where it must be — the alternative is sending more than the
   * request can hold.
   */
  static List<String> split(String text) {
    final List<String> parts = new ArrayList<>();
    final StringBuilder part = new StringBuilder();

    for (String paragraph : text.split("\n")) {
      if (paragraph.length() > MAX_CHARACTERS_PER_PART) {
        if (part.length() > 0) {
          parts.add(part.toString());
          part.setLength(0);
        }

        for (int at = 0; at < paragraph.length(); at += MAX_CHARACTERS_PER_PART) {
          parts.add(
              paragraph.substring(at, Math.min(paragraph.length(), at + MAX_CHARACTERS_PER_PART)));
        }

        continue;
      }

      if (part.length() + paragraph.length() + 1 > MAX_CHARACTERS_PER_PART) {
        parts.add(part.toString());
        part.setLength(0);
      }

      part.append(paragraph).append("\n");
    }

    if (part.length() > 0) {
      parts.add(part.toString());
    }

    return parts;
  }

  /**
   * Which parts of a long document to spend a request on, in document order.
   *
   * <p>Summarising the first {@value #MAX_PARTS} parts, which is what this used to do, reads a long
   * norm from its preamble: for Lei 14.133 that is the definitions, and the article that actually
   * defines "inviabilidade de competição" sits 45% in, unread and unsummarised. The document is
   * therefore scored against the terms of the class, and the requests go where the concept is
   * actually discussed.
   *
   * <p>A document with no match anywhere falls back to reading from the beginning: it may still be
   * relevant in ways the class name does not spell out, and the model is the judge of that.
   */
  static List<Integer> selectParts(List<String> parts, String concept) {
    if (parts.size() <= MAX_PARTS) {
      return indices(parts.size());
    }

    final List<String> stems = stemsOf(concept);

    if (stems.isEmpty()) {
      return indices(MAX_PARTS);
    }

    final List<Integer> ranked = indices(parts.size());
    final int[] scores = new int[parts.size()];

    for (int i = 0; i < parts.size(); i++) {
      scores[i] = score(parts.get(i), stems);
    }

    // most relevant first, ties broken by position so the earlier part wins
    ranked.sort((a, b) -> scores[a] != scores[b] ? scores[b] - scores[a] : a - b);

    final List<Integer> chosen = new ArrayList<>(ranked.subList(0, MAX_PARTS));

    if (score(parts.get(chosen.get(0)), stems) == 0) {
      // nothing anywhere mentions the class: read from the beginning, as before
      return indices(MAX_PARTS);
    }

    // the digest is given the parts in the order the document tells them
    chosen.sort(Integer::compareTo);

    return chosen;
  }

  private static List<Integer> indices(int count) {
    final List<Integer> indices = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      indices.add(i);
    }

    return indices;
  }

  /** How often this part touches the concept. */
  private static int score(String part, List<String> stems) {
    final String normalised = normalise(part);
    int score = 0;

    for (String stem : stems) {
      int at = normalised.indexOf(stem);

      while (at >= 0) {
        score++;
        at = normalised.indexOf(stem, at + stem.length());
      }
    }

    return score;
  }

  /**
   * The stems of a class name. Model names run words together — "InviabilidadeDeCompeticao" — so
   * the camel case is broken up first, and what is left is cut to its stem.
   */
  static List<String> stemsOf(String concept) {
    final List<String> stems = new ArrayList<>();

    if (concept == null) {
      return stems;
    }

    final String spaced = concept.replaceAll("(?<=[a-z0-9])(?=[A-Z])", " ");

    for (String word : normalise(spaced).split(" ")) {
      if (word.length() < MIN_TERM_LENGTH) {
        continue;
      }

      final String stem = word.length() <= STEM_LENGTH ? word : word.substring(0, STEM_LENGTH);

      if (!stems.contains(stem)) {
        stems.add(stem);
      }
    }

    return stems;
  }

  /** Lower case and unaccented, so "competição" and "competicao" are the same word. */
  private static String normalise(String text) {
    return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(java.util.Locale.ROOT)
        .replaceAll("[^a-z0-9]+", " ");
  }
}
