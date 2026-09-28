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
}
