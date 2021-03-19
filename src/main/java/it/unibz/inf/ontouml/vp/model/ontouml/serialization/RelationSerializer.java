package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import java.io.IOException;

public class RelationSerializer extends JsonSerializer<Relation> {

  @Override
  public void serialize(Relation relation, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(relation, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Relation relation, JsonGenerator jsonGen) throws IOException {
    ClassifierSerializer.serializeFields(relation, jsonGen);
  }
}
