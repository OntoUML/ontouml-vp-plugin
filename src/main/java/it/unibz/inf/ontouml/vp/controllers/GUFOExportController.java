package it.unibz.inf.ontouml.vp.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GufoTransformationServiceResult;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.ServerRequest;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import it.unibz.inf.ontouml.vp.views.GUFOExportView;
import it.unibz.inf.ontouml.vp.views.ProgressPanel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implementation of toolbar button Export to gUFO.
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public class GUFOExportController implements VPActionController {

  private ProgressPanel progressPanel;
  private ProgressDialog loading;
  private IDialog mainDialog;

  private ExportDialog menu;
  private GUFOExportView _exportMenuView;
  private IDialog _dialog;
  MenuExport requestMenu;

  @Override
  public void performAction(VPAction action) {

    requestMenu = new MenuExport();
    menu = new ExportDialog();

    if (OntoUMLPlugin.getExportToGUFOWindowOpen() == true) return;
    else OntoUMLPlugin.setExportToGUFOWindowOpen(true);

    ApplicationManager.instance().getViewManager().showDialog(menu);
  }

  /**
   * Called when the menu containing the button is accessed allowing for action manipulation, such
   * as enable/disable or selecting the button.
   *
   * <p>OBS: DOES NOT apply to this class.
   */
  @Override
  public void update(VPAction action) {}

  private FileDialog prepareChooseDestinationFileDialog(
      ProjectConfigurations projectConfigurations, String exportFormatExtension) {
    final FileDialog fileDialog =
        new FileDialog(
            (Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
            "Choose destination",
            FileDialog.SAVE);

    final String suggestedFolderPath = projectConfigurations.getExportGUFOFolderPath();
    String suggestedFileName = projectConfigurations.getExportGUFOFilename();

    if (suggestedFileName.isEmpty()) {
      String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
      suggestedFileName = projectName + exportFormatExtension;
    }

    fileDialog.setDirectory(suggestedFolderPath);
    fileDialog.setFile(suggestedFileName);

    return fileDialog;
  }

  private void saveFile(String gufoFile, String exportFormat) throws IOException {
    final Configurations configurations = Configurations.getInstance();
    final ProjectConfigurations projectConfigurations = configurations.getProjectConfigurations();
    final String exportFormatExtension = !"Turtle".equals(exportFormat) ? ".nt" : ".ttl";
    final FileDialog fd =
        prepareChooseDestinationFileDialog(projectConfigurations, exportFormatExtension);

    fd.setVisible(true);

    if (fd.getDirectory() != null && fd.getFile() != null) {
      final String fileDirectory = fd.getDirectory();
      final String fileName =
          !fd.getFile().endsWith(exportFormatExtension)
              ? fd.getFile() + exportFormatExtension
              : fd.getFile();

      Files.write(Paths.get(fileDirectory, fileName), gufoFile.getBytes());
      projectConfigurations.setExportGUFOFolderPath(fileDirectory);
      projectConfigurations.setExportGUFOFilename(fileName);
      configurations.save();
    }
  }

  public class ProgressDialog implements IDialogHandler {

    @Override
    public Component getComponent() {
      progressPanel = new ProgressPanel(requestMenu);
      return progressPanel;
    }

    @Override
    public void prepare(IDialog dialog) {
      mainDialog = dialog;
      mainDialog.setTitle("Export to GUFO");
      mainDialog.setModal(false);
      mainDialog.setResizable(false);
      dialog.setSize(progressPanel.getWidth(), progressPanel.getHeight() + 20);
      progressPanel.setContainerDialog(mainDialog);
    }

    @Override
    public void shown() {}

    @Override
    public boolean canClosed() {
      mainDialog.close();
      return true;
    }
  }

  protected class ExportDialog implements IDialogHandler {

    /**
     * Called once before the dialog is shown. Developer should return the content of the dialog
     * (similar to the content pane).
     */
    @Override
    public Component getComponent() {
      _exportMenuView =
          new GUFOExportView(Configurations.getInstance().getProjectConfigurations(), requestMenu);
      return _exportMenuView;
    }

    /**
     * Called after the getComponent(). A dialog is created on Visual Paradigm internally (it still
     * not shown out). Developer can set the outlook of the dialog on prepare().
     */
    @Override
    public void prepare(IDialog dialog) {
      _dialog = dialog;
      _dialog.setTitle(OntoUMLPlugin.PLUGIN_NAME + " Configurations");
      _dialog.setModal(false);
      _dialog.setResizable(false);
      _dialog.setSize(_exportMenuView.getWidth(), _exportMenuView.getHeight() + 20);
      _exportMenuView.setContainerDialog(_dialog);
    }

    /** Called when the dialog is shown. */
    @Override
    public void shown() {}

    /** Called when the dialog is closed by the user clicking on the close button of the frame. */
    @Override
    public boolean canClosed() {
      requestMenu.doStop();
      OntoUMLPlugin.setExportToGUFOWindowOpen(false);
      ViewManagerUtils.cleanAndShowMessage("Request cancelled by the user.");
      return true;
    }
  }

  public class MenuExport extends ServerRequest {

    final Configurations configs = Configurations.getInstance();
    final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();

    private String getOptionsObjectAsString() {
      final JsonObject options = new JsonObject();
      final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

      boolean createInverses =
          Boolean.parseBoolean(projectConfigurations.getExportGUFOInverseBox());
      boolean createObjectProperty =
          !Boolean.parseBoolean(projectConfigurations.getExportGUFOObjectBox());
      boolean preAnalysis = Boolean.parseBoolean(projectConfigurations.getExportGUFOAnalysisBox());
      boolean prefixPackages =
          Boolean.parseBoolean(projectConfigurations.getExportGUFOPackagesBox());

      options.addProperty("baseIRI", projectConfigurations.getExportGUFOIRI());
      options.addProperty("format", projectConfigurations.getExportGUFOFormat());
      options.addProperty("uriFormatBy", projectConfigurations.getExportGUFOURIFormat());
      options.addProperty("createInverses", createInverses);
      options.addProperty("createObjectProperty", createObjectProperty);
      options.addProperty("preAnalysis", preAnalysis);
      options.addProperty("prefixPackages", prefixPackages);
      options.add(
          "customElementMapping",
          new Gson()
              .fromJson(projectConfigurations.getExportGUFOElementMapping(), JsonObject.class));
      options.add(
          "customPackageMapping",
          new Gson()
              .fromJson(projectConfigurations.getExportGUFOPackageMapping(), JsonObject.class));

      return gson.toJson(options);
    }

    @Override
    public void run() {
      while (keepRunning()) {
        try {

          if (keepRunning()) {
            if (!_exportMenuView.getIsOpen()) {

              if (_exportMenuView.getIsToExport()) {
                final String project = Uml2OntoumlTransformer.transformAndSerialize();
                final GufoTransformationServiceResult result =
                    OntoUMLServerAccessController.requestProjectTransformationToGufo(
                        project, getOptionsObjectAsString());
                final String gufoFile = result.getResult();

                if (gufoFile != null) {
                  saveFile(gufoFile, projectConfigurations.getExportGUFOFormat());
                  ViewManagerUtils.cleanAndShowMessage("Model exported successfully.");
                  requestMenu.doStop();
                } else {
                  menu.canClosed();
                  requestMenu.doStop();
                  ViewManagerUtils.cleanAndShowMessage(
                      "Unable to transform to GUFO. Please check your model.");
                }
              } else {
                menu.canClosed();
                requestMenu.doStop();
                ViewManagerUtils.cleanAndShowMessage("Request cancelled by the user.");
              }

              OntoUMLPlugin.setExportToGUFOWindowOpen(false);
            }
          } else {
            OntoUMLPlugin.setExportToGUFOWindowOpen(false);
            menu.canClosed();
            requestMenu.doStop();
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
