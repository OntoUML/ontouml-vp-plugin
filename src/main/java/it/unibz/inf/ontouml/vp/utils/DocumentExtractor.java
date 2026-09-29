package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Turns a document into text, for the sources that are not web pages: the PDF a resolution was
 * published as, the spreadsheet an annex lives in, the Word file someone attached to a class.
 *
 * <p>Nothing here is bundled. The plugin ships as classes only — {@code assembly/assembly.xml}
 * publishes {@code target/classes} and no dependency jars — so a library such as PDFBox is not an
 * option, and conversion is delegated to a program on the user's machine:
 *
 * <ul>
 *   <li><b>Docling</b>, when the user turned it on. It reads PDF, Word, Excel, PowerPoint, HTML and
 *       images, and writes Markdown, which keeps the tables and headings a norm is written in.
 *   <li><b>pdftotext</b>, for PDF, as the light fallback.
 * </ul>
 *
 * <p>When neither can read the document the failure is <em>named</em> rather than swallowed: the
 * caller turns that reason into a declared-but-unread source in the prompt, so the model still
 * knows the class cites that norm, and cites it as unverified.
 */
public class DocumentExtractor {

  /** Set once per session, so the "this will take a while" warning is given once. */
  private static boolean warnedAboutFirstRun = false;

  /** How much longer the first conversion of a session may take, for the model download. */
  private static final int FIRST_RUN_ALLOWANCE = 4;

  /** Which invocation shape the installed Docling accepts; settled on the first conversion. */
  private static Boolean doclingTakesConvertSubcommand = null;

  /**
   * What came out of an attempt: the text, or the reason there is none — and, either way, the trail
   * of how we got there.
   *
   * <p>The trail exists because "the source was not read" is not a usable report. Which client ran,
   * what the server answered, whether a converter was even reachable: without those, a failure that
   * happens inside Visual Paradigm but not outside it cannot be told apart from one that is the
   * page's own doing. Each step appends its line, and the caller puts the whole thing in the AI
   * Conversation Log.
   */
  public static final class Extraction {

    private final String text;
    private final String reason;
    private final List<String> trail = new ArrayList<>();

    private Extraction(String text, String reason) {
      this.text = text;
      this.reason = reason;
    }

    static Extraction of(String text) {
      return new Extraction(text, null);
    }

    static Extraction failed(String reason) {
      return new Extraction(null, reason);
    }

    /** Adds a line to the trail and returns this, so it can be written inline at each step. */
    public Extraction noting(String line) {
      if (line != null && !line.trim().isEmpty()) {
        trail.add(line.trim());
      }

      return this;
    }

    /** Carries over what another attempt recorded, for a step that delegates to one. */
    Extraction noting(Extraction other) {
      trail.addAll(other.trail);
      return this;
    }

    /**
     * Adds a line at the front, for a step that ran before the ones already recorded. Retrieval
     * delegates to conversion and only then knows how the download went, but the log reads as the
     * story of one attempt, so it has to read in the order things happened.
     */
    public Extraction notingFirst(String line) {
      if (line != null && !line.trim().isEmpty()) {
        trail.add(0, line.trim());
      }

      return this;
    }

    public boolean succeeded() {
      return text != null;
    }

    public String getText() {
      return text;
    }

    /** A phrase for the prompt and the log, such as "PDF: Docling desligado e pdftotext ausente". */
    public String getReason() {
      return reason;
    }

    /** How the attempt went, one line per step, for the conversation log. */
    public List<String> getTrail() {
      return new ArrayList<>(trail);
    }
  }

  /**
   * Extracts the text of a document.
   *
   * @param file the document on disk — a local reference, or what curl just wrote to a temporary
   *     file.
   * @param label how to name the document in the reason, usually its URL or path.
   */
  public static Extraction extract(
      LlmConfigurations config, File file, String label, int maxCharacters) {
    return extract(config, TaskProgress.silent(), file, label, maxCharacters);
  }

