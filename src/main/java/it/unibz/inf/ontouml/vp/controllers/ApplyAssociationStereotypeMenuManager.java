package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Property;
import it.unibz.inf.ontouml.vp.utils.OntoUMLConstraintsManager;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import it.unibz.inf.ontouml.vp.utils.VPContextUtils;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplyAssociationStereotypeMenuManager extends ApplyStereotypeMenuManager {

  private final ApplyAssociationStereotypeId associationStereotypeId;
  private final Set<IModelElement> elements;
  private final Set<IModelElement> elementsWhereStereotypeIsAllowed;
  private final Set<IModelElement> elementsWhereStereotypeIsAllowedOnlyIfInverted;
  private final boolean hasSomeElementThatRequiresInversion;
  private final boolean allElementsCanReceiveTheStereotype;

  ApplyAssociationStereotypeMenuManager(VPAction action, VPContext context) {
    this.action = action;
    this.context = context;
    this.associationStereotypeId =
        ApplyAssociationStereotypeId.getFromActionId(action.getActionId());

    this.elements = VPContextUtils.getModelElements(context);
    this.elementsWhereStereotypeIsAllowed = getElementsWhereStereotypeIsAllowed(elements);
    this.elementsWhereStereotypeIsAllowedOnlyIfInverted =
        getElementsWhereStereotypeIsAllowedIfInverted(elements);

    this.allElementsCanReceiveTheStereotype =
        elements.stream()
            .allMatch(
                element ->
                    elementsWhereStereotypeIsAllowed.contains(element)
                        || elementsWhereStereotypeIsAllowedOnlyIfInverted.contains(element));

    this.hasSomeElementThatRequiresInversion =
        elementsWhereStereotypeIsAllowedOnlyIfInverted.stream()
            .anyMatch(element -> !elementsWhereStereotypeIsAllowed.contains(element));
  }

  @Override
  public void performAction() {
    boolean shouldProceed = true;

    if (shouldWarnAboutInvertingAssociations())
      shouldProceed = ViewManagerUtils.showInvertAssociationWarningDialog();

    if (!shouldProceed) return;

    elements.stream()
        .map(e -> (IAssociation) e)
        .filter(Association::holdsBetweenClasses)
        .forEach(
            association -> {
              String stereotype = associationStereotypeId.getStereotype();
              StereotypesManager.applyStereotype(association, stereotype);
              IAssociationEnd sourceEnd = !doesRequireInverting(association) ?
                  Association.getSourceEnd(association) : Association.getTargetEnd(association);
              IAssociationEnd targetEnd = !doesRequireInverting(association) ?
                  Association.getTargetEnd(association) : Association.getSourceEnd(association);

              setSourceEndProperties(association, sourceEnd);
              setTargetEndProperties(association, targetEnd);
            });
  }

  private void setTargetEndProperties(IAssociation association, IAssociationEnd targetEnd) {
    if(Association.hasMereologyStereotype(association)) {
      if(!Property.isWholeEnd(targetEnd)) {
        String defaultAggKind = Association.getDefaultAggregationKind(association);
        targetEnd.setAggregationKind(defaultAggKind);
      }
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
    } else {
      targetEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAVIGABLE);
    }

    String targetMultiplicity = targetEnd.getMultiplicity();

    if(targetMultiplicity == null || IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(targetMultiplicity)) {
      String defaultTargetMultiplicity = Association.getDefaultTargetMultiplicity(association);
      targetEnd.setMultiplicity(defaultTargetMultiplicity);
    }

    if (Association.isTargetAlwaysReadOnly(association)) {
      targetEnd.setReadOnly(true);
    }
  }

  private void setSourceEndProperties(IAssociation association, IAssociationEnd sourceEnd) {
    String sourceMultiplicity = sourceEnd.getMultiplicity();

    sourceEnd.setNavigable(IAssociationEnd.NAVIGABLE_UNSPECIFIED);
    sourceEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);

    if(sourceMultiplicity == null || IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(sourceMultiplicity)) {
      String defaultSourceMultiplicity = Association.getDefaultSourceMultiplicity(association);
      sourceEnd.setMultiplicity(defaultSourceMultiplicity);
    }

    if (Association.isSourceAlwaysReadOnly(association)) {
      sourceEnd.setReadOnly(true);
    }
  }

  @Override
  public void update() {
    updateMenuLabel();
    updateMenuEnable();
  }

  private void updateMenuLabel() {
    String label =
        hasSomeElementThatRequiresInversion
            ? associationStereotypeId.getDefaultLabel() + " (inverted)"
            : associationStereotypeId.getDefaultLabel();

    if (associationStereotypeId.isFixed()) action.setLabel(label);
    else if (allElementsCanReceiveTheStereotype) action.setLabel(label);
    else action.setLabel(associationStereotypeId.getDefaultLabel());
  }

  private void updateMenuEnable() {
    if (associationStereotypeId.isFixed()) {
      boolean holdsBetweenClasses =
          Association.holdsBetweenClasses((IAssociation) context.getModelElement());
      action.setEnabled(holdsBetweenClasses);
    } else {
      action.setEnabled(allElementsCanReceiveTheStereotype);
    }
  }

  private Set<IModelElement> getElementsWhereStereotypeIsAllowed(Set<IModelElement> elements) {
    String stereotype = associationStereotypeId.getStereotype();

    return elements.stream()
        .map(e -> (IAssociation) e)
        .filter(Association::holdsBetweenClasses)
        .filter(a -> OntoUMLConstraintsManager.isStereotypeAllowed(a, stereotype))
        .collect(Collectors.toSet());
  }

  private Set<IModelElement> getElementsWhereStereotypeIsAllowedIfInverted(
      Set<IModelElement> elements) {
    String stereotype = associationStereotypeId.getStereotype();

    return elements.stream()
        .map(e -> (IAssociation) e)
        .filter(Association::holdsBetweenClasses)
        .filter(a -> OntoUMLConstraintsManager.isStereotypeAllowedIfInverted(a, stereotype))
        .collect(Collectors.toSet());
  }

  private boolean shouldWarnAboutInvertingAssociations() {
    return elements.stream().anyMatch(element -> doesRequireInverting((IAssociation) element));
  }

  private boolean doesRequireInverting(IAssociation association) {
    return elementsWhereStereotypeIsAllowedOnlyIfInverted.contains(association)
        && !elementsWhereStereotypeIsAllowed.contains(association);
  }
}
