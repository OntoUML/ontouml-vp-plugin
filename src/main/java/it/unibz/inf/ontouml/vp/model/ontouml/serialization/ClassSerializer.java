package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.ClassStereotype;
import it.unibz.inf.ontouml.vp.model.ontouml.Nature;
import java.io.IOException;

public class ClassSerializer extends ClassifierSerializer<Class, ClassStereotype> {

  @Override
  public void serialize(Class clazz, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    super.serialize(clazz, jsonGen, provider);

    writeNullableBooleanField("isExtensional", clazz.isExtensional().orElse(null), jsonGen);
    writeNullableBooleanField("isPowertype", clazz.isPowertype().orElse(null), jsonGen);
    writeNullableIntegerField("order", clazz.getOrder().orElse(null), jsonGen);
    writeNullableArrayField("literals", clazz.getLiterals(), jsonGen);

    if (!clazz.getRestrictedTo().isEmpty()) {
      jsonGen.writeArrayFieldStart("restrictedTo");

      for (Nature nature : clazz.getRestrictedTo()) {
        jsonGen.writeString(nature.getName());
      }

      jsonGen.writeEndArray();
    } else {
      jsonGen.writeNullField("restrictedTo");
    }

    jsonGen.writeEndObject();
  }
}
