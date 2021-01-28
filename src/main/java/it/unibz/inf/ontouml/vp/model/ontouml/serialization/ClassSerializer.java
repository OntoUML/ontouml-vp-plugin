package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.ClassStereotype;
import it.unibz.inf.ontouml.vp.model.ontouml.Nature;
import java.io.IOException;

public class ClassSerializer extends ClassifierSerializer<Class, ClassStereotype> {

  @Override
  public void serialize(Class clas, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    super.serialize(clas, jsonGen, provider);

    writeNullableBooleanField("isExtensional", clas.isExtensional().orElse(null), jsonGen);
    writeNullableBooleanField("isPowertype", clas.isPowertype().orElse(null), jsonGen);
    writeNullableIntegerField("order", clas.getOrder().orElse(null), jsonGen);
    writeNullableArrayField("literals", clas.getLiterals(), jsonGen);

    if (!clas.getRestrictedTo().isEmpty()) {
      jsonGen.writeArrayFieldStart("restrictedTo");

      for (Nature nature : clas.getRestrictedTo()) jsonGen.writeString(nature.getName());

      jsonGen.writeEndArray();
    } else {
      jsonGen.writeNullField("restrictedTo");
    }

    jsonGen.writeEndObject();
  }
}
