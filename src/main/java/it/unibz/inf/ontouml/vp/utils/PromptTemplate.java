package it.unibz.inf.ontouml.vp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fills the {@code {name}} placeholders of a prompt in a single pass.
 *
 * <p>Replacing them one after another lets each replacement rescan the text the previous ones
 * inserted. A class whose documentation mentions {@code {template}}, or a web page that happens to
 * contain {@code {search}}, would then have it expanded into the prompt: the model would be handed
 * material it was never meant to see, placed where the page's author chose. Here every placeholder
 * is looked for once, in the prompt as written, and what is put in its place is never looked at
 * again.
 */
public class PromptTemplate {

  private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9]*)\\}");

  private PromptTemplate() {}

  /**
   * As {@link #fill(String, Map)}, with the values given as alternating names and texts. A null text
   * fills its placeholder with nothing, which is what an empty section of the context should do.
   */
  public static String fill(String prompt, String... namesAndTexts) {
    final Map<String, String> values = new HashMap<>();

    for (int i = 0; i + 1 < namesAndTexts.length; i += 2) {
      values.put(namesAndTexts[i], namesAndTexts[i + 1] == null ? "" : namesAndTexts[i + 1]);
    }

    return fill(prompt, values);
  }

  /**
   * @param values the text for each placeholder name. A placeholder that has no entry is left as
   *     written, which is what lets a prompt show the model a JSON example with braces of its own.
   */
  public static String fill(String prompt, Map<String, String> values) {
    final Matcher matcher = PLACEHOLDER.matcher(prompt);
    final StringBuffer filled = new StringBuffer();

    while (matcher.find()) {
      final String value = values.get(matcher.group(1));
      matcher.appendReplacement(
          filled, Matcher.quoteReplacement(value == null ? matcher.group() : value));
    }

    matcher.appendTail(filled);

    return filled.toString();
  }
}
