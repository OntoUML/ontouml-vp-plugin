package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
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

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in
 * model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyPropertiesController implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		if (context.getModelElement() == null) {
			return;
		}

		final IModelElement clickedElement = context.getModelElement();

		switch (action.getActionId()) {
		case ActionIdManager.CLASS_PROPERTY_SET_RESTRICTED_TO:
			this.setRestrictedTo(context, (IClass) clickedElement);
			break;

		case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_ABSTRACT:
		case ActionIdManager.CLASS_PROPERTY_SET_IS_ABSTRACT:
			if (!IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElement.getModelType())) {
				throw new UnsupportedOperationException("Unimplemented behavior");
			} else {
				final IClass clickedClass = (IClass) clickedElement;
				final boolean isAbstract = clickedClass.isAbstract();
				forEachSelectedClass(context, cla -> cla.setAbstract(!isAbstract));
			}
			break;

		case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_DERIVED:
		case ActionIdManager.CLASS_PROPERTY_SET_IS_DERIVED:
			if (!IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElement.getModelType())) {
				throw new UnsupportedOperationException("Unimplemented behavior");
			} else {
				final IClass clickedClass = (IClass) clickedElement;
				final boolean isDerived = ModelElement.isDerived(clickedClass);
				forEachSelectedClass(context, selected -> {
					ModelElement.setIsDerived(selected, !isDerived);
				});
			}
			break;

		case ActionIdManager.CLASS_PROPERTY_SET_IS_EXTENSIONAL:
			setBooleanTaggedValue(context, (IClass) clickedElement, StereotypesManager.PROPERTY_IS_EXTENSIONAL);
			break;

		case ActionIdManager.CLASS_PROPERTY_SET_IS_POWERTYPE:
			setBooleanTaggedValue(context, (IClass) clickedElement, StereotypesManager.PROPERTY_IS_POWERTYPE);
			break;

		case ActionIdManager.CLASS_PROPERTY_SET_ORDER:
			setOrderProperty(context, (IClass) clickedElement);
			break;
		case ActionIdManager.ASSOCIATION_PROPERTY_REVERSE_ASSOCIATION:
			if (IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElement.getModelType())) {
				IAssociation clickedAssociation = (IAssociation) clickedElement;
				forEachSelectedElement(clickedAssociation, clicked -> Association.invertAssociation(clicked));
			}
			break;
		}
		
	}


   @Override
   public void update(VPAction action, VPContext context) {
	   final IModelElement clickedElement = context.getModelElement();
	   final String clickedElementType = clickedElement.getModelType();
//      boolean clickedOnModelElement = context.getModelElement() != null;
//      boolean clickedOnClass = clickedOnModelElement && context.getModelElement() instanceof IClass;

		IModelElementFactory.instance();
		if (!IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)
				&& !IModelElementFactory.MODEL_TYPE_CLASS.equals(clickedElementType)) {
			action.setEnabled(false);
			return;
		}

      final IClass _class = clickedElement instanceof IClass ? (IClass) context.getModelElement() : null;

      final boolean isSmartModelingEnabled =
              Configurations.getInstance().getProjectConfigurations().isSmartModellingEnabled();

      boolean enabled = true;

      switch (action.getActionId()) {
         case ActionIdManager.CLASS_PROPERTY_SET_IS_ABSTRACT:
            action.setSelected(_class.isAbstract());
            enabled = Class.isAbstractEditable(_class) || !isSmartModelingEnabled;
            break;
         case ActionIdManager.CLASS_PROPERTY_SET_IS_DERIVED:
            action.setSelected(ModelElement.isDerived(_class));
            enabled = true;
            break;
         case ActionIdManager.CLASS_PROPERTY_SET_RESTRICTED_TO:
            enabled = Class.isRestrictedToEditable(_class)
                    || (!isSmartModelingEnabled && Class.hasValidStereotype(_class));
            break;
         case ActionIdManager.CLASS_PROPERTY_SET_IS_EXTENSIONAL:
            action.setSelected(Class.isExtensional(_class));
            enabled = Class.isCollective(_class) || Class.hasCollectiveNature(_class)
                    || (!isSmartModelingEnabled && Class.hasValidStereotype(_class));
            break;
         case ActionIdManager.CLASS_PROPERTY_SET_IS_POWERTYPE:
            action.setSelected(Class.isPowertype(_class));
            enabled = Class.isType(_class);
            break;
         case ActionIdManager.CLASS_PROPERTY_SET_ORDER:
            enabled = Class.isType(_class);
            if(Class.hasValidStereotype(_class)){
               String order = Class.getOrderOr(_class).orElse("1");
               action.setLabel("Set order"+" ("+order+")");}
            break;
         case ActionIdManager.ASSOCIATION_PROPERTY_REVERSE_ASSOCIATION:
         case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_ABSTRACT:
         case ActionIdManager.ASSOCIATION_PROPERTY_SET_IS_DERIVED:
        	 enabled = true;
      }

      action.setEnabled(enabled);

   }

   private void forEachSelectedClass(VPContext context, Consumer<IClass> consumer) {
      if (!(context.getModelElement() instanceof IClass))
         return;

      final IDiagramUIModel diagram = context.getDiagram();
      final IClass _class = (IClass) context.getModelElement();

      if (diagram == null) {
         consumer.accept(_class);
         return;
      }

      final IDiagramElement[] diagramElements = context.getDiagram().getSelectedDiagramElement();

      Arrays.stream(diagramElements)
              .filter(e -> e.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
              .map(e -> (IClass) e.getModelElement()).forEach(consumer);
   }

	@SuppressWarnings("unchecked")
	private <T extends IModelElement> void forEachSelectedElement(T element, Consumer<T> consumer) {
		if (element == null) {
			return;
		}

		final DiagramManager dm = ApplicationManager.instance().getDiagramManager();
		final IDiagramUIModel diagram = dm.getActiveDiagram();

		if (diagram == null) {
			consumer.accept(element);
			return;
		}

		final String selectedElementType = element.getModelType();
		final IDiagramElement[] selectedDiagramElements = diagram.getSelectedDiagramElement();

		Arrays.stream(selectedDiagramElements).map(selectedDiagramElement -> selectedDiagramElement.getModelElement())
				.filter(selectedElement -> selectedElement != null
						&& selectedElementType.equals(selectedElement.getModelType()))
				.forEach((Consumer<? super IModelElement>) consumer);;
	}

   private void setBooleanTaggedValue(VPContext context, IClass clickedClass, String metaProperty) {
      final ITaggedValue booleanTaggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(clickedClass,
              metaProperty);
      final boolean value = booleanTaggedValue != null && Boolean.parseBoolean(booleanTaggedValue.getValueAsString());

      forEachSelectedClass(context, cla -> {
         ITaggedValue taggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(cla, metaProperty);

         if (taggedValue == null)
            return;

         taggedValue.setValue(!value);
      });
   }

   private void setOrderProperty(VPContext context, IClass clickedClass) {
      final ITaggedValue baseTaggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(clickedClass,
              StereotypesManager.PROPERTY_ORDER);

      if (baseTaggedValue == null)
         return;

      final SetOrderView dialog = new SetOrderView(baseTaggedValue.getValueAsString());
      ApplicationManager.instance().getViewManager().showDialog(dialog);
      final String order = dialog.getOrder();

      forEachSelectedClass(context, cla -> {
         ITaggedValue taggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(cla,
                 StereotypesManager.PROPERTY_ORDER);

         if (taggedValue == null)
            return;

         taggedValue.setValue(order);
      });
   }

   private void setRestrictedTo(VPContext context, IClass clickedClass) {
      String currentRestrictions = Class.getRestrictedTo(clickedClass);
      currentRestrictions = currentRestrictions == null ? "" : currentRestrictions;

      String stereotype = ModelElement.getUniqueStereotypeName(clickedClass);
      List<String> selectableRestrictedTo = RestrictedTo.possibleRestrictedToValues(stereotype);

      final SelectRestrictionsView dialog = new SelectRestrictionsView(currentRestrictions, selectableRestrictedTo);
      ApplicationManager.instance().getViewManager().showDialog(dialog);
      final String newRestrictions = dialog.getSelectedValues();

      forEachSelectedClass(context, cla -> {
         Class.setRestrictedTo(cla, newRestrictions);
      });
   }

}