  /**
   * As {@link #extract(LlmConfigurations, File, String, int)}, telling {@code progress} which
   * converter is running. This is the part of reading a source that takes minutes, and the one a
   * person watching a dialog most wants named.
   */
  public static Extraction extract(
      LlmConfigurations config,
      TaskProgress progress,
      File file,
      String label,
      int maxCharacters) {
    final String kind = kindOf(file);
    final List<String> tried = new ArrayList<>();
    final List<String> trail = new ArrayList<>();

    trail.add("documento: " + kind + ", " + file.length() + " bytes");

    if (config.isUseDocling()) {
      final long startedAt = System.currentTimeMillis();

      progress.activity(
          "Convertendo "
              + kind
              + " com Docling"
              + (warnedAboutFirstRun
                  ? ""
                  : " — a primeira conversão baixa modelos e pode levar alguns minutos"));

      final Extraction docling = withDocling(config, file, maxCharacters);

      if (docling.succeeded()) {
        trail.add(
            "Docling: " + docling.getText().length() + " caracteres em " + seconds(startedAt));
        trail.forEach(docling::noting);

        return docling;
      }

      trail.add("Docling: " + docling.getReason() + " (" + seconds(startedAt) + ")");
      tried.add(docling.getReason());
    } else {
      trail.add("Docling: desligado nas configurações");
      tried.add("Docling desligado nas configurações");
    }

    if (isPdf(file)) {
      final long startedAt = System.currentTimeMillis();

      progress.activity("Extraindo o texto do PDF com pdftotext");

      final Extraction pdftotext = withPdftotext(config, file, maxCharacters);

      if (pdftotext.succeeded()) {
        trail.add(
            "pdftotext: " + pdftotext.getText().length() + " caracteres em " + seconds(startedAt));
        trail.forEach(pdftotext::noting);

        return pdftotext;
      }

      trail.add("pdftotext: " + pdftotext.getReason() + " (" + seconds(startedAt) + ")");
      tried.add(pdftotext.getReason());
    } else {
      trail.add("pdftotext: só lê PDF");
      tried.add("pdftotext só lê PDF");
    }

    final String reason = kind + " não convertido (" + String.join("; ", tried) + ")";
    System.out.println("Could not extract " + label + ": " + reason);

    final Extraction failure = Extraction.failed(reason);
    trail.forEach(failure::noting);

    return failure;
  }

  /** Elapsed time as the log should read it, since a conversion is measured in seconds. */
  private static String seconds(long startedAt) {
    return String.format(
        java.util.Locale.ROOT, "%.1fs", (System.currentTimeMillis() - startedAt) / 1000.0);
  }

  /**
   * Docling writes into a directory rather than to standard output, so each conversion gets a
   * temporary one of its own and the file it produced is read back from there.
   */
  private static Extraction withDocling(LlmConfigurations config, File file, int maxCharacters) {
    final String executable = ExternalCommand.resolve(config.getDoclingCommand());

    if (executable == null) {
      return Extraction.failed(
          "Docling não encontrado em \"" + config.getDoclingCommand() + "\"");
    }

    Path output = null;

    try {
      output = Files.createTempDirectory("ontouml-docling");

      // the first conversion of a session may be downloading the layout models, which measured
      // about four times longer than the same conversion once they were cached. Judging it by the
      // steady-state timeout would fail the run that installs them, on exactly the slow connection
      // that most needs the room.
      final boolean firstRun = !warnedAboutFirstRun;
      warnAboutFirstRun();

      final ExternalCommand.Result result =
          runDocling(
              executable,
              file,
              output,
              config.getDocumentConversionTimeoutSeconds()
                  * (firstRun ? FIRST_RUN_ALLOWANCE : 1));

      if (result == null) {
        return Extraction.failed("Docling não pôde ser executado");
      }

      if (!result.succeeded()) {
        return Extraction.failed(
            "Docling falhou (" + describe(result) + ")");
      }

      final String markdown = readConverted(output);

      if (markdown == null) {
        return Extraction.failed("Docling não produziu texto");
      }

      final String text = TextExtraction.trimTo(markdown, maxCharacters);

      return text == null ? Extraction.failed("Docling produziu texto vazio") : Extraction.of(text);
    } catch (IOException e) {
      return Extraction.failed("Docling: " + e.getMessage());
    } finally {
      deleteRecursively(output);
    }
  }

