package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SourceSummarizerTest {

  private static final int PART = 12000;

  @Test
  @DisplayName("a document that fits stays in one part")
  void keepsShortDocumentWhole() {
    assertThat(SourceSummarizer.split("Art. 1º\n\nArt. 2º")).hasSize(1);
  }

  @Test
  @DisplayName("cuts between paragraphs, never inside one")
  void cutsAtParagraphBoundaries() {
    final String paragraph = repeat("a", PART - 100) + "\n";
    final List<String> parts = SourceSummarizer.split(paragraph + paragraph + paragraph);

    assertThat(parts).hasSize(3);

    for (String part : parts) {
      // each part holds whole paragraphs: no part begins in the middle of a sentence
      assertThat(part.trim()).isEqualTo(paragraph.trim());
    }
  }

  @Test
  @DisplayName("no part exceeds what one request may carry")
  void respectsThePartSize() {
    final StringBuilder document = new StringBuilder();

    for (int i = 0; i < 50; i++) {
      document.append(repeat("palavra ", 500)).append("\n");
    }

    for (String part : SourceSummarizer.split(document.toString())) {
      assertThat(part.length()).isAtMost(PART);
    }
  }

  @Test
  @DisplayName("a single paragraph longer than a part is cut where it must be")
  void cutsAnOversizedParagraph() {
    final List<String> parts = SourceSummarizer.split(repeat("a", PART * 2 + 50));

    assertThat(parts).hasSize(3);
    assertThat(parts.get(0)).hasLength(PART);
    assertThat(parts.get(2)).hasLength(50);
  }

  @Test
  @DisplayName("splits the whole document; choosing what to read is a separate decision")
  void splitsEverything() {
    assertThat(SourceSummarizer.split(repeat("a", PART * 20))).hasSize(20);
  }

  @Test
  @DisplayName("a document that fits is read whole")
  void shortDocumentIsReadWhole() {
    final List<String> parts = SourceSummarizer.split(repeat("a", PART * 3));

    assertThat(SourceSummarizer.selectParts(parts, "Contrato")).containsExactly(0, 1, 2).inOrder();
  }

  @Test
  @DisplayName("breaks a run-together class name into stems that survive inflection")
  void stems() {
    // the class is "InviabilidadeDeCompeticao" and Lei 14.133 says "inviável a competição":
    // whole words share nothing, stems share everything
    assertThat(SourceSummarizer.stemsOf("InviabilidadeDeCompeticao"))
        .containsExactly("invia", "compe")
        .inOrder();
    assertThat(SourceSummarizer.stemsOf("Contrato")).containsExactly("contr");
  }

  @Test
  @DisplayName("spends the requests where the concept is actually discussed")
  void picksTheRelevantParts() {
    final List<String> parts = new ArrayList<>();

    // twenty parts of boilerplate, with the article that matters buried at 15 — where reading
    // from the beginning would never reach it
    for (int i = 0; i < 20; i++) {
      parts.add(repeat("disposições gerais e definições. ", 100));
    }

    parts.set(15, "Art. 74. É inexigível a licitação quando inviável a competição.");

    final List<Integer> chosen = SourceSummarizer.selectParts(parts, "InviabilidadeDeCompeticao");

    assertThat(chosen).contains(15);
    assertThat(chosen).hasSize(8);
  }

  @Test
  @DisplayName("keeps the chosen parts in the order the document tells them")
  void keepsDocumentOrder() {
    final List<String> parts = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      parts.add("texto irrelevante");
    }

    parts.set(17, "competição inviável");
    parts.set(3, "competição inviável");

    final List<Integer> chosen = SourceSummarizer.selectParts(parts, "InviabilidadeDeCompeticao");

    assertThat(chosen).isInOrder();
    assertThat(chosen).containsAtLeast(3, 17);
  }

  @Test
  @DisplayName("a document that never mentions the class is read from the beginning")
  void fallsBackToTheBeginning() {
    final List<String> parts = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      parts.add("nada a ver com o assunto");
    }

    assertThat(SourceSummarizer.selectParts(parts, "InviabilidadeDeCompeticao"))
        .containsExactly(0, 1, 2, 3, 4, 5, 6, 7)
        .inOrder();
  }

  private static String repeat(String text, int times) {
    final StringBuilder builder = new StringBuilder(text.length() * times);

    for (int i = 0; i < times; i++) {
      builder.append(text);
    }

    return builder.toString();
  }
}
