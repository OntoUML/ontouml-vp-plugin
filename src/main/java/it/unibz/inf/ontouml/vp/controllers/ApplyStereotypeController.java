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
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.utils.ActionIdManager;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;

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
         if (diagramElement.getModelElement().getModelType().equals(context.getModelElement().getModelType())) {
        	 defineActionBehavior(action, diagramElement.getModelElement());
         }
      }

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