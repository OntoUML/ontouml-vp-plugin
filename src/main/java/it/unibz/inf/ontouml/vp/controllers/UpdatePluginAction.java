package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
// import net.lingala.zip4j.core.ZipFile;
// import net.lingala.zip4j.exception.ZipException;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UpdatePluginAction implements VPActionController {

  private static final int BUFFER_SIZE = 4096;

  @Override
  public void performAction(VPAction arg0) {
    ViewUtils.updateDialog();

    FileDialog fd =
        new FileDialog(
            (Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
            "Select the zip file containing the OntoUML Plugin release",
            FileDialog.LOAD);

    fd.setDirectory(null);
    fd.setFile(null);
    fd.setFilenameFilter((dir, name) -> name != null && name.endsWith(".zip"));
    fd.setVisible(true);

    if (fd.getDirectory() != null && fd.getFile() != null) {
      final String fileName = fd.getFile();
      final VPPluginInfo info =
          ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID);
      final File pluginsDir = info.getPluginDir().getParentFile();
      final File zipFile = new File(fd.getDirectory(), fileName);
      final String destDirName = fileName.substring(0, fileName.lastIndexOf(".zip"));
      try {
        unzip(zipFile, pluginsDir.getAbsolutePath() + File.separator + destDirName);
        deleteFolderContents(
            pluginsDir,
            content ->
                content.isDirectory()
                    && content.getName().contains("ontouml")
                    && !content.getName().equals("ontouml-vp-plugin-0.3.0-SNAPSHOT"));
        ViewUtils.updateSuccessDialog();
      } catch (Exception e) {
        ViewUtils.updateErrorDialog();
        e.printStackTrace();
      }
    }
  }

  @Override
  public void update(VPAction arg0) {}

  public void unzip(File zipFile, String destDirectory) throws IOException {
    File destDir = new File(destDirectory);
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
    ZipEntry entry = zipIn.getNextEntry();
    // iterates over entries in the zip file
    while (entry != null) {
      String filePath = destDirectory + File.separator + entry.getName();
      if (!entry.isDirectory()) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);
      } else {
        // if the entry is a directory, make the directory
        File dir = new File(filePath);
        dir.mkdir();
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();
  }

  private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

  private void deleteFolderContents(File folder, Predicate<File> condition) {
    final File[] contents = folder.listFiles();

    if (contents == null) {
      return;
    }

    for (File content : contents) {
      if (condition == null || condition.test(content)) {
        if (content.isDirectory()) {
          deleteFolderContents(content, null);
        }
        content.delete();
      }
    }
  }
}
