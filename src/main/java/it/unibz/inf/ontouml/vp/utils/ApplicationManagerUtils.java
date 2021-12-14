package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

public class ApplicationManagerUtils {

  public static ApplicationManager getApplicationManager() {
    return ApplicationManager.instance();
  }

  public static ProjectManager getProjectManager() {
    return getApplicationManager().getProjectManager();
  }

  public static DiagramManager getDiagramManager() {
    return getApplicationManager().getDiagramManager();
  }

  public static IProject getCurrentProject() {
    return getProjectManager().getProject();
  }

  public static IDiagramUIModel getActiveDiagram() {
    return getDiagramManager().getActiveDiagram();
  }
}
