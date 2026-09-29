package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExternalCommandTest {

  @BeforeEach
  void forgetPreviousResolutions() {
    ExternalCommand.forgetResolutions();
  }

  @Test
  @DisplayName("finds a command that lives in one of the usual directories")
  void findsUsualCommand() {
    // /bin/sh is on every machine this plugin can run on, and in a directory that is probed
    assertThat(ExternalCommand.resolve("sh")).isNotNull();
  }

  @Test
  @DisplayName("probes ~/.local/bin, where pipx and pip --user put a command")
  void probesTheUserLocalBin() {
    // the homebrew Python refuses global installs, so this is where Docling actually lands
    assertThat(ExternalCommand.describeSearchedDirectories())
        .contains(System.getProperty("user.home") + "/.local/bin");
  }

  @Test
  @DisplayName("takes an absolute path as the decision the user already made")
  void acceptsAbsolutePath() {
    assertThat(ExternalCommand.resolve("/bin/sh")).isEqualTo("/bin/sh");
  }

  @Test
  @DisplayName("returns nothing for a command that is not installed, rather than throwing")
  void missingCommandResolvesToNull() {
    assertThat(ExternalCommand.resolve("nao-existe-este-comando-ontouml")).isNull();
    assertThat(ExternalCommand.resolve("/opt/nao/existe/docling")).isNull();
    assertThat(ExternalCommand.resolve("")).isNull();
    assertThat(ExternalCommand.resolve(null)).isNull();
  }

  @Test
  @DisplayName("does not take a file that cannot be executed for a command")
  void ignoresNonExecutable(@TempDir Path directory) throws IOException {
    final Path file = directory.resolve("docling");
    Files.write(file, "not a program".getBytes(StandardCharsets.UTF_8));

    assertThat(ExternalCommand.resolve(file.toString())).isNull();
  }

  @Test
  @DisplayName("running a command gives back what it wrote")
  void capturesOutput() {
    final ExternalCommand.Result result =
        ExternalCommand.run(ExternalCommand.argumentsOf("/bin/echo", "OntoUML"), 10);

    assertThat(result).isNotNull();
    assertThat(result.succeeded()).isTrue();
    assertThat(new String(result.getOutput(), StandardCharsets.UTF_8).trim()).isEqualTo("OntoUML");
  }

  @Test
  @DisplayName("a command that fails reports its code and its complaint")
  void reportsFailure(@TempDir Path directory) throws IOException {
    final Path script = write(directory, "echo pane 1>&2\nexit 3\n");

    final ExternalCommand.Result result =
        ExternalCommand.run(ExternalCommand.argumentsOf(script.toString()), 10);

    assertThat(result).isNotNull();
    assertThat(result.succeeded()).isFalse();
    assertThat(result.getExitCode()).isEqualTo(3);
    assertThat(result.getError()).contains("pane");
  }

  @Test
  @DisplayName("a command that will not finish is killed, and says so")
  void killsOnTimeout(@TempDir Path directory) throws IOException {
    final Path script = write(directory, "sleep 30\n");

    final ExternalCommand.Result result =
        ExternalCommand.run(ExternalCommand.argumentsOf(script.toString()), 1);

    assertThat(result).isNotNull();
    assertThat(result.succeeded()).isFalse();
    assertThat(result.getError()).contains("timed out");
  }

  @Test
  @DisplayName("an executable that cannot be started at all comes back as no result")
  void unstartableCommandGivesNoResult() {
    assertThat(ExternalCommand.run(ExternalCommand.argumentsOf("/opt/nao/existe/docling"), 5))
        .isNull();
  }

  private static Path write(Path directory, String script) throws IOException {
    final Path file = directory.resolve("script.sh");
    Files.write(file, ("#!/bin/sh\n" + script).getBytes(StandardCharsets.UTF_8));

    final File asFile = file.toFile();
    asFile.setExecutable(true);

    try {
      Files.setPosixFilePermissions(
          file,
          Set.of(
              PosixFilePermission.OWNER_READ,
              PosixFilePermission.OWNER_WRITE,
              PosixFilePermission.OWNER_EXECUTE));
    } catch (UnsupportedOperationException e) {
      // a file system without POSIX permissions; setExecutable above is all there is
    }

    return file;
  }
}
