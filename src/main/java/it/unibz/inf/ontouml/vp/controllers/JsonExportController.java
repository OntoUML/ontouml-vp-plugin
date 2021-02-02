package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import it.unibz.inf.ontouml.vp.views.ProgressPanel;
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

  private ProgressPanel progressPanel;
  private ProgressDialog loading;
  private IDialog mainDialog;

  Thread thread;

  /**
   * Performs model export in JSON format.
   *
   * @param action
   */
  @Override
  public void performAction(VPAction action) {
    final Configurations configs = Configurations.getInstance();
    final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();

    System.out.println("\n\nperformAction()!\n\n");

    FileDialog fd =
        new FileDialog(
            (Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
            "Choose destination",
            FileDialog.SAVE);

    String suggestedFolderPath = projectConfigurations.getExportFolderPath();
    String suggestedFileName = projectConfigurations.getExportFilename();

    System.out.println("\n\ncheckIsEmpty!\n\n");

    if (suggestedFileName.isEmpty()) {
      String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
      suggestedFileName = projectName + ".json";
    }

    System.out.println("\n\nWill set FileDialog props!\n\n");

    fd.setDirectory(suggestedFolderPath);
    fd.setFile(suggestedFileName);
    fd.setVisible(true);

    String fileDirectory = fd.getDirectory();

    if (fileDirectory != null) {

      System.out.println("\n\nWill create Progress Dialog!\n\n");
      loading = new ProgressDialog();
      System.out.println("\n\nWill show Progress Dialog!\n\n");
      ApplicationManager.instance().getViewManager().showDialog(loading);
      System.out.println("\n\nShowed Progress Dialog!\n\n");

      try {
        System.out.println("\n\ngetFile()!\n\n");
        String fileName = fd.getFile();

        System.out.println("\n\nendsWith\n\n");
        if (!fileName.endsWith(".json")) fileName += ".json";

        System.out.println("\n\ngenerateModel\n\n");
        final String jsonModel = ModelElement.generateModel(true);
        System.out.println("\n\nGENERATED\n\n");
        //        Transformer.transform();

        Files.write(Paths.get(fileDirectory, fileName), jsonModel.getBytes());
        projectConfigurations.setExportFolderPath(fileDirectory);
        projectConfigurations.setExportFilename(fileName);
        configs.save();
        ViewManagerUtils.cleanAndShowMessage("Model exported successfully.");
      } catch (IOException e) {
        ViewManagerUtils.cleanAndShowMessage("Model export failed.");
        e.printStackTrace();
      }

      mainDialog.close();
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

  protected class ProgressDialog implements IDialogHandler {

    @Override
    public Component getComponent() {
      progressPanel = new ProgressPanel("Building model...");
      return progressPanel;
    }

    @Override
    public void prepare(IDialog dialog) {
      mainDialog = dialog;
      mainDialog.setTitle("Export to JSON");
      mainDialog.setModal(false);
      mainDialog.setResizable(false);
      dialog.setSize(progressPanel.getWidth(), progressPanel.getHeight() + 20);
      progressPanel.setContainerDialog(mainDialog);
    }

    @Override
    public void shown() {}

    @Override
    public boolean canClosed() {
      return false;
    }
  }
}
