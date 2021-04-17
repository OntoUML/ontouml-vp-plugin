package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import java.io.IOException;

public class RelationDeserializer extends JsonDeserializer<Relation> {

  @Override
  public Relation deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Relation relation = new Relation();
    ElementDeserializer.deserialize(relation, root, codec);
    ModelElementDeserializer.deserialize(relation, root, codec);
    DecoratableDeserializer.deserialize(relation, root, codec);
    ClassifierDeserializer.deserialize(relation, root, codec);

    return relation;
  }
}
