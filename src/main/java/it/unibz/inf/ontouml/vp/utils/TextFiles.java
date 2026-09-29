package it.unibz.inf.ontouml.vp.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reading and writing the plugin's own text files, the same way on every platform.
 *
 * <p>Two things the JDK does not do by default. It reads and writes with the platform's charset —
 * Cp1252 on Windows with Java 11 — so the accented headings of the documentation template, which
 * the plugin recognises by their text, would arrive corrupted; everything here is UTF-8. And a
 * plain write truncates the file before it writes it, so a crash in the middle leaves an empty or
 * half-written file where the user's settings, API keys included, used to be; {@link
 * #writeAtomically} writes beside it and swaps.
 */
public class TextFiles {

  private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

  private TextFiles() {}

  public static String readUtf8(Path file) throws IOException {
    return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
  }

  /**
   * Reads a file that older versions wrote with the platform's charset. Such a file is UTF-8 when it
   * has only ASCII or was already written by this class, and otherwise — accented text saved on a
   * Windows machine — is not valid UTF-8; decoding it as UTF-8 anyway would silently turn every
   * accent into a replacement character, so it is read with the charset it was written in.
   */
  public static String readUtf8OrLegacy(Path file) throws IOException {
    final byte[] bytes = Files.readAllBytes(file);

    try {
      return StandardCharsets.UTF_8
          .newDecoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
          .decode(ByteBuffer.wrap(bytes))
          .toString();
    } catch (CharacterCodingException e) {
      return new String(bytes, legacyCharset());
    }
  }

  /**
   * Replaces the content of a file without ever leaving it half-written: the text goes to a
   * sibling temporary file, which then takes the place of the original in one step.
   *
   * <p>The temporary file is created readable by its owner only where the file system has such
   * permissions, since the files written here may hold API keys.
   */
  public static void writeAtomically(Path file, String content) throws IOException {
    final Path temporary = file.resolveSibling(file.getFileName() + ".tmp");

    try {
      Files.deleteIfExists(temporary);
      Files.createFile(temporary);
      restrictToOwner(temporary);
      Files.write(temporary, content.getBytes(StandardCharsets.UTF_8));

      try {
        Files.move(
            temporary,
            file,
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
      }
    } finally {
      Files.deleteIfExists(temporary);
    }
  }

  /**
   * Copies a file that could not be understood to a name that will not be overwritten, so the
   * next save cannot destroy what the user had in it.
   *
   * @return the copy.
   */
  public static Path backUp(Path file) throws IOException {
    final String base = file.getFileName() + "." + LocalDateTime.now().format(STAMP);
    Path copy = file.resolveSibling(base + ".bak");

    for (int n = 2; Files.exists(copy); n++) {
      copy = file.resolveSibling(base + "-" + n + ".bak");
    }

    return Files.copy(file, copy);
  }

  /**
   * The charset an older version would have written with: the platform's, unless that is already
   * UTF-8 — in which case the file was not written by this platform, and Windows-1252 is the
   * likeliest origin of accents that are not UTF-8.
   */
  private static Charset legacyCharset() {
    final Charset platform = Charset.defaultCharset();

    if (!StandardCharsets.UTF_8.equals(platform)) {
      return platform;
    }

    try {
      return Charset.forName("windows-1252");
    } catch (RuntimeException e) {
      return StandardCharsets.ISO_8859_1;
    }
  }

  private static void restrictToOwner(Path file) {
    try {
      Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
    } catch (UnsupportedOperationException | IOException e) {
      // not a POSIX file system (Windows): the user's profile folder is already private
    }
  }
}
