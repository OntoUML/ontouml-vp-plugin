package it.unibz.inf.ontouml.vp.model.uml;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IBaseDiagramElement;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModelComment;
import com.vp.plugin.model.IModelElement;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Diagram {

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
    return getSelectedDiagramElements(diagram).stream()
        .map(IBaseDiagramElement::getModelElement)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  public static Set<IModelElement> getSelectedModelElements(
      IDiagramUIModel diagram, String modelType) {
    return Diagram.getSelectedModelElements(diagram).stream()
        .filter(me -> me.getModelType().equals(modelType))
        .collect(Collectors.toSet());
  }

  // TODO: check whether it makes sense to use this function name to avoid null diagrams
  public static boolean isOntoUMLDiagram(IDiagramUIModel diagram) {
    return getComments(diagram).stream().anyMatch(DiagramComment::isOntoumlDiagramComment);
  }

  public static void setOntoumlDiagram(IDiagramUIModel diagram) {
    if (isOntoUMLDiagram(diagram)) return;

    IDiagramUIModelComment comment = diagram.createComment();
    DiagramComment.setOntoumlDiagramComment(comment);
  }

  public static void unsetOntoumlDiagram(IDiagramUIModel diagram) {
    if (!isOntoUMLDiagram(diagram)) return;

    getComments(diagram).stream()
        .filter(DiagramComment::isOntoumlDiagramComment)
        .forEach(diagram::removeComment);
  }

  public static boolean haveComments(IDiagramUIModel diagram) {
    return diagram.commentCount() > 0;
  }

  public static Set<IDiagramUIModelComment> getComments(IDiagramUIModel diagram) {
    return haveComments(diagram) ? Set.of(diagram.toCommentArray()) : Collections.emptySet();
  }

  public static boolean containsOntoumlElements(IDiagramUIModel diagram) {
    return Optional.ofNullable(diagram.toDiagramElementArray()).map(Set::of)
        .orElse(Collections.emptySet()).stream()
        .anyMatch(ModelElement::isOntoumlElement);
  }

  public static Set<IDiagramElement> getDiagramElements(IDiagramUIModel diagram) {
    return Optional.ofNullable(diagram.toDiagramElementArray()).map(Set::of)
        .orElse(Collections.emptySet());
  }
}
