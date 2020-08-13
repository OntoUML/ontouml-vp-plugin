package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;
import it.unibz.inf.ontouml.vp.views.SelectRestrictionsView;
import it.unibz.inf.ontouml.vp.views.SetOrderView;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyPropertiesController implements VPContextActionController {

   @Override
   public void performAction(VPAction action, VPContext context, ActionEvent event) {
      if (
         context.getModelElement() == null || 
         !(context.getModelElement() instanceof IClass)
      ) {
         return ;
      }

      final IClass clickedClass = (IClass) context.getModelElement();

      switch (action.getActionId()) {
         case ActionIds.PROPERTY_SET_RESTRICTED_TO:
            this.setRestrictedTo(context, clickedClass);
            break;

         case ActionIds.PROPERTY_SET_IS_ABSTRACT:
            final boolean isAbstract = clickedClass.isAbstract();
            forEachSelectedClass(context, cla -> cla.setAbstract(!isAbstract));
            break;

         case ActionIds.PROPERTY_SET_IS_DERIVED:
            final boolean isDerived = ModelElement.getIsDerived(clickedClass);
            forEachSelectedClass(context, selected ->  {
               ModelElement.setIsDerived(selected, !isDerived);
            });
            break;
         case ActionIds.PROPERTY_SET_IS_EXTENSIONAL:
            setBooleanTaggedValue(context, clickedClass, StereotypeUtils.PROPERTY_IS_EXTENSIONAL);
            break;

         case ActionIds.PROPERTY_SET_IS_POWERTYPE:
            setBooleanTaggedValue(context, clickedClass, StereotypeUtils.PROPERTY_IS_POWERTYPE);
            break;

         case ActionIds.PROPERTY_SET_ORDER:
            setOrderProperty(context, clickedClass);
            break;
      }
   }

   @Override
   public void update(VPAction action, VPContext context) {
      if (
         context.getModelElement() == null || 
         !(context.getModelElement() instanceof IClass)
      ) {
         return ;
      }

      final IClass _class = (IClass) context.getModelElement();
      final String stereotype = StereotypeUtils.getUniqueStereotypeName(_class);
      final Set<String> allClassStereotypes = StereotypeUtils.getOntoUMLClassStereotypeNames();

      switch (action.getActionId()) {
         case ActionIds.PROPERTY_SET_RESTRICTED_TO:
            if (allClassStereotypes.contains(stereotype)) {
               final boolean isSmartModelingEnabled = 
                  Configurations.getInstance()
                     .getProjectConfigurations()
                     .isSmartModellingEnabled();
               final List<String> nonFixedRestrictedTo =
                  Arrays.asList(StereotypeUtils.STR_CATEGORY,
                     StereotypeUtils.STR_MIXIN,
                     StereotypeUtils.STR_PHASE_MIXIN,
                     StereotypeUtils.STR_ROLE_MIXIN,
                     StereotypeUtils.STR_HISTORICAL_ROLE_MIXIN);
               
               action.setEnabled(!isSmartModelingEnabled ||
                  nonFixedRestrictedTo.contains(stereotype));
            } else {
               action.setEnabled(false);
            }
            break;
         case ActionIds.PROPERTY_SET_IS_ABSTRACT:
            action.setEnabled(true);
            action.setSelected(_class.isAbstract());
            break;
         case ActionIds.PROPERTY_SET_IS_DERIVED:
            action.setEnabled(true);
            action.setSelected(ModelElement.getIsDerived(_class));
            break;
         case ActionIds.PROPERTY_SET_IS_EXTENSIONAL:
            action.setEnabled(StereotypeUtils.STR_COLLECTIVE.equals(stereotype));
            action.setSelected(Class.getIsExtensional(_class));
            break;
         case ActionIds.PROPERTY_SET_IS_POWERTYPE:
            action.setEnabled(StereotypeUtils.STR_TYPE.equals(stereotype));
            action.setSelected(Class.getIsPowertype(_class));
            break;
         case ActionIds.PROPERTY_SET_ORDER:
            action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_TYPE));
            break;
      }
   }

   private void forEachSelectedClass(VPContext context, Consumer<IClass> consumer) {
      if (!(context.getModelElement() instanceof IClass))
         return;

      final IDiagramUIModel diagram = context.getDiagram();
      final IClass _class = (IClass) context.getModelElement();

      if(diagram == null) {
         consumer.accept(_class);
         return ;
      }

      final IDiagramElement[] diagramElements = context.getDiagram()
              .getSelectedDiagramElement();

      Arrays.stream(diagramElements)
              .filter(e -> e.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
              .map(e -> (IClass) e.getModelElement())
              .forEach(consumer);
   }

   private void setBooleanTaggedValue(VPContext context, IClass clickedClass, String metaProperty) {
      final ITaggedValue booleanTaggedValue = StereotypeUtils.reapplyStereotypeAndGetTaggedValue(clickedClass, metaProperty);
      final boolean value = booleanTaggedValue != null && Boolean.parseBoolean(booleanTaggedValue.getValueAsString());

      forEachSelectedClass(context, cla -> {
         ITaggedValue taggedValue = StereotypeUtils.reapplyStereotypeAndGetTaggedValue(cla, metaProperty);

         if (taggedValue == null)
            return;

         taggedValue.setValue(!value);
      });
   }

   private void setOrderProperty(VPContext context, IClass clickedClass) {
      final ITaggedValue baseTaggedValue =
              StereotypeUtils.reapplyStereotypeAndGetTaggedValue(clickedClass, StereotypeUtils.PROPERTY_ORDER);

      if (baseTaggedValue == null)
         return;

      final SetOrderView dialog = new SetOrderView(baseTaggedValue.getValueAsString());
      ApplicationManager.instance().getViewManager().showDialog(dialog);
      final String order = dialog.getOrder();

      forEachSelectedClass(context, cla -> {
         ITaggedValue taggedValue = StereotypeUtils.reapplyStereotypeAndGetTaggedValue(cla, StereotypeUtils.PROPERTY_ORDER);

         if (taggedValue == null)
            return;

         taggedValue.setValue(order);
      });
   }

   private void setRestrictedTo(VPContext context, IClass clickedClass) {
      System.out.println("\nClicked class: " + clickedClass.getName());
      String currentRestrictions = Class.getRestrictedTo(clickedClass);
      currentRestrictions = currentRestrictions == null ? "" : currentRestrictions;

      final SelectRestrictionsView dialog = new SelectRestrictionsView(currentRestrictions);
      ApplicationManager.instance().getViewManager().showDialog(dialog);
      final String newRestrictions = dialog.getSelectedValues();

      forEachSelectedClass(context, cla -> {
         System.out.println("setRestrictedTo on class: " + cla.getName());
         Class.setRestrictedTo(cla, newRestrictions);
      });
   }

}