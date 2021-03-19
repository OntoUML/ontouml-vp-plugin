package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DiagramDeserializer extends JsonDeserializer<Diagram> {

  @Override
  public Diagram deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Diagram diagram = new Diagram();

    ElementDeserializer.deserialize(diagram, root, codec);

    ModelElement owner = deserializeOwner(root, codec);
    diagram.setOwner(owner);

    List<ElementView<?, ?>> contents = deserializeContents(root, codec);
    diagram.setContents(contents);

    return diagram;
  }

  private ModelElement deserializeOwner(JsonNode root, ObjectCodec codec) throws IOException {
    OntoumlElement owner =
        deserializeObjectField(root, "owner", List.of(Class.class, Package.class), codec);
    return castOrNull(owner, ModelElement.class);
  }

  private List<ElementView<?, ?>> deserializeContents(JsonNode root, ObjectCodec codec)
      throws IOException {
    List<OntoumlElement> contents =
        deserializeArrayField(
            root,
            "contents",
            List.of(
                ClassView.class,
                PackageView.class,
                RelationView.class,
                GeneralizationView.class,
                GeneralizationSetView.class),
            codec);

    return contents.stream()
        .filter(x -> x instanceof ElementView<?, ?>)
        .map(x -> (ElementView<?, ?>) x)
        .collect(Collectors.toList());
  }
}
