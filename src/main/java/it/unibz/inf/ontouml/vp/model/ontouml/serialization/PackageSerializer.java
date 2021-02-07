package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.io.IOException;

public class PackageSerializer extends JsonSerializer<Package> {

  @Override
  public void serialize(Package pkg, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(pkg, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Package pkg, JsonGenerator jsonGen) throws IOException {
    ModelElementSerializer.serializeFields(pkg, jsonGen);
    Serializer.writeNullableArrayField("contents", pkg.getContents(), jsonGen);
  }
}
