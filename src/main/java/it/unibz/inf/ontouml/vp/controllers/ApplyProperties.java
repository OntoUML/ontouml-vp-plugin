package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.*;

import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;
import it.unibz.inf.ontouml.vp.views.SelectMultipleOptionsDialog;
import it.unibz.inf.ontouml.vp.views.SetOrderDialog;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyProperties implements VPContextActionController {

   @Override
   public void performAction(VPAction action, VPContext context, ActionEvent event) {
      if (context.getModelElement() == null)
         return;

      final IClass clickedClass = (IClass) context.getModelElement();

      switch (action.getActionId()) {
         case ActionIds.PROPERTY_SET_RESTRICTED_TO:
            this.setRestrictedTo(context, clickedClass);
            break;

         case ActionIds.PROPERTY_SET_IS_ABSTRACT:
            boolean isAbstract = !clickedClass.isAbstract();
            setPropertyOnSelectedClasses(context, cla -> cla.setAbstract(isAbstract));
            break;

         case ActionIds.PROPERTY_SET_IS_DERIVED:
            boolean removeSlash = clickedClass.getName().trim().startsWith("/");

            setPropertyOnSelectedClasses(context, cla -> {
               String currentName = cla.getName().trim();

               if (removeSlash && currentName.startsWith("/"))
                  cla.setName(currentName.substring(1));

               if (!removeSlash && !currentName.startsWith("/"))
                  cla.setName("/" + currentName);
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
      if (context.getModelElement() == null)
         return;

      final IClass _class = (IClass) context.getModelElement();
      final ITaggedValueContainer container = _class.getTaggedValues();
      final Iterator<?> values = container == null ? null : container.taggedValueIterator();
      final Iterator<?> stereotypes = _class.stereotypeModelIterator();
      final Set<String> allStereotypes = StereotypeUtils.getOntoUMLClassStereotypeNames();

      switch (action.getActionId()) {
         case ActionIds.PROPERTY_SET_RESTRICTED_TO:

            while (stereotypes != null && stereotypes.hasNext()) {
               final IStereotype stereotype = (IStereotype) stereotypes.next();

               if (allStereotypes.contains(stereotype.getName())) {
                  action.setEnabled(true);
                  return;
               }
            }

            action.setEnabled(false);
            break;
         case ActionIds.PROPERTY_SET_IS_ABSTRACT:
            action.setEnabled(true);
            action.setSelected(_class.isAbstract());
            break;
         case ActionIds.PROPERTY_SET_IS_DERIVED:
            action.setEnabled(true);
            action.setSelected(_class.getName().trim().startsWith("/"));
            break;
         case ActionIds.PROPERTY_SET_IS_EXTENSIONAL:
            while (values != null && values.hasNext()) {
               final ITaggedValue value = (ITaggedValue) values.next();

               if (value.getName().equals(StereotypeUtils.PROPERTY_IS_EXTENSIONAL)) {
                  action.setEnabled(true);
                  action.setSelected(value.getValueAsString().toLowerCase().equals("true"));
                  return;
               }
            }

            action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_COLLECTIVE));
            action.setSelected(false);
            break;
         case ActionIds.PROPERTY_SET_IS_POWERTYPE:
            while (values != null && values.hasNext()) {
               final ITaggedValue value = (ITaggedValue) values.next();

               if (value.getName().equals(StereotypeUtils.PROPERTY_IS_POWERTYPE)) {
                  action.setEnabled(true);
                  action.setSelected(value.getValueAsString().toLowerCase().equals("true"));
                  return;
               }
            }

            action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_TYPE));
            action.setSelected(false);
            break;
         case ActionIds.PROPERTY_SET_ORDER:
            action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_TYPE));
            break;
      }
   }

   private void setPropertyOnSelectedClasses(VPContext context, Consumer<IClass> consumer) {
      if (!(context.getModelElement() instanceof IClass))
         return;

      IDiagramElement[] diagramElements = ApplicationManager.instance()
              .getDiagramManager()
              .getActiveDiagram()
              .getSelectedDiagramElement();

      Arrays.stream(diagramElements)
              .filter(e -> e.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
              .map(e -> (IClass) e.getModelElement())
              .forEach(consumer);
   }

   private ITaggedValue getTaggedValue(IClass _class, String taggedValueName) {
      System.out.println("getTaggedValue "+_class.getName()+" "+taggedValueName);
      String stereotype = _class.toStereotypeArray() != null && _class.toStereotypeArray().length > 0 ?
              _class.toStereotypeArray()[0] : null;

      // Escape in case the stereotype is missing or incorrect
      if (stereotype == null || !StereotypeUtils.getOntoUMLClassStereotypeNames().contains(stereotype))
         return null;

      // Reapply stereotype making sure that the tagged values are there
       StereotypeUtils.applyStereotype(_class, stereotype);

      // Searches for tagged value
      ITaggedValueContainer taggedValueContainer = _class.getTaggedValues();

      // Deletes old tagged value
      ITaggedValue allowedTagged = taggedValueContainer.getTaggedValueByName("allowed");
      if(allowedTagged!=null)
         taggedValueContainer.removeTaggedValue(allowedTagged);

      if (taggedValueContainer != null)
        return taggedValueContainer.getTaggedValueByName(taggedValueName);

      return null;
   }

   private void setBooleanTaggedValue(VPContext context, IClass clickedClass, String metaProperty) {
      final ITaggedValue booleanTaggedValue = getTaggedValue(clickedClass, metaProperty);
      final boolean value = booleanTaggedValue != null && Boolean.parseBoolean(booleanTaggedValue.getValueAsString());

      setPropertyOnSelectedClasses(context, cla -> {
         ITaggedValue taggedValue = getTaggedValue(cla, metaProperty);

         if (taggedValue == null)
            return;

         taggedValue.setValue(!value);
      });
   }

   private void setOrderProperty(VPContext context, IClass clickedClass) {
      final ITaggedValue baseTaggedValue = getTaggedValue(clickedClass, StereotypeUtils.PROPERTY_ORDER);

      if (baseTaggedValue == null)
         return;

      final ViewManager vm = ApplicationManager.instance().getViewManager();
      final SetOrderDialog dialog = new SetOrderDialog(baseTaggedValue.getValueAsString());

      vm.showDialog(dialog);

      setPropertyOnSelectedClasses(context, cla -> {
         String order = dialog.getOrder();
         ITaggedValue taggedValue = getTaggedValue(cla, StereotypeUtils.PROPERTY_ORDER);

         if (taggedValue == null)
            return;

         taggedValue.setValue(order);
      });
   }

   private void setRestrictedTo(VPContext context, IClass clickedClass) {
      final ITaggedValue baseTaggedValue = getTaggedValue(clickedClass, StereotypeUtils.PROPERTY_RESTRICTED_TO);

      if (baseTaggedValue == null)
         return;

      final ViewManager vm = ApplicationManager.instance().getViewManager();
      final SelectMultipleOptionsDialog dialog = new SelectMultipleOptionsDialog(baseTaggedValue.getValueAsString());

      vm.showDialog(dialog);

      setPropertyOnSelectedClasses(context, cla -> {
         String restrictedTo = dialog.getSelectedValues();
         ITaggedValue taggedValue = getTaggedValue(cla, StereotypeUtils.PROPERTY_RESTRICTED_TO);

         if (taggedValue == null)
            return;

         taggedValue.setValue(restrictedTo);
      });
   }

}