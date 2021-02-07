package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Text;
import java.io.IOException;

public class TextSerializer extends JsonSerializer<Text> {
  @Override
  public void serialize(Text text, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(text, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Text text, JsonGenerator jsonGen) throws IOException {
    RectangularShapeSerializer.serializeFields(text, jsonGen);
  }
}
