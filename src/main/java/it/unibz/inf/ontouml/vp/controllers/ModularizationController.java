package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.ModularizationServiceResult;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml2vp.IProjectLoader;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import java.io.IOException;

public class ModularizationController implements VPActionController {

  @Override
  public void performAction(VPAction vpAction) {
    // TODO: add warnings
    // TODO: process exceptions
    // Serialize project
    try {
      final String serializedProject = Uml2OntoumlTransformer.transformAndSerialize();
      // Perform the request
      final ModularizationServiceResult serviceResult =
          OntoUMLServerAccessController.requestProjectModularization(serializedProject);
      // Load project
      Project modularizedProject = serviceResult.getResult();
      if (modularizedProject != null) IProjectLoader.load(modularizedProject, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(VPAction vpAction) {}
}
