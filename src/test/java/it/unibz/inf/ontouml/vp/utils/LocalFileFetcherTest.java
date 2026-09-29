package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.utils.DocumentExtractor.Extraction;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalFileFetcherTest {

  /** Defaults: local files readable, Docling off — the configuration a fresh install has. */
  private final LlmConfigurations config = new LlmConfigurations();

  @Test
  @DisplayName("reads a Markdown file attached to a class")
  void readsMarkdown(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("norma.md");
    Files.write(file, "# Norma\n\nArt. 8º da resolução.".getBytes(StandardCharsets.UTF_8));

    final Extraction extraction = LocalFileFetcher.fetch(config, file.toString(), 1000);

    assertThat(extraction.succeeded()).isTrue();
    assertThat(extraction.getText()).contains("Art. 8º da resolução.");
  }

  @Test
  @DisplayName("strips the markup of an HTML file, as it would of a page")
  void readsHtml(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("norma.html");
    Files.write(
        file, "<html><body><p>Primeiro</p><p>Segundo</p></body></html>".getBytes(StandardCharsets.UTF_8));

    assertThat(LocalFileFetcher.fetch(config, file.toString(), 1000).getText())
        .isEqualTo("Primeiro\n\nSegundo");
  }

  @Test
  @DisplayName("gives a saved file the same charset care as a page")
  void readsLegacyEncoding(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("antiga.txt");
    Files.write(file, "resolução".getBytes(Charset.forName("windows-1252")));

    assertThat(LocalFileFetcher.fetch(config, file.toString(), 1000).getText()).isEqualTo("resolução");
  }

  @Test
  @DisplayName("accepts a file:// locator")
  void readsFileUrl(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("norma.txt");
    Files.write(file, "conteúdo".getBytes(StandardCharsets.UTF_8));

    assertThat(LocalFileFetcher.fetch(config, file.toUri().toString(), 1000).getText())
        .isEqualTo("conteúdo");
  }

  @Test
  @DisplayName("says so when the file is gone, instead of failing silently")
  void namesMissingFile(@TempDir Path directory) {
    final Extraction extraction =
        LocalFileFetcher.fetch(config, directory.resolve("ausente.md").toString(), 1000);

    assertThat(extraction.succeeded()).isFalse();
    assertThat(extraction.getReason()).contains("arquivo não encontrado");
  }

  @Test
  @DisplayName("says so when the locator names a directory")
  void namesDirectory(@TempDir Path directory) {
    final Extraction extraction = LocalFileFetcher.fetch(config, directory.toString(), 1000);

    assertThat(extraction.succeeded()).isFalse();
    assertThat(extraction.getReason()).contains("é um diretório");
  }

  @Test
  @DisplayName("says so when the file is empty")
  void namesEmptyFile(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("vazio.txt");
    Files.createFile(file);

    assertThat(LocalFileFetcher.fetch(config, file.toString(), 1000).getReason()).contains("arquivo vazio");
  }

  @Test
  @DisplayName("treats everything that is not http(s) as a file")
  void recognisesFileLocators() {
    assertThat(LocalFileFetcher.isFileLocator("/Users/alex/norma.pdf")).isTrue();
    assertThat(LocalFileFetcher.isFileLocator("file:///Users/alex/norma.pdf")).isTrue();
    assertThat(LocalFileFetcher.isFileLocator("https://planalto.gov.br/lei")).isFalse();
    assertThat(LocalFileFetcher.isFileLocator("http://planalto.gov.br/lei")).isFalse();
    assertThat(LocalFileFetcher.isFileLocator("  ")).isFalse();
    assertThat(LocalFileFetcher.isFileLocator(null)).isFalse();
  }

  @Test
  @DisplayName("resolves an absolute path and a file:// URL to the same file")
  void resolvesBothForms(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("norma com espaço.txt");
    Files.write(file, "x".getBytes(StandardCharsets.UTF_8));

    final File fromPath = LocalFileFetcher.resolve(file.toString());
    final File fromUrl = LocalFileFetcher.resolve(file.toUri().toString());

    assertThat(fromPath).isNotNull();
    assertThat(fromUrl).isNotNull();
    assertThat(fromUrl.getAbsolutePath()).isEqualTo(fromPath.getAbsolutePath());
  }

  @Test
  @DisplayName("hands a file whose bytes betray a document to the converter, whatever it is named")
  void mislabelledDocumentGoesToTheConverter(@TempDir Path directory) throws IOException {
    // a PDF saved as .txt: stripping markup from it would put gibberish in the prompt
    final Path file = directory.resolve("norma.txt");
    Files.write(file, "%PDF-1.4\nbinary".getBytes(StandardCharsets.ISO_8859_1));

    final Extraction extraction = LocalFileFetcher.fetch(config, file.toString(), 1000);

    assertThat(extraction.succeeded()).isFalse();
    assertThat(extraction.getReason()).contains("não convertido");
  }
}
