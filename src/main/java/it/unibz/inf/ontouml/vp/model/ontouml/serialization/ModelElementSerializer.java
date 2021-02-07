package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import java.io.IOException;

public class ModelElementSerializer extends JsonSerializer<ModelElement> {

  @Override
  public void serialize(
      ModelElement modelElement, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(modelElement, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(ModelElement modelElement, JsonGenerator jsonGen) throws IOException {
    OntoumlElementSerializer.serializeFields(modelElement, jsonGen);

    if (modelElement.hasPropertyAssignments()) {
      jsonGen.writeObjectField("propertyAssignments", modelElement.getPropertyAssignments());
    } else {
      jsonGen.writeNullField("propertyAssignments");
    }
  }
}
