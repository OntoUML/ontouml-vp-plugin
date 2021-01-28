package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Element;
import java.io.IOException;

public class ElementSerializer<T extends Element> extends JsonSerializer<T> {

  @Override
  public void serialize(T element, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {

    jsonGen.writeStartObject();
    jsonGen.writeStringField("id", element.getId());
    jsonGen.writeObjectField("name", element.getName());
    jsonGen.writeObjectField("description", element.getDescription());
  }
}
