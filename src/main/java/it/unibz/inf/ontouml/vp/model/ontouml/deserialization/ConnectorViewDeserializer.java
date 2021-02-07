package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.DiagramElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;
import java.io.IOException;
import java.util.List;

public class RelationViewDeserializer extends JsonDeserializer<RelationView> {

  @Override
  public RelationView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    RelationView relationView = new RelationView();

    DiagramElementDeserializer.deserialize(relationView, root, codec);

    Relation stub =
        DeserializerUtils.deserializeObject(root, "modelElement", Relation.class, codec);
    relationView.setModelElement(stub);

    Path path = DeserializerUtils.deserializeObject(root, "shape", Path.class, codec);
    relationView.setPath(path);

    DiagramElement<?, ?> source = deserializeConnectorEnd(root, "source", codec);
    relationView.setSource(source);

    DiagramElement<?, ?> target = deserializeConnectorEnd(root, "target", codec);
    relationView.setTarget(target);

    return relationView;
  }

  private DiagramElement<?, ?> deserializeConnectorEnd(
      JsonNode root, String fieldName, ObjectCodec codec) throws IOException {

    List<Class<? extends OntoumlElement>> allowedTypes =
        List.of(ClassView.class, RelationView.class);

    OntoumlElement source =
        DeserializerUtils.deserializeObject(root, fieldName, allowedTypes, codec);

    return (source instanceof DiagramElement<?, ?>) ? (DiagramElement<?, ?>) source : null;
  }
}
