package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import it.unibz.inf.ontouml.vp.model.ontouml.model.GeneralizationSet;
import java.io.IOException;
import java.util.List;

public class GeneralizationSetDeserializer extends JsonDeserializer<GeneralizationSet> {

  @Override
  public GeneralizationSet deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    GeneralizationSet gs = new GeneralizationSet();
    ElementDeserializer.deserialize(gs, root, codec);
    ModelElementDeserializer.deserialize(gs, root, codec);

    boolean isComplete = deserializeBooleanField(root, "isComplete");
    gs.setComplete(isComplete);

    boolean isDisjoint = deserializeBooleanField(root, "isDisjoint");
    gs.setDisjoint(isDisjoint);

    Class categorizer = deserializeObject(root, "categorizer", Class.class, codec);
    gs.setCategorizer(categorizer);

    List<Generalization> generalizations =
        deserializeObjectArray(root, "generalizations", Generalization.class, codec);
    gs.setGeneralizations(generalizations);

    return gs;
  }
}
