package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeArrayField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
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

    List<DiagramElement<?, ?>> contents = deserializeContents(root, codec);
    diagram.setContents(contents);

    return diagram;
  }

  private List<DiagramElement<?, ?>> deserializeContents(JsonNode root, ObjectCodec codec)
      throws IOException {
    List<OntoumlElement> contents =
        deserializeArrayField(
            root,
            "contents",
            List.of(
                ClassView.class,
                RelationView.class,
                GeneralizationView.class,
                GeneralizationSetView.class),
            codec);

    return contents.stream()
        .filter(x -> x instanceof DiagramElement<?, ?>)
        .map(x -> (DiagramElement<?, ?>) x)
        .collect(Collectors.toList());
  }
}