  /**
   * Docling grew a {@code convert} subcommand along the way, and both shapes are out there. The
   * first conversion of a session settles which one this install takes, and the answer is kept:
   * a usage error costs one extra process, not one per document.
   */
  private static ExternalCommand.Result runDocling(
      String executable, File file, Path output, int timeoutSeconds) {
    if (!Boolean.FALSE.equals(doclingTakesConvertSubcommand)) {
      final ExternalCommand.Result withSubcommand =
          ExternalCommand.run(
              ExternalCommand.argumentsOf(
                  executable,
                  "convert",
                  "--to",
                  "md",
                  "--output",
                  output.toString(),
                  file.getAbsolutePath()),
              timeoutSeconds);

      if (withSubcommand != null && withSubcommand.succeeded()) {
        doclingTakesConvertSubcommand = Boolean.TRUE;
        return withSubcommand;
      }

      // it was settled as the working shape before, so this is a real failure, not a usage error
      if (Boolean.TRUE.equals(doclingTakesConvertSubcommand)) {
        return withSubcommand;
      }
    }

    final ExternalCommand.Result bare =
        ExternalCommand.run(
            ExternalCommand.argumentsOf(
                executable, "--to", "md", "--output", output.toString(), file.getAbsolutePath()),
            timeoutSeconds);

    if (bare != null && bare.succeeded()) {
      doclingTakesConvertSubcommand = Boolean.FALSE;
    }

    return bare;
  }

  /** The Markdown Docling left in the output directory, whatever it chose to name it. */
  private static String readConverted(Path output) throws IOException {
    try (Stream<Path> files = Files.walk(output)) {
      final List<Path> markdown = new ArrayList<>();

      files
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".md"))
          .forEach(markdown::add);

      if (markdown.isEmpty()) {
        return null;
      }

      final StringBuilder text = new StringBuilder();

      for (Path path : markdown) {
        text.append(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).append("\n");
      }

