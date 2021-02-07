package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IClassUIModel;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;

public class IClassUIModelTransformer {
  public static ClassView transform(IDiagramElement sourceElement) {
    if (!(sourceElement instanceof IClassUIModel)) return null;

    IClassUIModel source = (IClassUIModel) sourceElement;
    ClassView target = new ClassView();

    IDiagramElementTransformer.transform(source, target, Class.class);
    IShapeTransformer.transform(source, target);

    return target;
  }
}
