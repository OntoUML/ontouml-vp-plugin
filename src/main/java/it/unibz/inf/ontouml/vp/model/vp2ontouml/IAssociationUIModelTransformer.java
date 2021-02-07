package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;

public class IAssociationUIModelTransformer {
  public static RelationView transform(IDiagramElement sourceElement) {

    if (!(sourceElement instanceof IAssociationUIModel)) return null;

    IAssociationUIModel source = (IAssociationUIModel) sourceElement;
    RelationView target = new RelationView();

    IDiagramElementTransformer.transform(source, target, Relation.class);
    IConnectorTransformer.transform(source, target);

    return target;
  }
}
