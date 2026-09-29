package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DocumentationLanguageTest {

  @Test
  @DisplayName("the picker shows the label next to its ISO 639-1 code")
  void label() {
    assertThat(DocumentationLanguage.PORTUGUESE.toString()).isEqualTo("Português (pt)");
  }

  @Test
  @DisplayName("the prompt receives the English name next to the code")
  void promptValue() {
    assertThat(DocumentationLanguage.PORTUGUESE.getPromptValue()).isEqualTo("Portuguese (pt)");
  }

  @Test
  @DisplayName("every saved value reads back as the language that saved it")
  void roundTrip() {
    for (DocumentationLanguage language : DocumentationLanguage.values()) {
      assertThat(DocumentationLanguage.fromSetting(language.getPromptValue())).isEqualTo(language);
      assertThat(DocumentationLanguage.fromSetting(language.toString())).isEqualTo(language);
      assertThat(DocumentationLanguage.fromSetting(language.getCode())).isEqualTo(language);
    }
  }

  @Test
  @DisplayName("the old free-text default \"English (US)\" reads as American English")
  void legacyDefault() {
    assertThat(DocumentationLanguage.fromSetting("English (US)"))
        .isEqualTo(DocumentationLanguage.ENGLISH_US);
  }

  @Test
  @DisplayName("a variant shows its region and is sent with its BCP 47 tag")
  void variant() {
    assertThat(DocumentationLanguage.PORTUGUESE_BRAZIL.toString())
        .isEqualTo("Português — Brasil (pt-BR)");
    assertThat(DocumentationLanguage.PORTUGUESE_BRAZIL.getPromptValue())
        .isEqualTo("Portuguese as used in Brazil (pt-BR)");
    assertThat(DocumentationLanguage.PORTUGUESE_BRAZIL.isVariant()).isTrue();
    assertThat(DocumentationLanguage.PORTUGUESE.isVariant()).isFalse();
  }

  @Test
  @DisplayName("a tag is recognised whatever its case, and the base language still reads as itself")
  void tags() {
    assertThat(DocumentationLanguage.fromSetting("pt-br"))
        .isEqualTo(DocumentationLanguage.PORTUGUESE_BRAZIL);
    assertThat(DocumentationLanguage.fromSetting("Portuguese (PT)"))
        .isEqualTo(DocumentationLanguage.PORTUGUESE_PORTUGAL);
    assertThat(DocumentationLanguage.fromSetting("Portuguese"))
        .isEqualTo(DocumentationLanguage.PORTUGUESE);
    assertThat(DocumentationLanguage.fromSetting("English (en)"))
        .isEqualTo(DocumentationLanguage.ENGLISH);
  }

  @Test
  @DisplayName("every variant comes right after its own language, so the picker keeps them together")
  void variantsFollowTheirLanguage() {
    DocumentationLanguage base = null;

    for (DocumentationLanguage language : DocumentationLanguage.values()) {
      if (!language.isVariant()) {
        base = language;
      } else {
        assertThat(base).isNotNull();
        assertThat(language.getPromptValue()).contains(base.getPromptValue().split(" \\(")[0]);
      }
    }
  }

  @Test
  @DisplayName("a name in either English or the label's language is recognised, whatever its case")
  void names() {
    assertThat(DocumentationLanguage.fromSetting("german")).isEqualTo(DocumentationLanguage.GERMAN);
    assertThat(DocumentationLanguage.fromSetting("Alemão")).isEqualTo(DocumentationLanguage.GERMAN);
  }

  @Test
  @DisplayName("text naming none of the languages reads as none")
  void unknown() {
    assertThat(DocumentationLanguage.fromSetting("Klingon")).isNull();
    assertThat(DocumentationLanguage.fromSetting(null)).isNull();
  }

  @Test
  @DisplayName("no two languages share a code")
  void uniqueCodes() {
    final java.util.Set<String> codes = new java.util.HashSet<>();

    for (DocumentationLanguage language : DocumentationLanguage.values()) {
      assertThat(codes.add(language.getCode())).isTrue();
    }
  }
}
