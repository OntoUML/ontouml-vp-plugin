package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ClassViewDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();
  static String json =
      "{\n"
          + "  \"id\": \"cv1\",\n"
          + "  \"type\": \"ClassView\",\n"
          + "  \"modelElement\": {\n"
          + "    \"id\": \"cl1\",\n"
          + "    \"type\": \"Class\"\n"
          + "  },\n"
          + "  \"shape\": {\n"
          + "    \"id\": \"re1\",\n"
          + "    \"type\": \"Rectangle\",\n"
          + "    \"x\": 813.0,\n"
          + "    \"y\": 72.0,\n"
          + "    \"width\": 95.0,\n"
          + "    \"height\": 40.0\n"
          + "  }\n"
          + "}";

  static ClassView view;
  static Rectangle shape;

  @BeforeAll
  static void beforeAll() throws JsonProcessingException {
    view = mapper.readValue(json, ClassView.class);
    shape = view.getShape();
  }

  @Test
  void shouldDeserializeReference() throws JsonProcessingException {
    String json = "{\"id\": \"1\", \"type\": \"ClassView\"}";
    ClassView view = mapper.readValue(json, ClassView.class);

    assertThat(view.getId()).isEqualTo("1");
  }

  @Test
  void shouldDeserializeId() {
    assertThat(view.getId()).isEqualTo("cv1");
  }

  @Test
  void shouldDeserializeModelElement() {
    assertThat(view.getModelElement().getId()).isEqualTo("cl1");
  }

  @Test
  void shouldDeserializeX() {
    assertThat(view.getX()).isEqualTo(813.0);
  }

  @Test
  void shouldDeserializeY() {
    assertThat(view.getY()).isEqualTo(72.0);
  }

  @Test
  void shouldDeserializeWidth() {
    assertThat(view.getWidth()).isEqualTo(95.0);
  }

  @Test
  void shouldDeserializeHeight() {
    assertThat(view.getHeight()).isEqualTo(40.0);
  }

  @Test
  void shouldDeserializeShape() {
    assertThat(shape.getId()).isEqualTo("re1");
  }

  @Test
  void shouldDeserializeShapeX() {
    assertThat(shape.getX()).isEqualTo(813.0);
  }

  @Test
  void shouldDeserializeShapeY() {
    assertThat(shape.getY()).isEqualTo(72.0);
  }

  @Test
  void shouldDeserializeShapeWidth() {
    assertThat(shape.getWidth()).isEqualTo(95.0);
  }

  @Test
  void shouldDeserializeShapeHeight() {
    assertThat(shape.getHeight()).isEqualTo(40.0);
  }
}
