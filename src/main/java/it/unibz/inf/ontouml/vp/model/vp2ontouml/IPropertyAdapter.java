package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IMultiplicity;
import java.util.Iterator;

public class IPropertyAdapter implements IAdapter {

  IAttribute attribute;
  IAssociationEnd associationEnd;

  public IPropertyAdapter(IAttribute attribute) {
    this.attribute = attribute;
  }

  public IPropertyAdapter(IAssociationEnd associationEnd) {
    this.associationEnd = associationEnd;
  }

  @Override
  public boolean isEmpty() {
    return attribute == null && associationEnd == null;
  }

  @Override
  public IModelElement get() {
    if (isAttribute()) return attribute;
    if (isAssociationEnd()) return associationEnd;
    return null;
  }

  public boolean isAttribute() {
    return attribute != null;
  }

  public boolean isAssociationEnd() {
    return associationEnd != null;
  }

  public IMultiplicity getMultiplicityDetail() {
    return isAttribute()
        ? attribute.getMultiplicityDetail()
        : associationEnd.getMultiplicityDetail();
  }

  public boolean isOrdered() {
    IMultiplicity multiplicity = getMultiplicityDetail();
    return multiplicity != null && multiplicity.isOrdered();
  }

  public boolean isReadOnly() {
    if (isAttribute()) return attribute.isReadOnly();
    if (isAssociationEnd()) return associationEnd.isReadOnly();
    return false;
  }

  public boolean isDerived() {
    if (isAttribute()) return attribute.isDerived();
    if (isAssociationEnd()) return associationEnd.isDerived();
    return false;
  }

  public String getMultiplicity() {
    String multiplicity = null;
    if (isAttribute()) multiplicity = attribute.getMultiplicity();
    if (isAssociationEnd()) multiplicity = associationEnd.getMultiplicity();

    return "Unspecified".equals(multiplicity) ? null : multiplicity;
  }

  public String getAggregationKind() {
    if (isAttribute()) {
      int value = attribute.getAggregation();
      switch (value) {
        case 0:
          return "NONE";
        case 1:
          return "SHARED";
        case 2:
          return "COMPOSITE";
      }
    }

    if (isAssociationEnd()) {
      return associationEnd.getAggregationKind();
    }

    return null;
  }

  public IModelElement getTypeAsElement() {
    if (isAttribute()) return attribute.getTypeAsElement();
    if (isAssociationEnd()) return associationEnd.getTypeAsElement();

    return null;
  }

  public Iterator<?> subsettedPropertyIterator() {
    if (isAttribute()) return attribute.subsettedPropertyIterator();
    if (isAssociationEnd()) return associationEnd.subsettedPropertyIterator();
    return null;
  }

  public Iterator<?> redefinedPropertyIterator() {
    if (isAttribute()) return attribute.redefinedPropertyIterator();
    if (isAssociationEnd()) return associationEnd.redefinedPropertyIterator();
    return null;
  }
}
