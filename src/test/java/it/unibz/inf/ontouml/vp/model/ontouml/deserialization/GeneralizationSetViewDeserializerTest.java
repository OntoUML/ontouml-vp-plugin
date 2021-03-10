package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationSetView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Text;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GeneralizationSetViewDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();
  static String json =
      "{\n"
          + "  \"id\": \"gsv1\",\n"
          + "  \"type\": \"GeneralizationSetView\",\n"
          + "  \"modelElement\": {\n"
          + "    \"id\": \"gs1\",\n"
          + "    \"type\": \"GeneralizationSet\"\n"
          + "  },\n"
          + "  \"shape\": {\n"
          + "    \"id\": \"tx1\",\n"
          + "    \"type\": \"Text\",\n"
          + "    \"x\": 417,\n"
          + "    \"y\": 191,\n"
          + "    \"width\": 60,\n"
          + "    \"height\": 20\n"
          + "  }\n"
          + "}";

  static GeneralizationSetView view;
  static Text shape;

  @BeforeAll
  static void beforeAll() throws IOException {
    view = mapper.readValue(json, GeneralizationSetView.class);
    shape = view.getShape();
  }

  @Test
  void shouldDeserializeReference() throws IOException {
    String json = "{\"id\": \"1\", \"type\": \"GeneralizationSetView\"}";
    GeneralizationSetView view = mapper.readValue(json, GeneralizationSetView.class);

    assertThat(view.getId()).isEqualTo("1");
  }

  @Test
  void shouldDeserializeId() {
    assertThat(view.getId()).isEqualTo("gsv1");
  }

  @Test
  void shouldDeserializeModelElement() {
    assertThat(view.getModelElement().getId()).isEqualTo("gs1");
  }

  @Test
  void shouldDeserializeX() {
    assertThat(view.getX()).isEqualTo(417);
  }

  @Test
  void shouldDeserializeY() {
    assertThat(view.getY()).isEqualTo(191);
  }

  @Test
  void shouldDeserializeWidth() {
    assertThat(view.getWidth()).isEqualTo(60);
  }

  @Test
  void shouldDeserializeHeight() {
    assertThat(view.getHeight()).isEqualTo(20);
  }

  @Test
  void shouldDeserializeShape() {
    assertThat(shape.getId()).isEqualTo("tx1");
  }

  @Test
  void shouldDeserializeShapeX() {
    assertThat(shape.getX()).isEqualTo(417);
  }

  @Test
  void shouldDeserializeShapeY() {
    assertThat(shape.getY()).isEqualTo(191);
  }

  @Test
  void shouldDeserializeShapeWidth() {
    assertThat(shape.getWidth()).isEqualTo(60);
  }

  @Test
  void shouldDeserializeShapeHeight() {
    assertThat(shape.getHeight()).isEqualTo(20);
  }
}
