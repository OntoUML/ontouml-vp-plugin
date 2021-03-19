package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RectangularShape;
import java.io.IOException;

public class RectangularShapeSerializer extends JsonSerializer<RectangularShape> {
  @Override
  public void serialize(RectangularShape shape, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(shape, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(RectangularShape shape, JsonGenerator jsonGen) throws IOException {
    ShapeSerializer.serializeFields(shape, jsonGen);
    Serializer.writeNullableNumberField("x", shape.getX(), jsonGen);
    Serializer.writeNullableNumberField("y", shape.getY(), jsonGen);
    Serializer.writeNullableNumberField("width", shape.getWidth(), jsonGen);
    Serializer.writeNullableNumberField("height", shape.getHeight(), jsonGen);
  }
}
