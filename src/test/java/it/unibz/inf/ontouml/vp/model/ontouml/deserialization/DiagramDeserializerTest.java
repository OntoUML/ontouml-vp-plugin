package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DiagramDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\": \"d1\",\n"
          + "  \"name\": \"My Diagram\",\n"
          + "  \"description\": \"My diagram used for testing.\",\n"
          + "  \"type\": \"Diagram\",\n"
          + "  \"contents\": [\n"
          + "    {\n"
          + "      \"id\": \"cv1\",\n"
          + "      \"type\": \"ClassView\",\n"
          + "      \"modelElement\": { \"id\": \"c1\", \"type\": \"Class\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"re1\",\n"
          + "        \"type\": \"Rectangle\",\n"
          + "        \"x\": 813,\n"
          + "        \"y\": 72,\n"
          + "        \"width\": 95,\n"
          + "        \"height\": 40\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"id\": \"cv2\",\n"
          + "      \"type\": \"ClassView\",\n"
          + "      \"modelElement\": { \"id\": \"c2\", \"type\": \"Class\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"re2\",\n"
          + "        \"type\": \"Rectangle\",\n"
          + "        \"x\": 608,\n"
          + "        \"y\": 234,\n"
          + "        \"width\": 89,\n"
          + "        \"height\": 50\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"id\": \"cv3\",\n"
          + "      \"type\": \"ClassView\",\n"
          + "      \"modelElement\": { \"id\": \"c3\", \"type\": \"Class\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"iqX7Nu6GAqACBBip_shape\",\n"
          + "        \"type\": \"Rectangle\",\n"
          + "        \"x\": 550,\n"
          + "        \"y\": 72,\n"
          + "        \"width\": 91,\n"
          + "        \"height\": 40\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"id\": \"rv1\",\n"
          + "      \"type\": \"RelationView\",\n"
          + "      \"modelElement\": { \"id\": \"r1\", \"type\": \"Relation\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"pa1\",\n"
          + "        \"type\": \"Path\",\n"
          + "        \"points\": [\n"
          + "          { \"x\": 100, \"y\": 20 },\n"
          + "          { \"x\": 100, \"y\": 200 }\n"
          + "        ]\n"
          + "      },\n"
          + "      \"source\": { \"id\": \"cv2\", \"type\": \"ClassView\" },\n"
          + "      \"target\": { \"id\": \"cv3\", \"type\": \"ClassView\" }\n"
          + "    },\n"
          + "    {\n"
          + "      \"id\": \"gv1\",\n"
          + "      \"type\": \"GeneralizationView\",\n"
          + "      \"modelElement\": { \"id\": \"g1\", \"type\": \"Generalization\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"pa2\",\n"
          + "        \"type\": \"Path\",\n"
          + "        \"points\": [\n"
          + "          { \"x\": 270, \"y\": 118 },\n"
          + "          { \"x\": 270, \"y\": 168 }\n"
          + "        ]\n"
          + "      },\n"
          + "      \"source\": { \"id\": \"cv1\", \"type\": \"ClassView\" },\n"
          + "      \"target\": { \"id\": \"cv2\", \"type\": \"ClassView\" }\n"
          + "    },\n"
          + "    {\n"
          + "      \"id\": \"gv2\",\n"
          + "      \"type\": \"GeneralizationView\",\n"
          + "      \"modelElement\": { \"id\": \"g2\", \"type\": \"Generalization\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"pa3\",\n"
          + "        \"type\": \"Path\",\n"
          + "        \"points\": [\n"
          + "          { \"x\": 270, \"y\": 118 },\n"
          + "          { \"x\": 270, \"y\": 168 }\n"
          + "        ]\n"
          + "      },\n"
          + "      \"source\": {\n"
          + "        \"id\": \"cv1\",\n"
          + "        \"type\": \"ClassView\"\n"
          + "      },\n"
          + "      \"target\": {\n"
          + "        \"id\": \"cv3\",\n"
          + "        \"type\": \"ClassView\"\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"id\": \"gsv1\",\n"
          + "      \"type\": \"GeneralizationSetView\",\n"
          + "      \"modelElement\": { \"id\": \"gs1\", \"type\": \"GeneralizationSet\" },\n"
          + "      \"shape\": {\n"
          + "        \"id\": \"tx1\",\n"
          + "        \"type\": \"Text\",\n"
          + "        \"x\": 130,\n"
          + "        \"y\": 109,\n"
          + "        \"width\": 78,\n"
          + "        \"height\": 42\n"
          + "      }\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  static ObjectMapper mapper;
  static Diagram diagram;

  @BeforeAll
  static void beforeAll() throws IOException {
    mapper = new ObjectMapper();
    diagram = mapper.readValue(json, Diagram.class);
  }

  @Test
  void shouldDeserializeId() {
    assertThat(diagram.getId()).isEqualTo("d1");
  }

  @Test
  void shouldDeserializeName() {
    assertThat(diagram.getFirstName()).hasValue("My Diagram");
  }

  @Test
  void shouldDeserializDescription() {
    assertThat(diagram.getFirstDescription()).hasValue("My diagram used for testing.");
  }

  @Test
  void shouldDeserializeContents() {
    assertThat(diagram.getContents()).hasSize(7);
  }

  @Test
  void shouldDeserializeClasses() {
    List<ClassView> views =
        diagram.getContents().stream()
            .filter(ClassView.class::isInstance)
            .map(ClassView.class::cast)
            .collect(Collectors.toList());

    assertThat(views).hasSize(3);

    ClassView classView =
        views.stream().filter(v -> v.getId().equals("cv1")).findFirst().orElse(null);
    assertThat(classView).isNotNull();

    Rectangle rectangle = classView.getShape();
    assertThat(rectangle).isNotNull();

    assertThat(rectangle.getId()).isEqualTo("re1");
    assertThat(rectangle.getX()).isEqualTo(813);
    assertThat(rectangle.getWidth()).isEqualTo(95);
  }
}
