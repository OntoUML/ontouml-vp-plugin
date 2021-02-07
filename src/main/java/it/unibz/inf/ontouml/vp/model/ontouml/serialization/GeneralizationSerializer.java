package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import java.io.IOException;

public class GeneralizationSerializer extends JsonSerializer<Generalization> {

  @Override
  public void serialize(Generalization gen, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(gen, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Generalization gen, JsonGenerator jsonGen) throws IOException {
    ModelElementSerializer.serializeFields(gen, jsonGen);
    Serializer.writeNullableReferenceField("general", gen.getGeneral().orElse(null), jsonGen);
    Serializer.writeNullableReferenceField("specific", gen.getSpecific().orElse(null), jsonGen);
  }
}
