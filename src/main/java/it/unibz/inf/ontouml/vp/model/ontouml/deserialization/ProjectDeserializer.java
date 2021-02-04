package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;
import java.io.IOException;

public class ProjectDeserializer extends JsonDeserializer<Project> {

  @Override
  public Project deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Project project = new Project();

    ElementDeserializer.deserialize(project, root, codec);
    Package model = deserializeObject(root, "model", Package.class, codec);
    project.setModel(model);

    try {
      ReferenceResolver.resolveReferences(project);
    } catch (Exception e) {
      throw new JsonParseException(parser, "Cannot deserialize project", e);
    }

    return project;
  }
}
