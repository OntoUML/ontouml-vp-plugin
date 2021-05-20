package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DbMappingOptions;
import it.unibz.inf.ontouml.vp.model.Ontouml2DbServiceResult;
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
      
      saveFilePath();

      final String project = Uml2OntoumlTransformer.transformAndSerialize();
      final String options = new DbMappingOptions(projectConfigurations).toJson();
      final Ontouml2DbServiceResult serviceResult =
          OntoUMLServerAccessController.requestModelTransformationToDb(project, options);

      if (!context.isCancelled()) {
    	  if(projectConfigurations.isGenerateSchema()) {
    		  Files.write(schemaFilePath, serviceResult.getResult().getSchema().getBytes());
    	  }
    	  if(projectConfigurations.isGenerateObda()) {
    		  Files.write(obdaFilePath, serviceResult.getResult().getObda().getBytes());
    	  }
    	  if(projectConfigurations.isGenerateConnection()) {
    		  Files.write(propertiesFilePath, serviceResult.getResult().getConnection().getBytes());
    	  }
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

  private boolean putFilePath() {
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
    final String fileName = removeFileExtension(fileDialog.getFile());

    if (fileDirectory != null && fileName != null) {
      schemaFilePath = Paths.get(fileDirectory, fileName + schemaFormat);
      obdaFilePath = Paths.get(fileDirectory, fileName + obdaFormat);
      propertiesFilePath = Paths.get(fileDirectory, fileName + propertiesFormat);
      return true;
    }else {
    	return false;
    }
  }

  private void saveFilePath() {
    final Path directoryPath = schemaFilePath.getParent();
    final String directoryPathName = directoryPath.toAbsolutePath().getFileName().toString();
    final String filePathName = removeFileExtension(schemaFilePath.getFileName().toString());

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
  
  private String removeFileExtension(String fileName) {
	  if(fileName.lastIndexOf('.') > 0)
		  return fileName.substring(0, fileName.lastIndexOf('.'));
	  else return fileName;
  }
}
