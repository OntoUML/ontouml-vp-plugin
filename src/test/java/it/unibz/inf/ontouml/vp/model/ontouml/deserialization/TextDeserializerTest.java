package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Text;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TextDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();
  static Text text;
  static String json =
      "{\n"
          + "  \"id\": \"t1\",\n"
          + "  \"type\": \"Text\",\n"
          + "  \"x\": 130,\n"
          + "  \"y\": 109,\n"
          + "  \"value\": \"Hello World!\",\n"
          + "  \"width\": 78,\n"
          + "  \"height\": 42\n"
          + "}";

  @BeforeAll
  static void beforeAll() throws IOException {
    text = mapper.readValue(json, Text.class);
  }

  @Test
  void shouldDeserializeReference() throws IOException {
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
    assertThat(text.getX()).isEqualTo(130);
  }

  @Test
  void shouldDeserializeY() {
    assertThat(text.getY()).isEqualTo(109);
  }

  @Test
  void shouldDeserializeValue() {
    assertThat(text.getValue()).isEqualTo("Hello World!");
  }

  @Test
  void shouldDeserializeWidth() {
    assertThat(text.getWidth()).isEqualTo(78);
  }

  @Test
  void shouldDeserializeHeight() {
    assertThat(text.getHeight()).isEqualTo(42);
  }
}
