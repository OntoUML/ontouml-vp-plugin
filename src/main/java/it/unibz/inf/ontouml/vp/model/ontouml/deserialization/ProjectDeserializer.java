package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeArrayField;
import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObjectField;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;
import java.io.IOException;
import java.util.List;

public class ProjectDeserializer extends JsonDeserializer<Project> {

  @Override
  public Project deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    System.out.println("Deserializing project...");

    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Project project = new Project();

    ElementDeserializer.deserialize(project, root, codec);

    Package model = deserializeObjectField(root, "model", Package.class, codec);
    project.setModel(model);

    List<Diagram> diagrams = deserializeArrayField(root, "diagrams", Diagram.class, codec);
    project.setDiagrams(diagrams);

    try {
      ReferenceResolver.resolveReferences(project);
    } catch (Exception e) {
      throw new JsonParseException(parser, "Cannot deserialize project", e);
    }

    return project;
  }
}
