package it.unibz.inf.ontouml.vp.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Downloads a page and reduces it to plain text, so a source can be summarised from what it
 * actually says instead of from what the model remembers about its URL.
 *
 * <p>This is the first thing to run once a source is known: a search service is there to
 * <em>find</em> pages, and its snippet is only an advertisement for one. Every URL that reaches
 * the prompt should have been retrieved here first, falling back to the snippet only when
 * retrieval genuinely failed.
 *
 * <p>Retrieval is attempted twice: in-process over {@code HttpURLConnection}, and — because plenty
 * of official sites answer a bare Java client with 403 or a TLS handshake it cannot complete — with
 * {@code curl}, which is present on macOS, Linux and Windows 10+ alike.
 */
public class WebPageFetcher {

  /** Enough of a page to summarise; keeps a long document from swamping the request. */
  private static final int MAX_CHARACTERS = 6000;

  /** Cap on what is read off the wire, before markup is stripped. */
  private static final int MAX_BYTES = MAX_CHARACTERS * 8;

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

  /**
   * Retrieves the page and returns it as plain text.
   *
   * @return the text, or null when it cannot be retrieved — the caller must treat that as "no
   *     content", never as an invitation to summarise from memory.
   */
  public static String fetch(String url) {
    return fetch(url, MAX_CHARACTERS);
  }

  /** As {@link #fetch(String)}, keeping at most {@code maxCharacters} of the text. */
  public static String fetch(String url, int maxCharacters) {
    if (!isRetrievable(url)) {
      return null;
    }

    final Retrieval attempt = fetchOverHttp(url);
    String raw = attempt.text;

    // curl is worth trying when the request itself did not get through, but not when it did and
    // the answer was simply not something this class can read — a PDF stays a PDF
    if (raw == null && attempt.worthRetrying) {
      raw = fetchWithCurl(url);
    }

    return raw == null ? null : toPlainText(raw, maxCharacters);
  }

  /**
   * The outcome of one retrieval attempt, so a second attempt is only made when it could plausibly
   * do better than the first.
   */
  private static final class Retrieval {

    /** The request did not get through; another client may fare better. */
    private static final Retrieval FAILED = new Retrieval(null, true);

    /** The page came back, but it is not text this class can read. Trying again changes nothing. */
    private static final Retrieval UNUSABLE = new Retrieval(null, false);

    private final String text;
    private final boolean worthRetrying;

    private Retrieval(String text, boolean worthRetrying) {
      this.text = text;
      this.worthRetrying = worthRetrying;
    }

    private static Retrieval of(String text) {
      return new Retrieval(text, false);
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
   * Follows redirects by hand: {@code HttpURLConnection} drops the ones that switch protocol, and
   * an http URL landing on its https twin is the most common shape a normative source takes.
   */
  private static Retrieval fetchOverHttp(String url) {
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
            return Retrieval.UNUSABLE;
          }

          // relative Locations are legal, so resolve against the URL we asked for
          target = new URL(new URL(target), location).toString();
          continue;
        }

        if (status != HttpURLConnection.HTTP_OK) {
          System.out.println("Could not retrieve " + target + ": HTTP " + status);
          return worthAnotherClient(status) ? Retrieval.FAILED : Retrieval.UNUSABLE;
        }

        final String contentType = connection.getContentType();

        if (!isTextual(contentType)) {
          System.out.println(
              "Skipped " + target + ": content type " + contentType + " is not text");
          return Retrieval.UNUSABLE;
        }

        final byte[] bytes = read(decompress(connection.getInputStream(), connection));

        return bytes.length == 0 ? Retrieval.UNUSABLE : Retrieval.of(decode(bytes, contentType));
      } catch (Exception e) {
        System.out.println("Could not retrieve " + target + " in process: " + e);
        return Retrieval.FAILED;
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }

