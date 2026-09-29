package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextExtractionTest {

  @Test
  @DisplayName("decodes a page that declares its charset in the header")
  void decodesFromHeader() {
    final byte[] bytes = "resolução".getBytes(Charset.forName("windows-1252"));

    assertThat(TextExtraction.decode(bytes, "text/html; charset=windows-1252"))
        .isEqualTo("resolução");
  }

  @Test
  @DisplayName("decodes a page that declares its charset only in the document")
  void decodesFromDocument() {
    final String page = "<meta charset=\"iso-8859-1\"><p>resolução</p>";
    final byte[] bytes = page.getBytes(StandardCharsets.ISO_8859_1);

    assertThat(TextExtraction.decode(bytes, null)).contains("resolução");
  }

  @Test
  @DisplayName("falls back to the legacy Latin encoding when nothing declares one")
  void sniffsLegacyLatin() {
    // planalto.gov.br declares no charset; assuming UTF-8 there turns every accent into U+FFFD
    final byte[] bytes = "resolução".getBytes(Charset.forName("windows-1252"));

    assertThat(TextExtraction.decode(bytes, null)).isEqualTo("resolução");
  }

  @Test
  @DisplayName("keeps UTF-8 when the bytes really are UTF-8")
  void sniffsUtf8() {
    final byte[] bytes = "resolução".getBytes(StandardCharsets.UTF_8);

    assertThat(TextExtraction.decode(bytes, null)).isEqualTo("resolução");
  }

  @Test
  @DisplayName("does not judge the encoding by a character its own truncation cut in half")
  void ignoresTruncatedTrailingCharacter() {
    final byte[] whole = "aç".getBytes(StandardCharsets.UTF_8);
    final byte[] cut = new byte[whole.length - 1];
    System.arraycopy(whole, 0, cut, 0, cut.length);

    assertThat(TextExtraction.decode(cut, null)).startsWith("a");
  }

  @Test
  @DisplayName("turns block elements into line breaks and drops the rest of the markup")
  void stripsMarkupKeepingParagraphs() {
    final String html =
        "<html><head><style>p{}</style></head><body><p>Primeiro</p><p>Segundo</p>"
            + "<script>ignore()</script></body></html>";

    // paragraphs stay separated by a blank line: the blocks a page is written in are what make it
    // readable, to the model and to whoever opens the conversation log
    assertThat(TextExtraction.toPlainText(html, 1000)).isEqualTo("Primeiro\n\nSegundo");
  }

  @Test
  @DisplayName("never collapses more than one blank line between blocks")
  void collapsesRunsOfBlankLines() {
    assertThat(TextExtraction.trimTo("a\n\n\n\n\nb", 100)).isEqualTo("a\n\nb");
  }

  @Test
  @DisplayName("decodes the accented entities an official page is written with")
  void decodesAccentedEntities() {
    assertThat(TextExtraction.toPlainText("<p>comiss&atilde;o &amp; resolu&ccedil;&atilde;o</p>", 100))
        .isEqualTo("comissão & resolução");
  }

  @Test
  @DisplayName("decodes numeric entities, decimal and hexadecimal alike")
  void decodesNumericEntities() {
    assertThat(TextExtraction.decodeEntities("art. 8&#186; e 9&#xBA;")).isEqualTo("art. 8º e 9º");
  }

  @Test
  @DisplayName("caps the text at the requested length")
  void capsLength() {
    assertThat(TextExtraction.toPlainText("<p>abcdefghij</p>", 4)).isEqualTo("abcd");
  }

  @Test
  @DisplayName("returns null when the markup held no text")
  void nullWhenEmpty() {
    assertThat(TextExtraction.toPlainText("<p></p><div></div>", 100)).isNull();
  }

  @Test
  @DisplayName("recognises a PDF by its magic bytes")
  void recognisesPdf() {
    assertThat(TextExtraction.looksBinary("%PDF-1.4 anything".getBytes(StandardCharsets.UTF_8)))
        .isTrue();
  }

  @Test
  @DisplayName("recognises binary content by its null bytes")
  void recognisesBinary() {
    assertThat(TextExtraction.looksBinary(new byte[] {'a', 'b', 0, 'c'})).isTrue();
  }

  @Test
  @DisplayName("does not take accented text for binary content")
  void acceptsAccentedText() {
    assertThat(TextExtraction.looksBinary("resolução — artigo 8º".getBytes(StandardCharsets.UTF_8)))
        .isFalse();
  }
}
