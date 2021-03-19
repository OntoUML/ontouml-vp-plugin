package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Shape;
import java.io.IOException;

public class ShapeSerializer extends JsonSerializer<Shape> {
  @Override
  public void serialize(Shape shape, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(shape, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Shape shape, JsonGenerator jsonGen) throws IOException {
    ElementSerializer.serializeId(shape, jsonGen);
    OntoumlElementSerializer.serializeType(shape, jsonGen);
  }
}
