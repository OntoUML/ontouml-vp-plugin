package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Decoratable;
import java.io.IOException;

public class DecoratableSerializer extends JsonSerializer<Decoratable<?>> {

  @Override
  public void serialize(Decoratable<?> element, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(element, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Decoratable<?> element, JsonGenerator jsonGen) throws IOException {
    ModelElementSerializer.serializeFields(element, jsonGen);
    jsonGen.writeObjectField("stereotype", element.getStereotype().orElse(null));
  }
}
