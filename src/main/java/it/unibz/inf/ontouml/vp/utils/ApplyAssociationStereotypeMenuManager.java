package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
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
    this.associationStereotypeId = ApplyAssociationStereotypeId.getFromActionId(action.getActionId());

    this.elements = VPContextUtils.getModelElements(context);
    this.elementsWhereStereotypeIsAllowed = getElementsWhereStereotypeIsAllowed(elements);
    this.elementsWhereStereotypeIsAllowedOnlyIfInverted = getElementsWhereStereotypeIsAllowedIfInverted(elements);

    this.allElementsCanReceiveTheStereotype = elements.stream().allMatch(element ->
        elementsWhereStereotypeIsAllowed.contains(element)
        || elementsWhereStereotypeIsAllowedOnlyIfInverted.contains(element));

    this.hasSomeElementThatRequiresInversion = elementsWhereStereotypeIsAllowedOnlyIfInverted
        .stream().anyMatch(element -> !elementsWhereStereotypeIsAllowed.contains(element));
  }

  @Override
  public void performAction() {
    boolean shouldProceed = true;

    if(shouldWarnAboutInvertingAssociations()) {
      shouldProceed = ViewManagerUtils.associationInvertionWarningDialog();
    }

    if(!shouldProceed) {
      return ;
    }

    elements.forEach(element -> {
      IAssociation association = (IAssociation) element;

      if(doesRequireInverting(association)) {
        Association.invertAssociation(association,true);
      }

      StereotypesManager.applyStereotype(association,associationStereotypeId.getStereotype());
    });
  }

  @Override
  public void update() {
    updateLabel();
    updateEnabling();
  }

  private void updateLabel() {
    String invertLabel = hasSomeElementThatRequiresInversion ?
        associationStereotypeId.getDefaultLabel() + " (inverted)" :
        associationStereotypeId.getDefaultLabel();

    if(associationStereotypeId.isFixed()) {
      action.setLabel(invertLabel);
    } else if(allElementsCanReceiveTheStereotype) {
      action.setLabel(invertLabel);
    } else {
      action.setLabel(associationStereotypeId.getDefaultLabel());
    }
  }

  private void updateEnabling() {
    if(!associationStereotypeId.isFixed()) {
      action.setEnabled(allElementsCanReceiveTheStereotype);
    }
  }

  private Set<IModelElement> getElementsWhereStereotypeIsAllowed(Set<IModelElement> elements) {
    String stereotype = associationStereotypeId.getStereotype();

    return elements.stream()
        .filter(element -> OntoUMLConstraintsManager.isStereotypeAllowed((IAssociation) element, stereotype))
        .collect(Collectors.toSet());
  }

  private Set<IModelElement> getElementsWhereStereotypeIsAllowedIfInverted(Set<IModelElement> elements) {
    String stereotype = associationStereotypeId.getStereotype();

    return elements.stream()
        .filter(element -> OntoUMLConstraintsManager.isStereotypeAllowedIfInverted((IAssociation) element, stereotype))
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
