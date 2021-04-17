package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Literal;
import java.io.IOException;

public class LiteralSerializer extends JsonSerializer<Literal> {

  @Override
  public void serialize(Literal literal, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(literal, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Literal literal, JsonGenerator jsonGen) throws IOException {
    ModelElementSerializer.serializeFields(literal, jsonGen);
  }
}
