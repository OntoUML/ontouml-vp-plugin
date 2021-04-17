package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;
import java.io.IOException;

public class RectangleSerializer extends JsonSerializer<Rectangle> {
  @Override
  public void serialize(Rectangle rectangle, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(rectangle, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Rectangle rectangle, JsonGenerator jsonGen) throws IOException {
    RectangularShapeSerializer.serializeFields(rectangle, jsonGen);
  }
}
