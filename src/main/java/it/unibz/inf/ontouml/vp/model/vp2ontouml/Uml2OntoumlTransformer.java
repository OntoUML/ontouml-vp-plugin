package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.io.IOException;

public class Uml2OntoumlTransformer {

  public static String transformAndSerialize() throws IOException {
    final IProject source = ApplicationManager.instance().getProjectManager().getProject();
    Project target = IProjectTransformer.transform(source);
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    return mapper.writeValueAsString(target);
  }
}
