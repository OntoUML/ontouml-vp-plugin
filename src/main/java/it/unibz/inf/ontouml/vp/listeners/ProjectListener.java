package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.controllers.GitHubAccessController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GitHubRelease;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.time.ZonedDateTime;
import java.util.List;

public class ProjectListener implements IProjectListener {

  private ProjectModelListener projectModelListener;
  private ProjectDiagramListener projectDiagramListener;

  public ProjectListener() {
    projectModelListener = new ProjectModelListener();
    projectDiagramListener = new ProjectDiagramListener();
    addListeners();
  }

  private void addListeners() {
    try {
      final IProject project = ApplicationManager.instance().getProjectManager().getProject();

      project.addProjectModelListener(projectModelListener);
      project.addProjectDiagramListener(projectDiagramListener);

      projectModelListener.addListenersToModelElements();
      projectDiagramListener.addListenersToDiagrams();
    } catch (Exception e) {
      System.err.println("An error ocurred while adding listeners to the project.");
      e.printStackTrace();
    }
  }

  private void generateStereotypes() {
    try {
      StereotypesManager.generate();
    } catch (Exception e) {
      System.err.println("An error ocurred while generating stereotypes to the project.");
      e.printStackTrace();
    }
  }

  private void checkUpdates() {
    try {
      Configurations config = Configurations.getInstance();
      ZonedDateTime lastCheck = config.getLastUpdatesCheck();

      if (lastCheck != null && lastCheck.plusDays(1).isBefore(ZonedDateTime.now())) {
        GitHubAccessController.lookupUpdates();
        List<GitHubRelease> releases = config.getReleases();
        GitHubRelease latestRelease = config.getLatestRelease();
        boolean upToDate = false;

        for (GitHubRelease release : releases) {
          if (release.getTagName().equals(OntoUMLPlugin.PLUGIN_VERSION_RELEASE)
              && release.getCreatedAt().isBefore(latestRelease.getCreatedAt())) {
            upToDate = true;
          }
        }

        if (!upToDate) {
          System.out.println("New updates are available.");
          ViewManagerUtils.simpleLog(
              "New updates are available. Go to \"Update Plugin\" to get the latest version of the"
                  + " OntoUML Plugin for Visual Paradigm.");
        } else {
          System.out.println("No new updates available.");
          ViewManagerUtils.simpleLog(
              "Your OntoUML Plugin for Visual Paradigm is up to date with our latest release.");
        }
      } else {
        System.out.println("Last check for updates was already performed in the last 24 hours.");
      }

      config.setLastUpdatesCheck(ZonedDateTime.now());

    } catch (Exception e) {
      System.out.println("Failed to get releases from GitHub");
      e.printStackTrace();
    }
  }

  @Override
  public void projectAfterOpened(IProject project) {
    System.out.println("Project Listenner - After Opened");
    try {
      if (project == null) {
        return;
      }

      addListeners();
      generateStereotypes();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void projectNewed(IProject project) {
    System.out.println("Project Listenner - Newed");
    try {
      if (project == null) {
        return;
      }

      addListeners();
      generateStereotypes();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void projectOpened(IProject project) {
    System.out.println("Project Listenner - Opened");
    checkUpdates();
    generateStereotypes();
  }

  @Override
  public void projectPreSave(IProject project) {}

  @Override
  public void projectRenamed(IProject project) {}

  @Override
  public void projectSaved(IProject project) {
    checkUpdates();
  }
}
