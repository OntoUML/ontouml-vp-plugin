package it.unibz.inf.ontouml.vp.model;

import java.util.Locale;

/**
 * The languages the documentation of a class can be written in: each with its ISO 639-1 code, and
 * right after it, the regional or script variants that write it differently enough to matter —
 * spelling, vocabulary, or alphabet — each with its BCP 47 tag (e.g. {@code pt-BR}).
 *
 * <p>The label is what the picker shows; the English name, followed by the code, is what the
 * prompt receives in its {@code {language}} placeholder — the prompt itself is in English, and a
 * name the model reads in that same language leaves it nothing to interpret.
 */
public enum DocumentationLanguage {
  ARABIC("ar", "Árabe", "Arabic"),
  ARABIC_EGYPT("ar-EG", "Árabe — Egito", "Arabic as used in Egypt", "Arabic"),
  ARABIC_SAUDI_ARABIA("ar-SA", "Árabe — Arábia Saudita", "Arabic as used in Saudi Arabia", "Arabic"),
  BENGALI("bn", "Bengalês", "Bengali"),
  BENGALI_BANGLADESH("bn-BD", "Bengalês — Bangladesh", "Bengali as used in Bangladesh", "Bengali"),
  BENGALI_INDIA("bn-IN", "Bengalês — Índia", "Bengali as used in India", "Bengali"),
  BULGARIAN("bg", "Búlgaro", "Bulgarian"),
  CHINESE("zh", "Chinês simplificado e tradicional", "Chinese"),
  CHINESE_CHINA("zh-CN", "Chinês simplificado — China", "Simplified Chinese as used in China", "Chinese"),
  CHINESE_TAIWAN(
      "zh-TW", "Chinês tradicional — Taiwan", "Traditional Chinese as used in Taiwan", "Chinese"),
  CHINESE_HONG_KONG(
      "zh-HK",
      "Chinês tradicional — Hong Kong",
      "Traditional Chinese as used in Hong Kong",
      "Chinese"),
  CROATIAN("hr", "Croata", "Croatian"),
  CZECH("cs", "Tcheco", "Czech"),
  DANISH("da", "Dinamarquês", "Danish"),
  DUTCH("nl", "Holandês", "Dutch"),
  DUTCH_NETHERLANDS("nl-NL", "Holandês — Países Baixos", "Dutch as used in the Netherlands", "Dutch"),
  DUTCH_BELGIUM("nl-BE", "Holandês — Bélgica (flamengo)", "Dutch as used in Belgium (Flemish)", "Dutch"),
  ENGLISH("en", "Inglês", "English"),
  ENGLISH_US("en-US", "Inglês — Estados Unidos", "English as used in the United States", "English"),
  ENGLISH_UK("en-GB", "Inglês — Reino Unido", "English as used in the United Kingdom", "English"),
  ENGLISH_CANADA("en-CA", "Inglês — Canadá", "English as used in Canada", "English"),
  ENGLISH_AUSTRALIA("en-AU", "Inglês — Austrália", "English as used in Australia", "English"),
  ENGLISH_INDIA("en-IN", "Inglês — Índia", "English as used in India", "English"),
  ESTONIAN("et", "Estoniano", "Estonian"),
  FINNISH("fi", "Finlandês", "Finnish"),
  FRENCH("fr", "Francês", "French"),
  FRENCH_FRANCE("fr-FR", "Francês — França", "French as used in France", "French"),
  FRENCH_CANADA("fr-CA", "Francês — Canadá", "French as used in Canada", "French"),
  FRENCH_BELGIUM("fr-BE", "Francês — Bélgica", "French as used in Belgium", "French"),
  FRENCH_SWITZERLAND("fr-CH", "Francês — Suíça", "French as used in Switzerland", "French"),
  GERMAN("de", "Alemão", "German"),
  GERMAN_GERMANY("de-DE", "Alemão — Alemanha", "German as used in Germany", "German"),
  GERMAN_AUSTRIA("de-AT", "Alemão — Áustria", "German as used in Austria", "German"),
  GERMAN_SWITZERLAND("de-CH", "Alemão — Suíça", "German as used in Switzerland", "German"),
  GREEK("el", "Grego", "Greek"),
  HEBREW("he", "Hebraico", "Hebrew"),
  HINDI("hi", "Hindi", "Hindi"),
  HUNGARIAN("hu", "Húngaro", "Hungarian"),
  INDONESIAN("id", "Indonésio", "Indonesian"),
  ITALIAN("it", "Italiano", "Italian"),
  ITALIAN_ITALY("it-IT", "Italiano — Itália", "Italian as used in Italy", "Italian"),
  ITALIAN_SWITZERLAND("it-CH", "Italiano — Suíça", "Italian as used in Switzerland", "Italian"),
  JAPANESE("ja", "Japonês", "Japanese"),
  KOREAN("ko", "Coreano", "Korean"),
  LATVIAN("lv", "Letão", "Latvian"),
  LITHUANIAN("lt", "Lituano", "Lithuanian"),
  NORWEGIAN("no", "Norueguês", "Norwegian"),
  NORWEGIAN_BOKMAL("nb", "Norueguês — Bokmål", "Norwegian Bokmål", "Norwegian"),
  NORWEGIAN_NYNORSK("nn", "Norueguês — Nynorsk", "Norwegian Nynorsk", "Norwegian"),
  POLISH("pl", "Polonês", "Polish"),
  PORTUGUESE("pt", "Português", "Portuguese"),
  PORTUGUESE_BRAZIL("pt-BR", "Português — Brasil", "Portuguese as used in Brazil", "Portuguese"),
  PORTUGUESE_PORTUGAL(
      "pt-PT", "Português — Portugal", "Portuguese as used in Portugal", "Portuguese"),
  PORTUGUESE_ANGOLA("pt-AO", "Português — Angola", "Portuguese as used in Angola", "Portuguese"),
  PORTUGUESE_MOZAMBIQUE(
      "pt-MZ", "Português — Moçambique", "Portuguese as used in Mozambique", "Portuguese"),
  ROMANIAN("ro", "Romeno", "Romanian"),
  ROMANIAN_MOLDOVA("ro-MD", "Romeno — Moldávia", "Romanian as used in Moldova", "Romanian"),
  RUSSIAN("ru", "Russo", "Russian"),
  SERBIAN("sr", "Sérvio", "Serbian"),
  SERBIAN_CYRILLIC("sr-Cyrl", "Sérvio — alfabeto cirílico", "Serbian in Cyrillic script", "Serbian"),
  SERBIAN_LATIN("sr-Latn", "Sérvio — alfabeto latino", "Serbian in Latin script", "Serbian"),
  SLOVAK("sk", "Eslovaco", "Slovak"),
  SLOVENIAN("sl", "Esloveno", "Slovenian"),
  SPANISH("es", "Espanhol", "Spanish"),
  SPANISH_SPAIN("es-ES", "Espanhol — Espanha", "Spanish as used in Spain", "Spanish"),
  SPANISH_LATIN_AMERICA(
      "es-419", "Espanhol — América Latina", "Spanish as used in Latin America", "Spanish"),
  SPANISH_MEXICO("es-MX", "Espanhol — México", "Spanish as used in Mexico", "Spanish"),
  SPANISH_ARGENTINA("es-AR", "Espanhol — Argentina", "Spanish as used in Argentina", "Spanish"),
  SPANISH_COLOMBIA("es-CO", "Espanhol — Colômbia", "Spanish as used in Colombia", "Spanish"),
  SPANISH_US("es-US", "Espanhol — Estados Unidos", "Spanish as used in the United States", "Spanish"),
  SWAHILI("sw", "Suaíli", "Swahili"),
  SWAHILI_KENYA("sw-KE", "Suaíli — Quênia", "Swahili as used in Kenya", "Swahili"),
  SWAHILI_TANZANIA("sw-TZ", "Suaíli — Tanzânia", "Swahili as used in Tanzania", "Swahili"),
  SWEDISH("sv", "Sueco", "Swedish"),
  SWEDISH_FINLAND("sv-FI", "Sueco — Finlândia", "Swedish as used in Finland", "Swedish"),
  THAI("th", "Tailandês", "Thai"),
  TURKISH("tr", "Turco", "Turkish"),
  UKRAINIAN("uk", "Ucraniano", "Ukrainian"),
  VIETNAMESE("vi", "Vietnamita", "Vietnamese");

