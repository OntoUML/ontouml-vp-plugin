package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;

public class IAssociationClassTransformer {
  public static ModelElement transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IAssociationClass)) return null;

    IAssociationClass source = (IAssociationClass) sourceElement;

    Classifier<?, ?> fromClassifier = createClassifierStub(source.getFrom());
    Classifier<?, ?> toClassifier = createClassifierStub(source.getTo());

    Relation target = Relation.createDerivation(null, null, fromClassifier, toClassifier);

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);
    IStereotypeTransformer.transform(source, target);

    target.setDerived(true);
    target.setAbstract(false);

    return target;
  }

  private static IAssociationEnd getSourceEnd(IAssociation association) {
    return (IAssociationEnd) association.getFromEnd();
  }

  private static IAssociationEnd getTargetEnd(IAssociation association) {
    return (IAssociationEnd) association.getToEnd();
  }

  private static boolean isDerived(IAssociation association) {
    return association.isDerived()
        || getSourceEnd(association).isDerived()
        || getTargetEnd(association).isDerived();
  }

  private static Classifier<?, ?> createClassifierStub(IModelElement classifier) {
    OntoumlElement targetType = ReferenceTransformer.transformStub(classifier);
    return targetType instanceof Classifier<?, ?> ? (Classifier<?, ?>) targetType : null;
  }
}
