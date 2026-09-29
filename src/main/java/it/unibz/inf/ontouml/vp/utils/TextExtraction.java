package it.unibz.inf.ontouml.vp.utils;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns the bytes of a source into the plain text that reaches a prompt: decides the charset,
 * strips markup, and recognises the bytes that are not text at all.
 *
 * <p>This lives apart from {@link WebPageFetcher} because the same decisions apply to a page
 * downloaded over HTTP, to a file sitting on disk and to the Markdown a document converter writes.
 * A source read from disk deserves the same charset care as one read off the wire.
 */
public class TextExtraction {

  private TextExtraction() {}

  /**
   * Decodes with the charset the server declared, or the one the document declares about itself.
   * Guessing UTF-8 for a page served as ISO-8859-1 is how "resolução" reaches the model as
   * "resolu&#65533;&#65533;o".
   *
   * @param declaration a Content-Type header, or null when nothing declared one.
   */
  public static String decode(byte[] bytes, String declaration) {
    Charset charset = charsetOf(declaration);

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
    if (decodesAsUtf8(bytes, bytes.length)) {
      return StandardCharsets.UTF_8;
    }

    // the read stops at a byte count, so the last character may be one we cut in half — judging the
    // encoding by our own truncation would be wrong. But the tail is only forgiven when it really
    // is a cut-off UTF-8 sequence: simply dropping the last few bytes would turn "resolução" in
    // windows-1252 into the plain ASCII "resolu" and call the page UTF-8.
    final int truncated = truncatedSequenceAt(bytes);

    if (truncated > 0 && decodesAsUtf8(bytes, truncated)) {
      return StandardCharsets.UTF_8;
    }

    return LEGACY_LATIN;
  }

  /**
   * Where a multi-byte UTF-8 sequence starts that the end of the buffer cut short, or -1 when the
   * bytes do not end in one. A sequence qualifies only if its lead byte announces more bytes than
   * are left and every byte after it is a continuation byte.
   */
  private static int truncatedSequenceAt(byte[] bytes) {
    for (int at = bytes.length - 1; at >= 0 && at >= bytes.length - 3; at--) {
      final int value = bytes[at] & 0xFF;

      if (value < 0xC0) {
        // a continuation byte may precede the lead we are looking for; anything else cannot
        if (value < 0x80) {
          return -1;
        }

        continue;
      }

      final int expected = value < 0xE0 ? 2 : value < 0xF0 ? 3 : 4;

      return bytes.length - at < expected ? at : -1;
    }

    return -1;
  }

  private static boolean decodesAsUtf8(byte[] bytes, int length) {
    final CharsetDecoder decoder =
        StandardCharsets.UTF_8
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);

    try {
      decoder.decode(ByteBuffer.wrap(bytes, 0, Math.max(0, length)));
      return true;
    } catch (CharacterCodingException e) {
      return false;
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
   * Whether the bytes are a document rather than text. A PDF or an image put through markup
   * stripping yields gibberish, not a source, and gibberish in the prompt is worse than a source
   * the model was told it could not read.
   */
  public static boolean looksBinary(byte[] bytes) {
    if (isPdf(bytes)) {
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

  /** The document format most often linked as a normative source. */
  public static boolean isPdf(byte[] bytes) {
    return bytes.length >= 4
        && bytes[0] == '%'
        && bytes[1] == 'P'
        && bytes[2] == 'D'
        && bytes[3] == 'F';
  }

  /**
   * Strips the markup, keeping the line structure: the blocks a page is written in are what make
   * the text readable, both to the model and to whoever opens the AI Conversation Log.
   *
   * @return the text, or null when nothing was left of it.
   */
  public static String toPlainText(String html, int maxCharacters) {
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

    return trimTo(decodeEntities(broken.replaceAll("(?s)<[^>]+>", " ")), maxCharacters);
  }

  /**
   * Normalises whitespace and caps the length, for text that carries no markup to strip — what a
   * document converter writes, or a plain text file.
   */
  public static String trimTo(String text, int maxCharacters) {
    final String stripped =
        text
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
  public static String decodeEntities(String text) {
    String decoded = text;

    // numeric, decimal and hexadecimal
    final Matcher numeric = Pattern.compile("&#(x?)([0-9a-fA-F]+);").matcher(decoded);
    final StringBuffer buffer = new StringBuffer();

    while (numeric.find()) {
      try {
        final int code = Integer.parseInt(numeric.group(2), numeric.group(1).isEmpty() ? 10 : 16);
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
