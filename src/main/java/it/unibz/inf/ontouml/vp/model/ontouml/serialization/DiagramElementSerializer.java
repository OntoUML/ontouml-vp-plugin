package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.serialization.Serializer.writeNullableReferenceField;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ElementView;
import java.io.IOException;

public class DiagramElementSerializer extends JsonSerializer<ElementView<?, ?>> {

  @Override
  public void serialize(
          ElementView<?, ?> element, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(element, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(ElementView<?, ?> element, JsonGenerator jsonGen)
      throws IOException {
    ElementSerializer.serializeId(element, jsonGen);
    OntoumlElementSerializer.serializeType(element, jsonGen);
    writeNullableReferenceField("modelElement", element.getModelElement(), jsonGen);
    jsonGen.writeObjectField("shape", element.getShape());
  }
}
