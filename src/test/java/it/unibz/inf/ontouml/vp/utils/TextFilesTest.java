package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TextFilesTest {

  @TempDir Path directory;

  @Test
  @DisplayName("accented text survives a write and a read, whatever the platform charset is")
  void utf8RoundTrip() throws IOException {
    final Path file = directory.resolve("config.json");

    TextFiles.writeAtomically(file, "Descrição curta — Fontes Normativas");

    assertThat(Files.readAllBytes(file))
        .isEqualTo("Descrição curta — Fontes Normativas".getBytes(StandardCharsets.UTF_8));
    assertThat(TextFiles.readUtf8(file)).isEqualTo("Descrição curta — Fontes Normativas");
  }

  @Test
  @DisplayName("a write replaces the old content and leaves no temporary file behind")
  void replacesAndCleansUp() throws IOException {
    final Path file = directory.resolve("config.json");
    Files.write(file, "old".getBytes(StandardCharsets.UTF_8));

    TextFiles.writeAtomically(file, "new");

    assertThat(TextFiles.readUtf8(file)).isEqualTo("new");

    try (Stream<Path> files = Files.list(directory)) {
      final List<String> names =
          files.map(p -> p.getFileName().toString()).collect(Collectors.toList());
      assertThat(names).containsExactly("config.json");
    }
  }

  @Test
  @DisplayName("a write that cannot finish leaves the previous file as it was")
  void failedWriteKeepsTheOldFile() throws IOException {
    final Path file = directory.resolve("config.json");
    Files.write(file, "precious".getBytes(StandardCharsets.UTF_8));

    // the temporary file's name is taken by a directory, so the write cannot even start
    Files.createDirectory(directory.resolve("config.json.tmp"));
    Files.createFile(directory.resolve("config.json.tmp").resolve("blocker"));

    try {
      TextFiles.writeAtomically(file, "new");
    } catch (IOException expected) {
      // what matters is below
    }

    assertThat(TextFiles.readUtf8(file)).isEqualTo("precious");
  }

  @Test
  @DisplayName("text that is valid UTF-8 is read as UTF-8")
  void legacyReaderPrefersUtf8() throws IOException {
    final Path file = directory.resolve("a.json");
    Files.write(file, "chave: ção".getBytes(StandardCharsets.UTF_8));

    assertThat(TextFiles.readUtf8OrLegacy(file)).isEqualTo("chave: ção");
  }

  @Test
  @DisplayName("text that is not UTF-8 is not turned into replacement characters")
  void legacyReaderKeepsOldFilesReadable() throws IOException {
    final Path file = directory.resolve("old.json");
    // 0xE7 is "ç" in Cp1252 and Latin-1, and on its own is not valid UTF-8
    Files.write(file, new byte[] {'a', (byte) 0xE7, 'o'});

    final String read = TextFiles.readUtf8OrLegacy(file);

    assertThat(read).hasLength(3);
    assertThat(read).doesNotContain("�");
  }

  @Test
  @DisplayName("a backup never overwrites an earlier one")
  void backupsAreKept() throws IOException {
    final Path file = directory.resolve("config.json");
    Files.write(file, "first".getBytes(StandardCharsets.UTF_8));
    final Path one = TextFiles.backUp(file);

    Files.write(file, "second".getBytes(StandardCharsets.UTF_8));
    final Path two = TextFiles.backUp(file);

    assertThat(one.getFileName().toString()).isNotEqualTo(two.getFileName().toString());
    assertThat(TextFiles.readUtf8(one)).isEqualTo("first");
    assertThat(TextFiles.readUtf8(two)).isEqualTo("second");
    assertThat(TextFiles.readUtf8(file)).isEqualTo("second");
  }
}
