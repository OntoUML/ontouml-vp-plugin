package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.*;
import java.io.IOException;
import java.util.List;

public class ConnectorViewDeserializer {

  public static void deserialize(ConnectorView<?> view, JsonNode root, ObjectCodec codec)
      throws IOException {

    String id = root.get("id").asText();
    view.setId(id);

    Path path = deserializeObjectField(root, "shape", Path.class, codec);
    view.setPath(path);

    ElementView<?, ?> source = deserializeConnectorEnd(root, "source", codec);
    view.setSource(source);

    ElementView<?, ?> target = deserializeConnectorEnd(root, "target", codec);
    view.setTarget(target);
  }

  private static ElementView<?, ?> deserializeConnectorEnd(
      JsonNode root, String fieldName, ObjectCodec codec) throws IOException {

    List<Class<? extends OntoumlElement>> allowedTypes =
        List.of(ClassView.class, RelationView.class);

    OntoumlElement source =
        DeserializerUtils.deserializeObjectField(root, fieldName, allowedTypes, codec);

    return (source instanceof ElementView<?, ?>) ? (ElementView<?, ?>) source : null;
  }
}