  private final String code;
  private final String label;
  private final String englishName;
  /** The English name of the language this is a variant of; the same as its own for a base one. */
  private final String baseEnglishName;

  DocumentationLanguage(String code, String label, String englishName) {
    this(code, label, englishName, englishName);
  }

  DocumentationLanguage(String code, String label, String englishName, String baseEnglishName) {
    this.code = code;
    this.label = label;
    this.englishName = englishName;
    this.baseEnglishName = baseEnglishName;
  }

  /** The ISO 639-1 code, or for a variant its BCP 47 tag, e.g. {@code pt-BR}. */
  public String getCode() {
    return code;
  }

  /** Whether this is a regional or script variant of another entry rather than a language itself. */
  public boolean isVariant() {
    return !englishName.equals(baseEnglishName);
  }

  /** What is saved in the settings and sent to the model, e.g. {@code "Portuguese as used in Brazil (pt-BR)"}. */
  public String getPromptValue() {
    return englishName + " (" + code + ")";
  }

  /** What the picker shows, e.g. {@code "Português — Brasil (pt-BR)"}. */
  @Override
  public String toString() {
    return label + " (" + code + ")";
  }

  /**
   * The language a saved setting names, or {@code null} when it names none of these.
   *
   * <p>The setting used to be free text, so besides what this enum saves itself it also accepts
   * the bare code or tag, the English name or the label, with or without anything in parentheses
   * after it; and a language's English name followed by a variant's subtag in parentheses — which
   * is how {@code "English (US)"}, the old default, comes back as American English.
   */
  public static DocumentationLanguage fromSetting(String setting) {
    if (setting == null) {
      return null;
    }

    final String trimmed = setting.trim();
    final int parenthesis = trimmed.indexOf('(');
    final String name =
        (parenthesis < 0 ? trimmed : trimmed.substring(0, parenthesis)).trim().toLowerCase(Locale.ROOT);

    // what this enum saves or shows itself, exactly, before anything looser can take it for
    // another entry — case included, since "Portuguese (pt)" and "Portuguese (PT)" are not the same
    for (DocumentationLanguage language : values()) {
      if (trimmed.equals(language.getPromptValue()) || trimmed.equals(language.toString())) {
        return language;
      }
    }

    // the old free-text form of a variant, "<language> (<subtag>)", before the looser name match
    // below would take it for the base language alone. The subtag's case is compared as written:
    // "Dutch (NL)" is the Netherlands, but "Dutch (nl)" is just Dutch
    for (DocumentationLanguage language : values()) {
      if (language.isVariant() && parenthesis >= 0) {
        final String subtag = language.code.substring(language.code.indexOf('-') + 1);
        final String inParentheses =
            trimmed.substring(parenthesis + 1).replace(")", "").trim();

        if (name.equals(language.baseEnglishName.toLowerCase(Locale.ROOT))
            && inParentheses.equals(subtag)) {
          return language;
        }
      }
    }

    for (DocumentationLanguage language : values()) {
      if (name.equals(language.code.toLowerCase(Locale.ROOT))
          || name.equals(language.englishName.toLowerCase(Locale.ROOT))
          || name.equals(language.label.toLowerCase(Locale.ROOT))) {
        return language;
      }
    }

    return null;
  }
}
