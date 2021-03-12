package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.ontouml2vp.Ontouml2UmlLoader;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.SimpleServiceWorker;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementation toolbar button action responsible for exporting OntoUML model in JSON (according
 * to OntoUML Schema).
 *
 * @author Claudenir Fonseca
 */
public class JsonImportAndExportController implements VPActionController {

  private static final String IMPORT_ACTION_ID = "it.unibz.inf.ontouml.vp.actions.JsonImportAction";
  private static final String PROJECT_IMPORT_ACTION_ID = "project.import.JsonImportAction";
  private static final String EXPORT_ACTION_ID = "it.unibz.inf.ontouml.vp.actions.JsonExportAction";
  private static final String PROJECT_EXPORT_ACTION_ID = "project.export.JsonExportAction";

  private static final String MESSAGE_IMPORT_WARNING =
      "Warning: this action may override elements in your project. Do you wish to continue?";
  private static final String MESSAGE_MODEL_EXPORTED = "Model exported successfully.";
  private static final String MESSAGE_MODEL_IMPORTED = "Model imported successfully.";
  private static final String MESSAGE_MODEL_EXPORT_INVALID_FILE_ERROR =
      "Unable to export: invalid file.";
  private static final String MESSAGE_MODEL_IMPORT_INVALID_FILE_ERROR =
      "Unable to import: invalid file.";
  private static final String MESSAGE_MODEL_EXPORT_UNEXPECTED_ERROR =
      "Unable to export model due to an unexpected error.";
  private static final String MESSAGE_MODEL_IMPORT_UNEXPECTED_ERROR =
      "Unable to import model due to an unexpected error.";

  private Configurations configs;
  private ProjectConfigurations projectConfigurations;
  private VPAction action;
  private Path filePath;

  /**
   * Performs model export in JSON format.
   *
   * @param action
   */
  @Override
  public void performAction(VPAction action) {
    this.action = action;
    configs = Configurations.getInstance();
    projectConfigurations = configs.getProjectConfigurations();

    if (shouldImport()) {
      boolean shouldProceed = ViewManagerUtils.warningDialog(MESSAGE_IMPORT_WARNING);
      if (!shouldProceed) return;
    }

    filePath = getFilePath();

    if (shouldImport()) {
      new SimpleServiceWorker(this::importTask).execute();
    } else if (shouldExport()) {
      new SimpleServiceWorker(this::exportTask).execute();
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

  private boolean shouldExport() {
    return EXPORT_ACTION_ID.equals(action.getActionId())
        || PROJECT_EXPORT_ACTION_ID.equals(action.getActionId());
  }

  private boolean shouldImport() {
    return IMPORT_ACTION_ID.equals(action.getActionId())
        || PROJECT_IMPORT_ACTION_ID.equals(action.getActionId());
  }

  private Path getFilePath() {
    final FileDialog fileDialog;
    final Frame rootFrame = (Frame) ApplicationManager.instance().getViewManager().getRootFrame();
    final String suggestedFolderPath = projectConfigurations.getExportFolderPath();
    String suggestedFileName = projectConfigurations.getExportFilename();

    if (suggestedFileName.isEmpty()) {
      final String projectName =
          ApplicationManager.instance().getProjectManager().getProject().getName();
      suggestedFileName = projectName + ".json";
    }

    if (shouldExport()) {
      final String title = "Choose export file destination";
      fileDialog = new FileDialog(rootFrame, title, FileDialog.SAVE);
    } else if (shouldImport()) {
      final String title = "Select import file ";
      fileDialog = new FileDialog(rootFrame, title, FileDialog.LOAD);
    } else {
      throw new RuntimeException("Unexpected action: " + action.getActionId());
    }

    fileDialog.setFile(suggestedFileName);
    fileDialog.setDirectory(suggestedFolderPath);
    fileDialog.setMultipleMode(false);
    fileDialog.setFilenameFilter((dir, name) -> name != null && name.endsWith(".json"));

    fileDialog.setVisible(true);

    final String fileDirectory = fileDialog.getDirectory();
    final String fileName = fileDialog.getFile();

    if (fileDirectory != null && fileName != null) {
      return fileName.endsWith(".json")
          ? Paths.get(fileDirectory, fileName)
          : Paths.get(fileDirectory, fileName + ".json");
    }

    return null;
  }

  private void saveFilePath() {
    final Path directoryPath = filePath.getParent();
    final String directoryPathName = directoryPath.toAbsolutePath().getFileName().toString();
    final String filePathName = filePath.getFileName().toString();

    projectConfigurations.setExportFolderPath(directoryPathName);
    projectConfigurations.setExportFilename(filePathName);
    configs.save();
  }

  private List<String> importTask(SimpleServiceWorker context) {
    try {
      if (filePath == null) {
        context.cancel(true);
        return List.of();
      }

      final String importFileContents = Files.readString(filePath);

      if (!context.isCancelled()) {
        Ontouml2UmlLoader.deserializeAndLoad(importFileContents, false, false);
        saveFilePath();
        ViewManagerUtils.log(MESSAGE_MODEL_IMPORTED);
        return List.of(MESSAGE_MODEL_IMPORTED);
      }

      return List.of();
    } catch (IOException e) {
      e.printStackTrace();
      ViewManagerUtils.log(MESSAGE_MODEL_IMPORT_INVALID_FILE_ERROR);
      return List.of(MESSAGE_MODEL_IMPORT_INVALID_FILE_ERROR);
    } catch (Exception e) {
      e.printStackTrace();
      ViewManagerUtils.log(MESSAGE_MODEL_IMPORT_UNEXPECTED_ERROR);
      return List.of(MESSAGE_MODEL_IMPORT_UNEXPECTED_ERROR);
    }
  }

  private List<String> exportTask(SimpleServiceWorker context) {
    try {
      if (filePath == null) {
        context.cancel(true);
        return List.of();
      }

      final String exportFileContents = Uml2OntoumlTransformer.transformAndSerialize();

      if (!context.isCancelled()) {
        Files.write(filePath, exportFileContents.getBytes());
        saveFilePath();
        ViewManagerUtils.log(MESSAGE_MODEL_EXPORTED);
        return List.of(MESSAGE_MODEL_EXPORTED);
      }

      return List.of();
    } catch (IOException e) {
      e.printStackTrace();
      ViewManagerUtils.log(MESSAGE_MODEL_EXPORT_INVALID_FILE_ERROR);

      return List.of(MESSAGE_MODEL_EXPORT_INVALID_FILE_ERROR);
    } catch (Exception e) {
      e.printStackTrace();
      ViewManagerUtils.log(MESSAGE_MODEL_EXPORT_UNEXPECTED_ERROR);

      return List.of(MESSAGE_MODEL_EXPORT_UNEXPECTED_ERROR);
    }
  }
}
