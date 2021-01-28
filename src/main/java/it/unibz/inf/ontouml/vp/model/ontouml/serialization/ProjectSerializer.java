package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.io.IOException;

public class ProjectSerializer extends OntoumlElementSerializer<Project> {

  @Override
  public void serialize(Project project, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    super.serialize(project, jsonGen, provider);
    jsonGen.writeObjectField("model", project.getModel().orElse(null));
    writeNullableArrayField("diagrams", project.getDiagrams(), jsonGen);
    jsonGen.writeEndObject();
  }
}
