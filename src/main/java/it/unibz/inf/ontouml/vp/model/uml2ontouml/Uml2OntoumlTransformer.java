package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.io.IOException;

public class Uml2OntoumlTransformer {

  public static String transformAndSerialize() throws IOException {
    final IProject source = ApplicationManager.instance().getProjectManager().getProject();
    Project target = IProjectTransformer.transform(source);

    target
        .getAllGeneralizationSets()
        .forEach(
            gs -> {
              System.out.println("GS: " + gs.getFirstName().get());
              gs.getGeneralizations()
                  .forEach(
                      g -> {
                        System.out.println("General: " + g.getGeneral().get().getFirstName().get());
                        System.out.println(
                            "Specific: " + g.getSpecific().get().getFirstName().get());
                      });
            });

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    return mapper.writeValueAsString(target);
  }

  public static ModelElement transformModelElement(IModelElement source) {
    ModelElement target = null;

    if (source instanceof IClass || source instanceof IDataType) {
      target = IClassTransformer.transform(source);
    } else if (source instanceof IAssociation) {
      target = IAssociationTransformer.transform(source);
    } else if (source instanceof IAssociationClass) {
      target = IAssociationClassTransformer.transform(source);
    } else if (source instanceof IPackage || source instanceof IModel) {
      target = IPackageTransformer.transform(source);
    } else if (source instanceof IAttribute || source instanceof IAssociationEnd) {
      target = IPropertyTransformer.transform(source);
    } else if (source instanceof IGeneralization) {
      target = IGeneralizationTransformer.transform(source);
    } else if (source instanceof IGeneralizationSet) {
      target = IGeneralizationSetTransformer.transform(source);
    }

    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }
}
