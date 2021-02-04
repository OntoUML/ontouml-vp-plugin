package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.AggregationKind;
import it.unibz.inf.ontouml.vp.model.ontouml.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Property;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IPropertyTransformer {

  public static Property transform(IModelElement source) {
    if (source instanceof IAttribute) {
      return transform(new IPropertyAdapter((IAttribute) source));
    }

    if (source instanceof IAssociationEnd) {
      return transform(new IPropertyAdapter((IAssociationEnd) source));
    }

    return null;
  }

  public static Property transform(IPropertyAdapter source) {
    if (source.isEmpty()) {
      return null;
    }

    Property target = new Property();

    IModelElementTransformer.transform(source.get(), target);
    ITaggedValueTransformer.transform(source.get(), target);
    IStereotypeTransformer.transform(source.get(), target);

    boolean isDerived = source.isDerived();
    target.setDerived(isDerived);

    boolean isReadOnly = source.isReadOnly();
    target.setReadOnly(isReadOnly);

    boolean isOrdered = source.isOrdered();
    target.setOrdered(isOrdered);

    String cardinality = source.getMultiplicity();
    target.setCardinality(cardinality);

    AggregationKind aggregationKind = transformAggregationKind(source);
    target.setAggregationKind(aggregationKind);

    Classifier<?, ?> propertyType = transformPropertyType(source);
    target.setPropertyType(propertyType);

    List<Property> subsetted = transformSubsettedProperties(source);
    target.setSubsettedProperties(subsetted);

    List<Property> redefined = transformRedefinedProperties(source);
    target.setRedefinedProperties(redefined);

    return target;
  }

  private static AggregationKind transformAggregationKind(IPropertyAdapter property) {
    return AggregationKind.findByName(property.getAggregationKind())
        .orElseGet(() -> property.isAssociationEnd() ? AggregationKind.NONE : null);
  }

  private static Classifier<?, ?> transformPropertyType(IPropertyAdapter property) {
    IModelElement sourcePropertyType = property.getTypeAsElement();

    if (sourcePropertyType == null) {
      return null;
    }

    OntoumlElement targetType = ReferenceTransformer.transformStub(sourcePropertyType);
    return targetType instanceof Classifier<?, ?> ? (Classifier<?, ?>) targetType : null;
  }

  private static List<Property> transformSubsettedProperties(IPropertyAdapter property) {
    return transformProperties(property.subsettedPropertyIterator());
  }

  private static List<Property> transformRedefinedProperties(IPropertyAdapter property) {
    return transformProperties(property.redefinedPropertyIterator());
  }

  private static List<Property> transformProperties(Iterator<?> iterator) {
    List<Property> targetProperties = new ArrayList<>();

    while (iterator.hasNext()) {
      Object source = iterator.next();

      if (!isProperty(source)) {
        continue;
      }

      OntoumlElement target = ReferenceTransformer.transformStub((IModelElement) source);
      targetProperties.add((Property) target);
    }

    return targetProperties;
  }

  public static boolean isProperty(Object object) {
    return object instanceof IAttribute || object instanceof IAssociationEnd;
  }
}
