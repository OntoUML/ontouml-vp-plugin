package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoringUtils;
import java.util.Iterator;

public class DiagramListener implements IDiagramListener {

  public DiagramListener() {}

  @Override
  public void diagramElementAdded(IDiagramUIModel diagram, IDiagramElement shape) {
    try {
      smartPaint(shape);
    } catch (Exception e) {
      System.err.println("An error ocurred while adding an element to diagram.");
      e.printStackTrace();
    }
  }

  @Override
  public void diagramElementRemoved(IDiagramUIModel diagram, IDiagramElement shape) {}

  @Override
  public void diagramUIModelLoaded(IDiagramUIModel diagram) {
    try {
      smartPaint(diagram);
    } catch (Exception e) {
      System.err.println("An error ocurred while adding an element to diagram.");
      e.printStackTrace();
    }
  }

  @Override
  public void diagramUIModelPropertyChanged(
      IDiagramUIModel diagram,
      String propertyName,
      Object originalProperty,
      Object modifiedProperty) {}

  @Override
  public void diagramUIModelRenamed(IDiagramUIModel diagram) {}

  private void smartPaint(IDiagramElement diagramElement) {
    if (diagramElement != null
        && diagramElement instanceof IClassUIModel
        && Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
      SmartColoringUtils.paint((IClassUIModel) diagramElement);
    }
  }

  private void smartPaint(IDiagramUIModel diagram) {
    if (diagram != null
        && Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
      final Iterator<?> iter = diagram.diagramElementIterator();
      while (iter != null && iter.hasNext()) {
        final IDiagramElement next = (IDiagramElement) iter.next();

        if (next instanceof IClassUIModel) {
          SmartColoringUtils.paint((IClassUIModel) next);
        }
      }
    }
  }
}
