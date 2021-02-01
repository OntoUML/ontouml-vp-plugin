package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Relation;
import it.unibz.inf.ontouml.vp.model.ontouml.RelationStereotype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelationSerializerTest {
  ObjectMapper mapper;
  Class clazz;
  Relation relation;
  String json;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    clazz = new Class("c1", (String) null, (String) null);
    relation = new Relation("r1", "my relation", clazz, clazz);
    json = mapper.writeValueAsString(relation);
  }

  @Test
  void shouldSerializeId() {
    assertThat(json).contains("\"id\" : \"r1\"");
  }

  @Test
  void shouldSerializeType() {
    assertThat(json).contains("\"type\" : \"Relation\"");
  }

  @Test
  void shouldSerializeName() throws JsonProcessingException {
    relation.addName("pt", "Minha relação");
    relation.addName("en", "My relation");
    json = mapper.writeValueAsString(relation);

    assertThat(json).contains("\"name\" : {");
    assertThat(json).contains("\"pt\" : \"Minha relação\"");
    assertThat(json).contains("\"en\" : \"My relation\"");
  }

  @Test
  void shouldSerializeDescription() throws JsonProcessingException {
    relation.addDescription("pt", "Minha descrição.");
    relation.addDescription("en", "My description.");
    json = mapper.writeValueAsString(relation);

    assertThat(json).contains("\"description\" : {");
    assertThat(json).contains("\"pt\" : \"Minha descrição.\"");
    assertThat(json).contains("\"en\" : \"My description.\"");
  }

  @Test
  void shouldSerializeOntoumlStereotype() throws JsonProcessingException {
    relation.setOntoumlStereotype(RelationStereotype.MATERIAL);
    json = mapper.writeValueAsString(relation);
    assertThat(json).contains("\"stereotype\" : \"material\"");
  }

  @Test
  void shouldSerializeCustomStereotype() throws JsonProcessingException {
    relation.setCustomStereotype("custom");
    json = mapper.writeValueAsString(relation);
    assertThat(json).contains("\"stereotype\" : \"custom\"");
  }

  @Test
  void shouldSerializeIsAbstract() throws JsonProcessingException {
    relation.setAbstract(true);
    json = mapper.writeValueAsString(relation);
    assertThat(json).contains("\"isAbstract\" : true");
  }

  @Test
  void shouldSerializeIsDerived() throws JsonProcessingException {
    relation.setDerived(true);
    String json = mapper.writeValueAsString(relation);
    assertThat(json).contains("\"isDerived\" : true");
  }

  @Test
  void shouldSerializeEnds() throws JsonProcessingException {
    relation.getSourceEnd().setId("p0");
    relation.getSourceEnd().addName("source");
    relation.getTargetEnd().setId("p1");
    relation.getTargetEnd().addName("target");

    mapper = new ObjectMapper();
    json = mapper.writeValueAsString(relation);

    assertThat(json).contains("\"properties\":[{\"id\":\"p0\"");
    assertThat(json).contains(",{\"id\":\"p1\"");
  }
}
