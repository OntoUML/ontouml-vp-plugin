package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.GeneralizationSet;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationSetView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Text;
import java.io.IOException;

public class GeneralizationSetViewDeserializer extends JsonDeserializer<GeneralizationSetView> {

  @Override
  public GeneralizationSetView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    GeneralizationSetView view = new GeneralizationSetView();

    String id = root.get("id").asText();
    view.setId(id);

    GeneralizationSet gs =
        deserializeObjectField(root, "modelElement", GeneralizationSet.class, codec);
    view.setModelElement(gs);

    Text shape = deserializeObjectField(root, "shape", Text.class, codec);
    view.setShape(shape);

    return view;
  }
}
