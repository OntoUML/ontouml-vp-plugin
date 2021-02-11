package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;

public class Ontouml2UmlLoader {
  public static void deserializeAndLoad(String json, boolean diagramsOnly)
      throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    Project project = mapper.readValue(json, Project.class);
    IProjectLoader.load(project, false);
  }
}
