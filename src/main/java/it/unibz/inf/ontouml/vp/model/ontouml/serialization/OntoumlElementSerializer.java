package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import java.io.IOException;

public class OntoumlElementSerializer extends JsonSerializer<OntoumlElement> {

  @Override
  public void serialize(OntoumlElement element, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(element, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(OntoumlElement element, JsonGenerator jsonGen) throws IOException {
    ElementSerializer.serializeFields(element, jsonGen);
    serializeType(element, jsonGen);
  }

  static void serializeType(OntoumlElement element, JsonGenerator jsonGen) throws IOException {
    jsonGen.writeStringField("type", element.getType());
  }
}
