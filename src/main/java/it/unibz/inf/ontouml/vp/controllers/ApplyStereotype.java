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

		if (diagramElements == null)
			return;

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

		if (diagramElements == null )
			return;
		
		for (IDiagramElement diagramElement : diagramElements) {
			if (diagramElement.getModelElement().getModelType().equals(context.getModelElement().getModelType()))
				defineActionBehavior(action, diagramElement.getModelElement());
		}

	}

	private void applyStereotype(VPAction action, IModelElement element) {

		final IStereotype[] stereotypes = element.toStereotypeModelArray();

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}

		switch (action.getActionId()) {
		case ActionIds.TYPE:
			element.addStereotype(StereotypeUtils.STR_TYPE);
			break;
		case ActionIds.HISTORICAL_ROLE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_ROLE);
			break;
		case ActionIds.EVENT:
			element.addStereotype(StereotypeUtils.STR_EVENT);
			break;
		case ActionIds.ENUMERATION:
			element.addStereotype(StereotypeUtils.STR_ENUMERATION);
			break;
		case ActionIds.DATATYPE:
			element.addStereotype(StereotypeUtils.STR_DATATYPE);
			break;
		case ActionIds.SUBKIND:
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			break;
		case ActionIds.ROLE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
			break;
		case ActionIds.ROLE:
			element.addStereotype(StereotypeUtils.STR_ROLE);
			break;
		case ActionIds.RELATOR:
			element.addStereotype(StereotypeUtils.STR_RELATOR);
			break;
		case ActionIds.QUANTITY:
			element.addStereotype(StereotypeUtils.STR_QUANTITY);
			break;
		case ActionIds.QUALITY:
			element.addStereotype(StereotypeUtils.STR_QUALITY);
			break;
		case ActionIds.PHASE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
			break;
		case ActionIds.PHASE:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			break;
		case ActionIds.MODE:
			element.addStereotype(StereotypeUtils.STR_MODE);
			break;
		case ActionIds.MIXIN:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			break;
		case ActionIds.KIND:
			element.addStereotype(StereotypeUtils.STR_KIND);
			break;
		case ActionIds.COLLECTIVE:
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE);
			break;
		case ActionIds.CATEGORY:
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
			break;
		case ActionIds.INSTANTIATION:
			element.addStereotype(StereotypeUtils.STR_INSTANTIATION);
			break;
		case ActionIds.TERMINATION:
			element.addStereotype(StereotypeUtils.STR_TERMINATION);
			break;
		case ActionIds.PARTICIPATIONAL:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATIONAL);
			break;
		case ActionIds.PARTICIPATION:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATION);
			break;
		case ActionIds.HISTORICAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
			break;
		case ActionIds.CREATION:
			element.addStereotype(StereotypeUtils.STR_CREATION);
			break;
		case ActionIds.MANIFESTATION:
			element.addStereotype(StereotypeUtils.STR_MANIFESTATION);
			break;
		case ActionIds.MATERIAL:
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			break;
		case ActionIds.COMPARATIVE:
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			break;
		case ActionIds.MEDIATION:
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			break;
		case ActionIds.CHARACTERIZATION:
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			break;
		case ActionIds.EXTERNAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			break;
		case ActionIds.COMPONENT_OF:
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			break;
		case ActionIds.MEMBER_OF:
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			break;
		case ActionIds.SUB_COLLECTION_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			break;
		case ActionIds.SUB_QUANTITY_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			break;
		case ActionIds.BEGIN:
			element.addStereotype(StereotypeUtils.STR_BEGIN);
			break;
		case ActionIds.END:
			element.addStereotype(StereotypeUtils.STR_END);
			break;
		}

		boolean isSmartModelingEnabled = Configurations.getInstance().getProjectConfigurations().isSmartModellingEnabled();

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
			return false;

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