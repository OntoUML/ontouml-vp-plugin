package it.unibz.inf.ontouml.vp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads back a class description that was written with the documentation template.
 *
 * <p>Once classes are documented this way their {@code Description} field has structure, which is
 * what lets the feature feed a neighbouring class with just the short description and the normative
 * sources instead of the whole text. Descriptions written by hand are not in this format, so
 * parsing degrades to putting everything in the full description and using an excerpt as the short
 * one — the feature has to work before any class has been documented.
 */
public class DocumentationTemplate {

  private static final int EXCERPT_LENGTH = 240;

  /** Marks a source the model could not back with a search result. */
  public static final String UNVERIFIED_MARK = "(não verificado)";

  private final String shortDescription;
  private final String fullDescription;
  private final List<String> normativeSources;
  private final List<String> sources;
  private final boolean templated;

  private DocumentationTemplate(
      String shortDescription,
      String fullDescription,
      List<String> normativeSources,
      List<String> sources,
      boolean templated) {
    this.shortDescription = shortDescription;
    this.fullDescription = fullDescription;
    this.normativeSources = normativeSources;
    this.sources = sources;
    this.templated = templated;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public List<String> getNormativeSources() {
    return normativeSources;
  }

  public List<String> getSources() {
    return sources;
  }

  /** Whether the description actually followed the template. */
  public boolean isTemplated() {
    return templated;
  }

  public boolean isEmpty() {
    return shortDescription.isEmpty() && fullDescription.isEmpty();
  }

  /**
   * The four sections' headings. The bundled template's are English, unconditionally — the
   * documentation prompt tells the model to keep them as given regardless of what language it
   * writes the content in ({@code {language}}), so a fixed structure does not need to track that
   * setting. The Portuguese ones are matched too, only so a class documented under the plugin's
   * original template (Portuguese headings, before that {@code {language}} placeholder existed)
   * keeps parsing correctly. Matched whole, case-insensitively, never as a substring: "Fontes"
   * must not swallow "Fontes Normativas".
   */
  private static final String[] SHORT_DESCRIPTION_HEADINGS = {"Short description", "Descrição curta"};

  private static final String[] FULL_DESCRIPTION_HEADINGS = {
    "Full description", "Descrição completa"
  };

  private static final String[] NORMATIVE_SOURCES_HEADINGS = {
    "Normative sources", "Fontes Normativas"
  };

  private static final String[] SOURCES_HEADINGS = {"Sources", "Fontes"};

  private static final String[] NO_VERIFIED_SOURCE = {
    "No verified source.", "Nenhuma fonte verificada."
  };

  public static DocumentationTemplate parse(String description) {
    if (description == null || description.trim().isEmpty()) {
      return new DocumentationTemplate("", "", new ArrayList<>(), new ArrayList<>(), false);
    }

    final String text = description.replace("\r\n", "\n").replace('\r', '\n');

    final String shortDescription = section(text, SHORT_DESCRIPTION_HEADINGS);
    final String fullDescription = section(text, FULL_DESCRIPTION_HEADINGS);
    final List<String> normative = lines(section(text, NORMATIVE_SOURCES_HEADINGS));
    final List<String> sources = lines(section(text, SOURCES_HEADINGS));

    final boolean templated =
        !shortDescription.isEmpty() || !fullDescription.isEmpty() || !normative.isEmpty();

    if (!templated) {
      final String plain = text.trim();
      return new DocumentationTemplate(excerpt(plain), plain, new ArrayList<>(), sources, false);
    }

    return new DocumentationTemplate(
        shortDescription.isEmpty() ? excerpt(fullDescription) : shortDescription,
        fullDescription,
        normative,
        sources,
        true);
  }

  /**
   * The body of a section, matched on its heading in whichever of the four languages it was
   * written in. A heading only matches when nothing but whitespace follows the title on that line.
   */
  private static String section(String text, String[] headings) {
    final String[] lines = text.split("\n");
    final StringBuilder body = new StringBuilder();
    boolean inside = false;

    for (String line : lines) {
      final String stripped = strip(line);

      if (isHeading(stripped)) {
        if (inside) {
          break;
        }

        inside = matches(stripped, headings);
        continue;
      }

      if (inside) {
        body.append(line).append("\n");
      }
    }

    return body.toString().trim();
  }

  private static boolean matches(String stripped, String[] candidates) {
    for (String candidate : candidates) {
      if (stripped.equalsIgnoreCase(candidate)) {
        return true;
      }
    }

    return false;
  }

  private static String strip(String line) {
    return line.replaceAll("^[#*\\s]+", "").replaceAll("[*:\\s]+$", "").trim();
  }

  private static boolean isSourceHeading(String stripped) {
    return matches(stripped, NORMATIVE_SOURCES_HEADINGS) || matches(stripped, SOURCES_HEADINGS);
  }

  /**
   * Drops a "Fontes Normativas" or "Fontes" section that ended up with no entries, heading and all.
   * The prompt asks for this, but a model will still hand back an empty heading now and then, and an
   * empty section in the documentation reads like an oversight.
   */
  public static String withoutEmptySourceSections(String documentation) {
    if (documentation == null) {
      return null;
    }

    final String[] lines =
        documentation.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
    final StringBuilder kept = new StringBuilder();
    int i = 0;

    while (i < lines.length) {
      if (!isSourceHeading(strip(lines[i]))) {
        kept.append(lines[i]).append("\n");
        i++;
        continue;
      }

      final StringBuilder body = new StringBuilder();
      int j = i + 1;

      while (j < lines.length && !isHeading(strip(lines[j]))) {
        body.append(lines[j]).append("\n");
        j++;
      }

      if (!lines(body.toString()).isEmpty()) {
        kept.append(lines[i]).append("\n").append(body);
      }

      i = j;
    }

    return kept.toString().replaceAll("\n{3,}", "\n\n").trim();
  }

  private static boolean isHeading(String stripped) {
    return matches(stripped, SHORT_DESCRIPTION_HEADINGS)
        || matches(stripped, FULL_DESCRIPTION_HEADINGS)
        || isSourceHeading(stripped);
  }

  /** Non-empty lines of a section, with list bullets stripped. */
  private static List<String> lines(String body) {
    final List<String> collected = new ArrayList<>();

    for (String line : body.split("\n")) {
      final String cleaned = line.replaceAll("^[-*•\\s]+", "").trim();

      if (!cleaned.isEmpty() && !cleaned.startsWith("{") && !matches(cleaned, NO_VERIFIED_SOURCE)) {
        collected.add(cleaned);
      }
    }

    return collected;
  }

  /** Every source line of both sections, normative ones first. */
  public List<String> getAllSources() {
    final List<String> all = new ArrayList<>(normativeSources);
    all.addAll(sources);
    return all;
  }

  private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

  /** The URL inside a source line, trimmed of trailing punctuation, or null when there is none. */
  public static String urlOf(String sourceLine) {
    if (sourceLine == null) {
      return null;
    }

    final Matcher matcher = URL_PATTERN.matcher(sourceLine);

    if (!matcher.find()) {
      return null;
    }

    return matcher.group().replaceAll("[.,;:)\\]]+$", "");
  }

  /** The source line without its URL, used as the reference's caption. */
  public static String captionOf(String sourceLine) {
    final String url = urlOf(sourceLine);
    final String caption =
        (url == null ? sourceLine : sourceLine.replace(url, "")).replaceAll("[\\s—–-]+$", "").trim();

    return caption.isEmpty() ? sourceLine.trim() : caption;
  }

  private static String excerpt(String text) {
    final String singleLine = text.replaceAll("\\s+", " ").trim();

    return singleLine.length() <= EXCERPT_LENGTH
        ? singleLine
        : singleLine.substring(0, EXCERPT_LENGTH) + "...";
  }
}
