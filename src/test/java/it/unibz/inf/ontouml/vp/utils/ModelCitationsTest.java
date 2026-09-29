package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModelCitationsTest {

  private static JsonObject json(String text) {
    return new JsonParser().parse(text).getAsJsonObject();
  }

  @Test
  @DisplayName("a plain answer that names a URL from memory carries no citations")
  void answerWithoutCitations() {
    final JsonObject response =
        json(
            "{\"choices\":[{\"message\":{\"role\":\"assistant\","
                + "\"content\":\"Lei 14.133 — https://www.planalto.gov.br/x\"}}]}");

    assertThat(ModelCitations.in(response)).isEmpty();
  }

  @Test
  @DisplayName("OpenAI and OpenRouter list url_citation annotations on the message")
  void urlCitationAnnotations() {
    final JsonObject response =
        json(
            "{\"choices\":[{\"message\":{\"content\":\"x\",\"annotations\":["
                + "{\"type\":\"url_citation\",\"url_citation\":{\"url\":\"https://a.gov.br/1\",\"title\":\"A\"}},"
                + "{\"type\":\"url_citation\",\"url_citation\":{\"url\":\"https://b.gov.br/2\"}}]}}]}");

    assertThat(ModelCitations.in(response)).containsExactly("https://a.gov.br/1", "https://b.gov.br/2").inOrder();
  }

  @Test
  @DisplayName("Perplexity returns citations and search_results beside the choices")
  void perplexityStyle() {
    final JsonObject response =
        json(
            "{\"choices\":[{\"message\":{\"content\":\"x\"}}],"
                + "\"citations\":[\"https://a.gov.br/1\",\"https://b.gov.br/2\"],"
                + "\"search_results\":[{\"title\":\"B\",\"url\":\"https://b.gov.br/2\"},"
                + "{\"title\":\"C\",\"url\":\"https://c.gov.br/3\"}]}");

    assertThat(ModelCitations.in(response))
        .containsExactly("https://a.gov.br/1", "https://b.gov.br/2", "https://c.gov.br/3")
        .inOrder();
  }

  @Test
  @DisplayName("empty lists, non-URLs and malformed entries count for nothing")
  void ignoresNoise() {
    final JsonObject response =
        json(
            "{\"choices\":[{\"message\":{\"content\":\"x\",\"annotations\":[]}}],"
                + "\"citations\":[\"not a url\",42,{\"title\":\"no url\"}]}");

    assertThat(ModelCitations.in(response)).isEmpty();
  }

  @Test
  @DisplayName("a response without choices does not fail")
  void noChoices() {
    assertThat(ModelCitations.in(json("{}"))).isEmpty();
    assertThat(ModelCitations.in(null)).isEmpty();
  }
}
