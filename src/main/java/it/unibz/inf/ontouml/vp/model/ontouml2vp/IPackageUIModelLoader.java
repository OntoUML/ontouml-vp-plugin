package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getIModelElement;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.view.PackageView;

public class IPackageUIModelLoader {

  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(IClassDiagramUIModel toDiagram, PackageView fromView) {
    IModelElement toModelElement = getIModelElement(fromView);

    if (!(toModelElement instanceof IPackage) && !(toModelElement instanceof IModel)) {
      System.out.println(
          LoaderUtils.getIncompatibleMessage(fromView, toModelElement, IClass.class));
      return;
    }

    IShapeUIModel toView =
        (IShapeUIModel) diagramManager.createDiagramElement(toDiagram, toModelElement);
    fromView.setId(toView.getId());

    toView.resetCaption();

    toView.setX(fromView.getX());
    toView.setY(fromView.getY());
    toView.setWidth(fromView.getWidth());
    toView.setHeight(fromView.getHeight());
  }
}
