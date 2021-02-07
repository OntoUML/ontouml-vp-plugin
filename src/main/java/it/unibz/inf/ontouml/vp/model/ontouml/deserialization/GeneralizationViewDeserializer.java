package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;

import java.io.IOException;

public class RelationViewDeserializer extends JsonDeserializer<RelationView> {

  @Override
  public RelationView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    RelationView relationView = new RelationView();

    DiagramElementDeserializer.deserialize(relationView, root, codec);
    DeserializerUtils.deserializeObject(root, "modelElement", Relation.class, codec);
    DeserializerUtils.deserializeObject(root, "shape", Path.class, codec);

    return relationView;
  }
}
