package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationView;

public class IGeneralizationUIModelTransformer {
  public static GeneralizationView transform(IDiagramElement sourceElement) {
    if (!(sourceElement instanceof IGeneralizationUIModel)) return null;

    IGeneralizationUIModel source = (IGeneralizationUIModel) sourceElement;
    GeneralizationView target = new GeneralizationView();

    IDiagramElementTransformer.transform(source, target, Generalization.class);
    IConnectorTransformer.transform(source, target);

    return target;
  }
}
