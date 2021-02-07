package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.io.IOException;

public class ProjectSerializer extends JsonSerializer<Project> {

  @Override
  public void serialize(Project project, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(project, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Project project, JsonGenerator jsonGen) throws IOException {
    OntoumlElementSerializer.serializeFields(project, jsonGen);
    jsonGen.writeObjectField("model", project.getModel().orElse(null));
    Serializer.writeNullableArrayField("diagrams", project.getDiagrams(), jsonGen);
  }
}
