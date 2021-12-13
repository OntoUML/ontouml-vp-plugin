package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.Diagram;
import it.unibz.inf.ontouml.vp.utils.SmartColoringUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class DiagramListener implements IDiagramListener {

  public DiagramListener() {}

  @Override
  public void diagramElementAdded(IDiagramUIModel diagram, IDiagramElement shape) {
    try {
      runSmartPainting(shape);
      runOntoumlDiagramCheck(shape);
    } catch (Exception e) {
      System.err.println("An error occurred while adding an element to diagram.");
      e.printStackTrace();
    }
  }

  @Override
  public void diagramElementRemoved(IDiagramUIModel diagram, IDiagramElement shape) {
    try {
      runOntoumlDiagramCheck(diagram);
    } catch (Exception e) {
      System.err.println("An error occurred while removing an element from diagram.");
      e.printStackTrace();
    }
  }

  @Override
  public void diagramUIModelLoaded(IDiagramUIModel diagram) {
    try {
      runSmartPainting(diagram);
    } catch (Exception e) {
      System.err.println("An error occurred while adding an element to diagram.");
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

  private void runSmartPainting(IDiagramElement diagramElement) {
    if (isClassView(diagramElement) && isPaintingEnabled()) {
      paint((IClassUIModel) diagramElement);
    }
  }

  private void runSmartPainting(IDiagramUIModel diagram) {
    if (isDiagram(diagram) && isPaintingEnabled()) {
      Diagram.getDiagramElements(diagram).stream()
          .filter(this::isClassView)
          .forEach(e -> paint((IClassUIModel) e));
    }
  }

  private void paint(IClassUIModel element) {
    SmartColoringUtils.paint(element);
  }

  private boolean isClassView(Object element) {
    return element instanceof IClassUIModel;
  }

  private boolean isDiagram(Object diagram) {
    return diagram instanceof IDiagramUIModel;
  }

  private boolean isPaintingEnabled() {
    return Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled();
  }

  private void runOntoumlDiagramCheck(IDiagramElement element) {
    if (isOntoumlDiagramElement(element) && !isInOntoumlDiagram(element)) {
      IDiagramUIModel diagram = element.getDiagramUIModel();
      Diagram.setOntoumlDiagram(diagram);
    }
  }

  private void runOntoumlDiagramCheck(IDiagramUIModel diagram) {
    if (containsOntoumlElements(diagram) && !Diagram.isOntoUMLDiagram(diagram)) {
      Diagram.setOntoumlDiagram(diagram);
    } else if (!containsOntoumlElements(diagram) && Diagram.isOntoUMLDiagram(diagram)) {
      Diagram.unsetOntoumlDiagram(diagram);
    }
  }

  private boolean containsOntoumlElements(IDiagramUIModel diagram) {
    var iter = diagram.diagramElementIterator();

    while (iter.hasNext()) {
      IDiagramElement element = (IDiagramElement) iter.next();
      if (isOntoumlDiagramElement(element)) return true;
    }

    return false;
  }

  private boolean isOntoumlDiagramElement(IDiagramElement element) {
    if (isAssociationDiagramElement(element)) {
      IAssociation association = (IAssociation) element.getModelElement();
      return Association.isOntoumlAssociation(association);
    } else if (isClassView(element)) {
      IClass _class = (IClass) element.getModelElement();
      return Class.isOntoumlClass(_class);
    }
    return false;
  }

  private boolean isAssociationDiagramElement(Object element) {
    return element instanceof IAssociationUIModel;
  }

  private boolean isInOntoumlDiagram(IDiagramElement element) {
    return Diagram.isOntoUMLDiagram(element.getDiagramUIModel());
  }
}
