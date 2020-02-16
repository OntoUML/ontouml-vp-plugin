package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.*;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 * 
 * @author Claudenir Fonseca
 *
 */
public class ApplyStereotype implements VPContextActionController {

	// Default colors
	public static final Color COLOR_TYPE = new Color(211, 211, 252);
	public static final Color COLOR_POWERTYPE = new Color(211, 211, 252);

	public static final Color COLOR_EVENT = new Color(252, 252, 212);

	public static final Color COLOR_ENUMERATION = new Color(255, 255, 255);
	public static final Color COLOR_DATATYPE = new Color(255, 255, 255);

	public static final Color COLOR_FUNCTIONAL_COMPLEX_KIND = new Color(255, 218, 221);
	public static final Color COLOR_COLLECTIVE = new Color(255, 218, 221);
	public static final Color COLOR_QUANTITY = new Color(255, 218, 221);
	public static final Color COLOR_RELATOR = new Color(211, 255, 211);
	public static final Color COLOR_MODE = new Color(192, 237, 255);
	public static final Color COLOR_QUALITY = new Color(192, 237, 255);

	public static final Color COLOR_FUNCTIONAL_COMPLEX_SORTAL = new Color(255, 218, 221);
	public static final Color COLOR_COLLECTIVE_SORTAL = new Color(255, 218, 221);
	public static final Color COLOR_QUANTITY_SORTAL = new Color(255, 218, 221);
	public static final Color COLOR_RELATOR_SORTAL = new Color(211, 255, 211);
	public static final Color COLOR_MODE_SORTAL = new Color(192, 237, 255);
	public static final Color COLOR_QUALITY_SORTAL = new Color(192, 237, 255);

