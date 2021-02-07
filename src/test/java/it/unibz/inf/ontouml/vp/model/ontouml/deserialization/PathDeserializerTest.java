package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class PathDeserializerTest {

  static ObjectMapper mapper = new ObjectMapper();

  Path path;
  String json;

  @Test
  void shouldDeserializeId() throws JsonProcessingException {
    json = "{\"id\": \"1\", \"type\": \"Path\"}";
    path = mapper.readValue(json, Path.class);

    assertThat(path.getId()).isEqualTo("1");
  }

  @Test
  void shouldDeserializePointWithDoubleCoordinates() throws JsonProcessingException {
    json =
        "{\"id\": \"1\","
            + "\"type\": \"Path\","
            + "\"points\": ["
            + "{\"x\": \"10.0\", \"y\": \"20.0\"}"
            + "] }";
    path = mapper.readValue(json, Path.class);

    List<Point> points = path.getPoints();
    assertThat(points).hasSize(1);

    Point point = points.get(0);
    assertThat(point.getX()).isEqualTo(10.0);
    assertThat(point.getY()).isEqualTo(20.0);
  }

  @Test
  void shouldDeserializePointWithIntegerCoordinates() throws JsonProcessingException {
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
  void shouldDeserializeMultiplePoints() throws JsonProcessingException {
    json =
        "{\"id\": \"1\","
            + "\"type\": \"Path\","
            + "\"points\": ["
            + "{\"x\": \"10.0\", \"y\": \"20.0\"},"
            + "{\"x\": \"10.0\", \"y\": \"100.0\"}"
            + "] }";
    path = mapper.readValue(json, Path.class);

    List<Point> points = path.getPoints();
    assertThat(points).hasSize(2);

    Point first = points.get(0);
    assertThat(first.getX()).isEqualTo(10.0);
    assertThat(first.getY()).isEqualTo(20.0);

    Point second = points.get(1);
    assertThat(second.getX()).isEqualTo(10.0);
    assertThat(second.getY()).isEqualTo(100.0);
  }
}
