package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PathDeserializer extends JsonDeserializer<Path> {
  @Override
  public Path deserialize(JsonParser parser, DeserializationContext context) throws IOException {

    JsonNode root = parser.readValueAsTree();
    Path path = new Path();

    String id = root.get("id").asText();
    path.setId(id);

    List<Point> points = deserializePoints(root);
    path.setPoints(points);

    return path;
  }

  private List<Point> deserializePoints(JsonNode root) {
    JsonNode arrayNode = root.get("points");
    if (arrayNode == null || !arrayNode.isArray()) return null;

    List<Point> points = new ArrayList<>();
    Iterator<JsonNode> iterator = arrayNode.elements();
    while (iterator.hasNext()) {
      JsonNode pointNode = iterator.next();

      if (!pointNode.isObject()) continue;

      JsonNode xNode = pointNode.get("x");
      double x = (xNode != null) ? xNode.asDouble(0) : 0;

      JsonNode yNode = pointNode.get("y");
      double y = (yNode != null) ? yNode.asDouble(0) : 0;

      Point point = new Point(x, y);
      points.add(point);
    }

    return points;
  }
}
