package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.utils.DocumentExtractor.Extraction;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Downloads a source and reduces it to plain text, so it can be summarised from what it actually
 * says instead of from what the model remembers about its URL.
 *
 * <p>This is the first thing to run once a source is known: a search service is there to
 * <em>find</em> pages, and its snippet is only an advertisement for one. Every URL that reaches
 * the prompt should have been retrieved here first, falling back to the snippet only when
 * retrieval genuinely failed.
 *
 * <p>Retrieval goes through {@code curl} first. It carries its own TLS stack and redirect handling,
 * and plenty of official sites answer a bare Java client with 403 or a handshake it cannot
 * complete; {@code HttpURLConnection} stays as the fallback for a machine without curl.
 *
 * <p>What comes back is not always a page. A PDF, a spreadsheet or a Word file is written to a
 * temporary file and handed to {@link DocumentExtractor} — before this it was simply dropped, and
 * PDF is the format most Brazilian norms are published in.
 */
public class WebPageFetcher {

  /** Enough of a page to summarise; keeps a long document from swamping the request. */
  private static final int MAX_CHARACTERS = 6000;

  /**
   * How many bytes of markup to parse for a given number of characters of text.
   *
   * <p>This used to be a constant, {@code MAX_CHARACTERS * 8}, tuned for the 6.000-character search
   * path and then applied to every caller — including the one that asks for 120.000. Lei 14.133
   * arrived as its first 48 KB of HTML, which is the preamble and the definitions: the text stopped
   * at art. 6º, so a class about "inviabilidade de competição" was told, truthfully, that art. 74
   * was not in there. The budget has to follow what the caller asked for.
   */
  private static long bytesFor(int maxCharacters) {
    return Math.min((long) maxCharacters * 8, MAX_DOCUMENT_BYTES);
  }

  /** A document is downloaded whole: a converter cannot make sense of half a PDF. */
  private static final long MAX_DOCUMENT_BYTES = 64L * 1024 * 1024;

  private static final int TIMEOUT_MILLIS = 15000;

  /** Redirect chains that go past this are not leading anywhere useful. */
  private static final int MAX_REDIRECTS = 5;

  /**
   * Honest, but shaped like the strings servers are used to: the bare product name alone is enough
   * for some publishers to answer 403.
   */
  private static final String USER_AGENT =
      "Mozilla/5.0 (compatible; OntoUML-VP-Plugin; +https://github.com/OntoUML/ontouml-vp-plugin)";

  private static final String ACCEPT =
      "text/html,application/xhtml+xml,application/xml;q=0.9,text/plain;q=0.8,*/*;q=0.7";

  /** Settled on the first attempt of a session, so a machine without curl pays for one process. */
  private static Boolean curlAvailable = null;

  /**
   * Retrieves the source and returns it as plain text.
   *
   * @return the text, or null when it cannot be retrieved — the caller must treat that as "no
   *     content", never as an invitation to summarise from memory.
   */
  public static String fetch(LlmConfigurations config, String url) {
    return fetch(config, url, MAX_CHARACTERS);
  }

  /** As {@link #fetch(LlmConfigurations, String)}, keeping at most {@code maxCharacters} of the text. */
  public static String fetch(LlmConfigurations config, String url, int maxCharacters) {
    final Extraction extraction = retrieve(config, url, maxCharacters);

    return extraction.succeeded() ? extraction.getText() : null;
  }

  /**
   * As {@link #fetch(LlmConfigurations, String, int)}, but saying why there is no text. The reason is what lets a
   * source that could not be read still be declared to the model instead of disappearing.
   */
  public static Extraction retrieve(LlmConfigurations config, String url, int maxCharacters) {
    return retrieve(config, TaskProgress.silent(), url, maxCharacters);
  }

