package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;

import java.io.IOException;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

public class RelationViewDeserializer extends JsonDeserializer<RelationView> {

  @Override
  public RelationView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    RelationView view = new RelationView();

    ConnectorViewDeserializer.deserialize(view, root, codec);

    Relation stub = deserializeObjectField(root, "modelElement", Relation.class, codec);
    view.setModelElement(stub);

    return view;
  }
}
