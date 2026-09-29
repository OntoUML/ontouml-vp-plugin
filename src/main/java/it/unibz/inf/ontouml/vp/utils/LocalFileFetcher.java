package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.utils.DocumentExtractor.Extraction;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Reads a reference that points at a file rather than at a page. Visual Paradigm's References tab
 * offers "File" beside "URL", and a class documented from a norm the user has on disk cites it that
 * way — so a source that never leaves the machine is still a source.
 *
 * <p>Every refusal is named. A file that moved, a permission the process does not have, a format
 * nothing here can read: each one comes back as a reason the caller can put in front of the model
 * and in the log, because a source that vanishes silently is the failure mode this whole path
 * exists to avoid.
 */
public class LocalFileFetcher {

  /** Past this, a "reference" is a data dump and reading it would only cost time. */
  private static final long MAX_BYTES = 64L * 1024 * 1024;

  /** What can be read as text directly, without a converter. */
  private static final List<String> TEXT_EXTENSIONS =
      Arrays.asList("txt", "md", "markdown", "html", "htm", "xml", "json", "csv", "adoc", "rst");

  /** What still needs markup stripped after decoding. */
  private static final List<String> MARKUP_EXTENSIONS = Arrays.asList("html", "htm", "xml");

  private LocalFileFetcher() {}

  /**
   * Whether this locator names a file rather than a web page. Everything that is not http(s) and
   * is not empty is treated as a path: {@code file://} URLs, absolute paths, and the relative ones
   * Visual Paradigm stores when the file sits beside the project.
   */
  public static boolean isFileLocator(String locator) {
    if (locator == null || locator.trim().isEmpty()) {
      return false;
    }

    final String trimmed = locator.trim().toLowerCase(Locale.ROOT);

    return !trimmed.startsWith("http://") && !trimmed.startsWith("https://");
  }

  /** Reads the file behind a locator, or says why it could not be read. */
  public static Extraction fetch(LlmConfigurations config, String locator, int maxCharacters) {
    return fetch(config, TaskProgress.silent(), locator, maxCharacters);
  }

  /** As above, telling {@code progress} which file is being read. */
  public static Extraction fetch(
      LlmConfigurations config, TaskProgress progress, String locator, int maxCharacters) {
    final File file = resolve(locator);

    if (file != null) {
      progress.activity("Lendo o arquivo " + file.getName());
    }


    if (file == null) {
      return Extraction.failed("caminho inválido: " + locator);
    }

    if (!file.exists()) {
      return Extraction.failed("arquivo não encontrado: " + file.getAbsolutePath());
    }

    if (file.isDirectory()) {
      return Extraction.failed("é um diretório, não um arquivo: " + file.getAbsolutePath());
    }

    if (!file.canRead()) {
      return Extraction.failed("sem permissão de leitura: " + file.getAbsolutePath());
    }

    if (file.length() == 0) {
      return Extraction.failed("arquivo vazio: " + file.getAbsolutePath());
    }

    if (file.length() > MAX_BYTES) {
      return Extraction.failed(
          "arquivo grande demais ("
              + (file.length() / (1024 * 1024))
              + " MB): "
              + file.getAbsolutePath());
    }

    final String extension = extensionOf(file);

    if (TEXT_EXTENSIONS.contains(extension)) {
      return asText(config, progress, file, extension, maxCharacters);
    }

    return DocumentExtractor.extract(config, progress, file, file.getAbsolutePath(), maxCharacters);
  }

  /**
   * A text file still gets the charset treatment: a norm saved from an old official page is
   * windows-1252 as often as it is UTF-8, and guessing wrong mangles every accent in it.
   */
  private static Extraction asText(
      LlmConfigurations config,
      TaskProgress progress,
      File file,
      String extension,
      int maxCharacters) {
    try {
      final byte[] bytes = Files.readAllBytes(file.toPath());

      // the extension said text, but the bytes have the last word: a .txt that is really a PDF is
      // a mislabelled download, and stripping markup from it would produce gibberish
      if (TextExtraction.looksBinary(bytes)) {
        return DocumentExtractor.extract(
            config, progress, file, file.getAbsolutePath(), maxCharacters);
      }

      final String decoded = TextExtraction.decode(bytes, null);
      final String text =
          MARKUP_EXTENSIONS.contains(extension)
              ? TextExtraction.toPlainText(decoded, maxCharacters)
              : TextExtraction.trimTo(decoded, maxCharacters);
      final String note = "arquivo: " + bytes.length + " bytes lidos como ." + extension;

      return text == null
          ? Extraction.failed("nada legível em: " + file.getAbsolutePath()).noting(note)
          : Extraction.of(text).noting(note);
    } catch (IOException e) {
      return Extraction.failed("não foi possível ler " + file.getAbsolutePath() + ": " + e.getMessage());
    }
  }

  /**
   * The file a locator names. A relative path is resolved against the project's own folder, which
   * is where Visual Paradigm stores it from.
   */
  static File resolve(String locator) {
    if (locator == null || locator.trim().isEmpty()) {
      return null;
    }

    final String trimmed = locator.trim();

    if (trimmed.toLowerCase(Locale.ROOT).startsWith("file:")) {
      try {
        return new File(URI.create(trimmed.replace(" ", "%20")));
      } catch (IllegalArgumentException e) {
        return null;
      }
    }

    final File given = new File(trimmed);

    if (given.isAbsolute()) {
      return given;
    }

    final File projectDirectory = projectDirectory();

    return projectDirectory == null ? given : new File(projectDirectory, trimmed);
  }

  /** Null outside a running Visual Paradigm, or for a project that was never saved. */
  private static File projectDirectory() {
    try {
      final File projectFile =
          ApplicationManager.instance().getProjectManager().getProject().getProjectFile();

      return projectFile == null ? null : projectFile.getParentFile();
    } catch (RuntimeException | NoClassDefFoundError e) {
      return null;
    }
  }

  private static String extensionOf(File file) {
    final String name = file.getName().toLowerCase(Locale.ROOT);
    final int dot = name.lastIndexOf('.');

    return dot <= 0 || dot == name.length() - 1 ? "" : name.substring(dot + 1);
  }
}
