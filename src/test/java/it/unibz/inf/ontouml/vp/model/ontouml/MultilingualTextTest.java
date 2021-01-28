package it.unibz.inf.ontouml.vp.model.ontouml;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultilingualTextTest {
  MultilingualText text = new MultilingualText();

  @Test
  @DisplayName("Should retrieve text in the correct language")
  void getTextItalian() {
    text.putText("en", "Hello");
    text.putText("it", "Ciao");

    Optional<String> value = text.getText("it");
    assertThat(value).isPresent();
    assertThat(value).hasValue("Ciao");
  }

  @Test
  @DisplayName("If the requested language does not exist, getText() should return the English text")
  void shouldGetEnglishOnMissing() {
    text.putText("en", "Hello");
    text.putText("it", "Ciao");

    Optional<String> value = text.getText("pt");
    assertThat(value).isPresent();
    assertThat(value).hasValue("Hello");
  }

  @Test
  @DisplayName(
      "If the requested language does not exist and there is no English text, getText() should retrieve text in the first available language (in alphabetical order)")
  void shouldGetFirstLangaugeOnMissing() {
    text.putText("pt", "Oi");
    text.putText("it", "Ciao");

    Optional<String> value = text.getText("es");
    assertThat(value).isPresent();
    assertThat(value).hasValue("Ciao");
  }

  @Test
  @DisplayName("If no language is specified, getText() should return the English text")
  void shouldGetEnglishByDefault() {
    text.putText("cz", "Ahoj");
    text.putText("en", "Hello");
    text.putText("it", "Ciao");

    Optional<String> value = text.getText();
    assertThat(value).isPresent();
    assertThat(value).hasValue("Hello");
  }

  @Test
  @DisplayName(
      "If no language is specified, and there is no English text, getText() should retrieve text in the first available language (in alphabetical order)")
  void shouldGetFirstLangaugeByDefault() {
    text.putText("pt", "Oi");
    text.putText("es", "Hola");
    text.putText("it", "Ciao");

    Optional<String> value = text.getText();
    assertThat(value).isPresent();
    assertThat(value).hasValue("Hola");
  }

  @Test
  @DisplayName("Constructor clones multilingual text")
  void constructorShouldClone() {
    text.putText("it", "Ciao");
    MultilingualText clone = new MultilingualText(text);

    Optional<String> value = clone.getText("it");
    assertThat(value).isPresent();
    assertThat(value).hasValue("Ciao");
    assertThat(clone.getLanguages()).hasSize(1);
    assertThat(clone.getLanguages()).contains("it");
  }
}
