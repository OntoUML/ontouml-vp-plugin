package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes every LLM and retrieval exchange to a file while debug mode is on.
 *
 * <p>The in-memory log keeps the last 25 exchanges and dies with the session, which is right for
 * glancing at what was just asked and useless for the question that actually comes up: why did this
 * run behave differently from that one. Documenting a single class can produce a dozen exchanges —
 * one per source read, one per part of a long document — so the interesting ones scroll out of the
 * window before anyone thinks to look.
 *
 * <p>Debug mode turns the cap off and mirrors everything to disk, one file per session, in the
 * order things happened. It is off by default because a prompt carries the user's model, and an
 * exchange carries no API key but does carry whatever the model was asked about.
 */
public class DebugLogWriter {

  private static final String DIRECTORY_NAME = "ontouml-ai-logs";

  private static final DateTimeFormatter FILE_STAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

  private static final String SEPARATOR =
      "\n\n================================================================================\n\n";

  /** One file per session, settled on the first write. */
  private static Path sessionFile;

  /** Said once, so a directory that cannot be written does not spam the console. */
  private static boolean warned;

  private DebugLogWriter() {}

  /** Whether the user asked for everything to be recorded. */
  public static boolean isEnabled() {
    final LlmConfigurations config = configuration();

    return config != null && config.isDebugMode();
  }

  /** Appends one exchange, when debug mode is on. Never throws: logging must not break the run. */
  public static synchronized void write(LlmExchange exchange) {
    if (!isEnabled()) {
      return;
    }

    try {
      final Path file = sessionFile();

      if (file == null) {
        return;
      }

      Files.write(
          file,
          (exchange.toDetailedText() + SEPARATOR).getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException | RuntimeException e) {
      if (!warned) {
        warned = true;
        System.out.println("Could not write the debug conversation log: " + e);
      }
    }
  }

  private static Path sessionFile() throws IOException {
    if (sessionFile != null) {
      return sessionFile;
    }

    final File directory = directory();

    if (directory == null) {
      return null;
    }

    Files.createDirectories(directory.toPath());

    sessionFile =
        directory
            .toPath()
            .resolve("ontouml-ai-" + LocalDateTime.now().format(FILE_STAMP) + ".log");

    System.out.println("Debug mode is on; conversations are being written to " + sessionFile);

    return sessionFile;
  }

  /**
   * Where the logs go: the directory configured in the settings, or {@code ontouml-ai-logs} beside
   * the plugin's own configuration file in the Visual Paradigm workspace — the place someone
   * already knows how to find.
   */
  public static File directory() {
    final LlmConfigurations config = configuration();
    final String configured = config == null ? "" : config.getDebugLogDirectory();

    if (!configured.trim().isEmpty()) {
      return new File(configured.trim());
    }

    final File workspace = workspace();

    return workspace == null ? null : new File(workspace, DIRECTORY_NAME);
  }

  /** The path to show in the settings, even before anything has been written. */
  public static String describeDirectory() {
    final File directory = directory();

    return directory == null ? "(indisponível fora do Visual Paradigm)" : directory.getAbsolutePath();
  }

  /** The file of the current session, or null when nothing has been written yet. */
  public static synchronized String currentFile() {
    return sessionFile == null ? null : sessionFile.toString();
  }

  private static File workspace() {
    try {
      return ApplicationManager.instance().getWorkspaceLocation();
    } catch (RuntimeException | NoClassDefFoundError e) {
      return null;
    }
  }

  private static LlmConfigurations configuration() {
    try {
      return Configurations.getInstance().getLlmConfigurations();
    } catch (RuntimeException | NoClassDefFoundError e) {
      // outside a running Visual Paradigm there is no configuration, and so no debug mode
      return null;
    }
  }
}
