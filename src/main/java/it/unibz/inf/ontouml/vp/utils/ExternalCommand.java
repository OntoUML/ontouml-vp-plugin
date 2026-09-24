package it.unibz.inf.ontouml.vp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Finds and runs the external converters this plugin leans on, without a shell.
 *
 * <p>Finding them is the part that is easy to get wrong. Visual Paradigm started from the Dock or
 * the Finder on macOS does not inherit the shell's {@code PATH}: {@code curl} happens to work
 * because it lives in {@code /usr/bin}, but {@code docling} and {@code pdftotext} are usually in
 * {@code /opt/homebrew/bin}, {@code /usr/local/bin} or a virtualenv, and invoking them by bare name
 * would fail every time — while the user watches the very same command work in their terminal. So
 * a configured absolute path comes first, and a short list of the usual homes is probed after it.
 */
public class ExternalCommand {

  private ExternalCommand() {}

  /**
   * Where a tool installed by homebrew, pip or the system is likely to be.
   *
   * <p>{@code ~/.local/bin} earns its place: it is where {@code pipx} and {@code pip install
   * --user} put a command, and so the most likely home of Docling on a machine whose system Python
   * refuses to install anything globally — which is every current homebrew install.
   */
  private static String[] usualDirectories() {
    final String home = System.getProperty("user.home");

    return new String[] {
      home == null ? "/usr/local/bin" : home + "/.local/bin",
      "/opt/homebrew/bin",
      "/usr/local/bin",
      "/opt/local/bin",
      "/usr/bin",
      "/bin"
    };
  }

  /** The same list, for the settings to say where it looked. */
  public static String describeSearchedDirectories() {
    return String.join(", ", usualDirectories());
  }

  /** Resolution is stable for a session and costs file system calls, so it is remembered. */
  private static final Map<String, String> RESOLVED = new ConcurrentHashMap<>();

  /** Stands in for "looked, found nothing" — {@link ConcurrentHashMap} takes no null value. */
  private static final String NOT_FOUND = "";

  /**
   * The absolute path of an executable, or null when it cannot be found.
   *
   * @param command an absolute path, which is taken as given, or a bare name to look for.
   */
  public static String resolve(String command) {
    if (command == null || command.trim().isEmpty()) {
      return null;
    }

    final String wanted = command.trim();
    final String remembered = RESOLVED.get(wanted);

    if (remembered != null) {
      return remembered.equals(NOT_FOUND) ? null : remembered;
    }

    final String found = look(wanted);
    RESOLVED.put(wanted, found == null ? NOT_FOUND : found);

    return found;
  }

  private static String look(String command) {
    // an absolute or relative path is a decision the user already made; only check it is usable
    if (command.contains(File.separator)) {
      final File given = new File(command);
      return given.isFile() && given.canExecute() ? given.getAbsolutePath() : null;
    }

    for (String directory : usualDirectories()) {
      final File candidate = new File(directory, command);

      if (candidate.isFile() && candidate.canExecute()) {
        return candidate.getAbsolutePath();
      }
    }

    // the environment we were launched with, whatever it turned out to be
    final String path = System.getenv("PATH");

    if (path != null) {
      for (String directory : path.split(File.pathSeparator)) {
        final File candidate = new File(directory, command);

        if (candidate.isFile() && candidate.canExecute()) {
          return candidate.getAbsolutePath();
        }
      }
    }

    return null;
  }

  /** Forgets what was resolved, so a path corrected in the settings takes effect at once. */
  public static void forgetResolutions() {
    RESOLVED.clear();
  }

  /** What a finished process left behind. */
  public static final class Result {

    private final int exitCode;
    private final byte[] output;
    private final String error;

    private Result(int exitCode, byte[] output, String error) {
      this.exitCode = exitCode;
      this.output = output;
      this.error = error;
    }

    public boolean succeeded() {
      return exitCode == 0;
    }

    public int getExitCode() {
      return exitCode;
    }

    public byte[] getOutput() {
      return output;
    }

    /** Standard error, trimmed to something that fits in a log line. */
    public String getError() {
      return error;
    }
  }

  /**
   * Runs the command and waits for it, killing it when it overruns.
   *
   * @return the result, or null when the executable could not be started at all.
   */
  public static Result run(List<String> command, int timeoutSeconds) {
    Process process = null;

    try {
      final ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(false);
      process = builder.start();

      // both streams are drained on their own threads, and the wait is on the process itself.
      // Reading here instead would defeat the timeout: a read blocks until the child closes the
      // stream, so a converter that hangs would hold the documentation dialog for as long as it
      // liked, however few seconds the user configured.
      final StreamDrain output = new StreamDrain(process.getInputStream(), MAX_OUTPUT_BYTES);
      final StreamDrain errors = new StreamDrain(process.getErrorStream(), MAX_ERROR_BYTES);
      output.start();
      errors.start();

      if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
        process.destroyForcibly();
        output.join(1000);
        errors.join(1000);

        return new Result(-1, output.bytes(), "timed out after " + timeoutSeconds + "s");
      }

      // the process is gone, so the drains are about to see end of stream
      output.join(2000);
      errors.join(1000);

      return new Result(process.exitValue(), output.bytes(), errors.text());
    } catch (Exception e) {
      return null;
    } finally {
      if (process != null && process.isAlive()) {
        process.destroyForcibly();
      }
    }
  }

  /** A converted document can be long, but not unboundedly so. */
  private static final int MAX_OUTPUT_BYTES = 32 * 1024 * 1024;

  private static final int MAX_ERROR_BYTES = 64 * 1024;

  /** The command as a list, for the common shape of an executable plus its arguments. */
  public static List<String> argumentsOf(String executable, String... arguments) {
    final List<String> command = new ArrayList<>();
    command.add(executable);

    for (String argument : arguments) {
      command.add(argument);
    }

    return command;
  }

  /** Empties one of the child's streams as it is written, so a full pipe never blocks the child. */
  private static final class StreamDrain extends Thread {

    /** Enough of a complaint to put in a log line or a dialog. */
    private static final int MAX_CHARACTERS = 500;

    private final InputStream stream;
    private final int maxBytes;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private StreamDrain(InputStream stream, int maxBytes) {
      this.stream = stream;
      this.maxBytes = maxBytes;
      setDaemon(true);
    }

    @Override
    public void run() {
      try {
        final byte[] chunk = new byte[8192];
        int count;

        while (buffer.size() < maxBytes && (count = stream.read(chunk)) != -1) {
          buffer.write(chunk, 0, count);
        }
      } catch (Exception e) {
        // the process died mid-write; whatever was read is still worth reporting
      }
    }

    private synchronized byte[] bytes() {
      return buffer.toByteArray();
    }

    private String text() {
      final String all = new String(bytes(), StandardCharsets.UTF_8).trim();

      return all.length() <= MAX_CHARACTERS ? all : all.substring(0, MAX_CHARACTERS) + "...";
    }
  }
}
