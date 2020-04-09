package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.SmartModelling;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
 *
 */
public class ApplyStereotype implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {

		IDiagramElement[] diagramElements = ApplicationManager.instance().getDiagramManager().getActiveDiagram().getSelectedDiagramElement();

		if (diagramElements == null){
			applyStereotype(action, context.getModelElement());
			return;
		}

		for (IDiagramElement diagramElement : diagramElements) {
			if (diagramElement.getModelElement().getModelType().equals(context.getModelElement().getModelType()))
				applyStereotype(action, diagramElement.getModelElement());
		}

	}

	@Override
	public void update(VPAction action, VPContext context) {
		
		action.setEnabled(true);
		
		if(context.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS) && !isClassSelectionAllowed() )
			action.setEnabled(false);

		IDiagramElement[] diagramElements = ApplicationManager.instance().getDiagramManager().getActiveDiagram().getSelectedDiagramElement();

		if (diagramElements == null ) {
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
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TYPE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_TYPE, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_TYPE));
				break;
			case ActionIds.HISTORICAL_ROLE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_ROLE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.EVENT:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EVENT);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_EVENT, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_EVENT));
				break;
			case ActionIds.ENUMERATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ENUMERATION);
				break;
			case ActionIds.DATATYPE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_DATATYPE);
				break;
			case ActionIds.SUBKIND:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUBKIND);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_SUBKIND, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.ROLE_MIXIN:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE_MIXIN);
				break;
			case ActionIds.ROLE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_ROLE, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.RELATOR:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_RELATOR);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_RELATOR, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_RELATOR));
				break;
			case ActionIds.QUANTITY:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUANTITY);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_QUANTITY, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_QUANTITY));
				break;
			case ActionIds.QUALITY:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUALITY);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_QUALITY, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_QUALITY));
				break;
			case ActionIds.PHASE_MIXIN:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE_MIXIN);
				break;
			case ActionIds.PHASE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_PHASE, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.MODE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MODE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_MODE, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_MODE));
				break;
			case ActionIds.MIXIN:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MIXIN);
				break;
			case ActionIds.KIND:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_KIND);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_KIND, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_OBJECT));
				break;
			case ActionIds.COLLECTIVE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COLLECTIVE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_COLLECTIVE));
				break;
			case ActionIds.CATEGORY:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CATEGORY);
				break;
			case ActionIds.INSTANTIATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_INSTANTIATION);
				break;
			case ActionIds.TERMINATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TERMINATION);
				break;
			case ActionIds.PARTICIPATIONAL:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATIONAL);
				break;
			case ActionIds.PARTICIPATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATION);
				break;
			case ActionIds.HISTORICAL_DEPENDENCE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
				break;
			case ActionIds.CREATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CREATION);
				break;
			case ActionIds.MANIFESTATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MANIFESTATION);
				break;
			case ActionIds.MATERIAL:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MATERIAL);
				break;
			case ActionIds.COMPARATIVE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPARATIVE);
				break;
			case ActionIds.MEDIATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEDIATION);
				break;
			case ActionIds.CHARACTERIZATION:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CHARACTERIZATION);
				break;
			case ActionIds.EXTERNAL_DEPENDENCE:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
				break;
			case ActionIds.COMPONENT_OF:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPONENT_OF);
				break;
			case ActionIds.MEMBER_OF:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEMBER_OF);
				break;
			case ActionIds.SUB_COLLECTION_OF:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_COLLECTION_OF);
				break;
			case ActionIds.SUB_QUANTITY_OF:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_QUANTITY_OF);
				break;
			case ActionIds.BEGIN:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_BEGIN);
				break;
			case ActionIds.END:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_END);
				break;
		}

		boolean isSmartModelingEnabled = Configurations.getInstance().getProjectConfigurations()
				.isSmartModellingEnabled();

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
			if (isSmartModelingEnabled)
				SmartModelling.setClassMetaProperties((IClass) element);
		}

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
			if (isSmartModelingEnabled)
				SmartModelling.setAssociationMetaProperties((IAssociation) element);
		}

		if (Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled())
			SmartColoring.smartPaint();
	}

	private void defineActionBehavior(VPAction action, IModelElement element) {

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
			final IAssociation association = (IAssociation) element;
			SmartModelling.manageAssociationStereotypes(association, action);
			return;
		}

		if (element.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
			final IClass _class = (IClass) element;
			SmartModelling.manageClassStereotypes(_class, action);
			return;
		}
	}

	private boolean isClassSelectionAllowed() {

		IDiagramElement[] diagramElements = ApplicationManager.instance().getDiagramManager().getActiveDiagram().getSelectedDiagramElement();
		LinkedList<String> superClasses = new LinkedList<String>();

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