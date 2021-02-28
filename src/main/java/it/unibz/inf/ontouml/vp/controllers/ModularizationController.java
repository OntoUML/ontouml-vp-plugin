package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.ontouml2vp.Ontouml2UmlLoader;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import java.io.IOException;

public class ModularizationController implements VPActionController {

  @Override
  public void performAction(VPAction vpAction) {
    // TODO: add warnings
    // TODO: process exceptions
    // Serialize project
    try {
      final String project = Uml2OntoumlTransformer.transformAndSerialize();
      // Perform the request
      final String modularizedProject =
          OntoUMLServerAccessController.requestProjectModularization(project);
      // Deserialize project
      Ontouml2UmlLoader.deserializeAndLoad(modularizedProject, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(VPAction vpAction) {}
}
