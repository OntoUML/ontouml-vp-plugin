package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeClassifierField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import java.io.IOException;

public class GeneralizationDeserializer extends JsonDeserializer<Generalization> {

  @Override
  public Generalization deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Generalization gen = new Generalization();
    ElementDeserializer.deserialize(gen, root, codec);
    ModelElementDeserializer.deserialize(gen, root, codec);

    Classifier<?, ?> general = deserializeClassifierField(root, "general", codec);
    gen.setGeneral(general);

    Classifier<?, ?> specific = deserializeClassifierField(root, "specific", codec);
    gen.setSpecific(specific);

    return gen;
  }
}
