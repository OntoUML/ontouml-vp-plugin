package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import org.junit.jupiter.api.Test;

public class GeneralizationSerializerTest {

  ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  Class specificClass = new Class("c1", (String) null, (String) null);
  Class generalClass = new Class("c2", (String) null, (String) null);
  Generalization g = new Generalization("g1", specificClass, generalClass);

  @Test
  void shouldSerializeId() throws JsonProcessingException {
    String json = mapper.writeValueAsString(g);
    assertThat(json).contains("\"id\" : \"g1\"");
  }

  @Test
  void shouldSerializeType() throws JsonProcessingException {
    String json = mapper.writeValueAsString(g);
    assertThat(json).contains("\"type\" : \"Generalization\"");
  }

  @Test
  void shouldSerializeName() throws JsonProcessingException {
    g.addName("pt", "Minha generalização");
    g.addName("en", "My generalization");
    String json = mapper.writeValueAsString(g);

    assertThat(json).contains("\"name\" : {");
    assertThat(json).contains("\"pt\" : \"Minha generalização\"");
    assertThat(json).contains("\"en\" : \"My generalization\"");
  }

  @Test
  void shouldSerializeDescription() throws JsonProcessingException {
    g.addDescription("pt", "Minha descrição.");
    g.addDescription("en", "My description.");
    String json = mapper.writeValueAsString(g);

    assertThat(json).contains("\"description\" : {");
    assertThat(json).contains("\"pt\" : \"Minha descrição.\"");
    assertThat(json).contains("\"en\" : \"My description.\"");
  }

  @Test
  void shouldSerializeGeneralReference() throws JsonProcessingException {
    mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(g);

    assertThat(json).contains("\"general\":{\"id\":\"c2\",\"type\":\"Class\"}");
  }

  @Test
  void shouldSerializeSpecificReference() throws JsonProcessingException {
    mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(g);

    assertThat(json).contains("\"specific\":{\"id\":\"c1\",\"type\":\"Class\"}");
  }
}
