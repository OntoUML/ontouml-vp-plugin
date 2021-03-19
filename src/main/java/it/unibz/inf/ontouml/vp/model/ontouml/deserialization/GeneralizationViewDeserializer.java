package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationView;
import java.io.IOException;

public class GeneralizationViewDeserializer extends JsonDeserializer<GeneralizationView> {

  @Override
  public GeneralizationView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    GeneralizationView view = new GeneralizationView();

    ConnectorViewDeserializer.deserialize(view, root, codec);

    Generalization stub = deserializeObjectField(root, "modelElement", Generalization.class, codec);
    view.setModelElement(stub);

    return view;
  }
}
