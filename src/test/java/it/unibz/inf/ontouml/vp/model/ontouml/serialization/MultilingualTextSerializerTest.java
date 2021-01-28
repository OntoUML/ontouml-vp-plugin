package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MultilingualTextSerializerTest {
  static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  @Test
  @DisplayName("should serialize empty text object as null")
  void serializeEmptyObjectAsNull() throws IOException {
    MultilingualText text = new MultilingualText();
    String json = mapper.writeValueAsString(text);
    assertThat(json).isEqualTo("null");
  }

  @Test
  @DisplayName("should serialize text object with a single value as a string")
  void serializeSingleValuedObjectAsString() throws IOException {
    MultilingualText text = new MultilingualText("Person");
    String json = mapper.writeValueAsString(text);
    assertThat(json).isEqualTo("\"Person\"");
  }

  @Test
  @DisplayName(
      "should serialize text object with a multiple value as an object whose fields are the languages")
  void serializeMultiValuedObjectAsObject() throws IOException {
    MultilingualText text = new MultilingualText();
    text.putText("en", "Person");
    text.putText("pt", "Pessoa");
    text.putText("it", "Persona");

    String json = mapper.writeValueAsString(text);
    assertThat(json).contains("\"en\" : \"Person\"");
    assertThat(json).contains("\"pt\" : \"Pessoa\"");
    assertThat(json).contains("\"it\" : \"Persona\"");
  }
}
