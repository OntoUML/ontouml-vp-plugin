package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyStereotypeController implements VPContextActionController {

   @Override
   public void performAction(VPAction action, VPContext context, ActionEvent event) {
      IDiagramElement[] diagramElements = ApplicationManager.instance()
              .getDiagramManager()
              .getActiveDiagram()
              .getSelectedDiagramElement();

      if (diagramElements == null) {
         applyStereotype(action, context.getModelElement());
         return;
      }

      IModelElement clickedElement = context.getModelElement();

      for (IDiagramElement diagramElement : diagramElements) {
         IModelElement modelElement = diagramElement.getModelElement();

         if (modelElement.getModelType().equals(clickedElement.getModelType()))
            applyStereotype(action, modelElement);
      }
   }

   // TODO: change this method to be independent of diagram elements
   @Override
   public void update(VPAction action, VPContext context) {
      action.setEnabled(true);

      if (action.getActionId().contains("fixedMenu")) { return ; }

      if (
         context.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS) &&
         !isClassSelectionAllowed()
      ) {
         action.setEnabled(false);
      }

      final DiagramManager dm = ApplicationManager
            .instance().getDiagramManager();
      IDiagramElement[] diagramElements = 
            dm.getActiveDiagram() != null ?
            dm.getActiveDiagram().getSelectedDiagramElement() :
            null;

      if (diagramElements == null) {
         defineActionBehavior(action, context.getModelElement());
         return;
      }

      for (IDiagramElement diagramElement : diagramElements) {
         if (diagramElement.getModelElement().getModelType().equals(context.getModelElement().getModelType()))
            defineActionBehavior(action, diagramElement.getModelElement());
      }

   }

   private void applyStereotype(VPAction action, IModelElement element) {
      switch (action.getActionId()) {
         case ActionIds.TYPE:
         case ActionIds.TYPE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TYPE);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.HISTORICAL_ROLE:
         case ActionIds.HISTORICAL_ROLE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_ROLE);
            break;
         case ActionIds.HISTORICAL_ROLE_MIXIN:
         case ActionIds.HISTORICAL_ROLE_MIXIN_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_ROLE_MIXIN);
            break;
         case ActionIds.EVENT:
         case ActionIds.EVENT_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EVENT);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.SITUATION:
         case ActionIds.SITUATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SITUATION);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.ENUMERATION:
         case ActionIds.ENUMERATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ENUMERATION);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.DATATYPE:
         case ActionIds.DATATYPE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_DATATYPE);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.SUBKIND:
         case ActionIds.SUBKIND_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUBKIND);
            break;
         case ActionIds.ROLE_MIXIN:
         case ActionIds.ROLE_MIXIN_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE_MIXIN);
            break;
         case ActionIds.ROLE:
         case ActionIds.ROLE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE);
            break;
         case ActionIds.RELATOR:
         case ActionIds.RELATOR_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_RELATOR);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.QUANTITY:
         case ActionIds.QUANTITY_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUANTITY);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.QUALITY:
         case ActionIds.QUALITY_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUALITY);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.PHASE_MIXIN:
         case ActionIds.PHASE_MIXIN_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE_MIXIN);
            break;
         case ActionIds.PHASE:
         case ActionIds.PHASE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE);
            break;
         case ActionIds.MODE:
         case ActionIds.MODE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MODE);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.MIXIN:
         case ActionIds.MIXIN_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MIXIN);
            break;
         case ActionIds.KIND:
         case ActionIds.KIND_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_KIND);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.COLLECTIVE:
         case ActionIds.COLLECTIVE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COLLECTIVE);
            Class.setDefaultRestrictedTo((IClass) element);
            break;
         case ActionIds.CATEGORY:
         case ActionIds.CATEGORY_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CATEGORY);
            break;
         case ActionIds.INSTANTIATION:
         case ActionIds.INSTANTIATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_INSTANTIATION);
            break;
         case ActionIds.TERMINATION:
         case ActionIds.TERMINATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TERMINATION);
            break;
         case ActionIds.PARTICIPATIONAL:
         case ActionIds.PARTICIPATIONAL_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATIONAL);
            break;
         case ActionIds.PARTICIPATION:
         case ActionIds.PARTICIPATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATION);
            break;
         case ActionIds.HISTORICAL_DEPENDENCE:
         case ActionIds.HISTORICAL_DEPENDENCE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
            break;
         case ActionIds.CREATION:
         case ActionIds.CREATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CREATION);
            break;
         case ActionIds.MANIFESTATION:
         case ActionIds.MANIFESTATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MANIFESTATION);
            break;
         case ActionIds.BRINGS_ABOUT:
         case ActionIds.BRINGS_ABOUT_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_BRINGS_ABOUT);
            break;
         case ActionIds.TRIGGERS:
         case ActionIds.TRIGGERS_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TRIGGERS);
            break;
         case ActionIds.MATERIAL:
         case ActionIds.MATERIAL_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MATERIAL);
            break;
         case ActionIds.COMPARATIVE:
         case ActionIds.COMPARATIVE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPARATIVE);
            break;
         case ActionIds.MEDIATION:
         case ActionIds.MEDIATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEDIATION);
            break;
         case ActionIds.CHARACTERIZATION:
         case ActionIds.CHARACTERIZATION_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CHARACTERIZATION);
            break;
         case ActionIds.EXTERNAL_DEPENDENCE:
         case ActionIds.EXTERNAL_DEPENDENCE_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
            break;
         case ActionIds.COMPONENT_OF:
         case ActionIds.COMPONENT_OF_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPONENT_OF);
            break;
         case ActionIds.MEMBER_OF:
         case ActionIds.MEMBER_OF_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEMBER_OF);
            break;
         case ActionIds.SUB_COLLECTION_OF:
         case ActionIds.SUB_COLLECTION_OF_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_COLLECTION_OF);
            break;
         case ActionIds.SUB_QUANTITY_OF:
         case ActionIds.SUB_QUANTITY_OF_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_QUANTITY_OF);
            break;
         case ActionIds.BEGIN:
         case ActionIds.BEGIN_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_BEGIN);
            break;
         case ActionIds.END:
         case ActionIds.END_FIXED:
            StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_END);
            break;
      }

      boolean isSmartModelingEnabled = Configurations.getInstance().getProjectConfigurations()
              .isSmartModellingEnabled();
      boolean isClass = element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS);
      boolean isAssociation = element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

      if (isSmartModelingEnabled && isClass)
         SmartModellingController.setClassMetaProperties((IClass) element);

      if (isSmartModelingEnabled && isAssociation)
         SmartModellingController.setAssociationMetaProperties((IAssociation) element);
   }

   private void defineActionBehavior(VPAction action, IModelElement element) {

      if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
         final IAssociation association = (IAssociation) element;
         SmartModellingController.manageAssociationStereotypes(association, action);
         return;
      }

      if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
         final IClass _class = (IClass) element;
         SmartModellingController.manageClassStereotypes(_class, action);
         return;
      }
   }

   private boolean isClassSelectionAllowed() {

      final DiagramManager dm = ApplicationManager.instance()
            .getDiagramManager();
      IDiagramElement[] diagramElements = 
            dm.getActiveDiagram() != null ?
            dm.getActiveDiagram().getSelectedDiagramElement() :
            null ;
      LinkedList<String> superClasses = new LinkedList<>();

      if (diagramElements == null)
         return true;

      if (diagramElements.length == 1)
         return true;

      //build list of Ids of all classes selected
      for (IDiagramElement diagramElement : diagramElements) {

         ISimpleRelationship[] relationshipsTo = diagramElement.getModelElement().toToRelationshipArray();

         for (int i = 0; relationshipsTo != null && i < relationshipsTo.length; i++) {
            ISimpleRelationship relationshipTo = relationshipsTo[i];
            String superClassType = relationshipTo.getFrom() != null ? relationshipTo.getFrom().getModelType() : "";

            if (!(superClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS)))
               continue;

            IClass superClass = (IClass) relationshipTo.getFrom();

            superClasses.add(superClass.getId());
         }
      }

      //Iterates over the list of the superclasses Id
      //then iterate over the list again to count if the id
      //was inserted the same amount as the amount of selected classes
      int counter = 0;

      for (int i = 0; i < superClasses.size(); i++) {
         String id = superClasses.get(i);

         for (int j = 0; j < superClasses.size(); j++) {
            String idAux = superClasses.get(j);

            if (id.equals(idAux))
               counter++;
         }

         if (counter == countClassesSelected())
            return true;
         else
            counter = 0;
      }

      return false;
   }

   private int countClassesSelected() {

      IDiagramElement[] diagramElements = ApplicationManager.instance().getDiagramManager().getActiveDiagram().getSelectedDiagramElement();
      int count = 0;

      if (diagramElements == null)
         return count;

      for (IDiagramElement diagramElement : diagramElements) {
         if (diagramElement.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
            count++;
      }

      return count++;
   }

}