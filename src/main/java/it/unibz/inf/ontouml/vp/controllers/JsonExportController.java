package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implementation toolbar button action responsible for exporting OntoUML model in JSON (according
 * to OntoUML Schema).
 *
 * @author Claudenir Fonseca
 */
public class JsonExportController implements VPActionController {

  /**
   * Performs model export in JSON format.
   *
   * @param action
   */
  @Override
  public void performAction(VPAction action) {
    final Configurations configs = Configurations.getInstance();
    final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();

    String jsonModel = "";

    try {
      jsonModel = Uml2OntoumlTransformer.transformAndSerialize();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("NEW TRANSFORMATION IS CRASHING!");
    }

    FileDialog fd =
        new FileDialog(
            (Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
            "Choose destination",
            FileDialog.SAVE);

    String suggestedFolderPath = projectConfigurations.getExportFolderPath();
    String suggestedFileName = projectConfigurations.getExportFilename();

    if (suggestedFileName.isEmpty()) {
      String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
      suggestedFileName = projectName + ".json";
    }

    fd.setDirectory(suggestedFolderPath);
    fd.setFile(suggestedFileName);
    fd.setVisible(true);

    String fileDirectory = fd.getDirectory();

    if (fileDirectory != null) {

      //      loading = new ProgressDialog();
      //      ApplicationManager.instance().getViewManager().showDialog(loading);

      try {
        String fileName = fd.getFile();

        if (!fileName.endsWith(".json")) fileName += ".json";

        //        final String jsonModel = ModelElement.generateModel(true);

        Files.write(Paths.get(fileDirectory, fileName), jsonModel.getBytes());
        projectConfigurations.setExportFolderPath(fileDirectory);
        projectConfigurations.setExportFilename(fileName);
        configs.save();
        ViewManagerUtils.cleanAndShowMessage("Model exported successfully.");
      } catch (IOException e) {
        ViewManagerUtils.cleanAndShowMessage("Model export failed.");
        e.printStackTrace();
      }

      //      mainDialog.close();
    }
  }

  /**
   * Called when the menu containing the button is accessed allowing for action manipulation, such
   * as enable/disable or selecting the button.
   *
   * <p>OBS: DOES NOT apply to this class.
   */
  @Override
  public void update(VPAction action) {}
}
