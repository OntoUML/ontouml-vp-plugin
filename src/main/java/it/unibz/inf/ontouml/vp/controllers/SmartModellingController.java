package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.utils.Stereotype;

public class SmartModellingController {

  public static void setAggregationKind(IModelElement element) {
    final IAssociation association = (IAssociation) element;
    final IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();
    final IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
    final String toAgg = toEnd.getAggregationKind().toLowerCase();

    if (IAssociationEnd.AGGREGATION_KIND_none.equals(toAgg)) {
      toEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_composite);
    }

    fromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
  }

  public static void removeAggregationKind(IModelElement element) {
    IAssociationEnd compositionFromEnd = (IAssociationEnd) ((IAssociation) element).getFromEnd();
    IAssociationEnd compositionToEnd = (IAssociationEnd) ((IAssociation) element).getToEnd();

    compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
    compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
  }

  private static boolean setCardinalityIfEmpty(IAssociationEnd end, String cardinality) {

    if (end.getMultiplicity() == null || end.getMultiplicity().equals("Unspecified")) {
      end.setMultiplicity(cardinality);
      return true;
    } else {
      return false;
    }
  }

  public static void setAssociationMetaProperties(IAssociation association) {
    Association.setDefaultMultiplicity(association, false);
    Association.setDefaultAggregationKind(association, false);
    Association.setNavigability(association);
  }

  public static void setClassMetaProperties(IClass _class) {
    if (_class == null) return;

    String[] stereotypes = _class.toStereotypeArray();

    if (stereotypes == null || stereotypes.length != 1) return;

    switch (stereotypes[0]) {
      case Stereotype.CATEGORY:
      case Stereotype.ROLE_MIXIN:
      case Stereotype.PHASE_MIXIN:
      case Stereotype.MIXIN:
        _class.setAbstract(true);
        break;
    }
  }
}
