package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Point;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class GeneralizationViewDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();
  static String json =  "{\n"
          + "  \"id\": \"rv1\",\n"
          + "  \"type\": \"GeneralizationView\",\n"
          + "  \"modelElement\": { \"id\": \"re1\", \"type\": \"Generalization\" },\n"
          + "  \"shape\": {\n"
          + "    \"id\": \"pa1\",\n"
          + "    \"type\": \"Path\",\n"
          + "    \"points\": [\n"
          + "      { \"x\": 100.0, \"y\": 40.0 },\n"
          + "      { \"x\": 150.0, \"y\": 42.0 }\n"
          + "    ]\n"
          + "  },\n"
          + "  \"source\": { \"id\": \"cv1\", \"type\": \"ClassView\" },\n"
          + "  \"target\": { \"id\": \"cv2\", \"type\": \"ClassView\" }\n"
          + "}";
     

  static GeneralizationView view;
  static Path shape;

  @BeforeAll
  static void beforeAll() throws JsonProcessingException {
    view = mapper.readValue(json, GeneralizationView.class);
    shape = view.getShape();
  }

  @Test
  void shouldDeserializeReference() throws JsonProcessingException {
    String json = "{\"id\": \"1\", \"type\": \"GeneralizationView\"}";
    GeneralizationView view = mapper.readValue(json, GeneralizationView.class);

    assertThat(view.getId()).isEqualTo("1");
  }

  @Test
  void shouldDeserializeId() {
    assertThat(view.getId()).isEqualTo("rv1");
  }

  @Test
  void shouldDeserializeModelElement() {
    assertThat(view.getModelElement().getId()).isEqualTo("re1");
  }

  @Test
  void shouldDeserializeSource() {
    assertThat(view.getSource().getId()).isEqualTo("cv1");
  }

  @Test
  void shouldDeserializeTarget() {
    assertThat(view.getTarget().getId()).isEqualTo("cv2");
  }

  @Test
  void shouldDeserializeShape() {
    assertThat(shape.getId()).isEqualTo("pa1");
  }

  @Test
  void shouldDeserializePoints() {
    assertThat(shape.getPoints()).hasSize(2);
  }

  @Test
  void shouldDeserializePointsData() {
    Point firstPoint = shape.getPoints().get(0);
    assertThat(firstPoint.getX()).isEqualTo(100.0);
    assertThat(firstPoint.getY()).isEqualTo(40.0);

    Point secondPoint = shape.getPoints().get(1);
    assertThat(secondPoint.getX()).isEqualTo(150.0);
    assertThat(secondPoint.getY()).isEqualTo(42.0);
  }


}
