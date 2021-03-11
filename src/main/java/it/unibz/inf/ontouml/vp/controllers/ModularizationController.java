package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.ModularizationServiceResult;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml2vp.IProjectLoader;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.SimpleServiceWorker;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.io.IOException;
import java.util.List;

public class ModularizationController implements VPActionController {

  @Override
  public void performAction(VPAction vpAction) {
    final SimpleServiceWorker worker = new SimpleServiceWorker(this::task);
    worker.execute();
  }

  @Override
  public void update(VPAction vpAction) {}

  private List<String> task(SimpleServiceWorker context) {
    try {
      System.out.println("Starting modularization service...");
      System.out.println("Serializing project...");
      final String serializedProject = Uml2OntoumlTransformer.transformAndSerialize();
      System.out.println(serializedProject);
      System.out.println("Project serialized!");

      System.out.println("Requesting diagrams from the modularization service...");
      final ModularizationServiceResult serviceResult =
          OntoUMLServerAccessController.requestProjectModularization(serializedProject);
      System.out.println("Request answered by modularization service!");

      System.out.println(serviceResult.getIssues());

      // Load project
      System.out.println("Processing modularization service response...");
      Project modularizedProject = serviceResult.getResult();
      if (!context.isCancelled() && modularizedProject != null) {
        IProjectLoader.load(modularizedProject, false, true);
        ViewManagerUtils.log(serviceResult.getMessage());
      }
      System.out.println("Modularization service response processed!");
      System.out.println("Modularization service concluded.");

      return List.of(serviceResult.getMessage());
    } catch (IOException e) {
      if (!context.isCancelled()) {
        ViewManagerUtils.log(e.getMessage());
      }

      e.printStackTrace();
      return List.of(e.getMessage());
    }
  }
}
