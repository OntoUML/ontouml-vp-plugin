package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeNullableIntegerField;

import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RectangularShape;

public class RectangularShapeDeserializer {

  public static void deserialize(RectangularShape shape, JsonNode node) {

    String id = node.get("id").asText();
    shape.setId(id);

    Integer x = deserializeNullableIntegerField(node, "x");
    shape.setX(x);

    Integer y = deserializeNullableIntegerField(node, "y");
    shape.setY(y);

    Integer width = deserializeNullableIntegerField(node, "width");
    shape.setWidth(width);

    Integer height = deserializeNullableIntegerField(node, "height");
    shape.setHeight(height);
  }
}
