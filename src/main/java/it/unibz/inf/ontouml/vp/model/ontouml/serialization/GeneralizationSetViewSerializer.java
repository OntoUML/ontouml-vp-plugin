package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationSetView;
import java.io.IOException;

public class GeneralizationSetViewSerializer extends JsonSerializer<GeneralizationSetView> {
  @Override
  public void serialize(
      GeneralizationSetView gsView, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(gsView, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(GeneralizationSetView gsView, JsonGenerator jsonGen)
      throws IOException {
    NodeViewSerializer.serializeFields(gsView, jsonGen);
  }
}
