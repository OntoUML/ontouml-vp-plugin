package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import org.junit.jupiter.api.Test;

public class ProjectSerializerTest {
  ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  Project project = new Project("1", (String) null);

  @Test
  void shouldSerializeId() throws JsonProcessingException {
    String json = mapper.writeValueAsString(project);
    assertThat(json).contains("\"id\" : \"1\"");
  }

  @Test
  void shouldSerializeType() throws JsonProcessingException {
    String json = mapper.writeValueAsString(project);
    assertThat(json).contains("\"type\" : \"Project\"");
  }

  @Test
  void shouldSerializeName() throws JsonProcessingException {
    project.addName("pt-br", "Meu Projeto");
    project.addName("en", "My Project");

    String json = mapper.writeValueAsString(project);
    assertThat(json).contains("\"name\" : {");
    assertThat(json).contains("\"pt-br\" : \"Meu Projeto\"");
    assertThat(json).contains("\"en\" : \"My Project\"");
  }

  @Test
  void shouldSerializeDescription() throws JsonProcessingException {
    project.addDescription("it", "Il miglior progetto in modellazione concettuale.");
    project.addDescription("en", "The best conceptual modeling project.");

    String json = mapper.writeValueAsString(project);

    assertThat(json).contains("\"description\" : {");
    assertThat(json).contains("\"it\" : \"Il miglior progetto in modellazione concettuale.\"");
    assertThat(json).contains("\"en\" : \"The best conceptual modeling project.\"");
  }
}
