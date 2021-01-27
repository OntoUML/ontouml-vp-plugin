package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
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

public class DBExportServerRequest extends Thread {

  private ProgressDialogStandard loading;

  private final ProjectConfigurations projectConfigurations =
      Configurations.getInstance().getProjectConfigurations();

  @Override
  public void run() {
    HashSet<String> elements;
    try {

      loading = new ProgressDialogStandard();
      loading.setTitle("Generating schema");
      ApplicationManager.instance()
          .getViewManager()
          .showDialog(
              loading); // This class must extend Thread for the ProgressDialog to be shown
                        // properly.

      elements =
          Configurations.getInstance()
              .getProjectConfigurations()
              .getExportGUFOElementsDiagramTree();

      final BufferedReader script =
          OntoUMLServerAccessController.transformToDB(
              ModelElement.generateModel(elements, true),
              projectConfigurations.getMappingStrategy().toString(),
              projectConfigurations.getTargetDBMS().toString(),
              projectConfigurations.isStandardizeNames());

      loading.canClosed();

      if (script != null) {
        saveFile(script);
        ViewManagerUtils.cleanAndShowMessage("Object-relational mapping performed successfully.");
      } else {
        ViewManagerUtils.cleanAndShowMessage(
            "Unable to generate the files. Please check your model.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      loading.canClosed();
    }
  }

  private void saveFile(BufferedReader buffer) throws IOException {
    final Configurations configs = Configurations.getInstance();
    final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
    final FileDialog fd =
        new FileDialog(
            (Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
            "Choose destination",
            FileDialog.SAVE);

    String suggestedFolderPath = projectConfigurations.getExportFolderPath();
    String exportFormatExtension = ".sql";
    String suggestedFileName =
        ApplicationManager.instance().getProjectManager().getProject().getName()
            + exportFormatExtension;

    fd.setDirectory(suggestedFolderPath);
    fd.setFile(suggestedFileName);
    fd.setVisible(true);

    if (fd.getDirectory() != null && fd.getFile() != null) {
      final String fileDirectory = fd.getDirectory();
      final String fileName =
          !fd.getFile().endsWith(exportFormatExtension)
              ? fd.getFile() + exportFormatExtension
              : fd.getFile();
      final String output = buffer.lines().collect(Collectors.joining("\n"));

      Files.write(Paths.get(fileDirectory, fileName), output.getBytes());
      projectConfigurations.setExportFolderPath(fileDirectory);
      configs.save();
    }
  }
}