      return text.toString();
    }
  }

  private static Extraction withPdftotext(LlmConfigurations config, File file, int maxCharacters) {
    final String executable = ExternalCommand.resolve(config.getPdftotextCommand());

    if (executable == null) {
      return Extraction.failed(
          "pdftotext não encontrado em \"" + config.getPdftotextCommand() + "\"");
    }

    // "-" sends the text to standard output, so nothing has to be cleaned up afterwards
    final ExternalCommand.Result result =
        ExternalCommand.run(
            ExternalCommand.argumentsOf(executable, "-layout", file.getAbsolutePath(), "-"),
            config.getDocumentConversionTimeoutSeconds());

    if (result == null) {
      return Extraction.failed("pdftotext não pôde ser executado");
    }

    if (!result.succeeded()) {
      return Extraction.failed("pdftotext falhou (" + describe(result) + ")");
    }

    final String text =
        TextExtraction.trimTo(
            TextExtraction.decode(result.getOutput(), null), maxCharacters);

    return text == null
        ? Extraction.failed("pdftotext não extraiu texto — o PDF pode ser digitalizado")
        : Extraction.of(text);
  }

  /**
   * Whether this is a PDF, by the bytes rather than by the name: a normative document downloaded
   * from a URL with no extension is still a PDF.
   */
  private static boolean isPdf(File file) {
    if (file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
      return true;
    }

    try {
      final byte[] head = new byte[8];
      final int read = readHead(file, head);

      return read >= 4 && TextExtraction.isPdf(head);
    } catch (IOException e) {
      return false;
    }
  }

  private static int readHead(File file, byte[] buffer) throws IOException {
    try (java.io.InputStream stream = Files.newInputStream(file.toPath())) {
      return stream.read(buffer);
    }
  }

  private static String kindOf(File file) {
    final String name = file.getName().toLowerCase(Locale.ROOT);
    final int dot = name.lastIndexOf('.');

    if (dot <= 0 || dot == name.length() - 1) {
      return isPdf(file) ? "PDF" : "documento";
    }

    return name.substring(dot + 1).toUpperCase(Locale.ROOT);
  }

  private static String describe(ExternalCommand.Result result) {
    final String error = result.getError();

    if (error != null && !error.isEmpty()) {
      return error.replaceAll("\\s+", " ");
    }

    return "código " + result.getExitCode();
  }

  /**
   * Docling downloads its layout models the first time it runs, which can take minutes on a
   * connection it is sharing with nothing else. Saying so once beats a dialog that seems frozen.
   */
  private static synchronized void warnAboutFirstRun() {
    if (warnedAboutFirstRun) {
      return;
    }

    warnedAboutFirstRun = true;

    final String warning =
        "Converting a document with Docling. The first conversion after installing it downloads"
            + " its layout models and can take several minutes.";
    System.out.println(warning);

    try {
      ViewManagerUtils.log(warning);
    } catch (RuntimeException | NoClassDefFoundError e) {
      // no Visual Paradigm around this call — a notice that cannot be shown must not sink the
      // conversion it was only announcing
    }
  }

  /**
   * Checks that Docling is really usable, for the "Test" button in the settings.
   *
   * <p>A version banner alone would not be worth much: what breaks in practice is the invocation
   * shape (older installs take no {@code convert} subcommand) and reading back what it wrote. So a
   * small HTML file is actually converted. HTML is used rather than PDF on purpose — it exercises
   * the whole path in a second, while a first PDF conversion downloads the layout models and would
   * leave the settings dialog looking frozen for minutes.
   */
  public static String describeDoclingAvailability(LlmConfigurations config) {
    final String executable = ExternalCommand.resolve(config.getDoclingCommand());

    if (executable == null) {
      return "Docling não encontrado.\n\nProcurado como \""
          + config.getDoclingCommand()
          + "\" em "
          + ExternalCommand.describeSearchedDirectories()
          + " e no PATH do processo.\n\nInstale com \"pipx install docling\" (o Python do"
          + " Homebrew recusa instalações globais) e, se o Visual Paradigm foi aberto pelo Dock,"
          + " informe aqui o caminho completo do executável: aberto assim, ele não herda o PATH do"
          + " seu terminal.";
    }

    final StringBuilder report = new StringBuilder();
    report.append("Encontrado em: ").append(executable).append("\n");

    final ExternalCommand.Result version =
        ExternalCommand.run(ExternalCommand.argumentsOf(executable, "--version"), 120);

    if (version == null || !version.succeeded()) {
      report.append("\nNão respondeu a --version");

      if (version != null) {
        report.append(" (").append(describe(version)).append(")");
      }

      return report.append(".").toString();
    }

    report.append("Versão: ").append(firstLine(version)).append("\n");

    final Extraction conversion = convertProbe(config);

    if (!conversion.succeeded()) {
      return report
          .append("\nConversão de teste falhou: ")
          .append(conversion.getReason())
          .toString();
    }

    return report
        .append("\nConversão de teste concluída — Docling está funcionando.\n\nA primeira")
        .append(" conversão de um PDF baixa os modelos de layout e pode levar alguns minutos.")
        .toString();
  }

  /** Converts a small HTML file, to prove the invocation shape and the reading back of the output. */
  private static Extraction convertProbe(LlmConfigurations config) {
    Path probe = null;

    try {
      probe = Files.createTempFile("ontouml-docling-probe", ".html");
      Files.write(
          probe,
          ("<html><body><h1>OntoUML</h1><p>Teste de conversão.</p></body></html>")
              .getBytes(StandardCharsets.UTF_8));

      final Extraction extraction = withDocling(config, probe.toFile(), 500);

      if (extraction.succeeded() && !extraction.getText().contains("OntoUML")) {
        return Extraction.failed("o texto convertido não trouxe o conteúdo do arquivo de teste");
      }

      return extraction;
    } catch (IOException e) {
      return Extraction.failed("não foi possível criar o arquivo de teste: " + e.getMessage());
    } finally {
      if (probe != null) {
        try {
          Files.deleteIfExists(probe);
        } catch (IOException e) {
          // nothing worth saying about a leftover temporary file
        }
      }
    }
  }

  /** The same check for pdftotext, which is cheap enough to test on a real (tiny) PDF. */
  public static String describePdftotextAvailability(LlmConfigurations config) {
    final String executable = ExternalCommand.resolve(config.getPdftotextCommand());

    if (executable == null) {
      return "pdftotext não encontrado.\n\nProcurado como \""
          + config.getPdftotextCommand()
          + "\" em "
          + ExternalCommand.describeSearchedDirectories()
          + " e no PATH do processo.\n\nEle vem no pacote poppler (\"brew install poppler\" no"
          + " macOS, \"apt install poppler-utils\" no Linux). Se o Visual Paradigm foi aberto pelo"
          + " Dock, informe aqui o caminho completo: aberto assim, ele não herda o PATH do seu"
          + " terminal.";
    }

    Path probe = null;

    try {
      probe = Files.createTempFile("ontouml-pdftotext-probe", ".pdf");
      Files.write(probe, PROBE_PDF.getBytes(StandardCharsets.ISO_8859_1));

      final Extraction extraction = withPdftotext(config, probe.toFile(), 500);

      if (!extraction.succeeded()) {
        return "Encontrado em: " + executable + "\n\nConversão de teste falhou: "
            + extraction.getReason();
      }

      if (!extraction.getText().contains("OntoUML")) {
        return "Encontrado em: " + executable
            + "\n\nConversão de teste falhou: o texto extraído não trouxe o conteúdo do PDF de"
            + " teste.";
      }

      return "Encontrado em: " + executable
          + "\n\nConversão de teste concluída — pdftotext está funcionando.\n\nEle lê apenas PDF, e"
          + " apenas PDFs com texto: um documento digitalizado precisa do Docling, que faz OCR.";
    } catch (IOException e) {
      return "Não foi possível criar o PDF de teste: " + e.getMessage();
    } finally {
      if (probe != null) {
        try {
          Files.deleteIfExists(probe);
        } catch (IOException e) {
          // nothing worth saying about a leftover temporary file
        }
      }
    }
  }

  /**
   * The smallest PDF that says "OntoUML". It has no cross-reference table, which poppler rebuilds
   * on its own — enough to prove the executable reads a PDF and gives its text back.
   */
  private static final String PROBE_PDF =
      "%PDF-1.4\n"
          + "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
          + "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n"
          + "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 200 200]/Contents 4 0 R"
          + "/Resources<</Font<</F1 5 0 R>>>>>>endobj\n"
          + "4 0 obj<</Length 42>>stream\n"
          + "BT /F1 24 Tf 20 100 Td (OntoUML) Tj ET\n"
          + "endstream\n"
          + "endobj\n"
          + "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n"
          + "trailer<</Root 1 0 R/Size 6>>\n"
          + "%%EOF\n";

  /** Version banners go to standard output or standard error depending on the tool. */
  private static String firstLine(ExternalCommand.Result result) {
    final String out = new String(result.getOutput(), StandardCharsets.UTF_8).trim();
    final String text = out.isEmpty() ? result.getError() : out;

    if (text == null || text.isEmpty()) {
      return "(não informada)";
    }

    final int newline = text.indexOf('\n');

    return newline < 0 ? text : text.substring(0, newline);
  }

  private static void deleteRecursively(Path directory) {
    if (directory == null) {
      return;
    }

    try (Stream<Path> paths = Files.walk(directory)) {
      final List<Path> all = new ArrayList<>();
      paths.forEach(all::add);

      // deepest first, so a directory is only removed once it is empty
      for (int i = all.size() - 1; i >= 0; i--) {
        Files.deleteIfExists(all.get(i));
      }
    } catch (IOException e) {
      // a leftover temporary directory is the operating system's problem, not the user's
    }
  }
}
