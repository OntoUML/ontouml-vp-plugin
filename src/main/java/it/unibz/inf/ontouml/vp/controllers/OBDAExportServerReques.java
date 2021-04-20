package it.unibz.inf.ontouml.vp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vp.plugin.ApplicationManager;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.OBDAResult;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import it.unibz.inf.ontouml.vp.views.ProgressDialogStandard;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;

public class OBDAExportServerReques extends Thread {

  private ProgressDialogStandard loading;

  private final ProjectConfigurations projectConfigurations =
      Configurations.getInstance().getProjectConfigurations();

  @Override
  public void run() {
    HashSet<String> elements;
    try {

      loading = new ProgressDialogStandard();
      loading.setTitle("Generating the files");
      ApplicationManager.instance()
          .getViewManager()
          .showDialog(
              loading); // This class must extend Thread for the ProgressDialog to be shown
                        // properly.

      elements =
          Configurations.getInstance()
              .getProjectConfigurations()
              .getExportGUFOElementsDiagramTree();

      final BufferedReader buffer =
          OntoUMLServerAccessController.generateODBAFile(
              ModelElement.generateModel(elements, true),
              projectConfigurations.getMappingStrategy().toString(),
              projectConfigurations.getTargetDBMS().toString(),
              projectConfigurations.isStandardizeNames(),
              projectConfigurations.getExportGUFOIRI(),
              projectConfigurations.isGenerateSchema(),
              projectConfigurations.isGenerateConnection(),
              projectConfigurations.getHostNameConnection(),
              projectConfigurations.getDatabaseNameConnection(),
              projectConfigurations.getUserNameConnection(),
              projectConfigurations.getPassword());

      loading.canClosed();

      if (buffer != null) {
        saveFile(buffer);
        ViewManagerUtils.cleanAndShowMessage("OBDA mapping performed successfully.");
      } else {
        ViewManagerUtils.cleanAndShowMessage(
            "Unable to generate the OBDA mapping. Please check your model and parameters.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      loading.canClosed();
    }
  }

  private void saveFile(BufferedReader buffer) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = buffer.lines().collect(Collectors.joining("\n"));

    OBDAResult result = objectMapper.readValue(json, OBDAResult.class);

    final Configurations configs = Configurations.getInstance();
    final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
    final FileDialog fd =
        new FileDialog(
            (Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
            "Choose destination",
            FileDialog.SAVE);

    String suggestedFolderPath = projectConfigurations.getExportFolderPath();
    String suggestedFileName =
        ApplicationManager.instance().getProjectManager().getProject().getName();
    String fileName;

    fd.setDirectory(suggestedFolderPath);
    fd.setFile(suggestedFileName);
    fd.setVisible(true);

    if (fd.getDirectory() != null && fd.getFile() != null) {

      if (fd.getFile().indexOf('.') == -1) {
        fileName = fd.getFile();
      } else {
        fileName = fd.getFile().substring(0, fd.getFile().lastIndexOf('.'));
      }

      if (projectConfigurations.isGenerateSchema()) {
        Files.write(Paths.get(fd.getDirectory(), fileName + ".sql"), result.getSchema().getBytes());
      }

      if (projectConfigurations.isGenerateConnection()) {
        Files.write(
            Paths.get(fd.getDirectory(), fileName + ".properties"),
            result.getConnection().getBytes());
      }

      Files.write(
          Paths.get(fd.getDirectory(), fileName + ".obda"), result.getObdaFile().getBytes());

      projectConfigurations.setExportFolderPath(fd.getDirectory());
      configs.save();
    }
  }
}
