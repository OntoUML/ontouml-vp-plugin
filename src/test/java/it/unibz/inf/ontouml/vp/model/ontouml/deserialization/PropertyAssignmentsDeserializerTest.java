package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PropertyAssignmentsDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\" : \"1\",\n"
          + "  \"type\" : \"Class\",\n"
          + "  \"propertyAssignments\" : {\n"
          + "    \"authors\" : [ \"Tiago\", \"Davi\" ],\n"
          + "    \"weights\" : [ 10, 20 ],\n"
          + "    \"isLiked\" : true,\n"
          + "    \"score\" : 10,\n"
          + "    \"source\" : null,\n"
          + "    \"uri\" : \"https://schema.org/Car\",\n"
          + "    \"value\" : 8.9\n"
          + "  }\n"
          + "}";

  static ObjectMapper mapper;
  static Class clazz;

  @BeforeAll
  static void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    clazz = mapper.readValue(json, Class.class);
  }

  @Test
  void shouldDeserializeStringProperty() {
    assertThat(clazz.getPropertyAssignment("uri")).hasValue("https://schema.org/Car");
  }

  @Test
  void shouldDeserializeIntegerProperty() {
    assertThat(clazz.getPropertyAssignment("score")).hasValue(10);
  }

  @Test
  void shouldDeserializeFloatProperty() {
    assertThat(clazz.getPropertyAssignment("value")).hasValue(8.9);
  }

  @Test
  void shouldDeserializeBooleanProperty() {
    assertThat(clazz.getPropertyAssignment("isLiked")).hasValue(true);
  }

  @Test
  void shouldDeserializeNullProperty() {
    assertThat(clazz.getPropertyAssignment("source")).isEmpty();
  }

  @Test
  void shouldDeserializStringArrayProperty() {
    Optional<Object> value = clazz.getPropertyAssignment("authors");
    assertThat(value).isPresent();
    assertThat(value.get()).isInstanceOf(List.class);
    assertThat(value.get()).isEqualTo(List.of("Tiago", "Davi"));
  }

  @Test
  void shouldDeserializIntegerArrayProperty() {
    Optional<Object> value = clazz.getPropertyAssignment("weights");
    assertThat(value).isPresent();
    assertThat(value.get()).isInstanceOf(List.class);
    assertThat(value.get()).isEqualTo(List.of(10, 20));
  }
}
