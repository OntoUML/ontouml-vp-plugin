package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.GeneralizationSet;
import java.io.IOException;

public class GeneralizationSetSerializer extends JsonSerializer<GeneralizationSet> {

  @Override
  public void serialize(GeneralizationSet gs, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(gs, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(GeneralizationSet gs, JsonGenerator jsonGen) throws IOException {
    ModelElementSerializer.serializeFields(gs, jsonGen);
    jsonGen.writeBooleanField("isDisjoint", gs.isDisjoint());
    jsonGen.writeBooleanField("isComplete", gs.isComplete());
    Serializer.writeNullableReferenceField(
        "categorizer", gs.getCategorizer().orElse(null), jsonGen);
    Serializer.writeNullableReferenceArray("generalizations", gs.getGeneralizations(), jsonGen);
  }
}
