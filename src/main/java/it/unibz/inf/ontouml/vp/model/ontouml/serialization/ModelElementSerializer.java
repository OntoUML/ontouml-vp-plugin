package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.ModelElement;
import java.io.IOException;

public class ModelElementSerializer<T extends ModelElement> extends OntoumlElementSerializer<T> {

  @Override
  public void serialize(T element, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    super.serialize(element, jsonGen, provider);

    if (element.hasPropertyAssignments())
      jsonGen.writeObjectField("propertyAssignments", element.getPropertyAssignments());
    else jsonGen.writeNullField("propertyAssignments");
  }
}
