package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;

import java.io.IOException;

public class RectangleDeserializer extends JsonDeserializer<Rectangle> {
  @Override
  public Rectangle deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {

    JsonNode root = parser.readValueAsTree();

    Rectangle rectangle = new Rectangle();
    RectangularShapeDeserializer.deserialize(rectangle, root);

    return rectangle;
  }
}
