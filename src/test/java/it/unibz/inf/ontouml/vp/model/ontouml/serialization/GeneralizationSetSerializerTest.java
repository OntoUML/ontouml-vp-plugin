package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Generalization;
import it.unibz.inf.ontouml.vp.model.ontouml.GeneralizationSet;
import org.junit.jupiter.api.Test;

public class GeneralizationSetSerializerTest {

  ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  Class specificClass1 = new Class("sc1", (String) null, (String) null);
  Class specificClass2 = new Class("sc2", (String) null, (String) null);
  Class generalClass = new Class("gc1", (String) null, (String) null);
  Generalization g1 = new Generalization("g1", specificClass1, generalClass);
  Generalization g2 = new Generalization("g2", specificClass2, generalClass);
  GeneralizationSet gs = new GeneralizationSet("gs1", (String) null, g1, g2);

  @Test
  void shouldSerializeId() throws JsonProcessingException {
    String json = mapper.writeValueAsString(gs);
    assertThat(json).contains("\"id\" : \"gs1\"");
  }

  @Test
  void shouldSerializeType() throws JsonProcessingException {
    String json = mapper.writeValueAsString(gs);
    assertThat(json).contains("\"type\" : \"GeneralizationSet\"");
  }

  @Test
  void shouldSerializeName() throws JsonProcessingException {
    gs.addName("pt", "Meu conjunto de generalizações");
    gs.addName("en", "My generalization set");
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"name\" : {");
    assertThat(json).contains("\"pt\" : \"Meu conjunto de generalizações\"");
    assertThat(json).contains("\"en\" : \"My generalization set\"");
  }

  @Test
  void shouldSerializeDescription() throws JsonProcessingException {
    gs.addDescription("pt", "Minha descrição.");
    gs.addDescription("en", "My description.");
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"description\" : {");
    assertThat(json).contains("\"pt\" : \"Minha descrição.\"");
    assertThat(json).contains("\"en\" : \"My description.\"");
  }

  @Test
  void shouldSerializeIsDisjoint() throws JsonProcessingException {
    gs.setDisjoint(true);
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"isDisjoint\" : true");
  }

  @Test
  void shouldSerializeIsComplete() throws JsonProcessingException {
    gs.setComplete(true);
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"isComplete\" : true");
  }

  @Test
  void shouldSerializeEmptyCategorizerAsNull() throws JsonProcessingException {
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"categorizer\" : null");
  }

  @Test
  void shouldSerializeCategorizer() throws JsonProcessingException {
    Class categorizer = new Class("cat", (String) null, (String) null);
    gs.setCategorizer(categorizer);

    mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"categorizer\":{\"id\":\"cat\",\"type\":\"Class\"}");
  }

  @Test
  void shouldSerializeGeneralizationReferences() throws JsonProcessingException {
    mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(gs);

    assertThat(json).contains("\"generalizations\":[");
    assertThat(json).contains("{\"id\":\"g1\",\"type\":\"Generalization\"}");
    assertThat(json).contains("{\"id\":\"g2\",\"type\":\"Generalization\"}");
  }
}
