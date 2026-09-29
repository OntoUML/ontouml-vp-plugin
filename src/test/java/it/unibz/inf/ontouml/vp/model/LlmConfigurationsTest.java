package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import it.unibz.inf.ontouml.vp.model.LlmProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LlmConfigurationsTest {

  private static LlmConfigurations configured() {
    final LlmConfigurations config = new LlmConfigurations();
    config.setProvider(LlmProvider.OPENAI);
    config.setApiKey("sk-test");
    return config;
  }

  @Test
  @DisplayName("a fresh configuration has the integration on, so upgrading does not turn it off")
  void onByDefault() {
    assertThat(new LlmConfigurations().isLlmIntegrationEnabled()).isTrue();
  }

  @Test
  @DisplayName("switching the integration off makes an otherwise complete configuration unusable")
  void offOverridesAnOtherwiseValidSetup() {
    final LlmConfigurations config = configured();
    assertThat(config.isConfigured()).isTrue();

    config.setLlmIntegrationEnabled(false);

    assertThat(config.isConfigured()).isFalse();
  }

  @Test
  @DisplayName("turning it back on restores whatever isConfigured() said before")
  void onRestoresIt() {
    final LlmConfigurations config = configured();
    config.setLlmIntegrationEnabled(false);
    config.setLlmIntegrationEnabled(true);

    assertThat(config.isConfigured()).isTrue();
  }

  @Test
  @DisplayName("a settings file saved before this switch existed is read as switched on")
  void missingFieldDefaultsToOn() {
    final String json = "{\"provider\":\"OPENAI\",\"apiKey\":\"sk-test\"}";
    final LlmConfigurations config = new Gson().fromJson(json, LlmConfigurations.class);

    assertThat(config.isLlmIntegrationEnabled()).isTrue();
    assertThat(config.isConfigured()).isTrue();
  }

  @Test
  @DisplayName("an explicit false in the settings file is honoured")
  void explicitFalseIsHonoured() {
    final String json =
        "{\"provider\":\"OPENAI\",\"apiKey\":\"sk-test\",\"llmIntegrationEnabled\":false}";
    final LlmConfigurations config = new Gson().fromJson(json, LlmConfigurations.class);

    assertThat(config.isLlmIntegrationEnabled()).isFalse();
    assertThat(config.isConfigured()).isFalse();
  }

  @Test
  @DisplayName("a fresh configuration reads every cited source, with the pre-existing limits")
  void sourceReadingDefaults() {
    final LlmConfigurations config = new LlmConfigurations();

    assertThat(config.getSourceReadScope()).isEqualTo(SourceReadScope.ALL_NEIGHBOURS);
    assertThat(config.isReadCitedSources()).isTrue();
    assertThat(config.getMaxSourcesToRead()).isEqualTo(12);
    assertThat(config.getSourceReadMode()).isEqualTo(SourceReadMode.MAP_REDUCE);
  }

  @Test
  @DisplayName("\"Don't retrieve\" is the only scope isReadCitedSources() calls off")
  void noneScopeTurnsReadingOff() {
    final LlmConfigurations config = new LlmConfigurations();
    config.setSourceReadScope(SourceReadScope.NONE);

    assertThat(config.isReadCitedSources()).isFalse();

    config.setSourceReadScope(SourceReadScope.DIAGRAM_ONLY);

    assertThat(config.isReadCitedSources()).isTrue();
  }

  @Test
  @DisplayName("a settings file saved before scope/mode existed reads as the old, unrestricted behaviour")
  void missingSourceReadingFieldsDefaultToTheOldBehaviour() {
    final String json = "{\"provider\":\"OPENAI\",\"apiKey\":\"sk-test\"}";
    final LlmConfigurations config = new Gson().fromJson(json, LlmConfigurations.class);

    assertThat(config.getSourceReadScope()).isEqualTo(SourceReadScope.ALL_NEIGHBOURS);
    assertThat(config.getSourceReadMode()).isEqualTo(SourceReadMode.MAP_REDUCE);
    assertThat(config.getMaxCharactersPerSource()).isEqualTo(20_000);
  }

  @Test
  @DisplayName("an explicit scope and mode in the settings file are honoured")
  void explicitSourceReadingFieldsAreHonoured() {
    final String json =
        "{\"provider\":\"OPENAI\",\"apiKey\":\"sk-test\",\"sourceReadScope\":\"CURRENT_CLASS_ONLY\","
            + "\"sourceReadMode\":\"TRUNCATE\",\"maxCharactersPerSource\":5000}";
    final LlmConfigurations config = new Gson().fromJson(json, LlmConfigurations.class);

    assertThat(config.getSourceReadScope()).isEqualTo(SourceReadScope.CURRENT_CLASS_ONLY);
    assertThat(config.getSourceReadMode()).isEqualTo(SourceReadMode.TRUNCATE);
    assertThat(config.getMaxCharactersPerSource()).isEqualTo(5000);
  }

  @Test
  @DisplayName("the sources-to-read count is clamped to the documented limit")
  void maxSourcesToReadIsClamped() {
    final LlmConfigurations config = new LlmConfigurations();
    config.setMaxSourcesToRead(10_000);

    assertThat(config.getMaxSourcesToRead()).isEqualTo(LlmConfigurations.MAX_SOURCES_TO_READ_LIMIT);
  }

  @Test
  @DisplayName("a fresh configuration defaults the documentation language to American English")
  void documentationLanguageDefaultsToEnglish() {
    assertThat(new LlmConfigurations().getDocumentationLanguage()).isEqualTo("English as used in the United States (en-US)");
  }

  @Test
  @DisplayName("a settings file saved before the documentation language existed reads as American English")
  void missingDocumentationLanguageDefaultsToEnglish() {
    final String json = "{\"provider\":\"OPENAI\",\"apiKey\":\"sk-test\"}";
    final LlmConfigurations config = new Gson().fromJson(json, LlmConfigurations.class);

    assertThat(config.getDocumentationLanguage()).isEqualTo("English as used in the United States (en-US)");
  }

  @Test
  @DisplayName("an explicit documentation language in the settings file is honoured")
  void explicitDocumentationLanguageIsHonoured() {
    final LlmConfigurations config = new LlmConfigurations();
    config.setDocumentationLanguage("Deutsch");

    assertThat(config.getDocumentationLanguage()).isEqualTo("Deutsch");
  }

  @Test
  @DisplayName("a blank documentation language falls back to the default rather than being sent empty")
  void blankDocumentationLanguageFallsBackToDefault() {
    final LlmConfigurations config = new LlmConfigurations();
    config.setDocumentationLanguage("   ");

    assertThat(config.getDocumentationLanguage()).isEqualTo("English as used in the United States (en-US)");
  }
}
