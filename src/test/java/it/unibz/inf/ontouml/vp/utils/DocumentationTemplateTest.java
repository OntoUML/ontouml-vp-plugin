package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DocumentationTemplate} against a description with English headings (what the bundled
 * template now always produces, whatever language {@code {language}} asked the model to write the
 * content in) and against one with the plugin's original Portuguese headings, kept as a legacy
 * fallback so a class documented before the {@code {language}} placeholder existed still parses.
 */
class DocumentationTemplateTest {

  @Test
  @DisplayName("English headings are parsed")
  void english() {
    final String description =
        "# Contract\n\n"
            + "## Short description\n\nA binding agreement between two parties.\n\n"
            + "## Full description\n\nFull text here.\n\n"
            + "## Normative sources\n\n- Lei 14.133/2021 — https://x\n\n"
            + "## Sources\n\n- Some book\n";

    final DocumentationTemplate parsed = DocumentationTemplate.parse(description);

    assertThat(parsed.isTemplated()).isTrue();
    assertThat(parsed.getShortDescription()).isEqualTo("A binding agreement between two parties.");
    assertThat(parsed.getFullDescription()).isEqualTo("Full text here.");
    assertThat(parsed.getNormativeSources()).containsExactly("Lei 14.133/2021 — https://x");
    assertThat(parsed.getSources()).containsExactly("Some book");
  }

  @Test
  @DisplayName("\"Sources\" does not swallow \"Normative sources\"")
  void sourcesDoesNotSwallowNormativeSources() {
    final String description =
        "## Short description\n\nX.\n\n"
            + "## Full description\n\nY.\n\n"
            + "## Normative sources\n\n- Law X\n\n"
            + "## Sources\n\n- A book\n";

    final DocumentationTemplate parsed = DocumentationTemplate.parse(description);

    assertThat(parsed.getNormativeSources()).containsExactly("Law X");
    assertThat(parsed.getSources()).containsExactly("A book");
  }

  @Test
  @DisplayName("the plugin's original Portuguese headings still parse, for backward compatibility")
  void legacyPortuguese() {
    final String description =
        "## Descrição curta\n\nUm acordo vinculante entre duas partes.\n\n"
            + "## Descrição completa\n\nTexto completo aqui.\n\n"
            + "## Fontes Normativas\n\n- Lei 14.133/2021\n\n"
            + "## Fontes\n\n- Algum livro\n";

    final DocumentationTemplate parsed = DocumentationTemplate.parse(description);

    assertThat(parsed.getShortDescription()).isEqualTo("Um acordo vinculante entre duas partes.");
    assertThat(parsed.getNormativeSources()).containsExactly("Lei 14.133/2021");
    assertThat(parsed.getSources()).containsExactly("Algum livro");
  }

  @Test
  @DisplayName("\"Fontes\" does not swallow \"Fontes Normativas\"")
  void fontesDoesNotSwallowFontesNormativas() {
    final String description =
        "## Descrição curta\n\nX.\n\n"
            + "## Descrição completa\n\nY.\n\n"
            + "## Fontes Normativas\n\n- Lei X\n\n"
            + "## Fontes\n\n- Um livro\n";

    final DocumentationTemplate parsed = DocumentationTemplate.parse(description);

    assertThat(parsed.getNormativeSources()).containsExactly("Lei X");
    assertThat(parsed.getSources()).containsExactly("Um livro");
  }

  @Test
  @DisplayName("an empty source section is dropped, heading included")
  void emptySourceSectionsAreDropped() {
    final String documentation =
        "## Short description\n\nX.\n\n"
            + "## Full description\n\nY.\n\n"
            + "## Normative sources\n\n{no defensible normative source}\n\n"
            + "## Sources\n\n- A book\n";

    final String cleaned = DocumentationTemplate.withoutEmptySourceSections(documentation);

    assertThat(cleaned).doesNotContain("Normative sources");
    assertThat(cleaned).contains("Sources");
    assertThat(cleaned).contains("A book");
  }

  @Test
  @DisplayName("a plain, hand-written description is not mistaken for a templated one")
  void handWrittenDescriptionDegradesGracefully() {
    final DocumentationTemplate parsed = DocumentationTemplate.parse("Just some free text.");

    assertThat(parsed.isTemplated()).isFalse();
    assertThat(parsed.getFullDescription()).isEqualTo("Just some free text.");
  }
}
