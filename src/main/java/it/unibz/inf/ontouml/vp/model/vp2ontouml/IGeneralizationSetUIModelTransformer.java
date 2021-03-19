package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import it.unibz.inf.ontouml.vp.model.ontouml.model.GeneralizationSet;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationSetView;

public class IGeneralizationSetUIModelTransformer {

  public static GeneralizationSetView transform(IDiagramElement sourceElement) {
    if (!(sourceElement instanceof IGeneralizationSetUIModel)) return null;

    IGeneralizationSetUIModel source = (IGeneralizationSetUIModel) sourceElement;
    GeneralizationSetView target = new GeneralizationSetView();

    IDiagramElementTransformer.transform(source, target, GeneralizationSet.class);
    IShapeTransformer.transform(source, target);

    return target;
  }
}