    // a redirect chain this long is a loop, and curl would only walk the same one
    return Retrieval.UNUSABLE;
  }

  /**
   * The second attempt, for the pages the in-process client cannot get: {@code curl} carries its
   * own TLS stack and redirect handling, and is what a user would reach for to check the same URL.
   *
   * <p>The process is started without a shell and the URL is known to begin with a scheme, so it
   * cannot be taken for an option.
   */
  private static String fetchWithCurl(String url) {
    Process process = null;

    try {
      final ProcessBuilder builder =
          new ProcessBuilder(
              "curl",
              "--silent",
              "--show-error",
              "--location",
              "--compressed",
              "--max-redirs",
              String.valueOf(MAX_REDIRECTS),
              "--max-time",
              String.valueOf(TIMEOUT_MILLIS / 1000),
              "--user-agent",
              USER_AGENT,
              "--header",
              "Accept: " + ACCEPT,
              url.trim());

      builder.redirectErrorStream(false);
      process = builder.start();

      // reading stops at MAX_BYTES, so a long document leaves curl still writing: killing it there
      // is deliberate — we have all the text we were going to keep
      final byte[] bytes = read(process.getInputStream());
      final boolean truncated = bytes.length >= MAX_BYTES;

      if (!truncated && !process.waitFor(TIMEOUT_MILLIS * 2L, TimeUnit.MILLISECONDS)) {
        return null;
      }

      if (!truncated && (process.exitValue() != 0 || bytes.length == 0)) {
        System.out.println(
            "curl could not retrieve "
                + url
                + (process.exitValue() == 0
                    ? " (empty response)"
                    : " (exit " + process.exitValue() + ")"));
        return null;
      }

      if (bytes.length == 0 || looksBinary(bytes)) {
        return null;
      }

      return decode(bytes, null);
    } catch (Exception e) {
      // curl missing, or blocked from running: the caller simply gets "no content"
      System.out.println("curl is unavailable for " + url + ": " + e);
      return null;
    } finally {
      if (process != null && process.isAlive()) {
        process.destroyForcibly();
      }
    }
  }

  /**
   * Whether the bytes are a document rather than text. curl reports no content type of its own, and
   * a PDF or an image put through markup stripping yields gibberish, not a source.
   */
  private static boolean looksBinary(byte[] bytes) {
    // the document format most often linked as a normative source, and the one this class cannot
    // read at all
    if (bytes.length >= 4
        && bytes[0] == '%'
        && bytes[1] == 'P'
        && bytes[2] == 'D'
        && bytes[3] == 'F') {
      return true;
    }

    final int sampled = Math.min(bytes.length, 1024);
    int control = 0;

    for (int i = 0; i < sampled; i++) {
      final byte value = bytes[i];

      if (value == 0) {
        return true;
      }

      if (value > 0 && value < 0x09) {
        control++;
      }
    }

    return control * 100 > sampled;
  }

  /**
   * Whether a second client could get further than this status suggests. A page that is gone is
   * gone for {@code curl} too; a refusal or a throttle, on the other hand, is often aimed at the
   * client that asked.
   */
  private static boolean worthAnotherClient(int status) {
    return status == HttpURLConnection.HTTP_UNAUTHORIZED
        || status == HttpURLConnection.HTTP_FORBIDDEN
        || status == HttpURLConnection.HTTP_NOT_ACCEPTABLE
        || status == 429
        || status >= 500;
  }

  private static boolean isRedirect(int status) {
    return status == HttpURLConnection.HTTP_MOVED_PERM
        || status == HttpURLConnection.HTTP_MOVED_TEMP
        || status == HttpURLConnection.HTTP_SEE_OTHER
        || status == 307
        || status == 308;
  }

  /** Whether the response is something markup stripping can make sense of. A PDF is not. */
  private static boolean isTextual(String contentType) {
    if (contentType == null) {
      // servers that declare nothing are usually serving HTML; let the parse decide
      return true;
    }

    final String type = contentType.toLowerCase();

    return type.startsWith("text/")
        || type.contains("html")
        || type.contains("xml")
        || type.contains("json");
  }

  private static InputStream decompress(InputStream stream, HttpURLConnection connection)
      throws java.io.IOException {
    final String encoding = connection.getContentEncoding();

    if (encoding == null) {
      return stream;
    }

    if (encoding.equalsIgnoreCase("gzip")) {
      return new GZIPInputStream(stream);
    }

    if (encoding.equalsIgnoreCase("deflate")) {
      return new InflaterInputStream(stream);
    }

    return stream;
  }

  private static byte[] read(InputStream stream) throws java.io.IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final byte[] chunk = new byte[8192];
    int count;

    while (buffer.size() < MAX_BYTES && (count = stream.read(chunk)) != -1) {
      buffer.write(chunk, 0, count);
    }

    stream.close();

    return buffer.toByteArray();
  }

  /**
   * Decodes with the charset the server declared, or the one the document declares about itself.
   * Guessing UTF-8 for a page served as ISO-8859-1 is how "resolução" reaches the model as
   * "resolu&#65533;&#65533;o".
   */
  private static String decode(byte[] bytes, String contentType) {
    Charset charset = charsetOf(contentType);

    if (charset == null) {
      // the declaration inside the document is ASCII-compatible in every charset we may be facing
      charset = charsetOf(new String(bytes, StandardCharsets.ISO_8859_1));
    }

    return new String(bytes, charset == null ? sniff(bytes) : charset);
  }

  /**
   * The charset of a page that declares none — planalto.gov.br, the single most important publisher
   * for this plugin, is one of them. Defaulting to UTF-8 there turns every accent into a
   * replacement character, so the bytes decide: they are UTF-8 if they are valid UTF-8, and the
   * legacy Latin encoding otherwise.
   */
  private static Charset sniff(byte[] bytes) {
    final CharsetDecoder decoder =
        StandardCharsets.UTF_8
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);

    try {
      // the read stops at a byte count, so the last few bytes may be half of a character that was
      // never going to arrive — judging the encoding by those would be judging our own truncation
      decoder.decode(ByteBuffer.wrap(bytes, 0, Math.max(0, bytes.length - 4)));
      return StandardCharsets.UTF_8;
    } catch (CharacterCodingException e) {
      return LEGACY_LATIN;
    }
  }

  /** windows-1252, the encoding of the Brazilian official pages written before UTF-8 was assumed. */
  private static final Charset LEGACY_LATIN = legacyLatin();

  private static Charset legacyLatin() {
    try {
      return Charset.forName("windows-1252");
    } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
      return StandardCharsets.ISO_8859_1;
    }
  }

  private static final Pattern CHARSET =
      Pattern.compile("charset\\s*=\\s*[\"']?([A-Za-z0-9_:.\\-]+)", Pattern.CASE_INSENSITIVE);

  private static Charset charsetOf(String declaration) {
    if (declaration == null) {
      return null;
    }

    final Matcher matcher = CHARSET.matcher(declaration);

    if (!matcher.find()) {
      return null;
    }

    try {
      return Charset.forName(matcher.group(1));
    } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
      return null;
    }
  }

  /**
   * Strips the markup, keeping the line structure: the blocks a page is written in are what make
   * the text readable, both to the model and to whoever opens the AI Conversation Log.
   */
  private static String toPlainText(String html, int maxCharacters) {
    final String withoutNoise =
        html.replaceAll("(?is)<script.*?</script>", " ")
            .replaceAll("(?is)<style.*?</style>", " ")
            .replaceAll("(?is)<head.*?</head>", " ")
            .replaceAll("(?is)<noscript.*?</noscript>", " ")
            .replaceAll("(?s)<!--.*?-->", " ");

    final String broken =
        withoutNoise
            .replaceAll("(?i)<br\\s*/?>", "\n")
            .replaceAll("(?i)</(p|div|li|tr|h[1-6]|section|article|header|footer|table)\\s*>", "\n")
            .replaceAll(
                "(?i)<(p|div|li|tr|h[1-6]|section|article|header|footer|table)[^>]*>", "\n");

    final String stripped =
        decodeEntities(broken.replaceAll("(?s)<[^>]+>", " "))
            // spaces and tabs collapse, newlines survive
            .replaceAll("[ \\t\\x0B\\f\\u00a0]+", " ")
            .replaceAll(" ?\\r?\\n ?", "\n")
            .replaceAll("\\n{3,}", "\n\n")
            .trim();

    if (stripped.isEmpty()) {
      return null;
    }

    return stripped.length() <= maxCharacters ? stripped : stripped.substring(0, maxCharacters);
  }

  /**
   * Decodes the character entities a page is likely to carry. Accented ones matter here: leaving
   * "comiss&atilde;o" in the text would hand the model mangled Portuguese to summarise.
   */
  private static String decodeEntities(String text) {
    String decoded = text;

    // numeric, decimal and hexadecimal
    final Matcher numeric = Pattern.compile("&#(x?)([0-9a-fA-F]+);").matcher(decoded);
    final StringBuffer buffer = new StringBuffer();

    while (numeric.find()) {
      try {
        final int code =
            Integer.parseInt(numeric.group(2), numeric.group(1).isEmpty() ? 10 : 16);
        numeric.appendReplacement(buffer, Matcher.quoteReplacement(String.valueOf((char) code)));
      } catch (NumberFormatException e) {
        numeric.appendReplacement(buffer, Matcher.quoteReplacement(numeric.group()));
      }
    }

    numeric.appendTail(buffer);
    decoded = buffer.toString();

    for (int i = 0; i < NAMED_ENTITIES.length; i += 2) {
      decoded = decoded.replace(NAMED_ENTITIES[i], NAMED_ENTITIES[i + 1]);
    }

    return decoded;
  }

  private static final String[] NAMED_ENTITIES = {
    "&nbsp;", " ", "&lt;", "<", "&gt;", ">", "&quot;", "\"", "&apos;", "'",
    "&aacute;", "á", "&agrave;", "à", "&atilde;", "ã", "&acirc;", "â", "&auml;", "ä",
    "&eacute;", "é", "&ecirc;", "ê", "&egrave;", "è", "&iacute;", "í", "&icirc;", "î",
    "&oacute;", "ó", "&otilde;", "õ", "&ocirc;", "ô", "&ouml;", "ö",
    "&uacute;", "ú", "&ucirc;", "û", "&uuml;", "ü", "&ccedil;", "ç", "&ntilde;", "ñ",
    "&Aacute;", "Á", "&Atilde;", "Ã", "&Acirc;", "Â", "&Eacute;", "É", "&Ecirc;", "Ê",
    "&Iacute;", "Í", "&Oacute;", "Ó", "&Otilde;", "Õ", "&Ocirc;", "Ô", "&Uacute;", "Ú",
    "&Ccedil;", "Ç", "&ordm;", "º", "&ordf;", "ª", "&deg;", "°", "&sect;", "§",
    "&mdash;", "—", "&ndash;", "–", "&hellip;", "…", "&laquo;", "«", "&raquo;", "»",
    // last, so an escaped "&amp;lt;" does not turn into a tag
    "&amp;", "&"
  };
}
