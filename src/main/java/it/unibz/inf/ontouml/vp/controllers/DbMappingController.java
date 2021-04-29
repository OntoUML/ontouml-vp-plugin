package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DbMappingOptions;
import it.unibz.inf.ontouml.vp.model.DbMappingToDbServiceResult;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.SimpleServiceWorker;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DbMappingController implements VPActionController {

  private static final String MESSAGE_MODEL_EXPORTED = "Model exported successfully.";

  private Configurations configurations;
  private ProjectConfigurations projectConfigurations;
  private String schemaFormat = ".sql";
  private String obdaFormat = ".obda";
  private String propertiesFormat = ".properties";
  private Path schemaFilePath;
  private Path obdaFilePath;
  private Path propertiesFilePath;

  @Override
  public void performAction(VPAction action) {
    schemaFilePath = null;
    obdaFilePath = null;
    propertiesFilePath = null;
    configurations = Configurations.getInstance();
    projectConfigurations = configurations.getProjectConfigurations();
    DbMappingDialogHandler mappingDialog = new DbMappingDialogHandler(projectConfigurations);
    mappingDialog.showDialog();

    if (mappingDialog.wasCancelled()) {
      ViewManagerUtils.log("Request cancelled by the user.");
      return;
    }

    putFilePath();

    new SimpleServiceWorker(this::task).execute();
  }

  private List<String> task(SimpleServiceWorker context) {
    try {
      if (schemaFilePath == null) {
        context.cancel(true);
        return List.of();
      }

      final String project = Uml2OntoumlTransformer.transformAndSerialize();
      final String options = new DbMappingOptions(projectConfigurations).toJson();
      final DbMappingToDbServiceResult result =
          OntoUMLServerAccessController.requestMappingToDB(project, options);

      if (!context.isCancelled()) {
        // Files.write(schemaFilePath, "schema".getBytes());
        // Files.write(obdaFilePath, "OBDA".getBytes());
        // Files.write(propertiesFilePath, "properties".getBytes());
        Files.write(schemaFilePath, result.getSchema().getBytes());
        Files.write(obdaFilePath, result.getObda().getBytes());
        Files.write(propertiesFilePath, result.getProperties().getBytes());
        saveFilePath();
        ViewManagerUtils.log(MESSAGE_MODEL_EXPORTED);
        return List.of(MESSAGE_MODEL_EXPORTED);
      }

      return List.of();

    } catch (IOException e) {
      if (!context.isCancelled()) {
        ViewManagerUtils.log(e.getMessage());
      }
      e.printStackTrace();
      return List.of(e.getMessage());
    }
  }

  private void putFilePath() {
    final FileDialog fileDialog;
    final Frame rootFrame = (Frame) ApplicationManager.instance().getViewManager().getRootFrame();
    final String suggestedFolderPath = projectConfigurations.getDbMappingFolderPath();
    String suggestedFileName = projectConfigurations.getDbMappingFileName();

    if (suggestedFileName.isEmpty()) {
      suggestedFileName = ApplicationManager.instance().getProjectManager().getProject().getName();
    }

    final String title = "Choose file destination";
    fileDialog = new FileDialog(rootFrame, title, FileDialog.SAVE);

    fileDialog.setFile(suggestedFileName);
    fileDialog.setDirectory(suggestedFolderPath);
    fileDialog.setMultipleMode(false);
    fileDialog.setVisible(true);

    final String fileDirectory = fileDialog.getDirectory();
    final String fileName = fileDialog.getFile();

    if (fileDirectory != null && fileName != null) {
      schemaFilePath = Paths.get(fileDirectory, fileName + schemaFormat);
      obdaFilePath = Paths.get(fileDirectory, fileName + obdaFormat);
      propertiesFilePath = Paths.get(fileDirectory, fileName + propertiesFormat);
    }
  }

  private void saveFilePath() {
    final Path directoryPath = schemaFilePath.getParent();
    final String directoryPathName = directoryPath.toAbsolutePath().getFileName().toString();
    final String filePathName = schemaFilePath.getFileName().toString();

    projectConfigurations.setDbMappingFolderPath(directoryPathName);
    projectConfigurations.setDbMappingFileName(filePathName);
    configurations.save();
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
