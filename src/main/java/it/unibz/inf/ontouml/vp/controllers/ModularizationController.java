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
      System.out.println("Accessing modularization service...");
      System.out.println("Serializing project to send to the modularization service...");
      final String project = Uml2OntoumlTransformer.transformAndSerialize();
      System.out.println("Project serialized!");

      // Perform the request
      System.out.println("Requesting diagrams from the modularization service...");
      final String modularizedProject =
          OntoUMLServerAccessController.requestProjectModularization(project);
      System.out.println("Request answered by modularization service!");

      // Deserialize project

      System.out.println("Processing modularization service response...");
      Ontouml2UmlLoader.deserializeAndLoad(modularizedProject, false, true);
      System.out.println("Modularization service response processed!");
      System.out.println("Modularization service successfully used.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(VPAction vpAction) {}
}
