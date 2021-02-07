package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class TextDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();
  static Text text;
  static String json =
      "{\n"
          + "  \"id\": \"t1\",\n"
          + "  \"type\": \"Text\",\n"
          + "  \"x\": 130.0,\n"
          + "  \"y\": 109.0,\n"
          + "  \"width\": 78.0,\n"
          + "  \"height\": 42.0\n"
          + "}";

  @BeforeAll
  static void beforeAll() throws JsonProcessingException {
    text = mapper.readValue(json, Text.class);
  }

  @Test
  void shouldDeserializeReference() throws JsonProcessingException {
    String json = "{\"id\": \"t1\", \"type\": \"Text\"}";
    Text text = mapper.readValue(json, Text.class);

    assertThat(text.getId()).isEqualTo("t1");
  }

  @Test
  void shouldDeserializeId() {
    assertThat(text.getId()).isEqualTo("t1");
  }

  @Test
  void shouldDeserializeX() {
    assertThat(text.getX()).isEqualTo(130.0);
  }

  @Test
  void shouldDeserializeY() {
    assertThat(text.getY()).isEqualTo(109.0);
  }

  @Test
  void shouldDeserializeWidth() {
    assertThat(text.getWidth()).isEqualTo(78.0);
  }

  @Test
  void shouldDeserializeHeight() {
    assertThat(text.getHeight()).isEqualTo(42.0);
  }
}
