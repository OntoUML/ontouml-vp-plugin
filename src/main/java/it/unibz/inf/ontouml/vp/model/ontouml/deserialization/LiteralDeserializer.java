package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.Literal;
import java.io.IOException;

public class LiteralDeserializer extends JsonDeserializer<Literal> {

  @Override
  public Literal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Literal literal = new Literal();
    ElementDeserializer.deserialize(literal, root, codec);
    ModelElementDeserializer.deserialize(literal, root, codec);

    return literal;
  }
}
