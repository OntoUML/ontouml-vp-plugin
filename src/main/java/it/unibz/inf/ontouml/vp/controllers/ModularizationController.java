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
      System.out.println("Accessing modularization service...");
      System.out.println("Serializing project to send to the modularization service...");
      final String serializedProject = Uml2OntoumlTransformer.transformAndSerialize();
      System.out.println("Project serialized!");
      // Perform the request
      System.out.println("Requesting diagrams from the modularization service...");
      final ModularizationServiceResult serviceResult =
          OntoUMLServerAccessController.requestProjectModularization(serializedProject);
      System.out.println("Request answered by modularization service!");
      // Load project
      System.out.println("Processing modularization service response...");
      Project modularizedProject = serviceResult.getResult();
      if (modularizedProject != null) IProjectLoader.load(modularizedProject, false, true);
      System.out.println("Modularization service response processed!");
      System.out.println("Modularization service successfully used.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(VPAction vpAction) {}
}