  /**
   * As {@link #retrieve(LlmConfigurations, String, int)}, telling {@code progress} what is being
   * done as it goes: which client is downloading, and that a document is being converted.
   */
  public static Extraction retrieve(
      LlmConfigurations config, TaskProgress progress, String url, int maxCharacters) {
    if (!isRetrievable(url)) {
      return Extraction.failed("não é uma URL http(s): " + url);
    }

    Attempt viaCurl = null;

    if (!Boolean.FALSE.equals(curlAvailable)) {
      progress.activity("Baixando com curl: " + shorten(url));
      viaCurl = fetchWithCurl(config, progress, url, maxCharacters);

      if (viaCurl.extraction.succeeded()) {
        return viaCurl.extraction;
      }
    }

    // the in-process client is worth a turn only when curl never got a body: if it did, and the
    // body was something we cannot read, asking again over HTTP fetches the same bytes
    if (viaCurl == null || viaCurl.worthAnotherClient) {
      progress.activity("Baixando (HTTP): " + shorten(url));
      final Extraction viaHttp = fetchOverHttp(config, progress, url, maxCharacters);

      if (viaCurl != null) {
        // the curl attempt is part of how we got here, whichever client ended up answering
        for (String line : viaCurl.extraction.getTrail()) {
          viaHttp.noting(line);
        }
      }

      if (viaHttp.succeeded() || viaCurl == null) {
        return viaHttp;
      }
    }

    return viaCurl.extraction;
  }

  /**
   * One retrieval attempt, and whether a different client could plausibly do better than it did.
   * A response we downloaded but cannot read is the same response for everyone; a request that
   * never completed is not.
   */
  private static final class Attempt {

    private final Extraction extraction;
    private final boolean worthAnotherClient;

    private Attempt(Extraction extraction, boolean worthAnotherClient) {
      this.extraction = extraction;
      this.worthAnotherClient = worthAnotherClient;
    }
  }

  /**
   * Whether the URL is one this class can even try. Only {@code http}/{@code https} is retrievable,
   * which is also what keeps a URL from being read as an option when it is handed to {@code curl}.
   */
  public static boolean isRetrievable(String url) {
    if (url == null) {
      return false;
    }

    final String trimmed = url.trim();

    if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
      return false;
    }

    // a URL carrying whitespace or control characters is malformed, and not worth passing on
    for (int i = 0; i < trimmed.length(); i++) {
      if (Character.isWhitespace(trimmed.charAt(i)) || trimmed.charAt(i) < 0x20) {
        return false;
      }
    }

