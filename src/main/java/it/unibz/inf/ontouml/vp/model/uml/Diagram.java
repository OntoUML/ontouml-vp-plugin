package it.unibz.inf.ontouml.vp.model.uml;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IBaseDiagramElement;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Diagram {

  public static Set<IDiagramElement> getSelectedDiagramElements() {
    final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
    final IDiagramUIModel diagram = dm.getActiveDiagram();

    return getSelectedDiagramElements(diagram);
  }

  public static Set<IDiagramElement> getSelectedDiagramElements(IDiagramUIModel diagram) {
    final IDiagramElement[] selectedElementsArray =
        diagram != null ? diagram.getSelectedDiagramElement() : null;

    return selectedElementsArray != null ? Set.of(selectedElementsArray) : Collections.emptySet();
  }

  public static Set<IModelElement> getSelectedModelElements() {
    final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
    final IDiagramUIModel diagram = dm.getActiveDiagram();

    return getSelectedModelElements(diagram);
  }

  public static Set<IModelElement> getSelectedModelElements(IDiagramUIModel diagram) {
    return getSelectedDiagramElements(diagram)
        .stream()
        .map(IBaseDiagramElement::getModelElement)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  public static Set<IModelElement> getSelectedModelElements(String modelType) {
    final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
    final IDiagramUIModel diagram = dm.getActiveDiagram();

    return getSelectedModelElements(diagram, modelType);
  }

  public static Set<IModelElement> getSelectedModelElements(IDiagramUIModel diagram, String modelType) {
    return Diagram.getSelectedModelElements()
        .stream()
        .filter(me -> me.getModelType().equals(modelType))
        .collect(Collectors.toSet());
  }

}
