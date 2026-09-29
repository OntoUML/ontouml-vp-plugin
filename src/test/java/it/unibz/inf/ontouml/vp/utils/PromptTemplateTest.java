package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PromptTemplateTest {

  @Test
  @DisplayName("every placeholder is filled")
  void fillsPlaceholders() {
    assertThat(PromptTemplate.fill("A {x} and {y}.", "x", "1", "y", "2")).isEqualTo("A 1 and 2.");
  }

  @Test
  @DisplayName("a placeholder inside a value is not expanded by a later one")
  void insertedTextIsNotRescanned() {
    final String prompt =
        PromptTemplate.fill(
            "Class: {class}\nSearch: {search}\nTemplate: {template}",
            "class", "documented as {template} and {search}",
            "search", "a page saying {template}",
            "template", "THE TEMPLATE");

    assertThat(prompt)
        .isEqualTo(
            "Class: documented as {template} and {search}\n"
                + "Search: a page saying {template}\n"
                + "Template: THE TEMPLATE");
  }

  @Test
  @DisplayName("braces that are not placeholders, such as a JSON example, are left alone")
  void unknownBracesStay() {
    final String prompt = "{\"answer\": \"x\"} for {class} {unknown}";

    assertThat(PromptTemplate.fill(prompt, "class", "C")).isEqualTo("{\"answer\": \"x\"} for C {unknown}");
  }

  @Test
  @DisplayName("dollar signs and backslashes in a value are inserted as they are")
  void specialCharactersInValues() {
    assertThat(PromptTemplate.fill("[{x}]", "x", "R$ 10 \\ $1")).isEqualTo("[R$ 10 \\ $1]");
  }

  @Test
  @DisplayName("a null value fills its placeholder with nothing")
  void nullBecomesEmpty() {
    assertThat(PromptTemplate.fill("a{x}b", "x", null)).isEqualTo("ab");
  }

  @Test
  @DisplayName("the map form behaves the same")
  void mapForm() {
    assertThat(PromptTemplate.fill("{a}{b}", Map.of("a", "{b}", "b", "2"))).isEqualTo("{b}2");
  }
}
