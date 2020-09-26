package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ActionIdManager;
import it.unibz.inf.ontouml.vp.utils.OntoUMLConstraintsManager;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyStereotypeController implements VPContextActionController {

  @Override
  public void performAction(VPAction action, VPContext context, ActionEvent event) {
    final IModelElement clickedElement = context.getModelElement();
    final String clickedElementType = clickedElement.getModelType();

    if (action.getActionId().contains("fixedMenu")) {
      ModelElement.forEachSelectedElement(
          clickedElement, selectedElement -> applyStereotype(action, selectedElement));
      return;
    }

    switch (clickedElementType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        final IAssociation clickedAssociation = (IAssociation) clickedElement;
        final IClass associationSource = Association.getSource(clickedAssociation);
        final IClass associationTarget = Association.getTarget(clickedAssociation);

        if (isStereotypeActionAllowed(action.getActionId(), associationSource, associationTarget)) {
          applyStereotype(action, clickedAssociation);
        } else {
          final boolean shouldProceed = ViewManagerUtils.associationInvertionWarningDialog();

          if (shouldProceed) {
            Association.invertAssociation(clickedAssociation);
            applyStereotype(action, clickedAssociation);
          }
        }
        return;
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
      case IModelElementFactory.MODEL_TYPE_CLASS:
        applyStereotype(action, clickedElement);
        return;
      default:
        throw new UnsupportedOperationException("Unexpected element of type " + clickedElementType);
    }
  }

  @Override
  public void update(VPAction action, VPContext context) {
    if (action.getActionId().contains("fixedMenu")) {
      action.setEnabled(true);
      return;
    }

    final IModelElement clickedElement = context.getModelElement();
    final String clickedElementType = clickedElement != null ? clickedElement.getModelType() : "";

    switch (clickedElementType) {
      case IModelElementFactory.MODEL_TYPE_CLASS:
        action.setEnabled(isStereotypeActionAllowed(action.getActionId(), (IClass) clickedElement));
        break;
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        final String actionId = action.getActionId();
        final IAssociation clickedAssociation = (IAssociation) clickedElement;
        final IClass source = Association.getSource(clickedAssociation);
        final IClass target = Association.getTarget(clickedAssociation);
        final boolean isActionAllowedOnAssociation =
            isStereotypeActionAllowed(actionId, source, target);
        final boolean isActionAllowedOnInvertedAssociation =
            isStereotypeActionAllowed(actionId, target, source);

        action.setLabel(ActionIdManager.getActionLabelById(actionId));

        if (isActionAllowedOnAssociation) {
          action.setEnabled(true);
        } else if (isActionAllowedOnInvertedAssociation) {
          action.setEnabled(true);
          action.setLabel(action.getLabel() + " (reverse direction)");
        } else {
          action.setEnabled(false);
        }
        break;
      default:
        throw new UnsupportedOperationException("Unexpected element of type " + clickedElementType);
    }
  }

  private boolean isStereotypeActionAllowed(String actionId, IClass _class) {
    final Set<IClass> children = Class.getChildren(_class);

    for (IClass child : children) {
      final List<String> allowedActions =
          OntoUMLConstraintsManager.getAllowedActionsBasedOnChild(child);
      if (!allowedActions.contains(actionId)) {
        return false;
      }
    }

    final Set<IClass> parents = Class.getParents(_class);

    for (IClass parent : parents) {
      final List<String> allowedActions =
          OntoUMLConstraintsManager.getAllowedActionsBasedOnParent(parent);
      if (!allowedActions.contains(actionId)) {
        return false;
      }
    }

    return true;
  }

  private boolean isStereotypeActionAllowed(
      String actionId, IClass associationSource, IClass associationTarget) {
    final List<String> allowedActions =
        OntoUMLConstraintsManager.getAllowedActionsBasedOnSourceAndTarget(
            associationSource, associationTarget);

    return allowedActions.contains(actionId);
  }

  private void applyStereotype(VPAction action, IModelElement element) {
    switch (action.getActionId()) {
      case ActionIdManager.TYPE:
      case ActionIdManager.TYPE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.TYPE);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.HISTORICAL_ROLE:
      case ActionIdManager.HISTORICAL_ROLE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.HISTORICAL_ROLE);
        break;
      case ActionIdManager.HISTORICAL_ROLE_MIXIN:
      case ActionIdManager.HISTORICAL_ROLE_MIXIN_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.HISTORICAL_ROLE_MIXIN);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.EVENT:
      case ActionIdManager.EVENT_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.EVENT);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.SITUATION:
      case ActionIdManager.SITUATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.SITUATION);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.ENUMERATION:
      case ActionIdManager.ENUMERATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.ENUMERATION);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.DATATYPE:
      case ActionIdManager.DATATYPE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.DATATYPE);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.ABSTRACT:
      case ActionIdManager.ABSTRACT_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.ABSTRACT);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.SUBKIND:
      case ActionIdManager.SUBKIND_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.SUBKIND);
        break;
      case ActionIdManager.ROLE_MIXIN:
      case ActionIdManager.ROLE_MIXIN_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.ROLE_MIXIN);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.ROLE:
      case ActionIdManager.ROLE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.ROLE);
        break;
      case ActionIdManager.RELATOR:
      case ActionIdManager.RELATOR_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.RELATOR);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.QUANTITY:
      case ActionIdManager.QUANTITY_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.QUANTITY);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.QUALITY:
      case ActionIdManager.QUALITY_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.QUALITY);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.PHASE_MIXIN:
      case ActionIdManager.PHASE_MIXIN_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.PHASE_MIXIN);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.PHASE:
      case ActionIdManager.PHASE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.PHASE);
        break;
      case ActionIdManager.MODE:
      case ActionIdManager.MODE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.MODE);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.MIXIN:
      case ActionIdManager.MIXIN_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.MIXIN);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.KIND:
      case ActionIdManager.KIND_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.KIND);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.COLLECTIVE:
      case ActionIdManager.COLLECTIVE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.COLLECTIVE);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.CATEGORY:
      case ActionIdManager.CATEGORY_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.CATEGORY);
        Class.setDefaultRestrictedTo((IClass) element);
        break;
      case ActionIdManager.INSTANTIATION:
      case ActionIdManager.INSTANTIATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.INSTANTIATION);
        break;
      case ActionIdManager.TERMINATION:
      case ActionIdManager.TERMINATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.TERMINATION);
        break;
      case ActionIdManager.PARTICIPATIONAL:
      case ActionIdManager.PARTICIPATIONAL_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.PARTICIPATIONAL);
        break;
      case ActionIdManager.PARTICIPATION:
      case ActionIdManager.PARTICIPATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.PARTICIPATION);
        break;
      case ActionIdManager.HISTORICAL_DEPENDENCE:
      case ActionIdManager.HISTORICAL_DEPENDENCE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.HISTORICAL_DEPENDENCE);
        break;
      case ActionIdManager.CREATION:
      case ActionIdManager.CREATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.CREATION);
        break;
      case ActionIdManager.MANIFESTATION:
      case ActionIdManager.MANIFESTATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.MANIFESTATION);
        break;
      case ActionIdManager.BRINGS_ABOUT:
      case ActionIdManager.BRINGS_ABOUT_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.BRINGS_ABOUT);
        break;
      case ActionIdManager.TRIGGERS:
      case ActionIdManager.TRIGGERS_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.TRIGGERS);
        break;
      case ActionIdManager.MATERIAL:
      case ActionIdManager.MATERIAL_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.MATERIAL);
        break;
      case ActionIdManager.COMPARATIVE:
      case ActionIdManager.COMPARATIVE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.COMPARATIVE);
        break;
      case ActionIdManager.MEDIATION:
      case ActionIdManager.MEDIATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.MEDIATION);
        break;
      case ActionIdManager.CHARACTERIZATION:
      case ActionIdManager.CHARACTERIZATION_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.CHARACTERIZATION);
        break;
      case ActionIdManager.EXTERNAL_DEPENDENCE:
      case ActionIdManager.EXTERNAL_DEPENDENCE_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.EXTERNAL_DEPENDENCE);
        break;
      case ActionIdManager.COMPONENT_OF:
      case ActionIdManager.COMPONENT_OF_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.COMPONENT_OF);
        break;
      case ActionIdManager.MEMBER_OF:
      case ActionIdManager.MEMBER_OF_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.MEMBER_OF);
        break;
      case ActionIdManager.SUB_COLLECTION_OF:
      case ActionIdManager.SUB_COLLECTION_OF_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.SUB_COLLECTION_OF);
        break;
      case ActionIdManager.SUB_QUANTITY_OF:
      case ActionIdManager.SUB_QUANTITY_OF_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.SUB_QUANTITY_OF);
        break;
      case ActionIdManager.BEGIN:
      case ActionIdManager.BEGIN_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.BEGIN);
        break;
      case ActionIdManager.END:
      case ActionIdManager.END_FIXED:
        StereotypesManager.applyStereotype(element, Stereotype.END);
        break;
    }

    boolean isSmartModelingEnabled =
        Configurations.getInstance().getProjectConfigurations().isSmartModellingEnabled();
    boolean isClass = element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS);
    boolean isAssociation =
        element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

    if (isSmartModelingEnabled && isClass)
      SmartModellingController.setClassMetaProperties((IClass) element);

    if (isSmartModelingEnabled && isAssociation)
      SmartModellingController.setAssociationMetaProperties((IAssociation) element);
  }
}
