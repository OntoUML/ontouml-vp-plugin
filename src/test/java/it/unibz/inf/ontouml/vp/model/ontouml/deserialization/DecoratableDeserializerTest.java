package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import org.junit.jupiter.api.Test;

class DecoratableDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();

  @Test
  void shouldDeserializeOntoumlClassStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"c1\",\n"
            + "  \"type\": \"Class\",\n"
            + "  \"stereotype\": \"kind\"\n"
            + "}";

    Class clazz = mapper.readValue(json, Class.class);
    assertThat(clazz.getOntoumlStereotype()).hasValue(ClassStereotype.KIND);
    assertThat(clazz.getCustomStereotype()).isEmpty();
  }

  @Test
  void shouldDeserializeCustomStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"c1\",\n"
            + "  \"type\": \"Class\",\n"
            + "  \"stereotype\": \"custom\"\n"
            + "}";

    Class clazz = mapper.readValue(json, Class.class);
    assertThat(clazz.getOntoumlStereotype()).isEmpty();
    assertThat(clazz.getCustomStereotype()).hasValue("custom");
  }

  @Test
  void shouldDeserializeOntoumlRelationStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"r1\",\n"
            + "  \"type\": \"Relation\",\n"
            + "  \"stereotype\": \"material\"\n"
            + "}";

    Relation relation = mapper.readValue(json, Relation.class);
    assertThat(relation.getOntoumlStereotype()).hasValue(RelationStereotype.MATERIAL);
    assertThat(relation.getCustomStereotype()).isEmpty();
  }

  @Test
  void shouldDeserializeCustomRelationStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"r1\",\n"
            + "  \"type\": \"Relation\",\n"
            + "  \"stereotype\": \"custom\"\n"
            + "}";

    Relation relation = mapper.readValue(json, Relation.class);
    assertThat(relation.getOntoumlStereotype()).isEmpty();
    assertThat(relation.getCustomStereotype()).hasValue("custom");
  }

  @Test
  void shouldDeserializeOntoumlPropertyStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"p1\",\n"
            + "  \"type\": \"Property\",\n"
            + "  \"stereotype\": \"begin\"\n"
            + "}";

    Property property = mapper.readValue(json, Property.class);
    assertThat(property.getOntoumlStereotype()).hasValue(PropertyStereotype.BEGIN);
    assertThat(property.getCustomStereotype()).isEmpty();
  }

  @Test
  void shouldDeserializeCustomPropertyStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"p1\",\n"
            + "  \"type\": \"Property\",\n"
            + "  \"stereotype\": \"custom\"\n"
            + "}";

    Property property = mapper.readValue(json, Property.class);
    assertThat(property.getOntoumlStereotype()).isEmpty();
    assertThat(property.getCustomStereotype()).hasValue("custom");
  }
}
