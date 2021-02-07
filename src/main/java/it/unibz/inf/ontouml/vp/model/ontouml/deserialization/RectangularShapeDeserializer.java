package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RectangularShape;

import java.io.IOException;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeNullableDoubleField;
import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

public class RectangularShapeDeserializer {

  public static void deserialize(RectangularShape shape, JsonNode node)
      throws IOException {

    String id = node.get("id").asText();
    shape.setId(id);

    Double x = deserializeNullableDoubleField(node, "x");
    shape.setX(x);

    Double y = deserializeNullableDoubleField(node, "y");
    shape.setY(y);

    Double width = deserializeNullableDoubleField(node, "width");
    shape.setWidth(width);

    Double height = deserializeNullableDoubleField(node, "height");
    shape.setHeight(height);
  }
}
