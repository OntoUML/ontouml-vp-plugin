package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Property;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import java.util.List;

public class IAssociationTransformer {
  public static ModelElement transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IAssociation)) return null;

    IAssociation source = (IAssociation) sourceElement;
    Relation target = new Relation();

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);
    IStereotypeTransformer.transform(source, target);

    boolean isDerived = isDerived(source);
    target.setDerived(isDerived);

    boolean isAbstract = source.isAbstract();
    target.setAbstract(isAbstract);

    Property sourceEnd = IPropertyTransformer.transform(getSourceEnd(source));
    Property targetEnd = IPropertyTransformer.transform(getTargetEnd(source));
    target.setProperties(List.of(sourceEnd, targetEnd));

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
}