    return true;
  }

  /**
   * The main path: {@code curl} writes the body to a temporary file and reports the content type it
   * saw, which is what tells a page from a document.
   *
   * <p>The process is started without a shell and the URL is known to begin with a scheme, so it
   * cannot be taken for an option.
   */
  private static Attempt fetchWithCurl(
      LlmConfigurations config, TaskProgress progress, String url, int maxCharacters) {
    final String executable = ExternalCommand.resolve("curl");

    if (executable == null) {
      curlAvailable = Boolean.FALSE;
      return new Attempt(
          Extraction.failed("curl não encontrado")
              .noting("curl: não encontrado em " + ExternalCommand.describeSearchedDirectories()),
          true);
    }

    Path body = null;
    final long startedAt = System.currentTimeMillis();

    try {
      body = Files.createTempFile("ontouml-source", null);

      final ExternalCommand.Result result =
          ExternalCommand.run(
              ExternalCommand.argumentsOf(
                  executable,
                  "--silent",
                  "--show-error",
                  "--location",
                  "--compressed",
                  // without this a 404 error page is written out and read as if it were the source
                  "--fail",
                  "--max-redirs",
                  String.valueOf(MAX_REDIRECTS),
                  "--max-time",
                  String.valueOf(TIMEOUT_MILLIS / 1000),
                  "--max-filesize",
                  String.valueOf(MAX_DOCUMENT_BYTES),
                  "--user-agent",
                  USER_AGENT,
                  "--header",
                  "Accept: " + ACCEPT,
                  "--output",
                  body.toString(),
                  "--write-out",
                  "%{content_type}",
                  url.trim()),
              TIMEOUT_MILLIS / 1000 * 2);

      if (result == null) {
        curlAvailable = Boolean.FALSE;
        return new Attempt(
            Extraction.failed("curl não pôde ser executado")
                .noting("curl: " + executable + " não pôde ser iniciado neste processo"),
            true);
      }

      curlAvailable = Boolean.TRUE;

      if (!result.succeeded()) {
        return new Attempt(
            Extraction.failed("curl falhou em " + url + " (" + describe(result) + ")")
                .noting("curl: " + executable + " → " + describe(result) + elapsed(startedAt)),
            worthAnotherClient(result));
      }

      final String contentType = new String(result.getOutput(), StandardCharsets.UTF_8).trim();
      final long size = body.toFile().length();

      // the body is in hand: whatever we make of it, another client would get the same bytes
      return new Attempt(
          interpret(config, progress, body.toFile(), contentType, url, maxCharacters)
              .notingFirst(
                  "curl: "
                      + executable
                      + " → ok, "
                      + (contentType.isEmpty() ? "sem content-type" : contentType)
                      + ", "
                      + size
                      + " bytes"
                      + elapsed(startedAt)),
          false);
    } catch (Exception e) {
      return new Attempt(Extraction.failed("curl: " + e).noting("curl: " + e), true);
    } finally {
      deleteQuietly(body);
    }
  }

  /**
   * Decides what was downloaded and turns it into text. The bytes have the last word over the
   * declared type: a PDF served with no {@code Content-Type} used to pass for HTML here, and markup
   * stripping turned it into gibberish that reached the prompt as if it were the source.
   */
  private static Extraction interpret(
      LlmConfigurations config,
      TaskProgress progress,
      File body,
      String contentType,
      String label,
      int maxCharacters) {
    if (!body.isFile() || body.length() == 0) {
      return Extraction.failed("resposta vazia de " + label);
    }

    try {
      final byte[] head = readUpTo(body, bytesFor(maxCharacters));

      if (TextExtraction.looksBinary(head) || isDocument(contentType)) {
        return DocumentExtractor.extract(config, progress, body, label, maxCharacters);
      }

      if (!isTextual(contentType)) {
        return Extraction.failed("tipo de conteúdo não legível (" + contentType + ") em " + label);
      }

      final String text =
          TextExtraction.toPlainText(TextExtraction.decode(head, contentType), maxCharacters);

      return text == null
          ? Extraction.failed("nada legível em " + label)
          : Extraction.of(text);
    } catch (Exception e) {
      return Extraction.failed("não foi possível ler a resposta de " + label + ": " + e);
    }
  }

  /**
   * The fallback, for a machine without curl. Redirects are followed by hand:
   * {@code HttpURLConnection} drops the ones that switch protocol, and an http URL landing on its
   * https twin is the most common shape a normative source takes.
   */
  private static Extraction fetchOverHttp(
      LlmConfigurations config, TaskProgress progress, String url, int maxCharacters) {
    String target = url.trim();

    for (int redirect = 0; redirect <= MAX_REDIRECTS; redirect++) {
      HttpURLConnection connection = null;

      try {
        connection = (HttpURLConnection) new URL(target).openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", ACCEPT);
        connection.setRequestProperty("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");

        final int status = connection.getResponseCode();

        if (isRedirect(status)) {
          final String location = connection.getHeaderField("Location");

          if (location == null) {
            return Extraction.failed("redirecionamento sem destino em " + target);
          }

          // relative Locations are legal, so resolve against the URL we asked for
          target = new URL(new URL(target), location).toString();
          continue;
        }

        if (status != HttpURLConnection.HTTP_OK) {
          return Extraction.failed("HTTP " + status + " em " + target)
              .noting("HttpURLConnection: HTTP " + status + " em " + target);
        }

        final String contentType = connection.getContentType();

        // How much to read is decided by what is coming. MAX_BYTES is "enough of a page to
        // summarise", and applying it to a document hands the converter a file cut in half: a
        // 508 KB PDF arrived as 49 KB and pdftotext answered "Couldn't read xref table". A
        // document is read whole, up to the document cap.
        final byte[] bytes =
            read(
                decompress(connection.getInputStream(), connection),
                isDocument(contentType) ? MAX_DOCUMENT_BYTES : bytesFor(maxCharacters));

        if (bytes.length == 0) {
          return Extraction.failed("resposta vazia de " + target);
        }

        // a document has to be on disk for a converter to open it
        if (isDocument(contentType)) {
          return extractFromBytes(config, progress, bytes, target, maxCharacters);
        }

        // the server declared nothing (or lied) and the bytes are a document: what we hold is the
        // truncated head of one, so it has to be fetched again under the document cap
        if (TextExtraction.looksBinary(bytes)) {
          return refetchAsDocument(config, progress, target, maxCharacters)
              .noting("HttpURLConnection: documento não declarado, relido inteiro");
        }

        if (!isTextual(contentType)) {
          return Extraction.failed(
              "tipo de conteúdo não legível (" + contentType + ") em " + target);
        }

        final String text =
            TextExtraction.toPlainText(TextExtraction.decode(bytes, contentType), maxCharacters);
        final String note =
            "HttpURLConnection: ok, "
                + (contentType == null ? "sem content-type" : contentType)
                + ", "
                + bytes.length
                + " bytes";

        return text == null
            ? Extraction.failed("nada legível em " + target).noting(note)
            : Extraction.of(text).noting(note);
      } catch (Exception e) {
        return Extraction.failed("não foi possível acessar " + target + ": " + e)
            .noting("HttpURLConnection: " + e);
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }

    return Extraction.failed("cadeia de redirecionamentos longa demais a partir de " + url);
  }

  private static Extraction extractFromBytes(
      LlmConfigurations config,
      TaskProgress progress,
      byte[] bytes,
      String label,
      int maxCharacters) {
    Path body = null;

    try {
      body = Files.createTempFile("ontouml-source", null);
      Files.write(body, bytes);

      return DocumentExtractor.extract(config, progress, body.toFile(), label, maxCharacters);
    } catch (Exception e) {
      return Extraction.failed("não foi possível guardar o documento de " + label + ": " + e);
    } finally {
      deleteQuietly(body);
    }
  }

  /** Whether the response is a document a converter may open rather than markup to strip. */
  private static boolean isDocument(String contentType) {
    if (contentType == null) {
      return false;
    }

    final String type = contentType.toLowerCase(Locale.ROOT);

    return type.contains("pdf")
        || type.contains("msword")
        || type.contains("officedocument")
        || type.contains("ms-excel")
        || type.contains("ms-powerpoint")
        || type.contains("opendocument")
        || type.contains("rtf");
  }

  /** Whether the response is something markup stripping can make sense of. A PDF is not. */
  private static boolean isTextual(String contentType) {
    if (contentType == null || contentType.isEmpty()) {
      // servers that declare nothing are usually serving HTML; the byte check above already
      // caught the ones that were not
      return true;
    }

    final String type = contentType.toLowerCase(Locale.ROOT);

    return type.startsWith("text/")
        || type.contains("html")
        || type.contains("xml")
        || type.contains("json");
  }

  private static boolean isRedirect(int status) {
    return status == HttpURLConnection.HTTP_MOVED_PERM
        || status == HttpURLConnection.HTTP_MOVED_TEMP
        || status == HttpURLConnection.HTTP_SEE_OTHER
        || status == 307
        || status == 308;
  }

  private static InputStream decompress(InputStream stream, HttpURLConnection connection)
      throws java.io.IOException {
    final String encoding = connection.getContentEncoding();

    if (encoding == null) {
      return stream;
    }

    if (encoding.equalsIgnoreCase("gzip")) {
      return new java.util.zip.GZIPInputStream(stream);
    }

    if (encoding.equalsIgnoreCase("deflate")) {
      return new java.util.zip.InflaterInputStream(stream);
    }

    return stream;
  }

  private static byte[] read(InputStream stream, long maxBytes) throws java.io.IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final byte[] chunk = new byte[8192];
    int count;

    while (buffer.size() < maxBytes && (count = stream.read(chunk)) != -1) {
      buffer.write(chunk, 0, count);
    }

    stream.close();

    return buffer.toByteArray();
  }

  /**
   * Downloads again, keeping the whole body, for a document the server did not declare as one. The
   * second request is the price of a publisher that serves a PDF with no {@code Content-Type}; the
   * alternative is handing a converter a truncated file, which fails in a way that reads like a
   * broken document rather than a truncated download.
   */
  private static Extraction refetchAsDocument(
      LlmConfigurations config, TaskProgress progress, String target, int maxCharacters) {
    HttpURLConnection connection = null;

    try {
      connection = (HttpURLConnection) new URL(target).openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(TIMEOUT_MILLIS);
      connection.setReadTimeout(TIMEOUT_MILLIS);
      connection.setRequestProperty("User-Agent", USER_AGENT);
      connection.setRequestProperty("Accept", ACCEPT);

      final byte[] bytes =
          read(decompress(connection.getInputStream(), connection), MAX_DOCUMENT_BYTES);

      return bytes.length == 0
          ? Extraction.failed("resposta vazia ao reler " + target)
          : extractFromBytes(config, progress, bytes, target, maxCharacters);
    } catch (Exception e) {
      return Extraction.failed("não foi possível reler " + target + ": " + e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * The head of a downloaded body, for the text path. A document is read whole by its converter
   * straight from the file, so only the markup path needs a cap here.
   */
  private static byte[] readUpTo(File file, long maxBytes) throws java.io.IOException {
    try (InputStream stream = Files.newInputStream(file.toPath())) {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      final byte[] chunk = new byte[8192];
      int count;

      while (buffer.size() < maxBytes && (count = stream.read(chunk)) != -1) {
        buffer.write(chunk, 0, count);
      }

      return buffer.toByteArray();
    }
  }

  /**
   * Whether the in-process client deserves a turn after curl failed. A server that answered with a
   * status — 404, 403, 500 — gave its answer, and asking again from another client only costs a
   * second round trip per dead link, of which a model can carry many. A request that never
   * completed is a different matter: a TLS quirk or a proxy may well go the other client's way.
   */
  private static boolean worthAnotherClient(ExternalCommand.Result result) {
    final String error = result.getError();

    return error == null || !error.contains("returned error:");
  }

  /** A URL short enough for one line of the progress dialog. */
  static String shorten(String url) {
    final String trimmed = url == null ? "" : url.trim();

    return trimmed.length() <= 70 ? trimmed : trimmed.substring(0, 67) + "...";
  }

  /** Elapsed time for a trail line, in the form " (1,2 s)". */
  private static String elapsed(long startedAt) {
    return String.format(
        java.util.Locale.ROOT, " (%.1fs)", (System.currentTimeMillis() - startedAt) / 1000.0);
  }

  private static String describe(ExternalCommand.Result result) {
    final String error = result.getError();

    return error == null || error.isEmpty()
        ? "código " + result.getExitCode()
        : error.replaceAll("\\s+", " ");
  }

  private static void deleteQuietly(Path path) {
    if (path == null) {
      return;
    }

    try {
      Files.deleteIfExists(path);
    } catch (Exception e) {
      // a stray temporary file is not worth a message
    }
  }

  /** Kept for callers that only want to know whether curl was usable, such as the settings view. */
  public static boolean isCurlAvailable() {
    return ExternalCommand.resolve("curl") != null;
  }
}
