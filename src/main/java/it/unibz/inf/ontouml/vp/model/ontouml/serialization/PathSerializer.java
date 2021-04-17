package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Point;
import java.io.IOException;

public class PathSerializer extends JsonSerializer<Path> {
  @Override
  public void serialize(Path path, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(path, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Path path, JsonGenerator jsonGen) throws IOException {
    ShapeSerializer.serializeFields(path, jsonGen);

    jsonGen.writeArrayFieldStart("points");

    for (Point point : path.getPoints()) {

      jsonGen.writeStartObject();
      Serializer.writeNullableNumberField("x", point.getX(), jsonGen);
      Serializer.writeNullableNumberField("y", point.getY(), jsonGen);
      jsonGen.writeEndObject();
    }

    jsonGen.writeEndArray();
  }
}
