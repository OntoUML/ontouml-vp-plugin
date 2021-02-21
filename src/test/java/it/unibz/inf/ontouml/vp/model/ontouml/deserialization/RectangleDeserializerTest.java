package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RectangleDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();
  static Rectangle rectangle;
  static String json =
      "{\n"
          + "  \"id\": \"r1\",\n"
          + "  \"type\": \"Rectangle\",\n"
          + "  \"x\": 406,\n"
          + "  \"y\": 285,\n"
          + "  \"width\": 84,\n"
          + "  \"height\": 40\n"
          + "}";

  @BeforeAll
  static void beforeAll() throws JsonProcessingException {
    rectangle = mapper.readValue(json, Rectangle.class);
  }

  @Test
  void shouldDeserializeReference() throws JsonProcessingException {
    String json = "{\"id\": \"1\", \"type\": \"Rectangle\"}";
    Rectangle rectangle = mapper.readValue(json, Rectangle.class);

    assertThat(rectangle.getId()).isEqualTo("1");
  }

  @Test
  void shouldDeserializeId() {
    assertThat(rectangle.getId()).isEqualTo("r1");
  }

  @Test
  void shouldDeserializeX() {
    assertThat(rectangle.getX()).isEqualTo(406);
  }

  @Test
  void shouldDeserializeY() {
    assertThat(rectangle.getY()).isEqualTo(285);
  }

  @Test
  void shouldDeserializeWidth() {
    assertThat(rectangle.getWidth()).isEqualTo(84);
  }

  @Test
  void shouldDeserializeHeight() {
    assertThat(rectangle.getHeight()).isEqualTo(40);
  }
}