	public static final Color COLOR_NON_SORTAL = new Color(224, 224, 224);

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		final IModelElement element = context.getModelElement();
		final IStereotype[] stereotypes = element.toStereotypeModelArray();

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}

		switch (action.getActionId()) {
		case ActionIds.TYPE:
			element.addStereotype(StereotypeUtils.STR_TYPE);
			paint(context, COLOR_TYPE);
			break;
		case ActionIds.POWERTYPE:
			element.addStereotype(StereotypeUtils.STR_POWERTYPE);
			paint(context, COLOR_POWERTYPE);
			break;
		case ActionIds.HISTORICAL_ROLE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_ROLE);
			paint(context, getSpecializedColor(context));
			break;
		case ActionIds.EVENT:
			element.addStereotype(StereotypeUtils.STR_EVENT);
			paint(context, COLOR_EVENT);
			break;
		case ActionIds.ENUMERATION:
			element.addStereotype(StereotypeUtils.STR_ENUMERATION);
			paint(context, COLOR_ENUMERATION);
			break;
		case ActionIds.DATATYPE:
			element.addStereotype(StereotypeUtils.STR_DATATYPE);
			paint(context, COLOR_DATATYPE);
			break;
		case ActionIds.SUBKIND:
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			paint(context, getSpecializedColor(context));
			break;
		case ActionIds.ROLE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ActionIds.ROLE:
			element.addStereotype(StereotypeUtils.STR_ROLE);
			paint(context, getSpecializedColor(context));
			break;
		case ActionIds.RELATOR:
			element.addStereotype(StereotypeUtils.STR_RELATOR);
			paint(context, COLOR_RELATOR);
			break;
		case ActionIds.QUANTITY:
			element.addStereotype(StereotypeUtils.STR_QUANTITY);
			paint(context, COLOR_QUANTITY);
			break;
		case ActionIds.QUALITY:
			element.addStereotype(StereotypeUtils.STR_QUALITY);
			paint(context, COLOR_QUALITY);
			break;
		case ActionIds.PHASE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ActionIds.PHASE:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			paint(context, getSpecializedColor(context));
			break;
		case ActionIds.MODE:
			element.addStereotype(StereotypeUtils.STR_MODE);
			paint(context, COLOR_MODE);
			break;
		case ActionIds.MIXIN:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ActionIds.KIND:
			element.addStereotype(StereotypeUtils.STR_KIND);
			paint(context, COLOR_FUNCTIONAL_COMPLEX_KIND);
			break;
		case ActionIds.COLLECTIVE:
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE);
			paint(context, COLOR_COLLECTIVE);
			break;
		case ActionIds.CATEGORY:
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
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
	}

	@Override
	public void update(VPAction action, VPContext context) {
		IModelElement element = context.getModelElement();

		if (!element.getModelType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION))
			return;

		IAssociation association = (IAssociation) element;

		if (!association.getFrom().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
			return;

		if (!association.getTo().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
			return;

		IClass source = (IClass) association.getFrom();
		IClass target = (IClass) association.getTo();

		ArrayList<String> sourceStereotypes = new ArrayList<String>(Arrays.asList(source.toStereotypeArray()));
		ArrayList<String> targetStereotypes = new ArrayList<String>(Arrays.asList(target.toStereotypeArray()));

		if (sourceStereotypes.size() == 0 || targetStereotypes.size() == 0) {
			//if any end has no stereotypes everything is allowed
			action.setEnabled(true);
		} else if (sourceStereotypes.size() == 1 && targetStereotypes.size() == 1) {

			String sourceStereotype = sourceStereotypes.get(0);
			String targetStereotype = targetStereotypes.get(0);

			switch (sourceStereotype) {
			case StereotypeUtils.STR_KIND:
				SourceKind.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_QUANTITY:
				SourceQuantity.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_COLLECTIVE:
				SourceCollective.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_RELATOR:
				SourceRelator.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_MODE:
				SourceMode.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_QUALITY:
				SourceQuality.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_SUBKIND:
				SourceSubKind.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_ROLE:
				SourceRole.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_PHASE:
				SourcePhase.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_CATEGORY:
				SourceCategory.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_MIXIN:
				SourceMixin.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_ROLE_MIXIN:
				SourceRoleMixin.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_PHASE_MIXIN:
				SourcePhaseMixin.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_HISTORICAL_ROLE:
				SourceHistoricalRole.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_EVENT:
				SourceEvent.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_TYPE:
				SourceType.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_DATATYPE:
				SourceDataType.setAction(action, targetStereotype);
				break;
			case StereotypeUtils.STR_ENUMERATION:
				SourceEnumeration.setAction(action, targetStereotype);
				break;
			default:
				action.setEnabled(false);
			}
		} else {
			//if any end has more than 1 stereotypes nothing is allowed
			action.setEnabled(false);
		}

	}

	/**
	 * 
	 * Paints the assigned diagram element with the assigned color. No effect whenever auto-coloring is disabled or color is <code>null</code>.
	 * 
	 * @param diagramElement
	 * @param color
	 */
	private void paint(VPContext context, Color color) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled() || color == null) {
			return;
		}

		final IModelElement _class = context.getModelElement();
		final IDiagramElement[] diagramElements = _class.getDiagramElements();

		for (int i = 0; diagramElements != null && i < diagramElements.length; i++) {
			IDiagramElement diagramElement = diagramElements[i];
			if (diagramElement instanceof IShapeUIModel) {
				((IShapeUIModel) diagramElement).getFillColor().setColor1(color);
			} else {
				diagramElement.setForeground(color);
			}
		}

	}

	/**
	 * 
	 * Returns first sortal color occurring on one of the generalizations of this class. If generalization with such color is found, returns <code>null</code>.
	 * 
	 * @param context
	 * 
	 */
	private Color getSpecializedColor(VPContext context) {
		final IModelElement sourceModelElement = context.getModelElement();
		final ISimpleRelationship[] specializations = sourceModelElement.toToRelationshipArray();

		for (int i = 0; specializations != null && i < specializations.length; i++) {
			if (!(specializations[i] instanceof IGeneralization)) {
				continue;
			}

			final IModelElement superClass = specializations[i].getFrom();
			final IDiagramElement[] superDiagramElements = superClass.getDiagramElements();

			for (int j = 0; j < superDiagramElements.length; j++) {
				if (!(superDiagramElements[j] instanceof IShapeUIModel)) {
					continue;
				}

				final Color superColor = ((IShapeUIModel) superDiagramElements[j]).getFillColor().getColor1();

				if (superColor.equals(COLOR_FUNCTIONAL_COMPLEX_KIND) || superColor.equals(COLOR_FUNCTIONAL_COMPLEX_SORTAL)) {
					return COLOR_FUNCTIONAL_COMPLEX_SORTAL;
				} else if (superColor.equals(COLOR_COLLECTIVE) || superColor.equals(COLOR_COLLECTIVE_SORTAL)) {
					return COLOR_COLLECTIVE_SORTAL;
				} else if (superColor.equals(COLOR_QUANTITY) || superColor.equals(COLOR_QUANTITY_SORTAL)) {
					return COLOR_QUANTITY_SORTAL;
				} else if (superColor.equals(COLOR_RELATOR) || superColor.equals(COLOR_RELATOR_SORTAL)) {
					return COLOR_RELATOR_SORTAL;
				} else if (superColor.equals(COLOR_MODE) || superColor.equals(COLOR_MODE_SORTAL)) {
					return COLOR_MODE_SORTAL;
				} else if (superColor.equals(COLOR_QUALITY) || superColor.equals(COLOR_QUALITY_SORTAL)) {
					return COLOR_QUALITY_SORTAL;
				}
			}
		}

		return null;
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
