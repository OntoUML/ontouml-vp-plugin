package it.unibz.inf.ontouml.vp.controllers;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The sentinel that tells a refusal from a summary. Getting this wrong is expensive: a refusal
 * mistaken for a summary goes into the prompt where a summary goes, telling the model that a source
 * nobody could open had been read — and taking the real reason with it.
 */
class NoContentAnswerTest {

  @Test
  @DisplayName("the exact phrase the prompt asks for")
  void exactPhrase() {
    assertThat(LlmAccessController.isNoContentAnswer("(conteúdo não recuperado)")).isTrue();
  }

  @Test
  @DisplayName("the same phrase without the parentheses, which is what actually happened")
  void withoutParentheses() {
    // Gemini, asked for "exactly (conteúdo não recuperado)", answered this — and the literal
    // contains() check let it through as a summary
    assertThat(LlmAccessController.isNoContentAnswer("conteúdo não recuperado")).isTrue();
  }

  @Test
  @DisplayName("unaccented, upper case, punctuated, padded — still a refusal")
  void otherDressings() {
    assertThat(LlmAccessController.isNoContentAnswer("Conteudo nao recuperado.")).isTrue();
    assertThat(LlmAccessController.isNoContentAnswer("CONTEÚDO NÃO RECUPERADO")).isTrue();
    assertThat(LlmAccessController.isNoContentAnswer("  (conteúdo  não   recuperado)  ")).isTrue();
    assertThat(LlmAccessController.isNoContentAnswer("[conteúdo não recuperado]")).isTrue();
  }

  @Test
  @DisplayName("nothing at all is nothing to summarise")
  void emptyAnswers() {
    assertThat(LlmAccessController.isNoContentAnswer(null)).isTrue();
    assertThat(LlmAccessController.isNoContentAnswer("   ")).isTrue();
  }

  @Test
  @DisplayName("a real summary is a summary")
  void realSummary() {
    assertThat(
            LlmAccessController.isNoContentAnswer(
                "A Lei 14.133/2021 institui normas de licitação e contratação para as"
                    + " administrações públicas, definindo no art. 6º os conceitos aplicáveis."))
        .isFalse();
  }

  @Test
  @DisplayName("a summary that happens to mention the phrase is still a summary")
  void summaryMentioningThePhrase() {
    // the length guard earns its keep here: a refusal is a sentence, not a paragraph
    assertThat(
            LlmAccessController.isNoContentAnswer(
                "O documento trata da recuperação de conteúdo digital em acervos públicos e"
                    + " estabelece prazos para que o conteúdo não recuperado seja reprocessado"
                    + " pelo órgão responsável, conforme o art. 12."))
        .isFalse();
  }
}
