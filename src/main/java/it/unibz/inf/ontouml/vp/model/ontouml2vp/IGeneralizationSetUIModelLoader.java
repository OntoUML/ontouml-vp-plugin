package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getIModelElement;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationSetView;

public class IGeneralizationSetUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, GeneralizationSetView fromView) {
    IModelElement toModelElement = getIModelElement(fromView);

    if (!(toModelElement instanceof IGeneralizationSet)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IGeneralizationSet.class));
      return;
    }

    IGeneralizationSetUIModel toView =
        (IGeneralizationSetUIModel) diagramManager.createDiagramElement(toDiagram, toModelElement);
    fromView.setId(toView.getId());

    toView.setShowConstraints(true);
    toView.setNotation(2);
    toView.resetCaption();

    toView.setX((int) fromView.getX());
    toView.setY((int) fromView.getY());
    toView.setWidth((int) fromView.getWidth());
    toView.setHeight((int) fromView.getHeight());
  }
}
