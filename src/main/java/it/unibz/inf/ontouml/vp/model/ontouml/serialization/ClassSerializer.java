package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Nature;
import java.io.IOException;

public class ClassSerializer extends JsonSerializer<Class> {

  @Override
  public void serialize(Class clazz, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(clazz, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Class clazz, JsonGenerator jsonGen) throws IOException {
    ClassifierSerializer.serializeFields(clazz, jsonGen);
    Serializer.writeNullableBooleanField(
        "isExtensional", clazz.isExtensional().orElse(null), jsonGen);
    Serializer.writeNullableBooleanField("isPowertype", clazz.isPowertype().orElse(null), jsonGen);
    Serializer.writeNullableStringField("order", clazz.getOrderAsString().orElse(null), jsonGen);
    Serializer.writeNullableArrayField("literals", clazz.getLiterals(), jsonGen);

    if (!clazz.getRestrictedTo().isEmpty()) {
      jsonGen.writeArrayFieldStart("restrictedTo");
      for (Nature nature : clazz.getRestrictedTo()) {
        jsonGen.writeString(nature.getName());
      }
      jsonGen.writeEndArray();
    } else {
      jsonGen.writeNullField("restrictedTo");
    }
  }
}
