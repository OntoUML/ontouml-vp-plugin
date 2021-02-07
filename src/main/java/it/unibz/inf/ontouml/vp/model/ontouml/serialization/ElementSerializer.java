package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Element;
import java.io.IOException;

public class ElementSerializer extends JsonSerializer<Element> {

  @Override
  public void serialize(Element element, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(element, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Element element, JsonGenerator jsonGen) throws IOException {
    serializeId(element, jsonGen);
    jsonGen.writeObjectField("name", element.getName());
    jsonGen.writeObjectField("description", element.getDescription());
  }

  static void serializeId(Element element, JsonGenerator jsonGen) throws IOException {
    jsonGen.writeStringField("id", element.getId());
  }
}
