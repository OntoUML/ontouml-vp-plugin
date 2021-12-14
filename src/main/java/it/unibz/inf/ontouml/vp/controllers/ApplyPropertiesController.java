package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ActionIdManager;
import it.unibz.inf.ontouml.vp.utils.RestrictedTo;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import it.unibz.inf.ontouml.vp.views.SelectRestrictionsView;
import it.unibz.inf.ontouml.vp.views.SetOrderView;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyPropertiesController implements VPContextActionController {

  @Override
  public void performAction(VPAction action, VPContext context, ActionEvent event) {
    final IModelElement clickedElement = context.getModelElement();
    final String clickedElementType = clickedElement.getModelType();

    if (!IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)
        && !IModelElementFactory.MODEL_TYPE_ASSOCIATION_END.equals(clickedElementType)
        && !IModelElementFactory.MODEL_TYPE_ATTRIBUTE.equals(clickedElementType)
        && !IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElementType)) {
      return;
    }

    final IAssociation clickedAssociation =
        IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)
            ? (IAssociation) context.getModelElement()
            : null;
    final IClass clickedClass =
        IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElementType)
            ? (IClass) context.getModelElement()
            : null;

    switch (action.getActionId()) {
      case ActionIdManager.CLASS_PROPERTY_SET_RESTRICTED_TO:
        this.setRestrictedTo(clickedClass);
        break;

      case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_ABSTRACT:
      case ActionIdManager.CLASS_PROPERTY_SET_IS_ABSTRACT:
        {
          final boolean isAbstract = ModelElement.isAbstract(clickedElement);
          ModelElement.forEachSelectedElement(
              clickedElement,
              selectedElement -> ModelElement.setAbstract(selectedElement, !isAbstract));
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_DERIVED:
      case ActionIdManager.CLASS_PROPERTY_SET_IS_DERIVED:
        {
          final boolean isDerived = ModelElement.isDerived(clickedElement);
          ModelElement.forEachSelectedElement(
              clickedElement,
              selectedElement -> ModelElement.setDerived(selectedElement, !isDerived));
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_SOURCE_SET_IS_DERIVED:
        {
          final IAssociationEnd sourceEnd = Association.getSourceEnd(clickedAssociation);
          sourceEnd.setDerived(!sourceEnd.isDerived());
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_TARGET_SET_IS_DERIVED:
        {
          final IAssociationEnd targetEnd = Association.getTargetEnd(clickedAssociation);
          targetEnd.setDerived(!targetEnd.isDerived());
          break;
        }

      case ActionIdManager.ATTRIBUTE_PROPERTY_SET_IS_DERIVED:
        {
          final boolean isDerived = ModelElement.isDerived(clickedElement);
          ModelElement.setDerived(clickedElement, !isDerived);
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_SOURCE_SET_IS_ORDERED:
        {
          final IAssociationEnd sourceEnd = Association.getSourceEnd(clickedAssociation);
          ModelElement.setOrdered(sourceEnd, !ModelElement.isOrdered(sourceEnd));
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_TARGET_SET_IS_ORDERED:
        {
          final IAssociationEnd targetEnd = Association.getTargetEnd(clickedAssociation);
          ModelElement.setOrdered(targetEnd, !ModelElement.isOrdered(targetEnd));
          break;
        }

      case ActionIdManager.ATTRIBUTE_PROPERTY_SET_IS_ORDERED:
        {
          final boolean isOrdered = ModelElement.isOrdered(clickedElement);
          ModelElement.setOrdered(clickedElement, !isOrdered);
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_SOURCE_SET_IS_READ_ONLY:
        {
          final IAssociationEnd sourceEnd = Association.getSourceEnd(clickedAssociation);
          ModelElement.setReadOnly(sourceEnd, !ModelElement.isReadOnly(sourceEnd));
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_TARGET_SET_IS_READ_ONLY:
        {
          final IAssociationEnd targetEnd = Association.getTargetEnd(clickedAssociation);
          ModelElement.setReadOnly(targetEnd, !ModelElement.isReadOnly(targetEnd));
          break;
        }

      case ActionIdManager.ATTRIBUTE_PROPERTY_SET_IS_READ_ONLY:
        {
          final boolean isReadOnly = ModelElement.isReadOnly(clickedElement);
          ModelElement.setReadOnly(clickedElement, !isReadOnly);
          break;
        }

      case ActionIdManager.CLASS_PROPERTY_SET_IS_EXTENSIONAL:
        setBooleanTaggedValue(clickedClass, StereotypesManager.PROPERTY_IS_EXTENSIONAL);
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_IS_POWERTYPE:
        setBooleanTaggedValue(clickedClass, StereotypesManager.PROPERTY_IS_POWERTYPE);
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_ORDER:
        setOrderProperty(clickedClass);
        break;
    }
  }

  @Override
  public void update(VPAction action, VPContext context) {
    final IModelElement clickedElement = context.getModelElement();
    final String clickedElementType = clickedElement.getModelType();

    if (!IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)
        && !IModelElementFactory.MODEL_TYPE_ASSOCIATION_END.equals(clickedElementType)
        && !IModelElementFactory.MODEL_TYPE_ATTRIBUTE.equals(clickedElementType)
        && !IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElementType)) {
      action.setEnabled(false);
      return;
    }

    final IClass clickedClass =
        IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElementType)
            ? (IClass) context.getModelElement()
            : null;
    final IAssociation clickedAssociation =
        IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)
            ? (IAssociation) context.getModelElement()
            : null;
    final boolean isSmartModelingEnabled =
        Configurations.getInstance().getProjectConfigurations().isSmartModellingEnabled();
    boolean enabled = true;

    switch (action.getActionId()) {
      case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_ABSTRACT:
        action.setSelected(ModelElement.isAbstract(clickedElement));
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_IS_ABSTRACT:
        action.setSelected(clickedClass.isAbstract());
        enabled = Class.isAbstractEditable(clickedClass) || !isSmartModelingEnabled;
        break;

      case ActionIdManager.ASSOCIATION_PROPERTY_SOURCE_SET_IS_DERIVED:
        {
          final IAssociationEnd sourceEnd = Association.getSourceEnd(clickedAssociation);
          action.setSelected(sourceEnd.isDerived());
          enabled = true;
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_TARGET_SET_IS_DERIVED:
        {
          final IAssociationEnd targetEnd = Association.getTargetEnd(clickedAssociation);
          action.setSelected(targetEnd.isDerived());
          enabled = true;
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_DERIVED:
      case ActionIdManager.ATTRIBUTE_PROPERTY_SET_IS_DERIVED:
      case ActionIdManager.CLASS_PROPERTY_SET_IS_DERIVED:
        action.setSelected(ModelElement.isDerived(clickedElement));
        enabled = true;
        break;

      case ActionIdManager.ASSOCIATION_PROPERTY_SOURCE_SET_IS_ORDERED:
        {
          final IAssociationEnd sourceEnd = Association.getSourceEnd(clickedAssociation);
          action.setSelected(ModelElement.isOrdered(sourceEnd));
          enabled = true;
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_TARGET_SET_IS_ORDERED:
        {
          final IAssociationEnd targetEnd = Association.getTargetEnd(clickedAssociation);
          action.setSelected(ModelElement.isOrdered(targetEnd));
          enabled = true;
          break;
        }

      case ActionIdManager.ATTRIBUTE_PROPERTY_SET_IS_ORDERED:
        action.setSelected(ModelElement.isOrdered(clickedElement));
        enabled = true;
        break;

      case ActionIdManager.ASSOCIATION_PROPERTY_SOURCE_SET_IS_READ_ONLY:
        {
          final IAssociationEnd sourceEnd = Association.getSourceEnd(clickedAssociation);
          action.setSelected(ModelElement.isReadOnly(sourceEnd));
          enabled = true;
          break;
        }

      case ActionIdManager.ASSOCIATION_PROPERTY_TARGET_SET_IS_READ_ONLY:
        {
          final IAssociationEnd targetEnd = Association.getTargetEnd(clickedAssociation);
          action.setSelected(ModelElement.isReadOnly(targetEnd));
          enabled = true;
          break;
        }

      case ActionIdManager.ATTRIBUTE_PROPERTY_SET_IS_READ_ONLY:
        action.setSelected(ModelElement.isReadOnly(clickedElement));
        enabled = true;
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_RESTRICTED_TO:
        enabled =
            Class.isRestrictedToEditable(clickedClass)
                || (!isSmartModelingEnabled && Class.isOntoumlClass(clickedClass));
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_IS_EXTENSIONAL:
        action.setSelected(Class.isExtensional(clickedClass));
        enabled =
            Class.isCollective(clickedClass)
                || Class.hasCollectiveNature(clickedClass)
                || (!isSmartModelingEnabled && Class.isOntoumlClass(clickedClass));
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_IS_POWERTYPE:
        action.setSelected(Class.isPowertype(clickedClass));
        enabled = Class.isType(clickedClass);
        break;

      case ActionIdManager.CLASS_PROPERTY_SET_ORDER:
        enabled = Class.isType(clickedClass);
        if (Class.isOntoumlClass(clickedClass)) {
          String order = Class.getOrderOr(clickedClass).orElse("1");
          action.setLabel("Set order" + " (" + order + ")");
        }
        break;
    }

    action.setEnabled(enabled);
  }

  private void setBooleanTaggedValue(IClass clickedClass, String metaProperty) {
    final ITaggedValue booleanTaggedValue =
        StereotypesManager.reapplyStereotypeAndGetTaggedValue(clickedClass, metaProperty);
    final boolean value =
        booleanTaggedValue != null && Boolean.parseBoolean(booleanTaggedValue.getValueAsString());

    ModelElement.forEachSelectedElement(
        clickedClass,
        selectedElement -> {
          ITaggedValue taggedValue =
              StereotypesManager.reapplyStereotypeAndGetTaggedValue(selectedElement, metaProperty);

          if (taggedValue == null) {
            return;
          }

          taggedValue.setValue(!value);
        });
  }

  private void setOrderProperty(IClass clickedClass) {
    final ITaggedValue baseTaggedValue =
        StereotypesManager.reapplyStereotypeAndGetTaggedValue(
            clickedClass, StereotypesManager.PROPERTY_ORDER);

    if (baseTaggedValue == null) {
      return;
    }

    final SetOrderView dialog = new SetOrderView(baseTaggedValue.getValueAsString());
    ApplicationManager.instance().getViewManager().showDialog(dialog);
    final String order = dialog.getOrder();

    ModelElement.forEachSelectedElement(
        clickedClass,
        selectedClass -> {
          ITaggedValue taggedValue =
              StereotypesManager.reapplyStereotypeAndGetTaggedValue(
                  selectedClass, StereotypesManager.PROPERTY_ORDER);

          if (taggedValue == null) {
            return;
          }

          taggedValue.setValue(order);
        });
  }

  private void setRestrictedTo(IClass clickedClass) {
    String currentRestrictions = Class.getRestrictedTo(clickedClass);
    currentRestrictions = currentRestrictions == null ? "" : currentRestrictions;

    String stereotype = ModelElement.getUniqueStereotypeName(clickedClass);
    List<String> selectableRestrictedTo = RestrictedTo.possibleRestrictedToValues(stereotype);

    final SelectRestrictionsView dialog =
        new SelectRestrictionsView(currentRestrictions, selectableRestrictedTo);
    ApplicationManager.instance().getViewManager().showDialog(dialog);
    final String newRestrictions = dialog.getSelectedValues();

    ModelElement.forEachSelectedElement(
        clickedClass,
        selectedClass -> {
          Class.setRestrictedTo(selectedClass, newRestrictions);
        });
  }
}
