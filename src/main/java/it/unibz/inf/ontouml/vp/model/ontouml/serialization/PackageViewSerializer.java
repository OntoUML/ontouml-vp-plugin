package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.PackageView;
import java.io.IOException;

public class PackageViewSerializer extends JsonSerializer<PackageView> {

  @Override
  public void serialize(PackageView classView, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(classView, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(PackageView classView, JsonGenerator jsonGen) throws IOException {
    NodeViewSerializer.serializeFields(classView, jsonGen);
  }
}
