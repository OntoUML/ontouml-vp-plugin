package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ProjectListener implements IProjectListener {

  private ProjectModelListener projectModelListener;
  private ProjectDiagramListener projectDiagramListener;

  public ProjectListener() {
    projectModelListener = new ProjectModelListener();
    projectDiagramListener = new ProjectDiagramListener();
    addListeners();
    StereotypeUtils.generate();
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

  @Override
  public void projectAfterOpened(IProject project) {
    try {
      if (project == null) {
        return;
      }

      addListeners();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void projectNewed(IProject project) {
    try {
      if (project == null) {
        return;
      }

      addListeners();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void projectOpened(IProject project) {}

  @Override
  public void projectPreSave(IProject project) {}

  @Override
  public void projectRenamed(IProject project) {}

  @Override
  public void projectSaved(IProject project) {}
}
