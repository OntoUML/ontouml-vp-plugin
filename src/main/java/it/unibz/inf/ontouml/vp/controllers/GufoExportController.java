package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GufoExportOptions;
import it.unibz.inf.ontouml.vp.model.GufoTransformationServiceResult;
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

/**
 * Implementation of toolbar button Export to gUFO.
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public class GufoExportController implements VPActionController {

  private static final String MESSAGE_MODEL_EXPORTED = "Model exported successfully.";

  private Configurations configurations;
  private ProjectConfigurations projectConfigurations;
  private String fileFormat = ".ttl";
  private Path filePath;

  @Override
  public void performAction(VPAction action) {
    configurations = Configurations.getInstance();
    projectConfigurations = configurations.getProjectConfigurations();
    GufoExportDialogHandler exportDialog = new GufoExportDialogHandler(projectConfigurations);
    exportDialog.showDialog();

    if (exportDialog.wasCancelled()) {
      ViewManagerUtils.log("Request cancelled by the user.");
      return;
    }

    fileFormat = !"Turtle".equals(projectConfigurations.getExportGUFOFormat()) ? ".nt" : ".ttl";
    filePath = getFilePath();

    new SimpleServiceWorker(this::task).execute();
  }

  private List<String> task(SimpleServiceWorker context) {
    try {
      if (filePath == null) {
        context.cancel(true);
        return List.of();
      }

      final String project = Uml2OntoumlTransformer.transformAndSerialize();
      final String options = new GufoExportOptions(projectConfigurations).toJson();
      final GufoTransformationServiceResult result =
          OntoUMLServerAccessController.requestModelTransformationToGufo(project, options);
      final String gufoFile = result != null ? result.getResult() : "";

      if (!context.isCancelled()) {
        Files.write(filePath, gufoFile.getBytes());
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

  private Path getFilePath() {
    final FileDialog fileDialog;
    final Frame rootFrame = (Frame) ApplicationManager.instance().getViewManager().getRootFrame();
    final String suggestedFolderPath = projectConfigurations.getExportGUFOFolderPath();
    String suggestedFileName = projectConfigurations.getExportGUFOFilename();

    if (suggestedFileName.isEmpty()) {
      final String projectName =
          ApplicationManager.instance().getProjectManager().getProject().getName();
      suggestedFileName = projectName + fileFormat;
    }

    final String title = "Choose export file destination";
    fileDialog = new FileDialog(rootFrame, title, FileDialog.SAVE);

    fileDialog.setFile(suggestedFileName);
    fileDialog.setDirectory(suggestedFolderPath);
    fileDialog.setMultipleMode(false);
    fileDialog.setFilenameFilter((dir, name) -> name != null && name.endsWith(fileFormat));
    fileDialog.setVisible(true);

    final String fileDirectory = fileDialog.getDirectory();
    final String fileName = fileDialog.getFile();

    if (fileDirectory != null && fileName != null) {
      return fileName.endsWith(fileFormat)
          ? Paths.get(fileDirectory, fileName)
          : Paths.get(fileDirectory, fileName + fileFormat);
    }

    return null;
  }

  private void saveFilePath() {
    final Path directoryPath = filePath.getParent();
    final String directoryPathName = directoryPath.toAbsolutePath().getFileName().toString();
    final String filePathName = filePath.getFileName().toString();

    projectConfigurations.setExportGUFOFolderPath(directoryPathName);
    projectConfigurations.setExportGUFOFilename(filePathName);
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
