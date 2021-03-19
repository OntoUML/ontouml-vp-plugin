package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Point;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PathDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();

  Path path;
  String json;

  @Test
  void shouldDeserializeId() throws IOException {
    json = "{\"id\": \"1\", \"type\": \"Path\"}";
    path = mapper.readValue(json, Path.class);

    assertThat(path.getId()).isEqualTo("1");
  }

  @Test
  void shouldDeserializePointWithDoubleCoordinates() throws IOException {
    json =
        "{\"id\": \"1\","
            + "\"type\": \"Path\","
            + "\"points\": ["
            + "{\"x\": \"10\", \"y\": \"20\"}"
            + "] }";
    path = mapper.readValue(json, Path.class);

    List<Point> points = path.getPoints();
    assertThat(points).hasSize(1);

    Point point = points.get(0);
    assertThat(point.getX()).isEqualTo(10);
    assertThat(point.getY()).isEqualTo(20);
  }

  @Test
  void shouldDeserializePointWithIntegerCoordinates() throws IOException {
    json =
        "{\"id\": \"1\","
            + "\"type\": \"Path\","
            + "\"points\": ["
            + "{\"x\": \"5\", \"y\": \"15\"}"
            + "] }";
    path = mapper.readValue(json, Path.class);

    List<Point> points = path.getPoints();
    assertThat(points).hasSize(1);

    Point point = points.get(0);
    assertThat(point.getX()).isEqualTo(5);
    assertThat(point.getY()).isEqualTo(15);
  }

  @Test
  void shouldDeserializeMultiplePoints() throws IOException {
    json =
        "{\"id\": \"1\","
            + "\"type\": \"Path\","
            + "\"points\": ["
            + "{\"x\": \"10\", \"y\": \"20\"},"
            + "{\"x\": \"10\", \"y\": \"100\"}"
            + "] }";
    path = mapper.readValue(json, Path.class);

    List<Point> points = path.getPoints();
    assertThat(points).hasSize(2);

    Point first = points.get(0);
    assertThat(first.getX()).isEqualTo(10);
    assertThat(first.getY()).isEqualTo(20);

    Point second = points.get(1);
    assertThat(second.getX()).isEqualTo(10);
    assertThat(second.getY()).isEqualTo(100);
  }
}
