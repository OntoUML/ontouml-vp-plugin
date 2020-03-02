package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.features.constraints.AssociationConstraints;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 * 
 * @author Claudenir Fonseca
 *
 */
public class ApplyStereotype implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		final IModelElement element = context.getModelElement();
		final IStereotype[] stereotypes = element.toStereotypeModelArray();

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}

		switch (action.getActionId()) {
		/*
		 * case ActionIds.POWERTYPE: element.addStereotype(StereotypeUtils.STR_POWERTYPE); paint(context, COLOR_POWERTYPE); break;
		 */
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
			setAbstract(element);
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
			setAbstract(element);
			break;
		case ActionIds.PHASE:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			break;
		case ActionIds.MODE:
			element.addStereotype(StereotypeUtils.STR_MODE);
			break;
		case ActionIds.MIXIN:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			setAbstract(element);
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
			removeAggregationKind(element);
			break;
		case ActionIds.TERMINATION:
			element.addStereotype(StereotypeUtils.STR_TERMINATION);
			removeAggregationKind(element);
			break;
		case ActionIds.PARTICIPATIONAL:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATIONAL);
			setAggregationKind(element);
			break;
		case ActionIds.PARTICIPATION:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATION);
			removeAggregationKind(element);
			break;
		case ActionIds.HISTORICAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
			removeAggregationKind(element);
			break;
		case ActionIds.CREATION:
			element.addStereotype(StereotypeUtils.STR_CREATION);
			removeAggregationKind(element);
			break;
		case ActionIds.MANIFESTATION:
			element.addStereotype(StereotypeUtils.STR_MANIFESTATION);
			removeAggregationKind(element);
			break;
		case ActionIds.MATERIAL:
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			removeAggregationKind(element);
			break;
		case ActionIds.COMPARATIVE:
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			removeAggregationKind(element);
			break;
		case ActionIds.MEDIATION:
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			removeAggregationKind(element);
			break;
		case ActionIds.CHARACTERIZATION:
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			removeAggregationKind(element);
			break;
		case ActionIds.EXTERNAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			removeAggregationKind(element);
			break;
		case ActionIds.COMPONENT_OF:
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			setAggregationKind(element);
			break;
		case ActionIds.MEMBER_OF:
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			setAggregationKind(element);
			break;
		case ActionIds.SUB_COLLECTION_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			setAggregationKind(element);
			break;
		case ActionIds.SUB_QUANTITY_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			setAggregationKind(element);
			break;
		case ActionIds.BEGIN:
			element.addStereotype(StereotypeUtils.STR_BEGIN);
			break;
		case ActionIds.END:
			element.addStereotype(StereotypeUtils.STR_END);
			break;
		}
		
		SmartColoring.paint((IClass) element);
		SmartColoring.smartPaint();
	}

	@Override
	public void update(VPAction action, VPContext context) {
		final IModelElement element = context.getModelElement();

		if (!element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION))
			return;

		final IAssociation association = (IAssociation) element;

		if (!association.getFrom().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
			return;

		if (!association.getTo().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
			return;

		final IClass source = (IClass) association.getFrom();
		final IClass target = (IClass) association.getTo();

		final ArrayList<String> sourceStereotypes = new ArrayList<String>(Arrays.asList(source.toStereotypeArray()));
		final ArrayList<String> targetStereotypes = new ArrayList<String>(Arrays.asList(target.toStereotypeArray()));

		if (sourceStereotypes.size() == 0 || targetStereotypes.size() == 0) {
			// if any end has no stereotypes everything is allowed
			action.setEnabled(true);
			return;
		}

		if (sourceStereotypes.size() > 1 || targetStereotypes.size() > 1) {
			// if any end has more than 1 stereotypes nothing is allowed
			action.setEnabled(false);
			return;
		}

		// continue if both ends has ONLY ONE stereotype in both ends
		final String sourceStereotype = sourceStereotypes.get(0);
		final String targetStereotype = targetStereotypes.get(0);
		final ArrayList<String> allowedCombinations = AssociationConstraints.allowedCombinations.get(new SimpleEntry<String, String>(sourceStereotype, targetStereotype));

		if (allowedCombinations == null || !allowedCombinations.contains(action.getActionId())) {
			action.setEnabled(false);
		}
		else {
			action.setEnabled(true);
		}
	}

	private void setAggregationKind(IModelElement element) {
		IAssociation association = (IAssociation) element;
		IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) association.getToEnd();

		if (compositionToEnd.getAggregationKind().equals(IAssociationEnd.AGGREGATION_KIND_NONE)) {
			compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
			compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		}

		return;
	}

	private void removeAggregationKind(IModelElement element) {
		IAssociationEnd compositionFromEnd = (IAssociationEnd) ((IAssociation) element).getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) ((IAssociation) element).getToEnd();

		compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);

		return;
	}

	private void setAbstract(IModelElement element) {
		((IClass) element).setAbstract(true);

		return;
	}
}
