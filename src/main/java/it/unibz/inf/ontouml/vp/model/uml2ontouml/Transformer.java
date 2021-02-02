package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Transformer {

  public static String transformAndSerialize() throws IOException {
    Project project = transform();
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    return mapper.writeValueAsString(project);
  }

  public static Project transform() throws IOException {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();

    final String[] elementTypes = {
      IModelElementFactory.MODEL_TYPE_PACKAGE,
      IModelElementFactory.MODEL_TYPE_MODEL,
      IModelElementFactory.MODEL_TYPE_CLASS,
      IModelElementFactory.MODEL_TYPE_DATA_TYPE,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS,
      IModelElementFactory.MODEL_TYPE_ATTRIBUTE,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION_END,
      IModelElementFactory.MODEL_TYPE_ENUMERATION_LITERAL
    };

    List<Object> created =
        Stream.of(project.toAllLevelModelElementArray(elementTypes))
            .filter(x -> x instanceof IClass)
            .map(
                x -> {
                  System.out.println("id: " + x.getId());
                  System.out.println("name: " + x.getName());
                  System.out.println("type: " + x.getModelType());
                  System.out.println("hasParent? " + (x.getParent() != null));
                  if (x.getParent() != null) {
                    System.out.println("parent.name: " + x.getParent().getName());
                    System.out.println("parent.type: " + x.getParent().getModelType());
                  }
                  System.out.println("child.length: " + x.toChildArray().length);
                  System.out.println("project.name: " + x.getProject().getName());

                  return null;
                })
            .collect(Collectors.toList());

    //    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    //    System.out.println(mapper.writeValueAsString(created));

    return null;
  }
}
