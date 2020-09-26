package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectDiagramListener;
import java.util.Iterator;

public class ProjectDiagramListener implements IProjectDiagramListener {

  private DiagramListener diagramListener;

  public ProjectDiagramListener() {
    diagramListener = new DiagramListener();
  }

  @Override
  public void diagramAdded(IProject project, IDiagramUIModel diagram) {
    try {
      addListenerToDiagram(diagram);
    } catch (Exception e) {
      System.err.println("An error ocurred while adding diagram.");
      e.printStackTrace();
    }
  }

  @Override
  public void diagramRemoved(IProject project, IDiagramUIModel diagram) {
    try {
      removeListenerFromDiagram(diagram);
    } catch (Exception e) {
      System.err.println("An error ocurred while removing diagram.");
      e.printStackTrace();
    }
  }

  private void addListenerToDiagram(IDiagramUIModel diagram) {
    if (diagram != null) {
      diagram.addDiagramListener(diagramListener);
    }
  }

  private void removeListenerFromDiagram(IDiagramUIModel diagram) {
    if (diagram != null) {
      diagram.removeDiagramListener(diagramListener);
    }
  }

  public void addListenersToDiagrams() {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final Iterator<?> iter = project.diagramIterator();

    while (iter != null && iter.hasNext()) {
      final IDiagramUIModel diagram = (IDiagramUIModel) iter.next();
      diagram.addDiagramListener(diagramListener);
    }
  }
}
