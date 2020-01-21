package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.IStereotype;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in
 * model elements.
 * 
 * @author Claudenir Fonseca
 *
 */
public class ApplyStereotype implements VPContextActionController {

	//Class stereotypes (ordered as in plugin.xml)
	public static final String ACTION_ADD_STEREOTYPE_TYPE = "it.unibz.inf.ontouml.vp.addTypeStereotype";
	public static final String ACTION_ADD_STEREOTYPE_POWERTYPE = "it.unibz.inf.ontouml.vp.addPowertypeStereotype";
	
	public static final String ACTION_ADD_STEREOTYPE_HISTORICAL_ROLE = "it.unibz.inf.ontouml.vp.addHistoricalRoleStereotype";
	public static final String ACTION_ADD_STEREOTYPE_EVENT = "it.unibz.inf.ontouml.vp.addEventStereotype";
	
	public static final String ACTION_ADD_STEREOTYPE_ENUMERATION = "it.unibz.inf.ontouml.vp.addEnumerationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_DATATYPE = "it.unibz.inf.ontouml.vp.addDatatypeStereotype";
	
	public static final String ACTION_ADD_STEREOTYPE_SUBKIND = "it.unibz.inf.ontouml.vp.addSubkindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_ROLE_MIXIN = "it.unibz.inf.ontouml.vp.addRoleMixinStereotype";
	public static final String ACTION_ADD_STEREOTYPE_ROLE = "it.unibz.inf.ontouml.vp.addRoleStereotype";
	public static final String ACTION_ADD_STEREOTYPE_RELATOR_KIND = "it.unibz.inf.ontouml.vp.addRelatorKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_QUANTITY = "it.unibz.inf.ontouml.vp.addQuantityStereotype";
	public static final String ACTION_ADD_STEREOTYPE_QUALITY_KIND = "it.unibz.inf.ontouml.vp.addQualityKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_PHASE_MIXIN = "it.unibz.inf.ontouml.vp.addPhaseMixinStereotype";
	public static final String ACTION_ADD_STEREOTYPE_PHASE = "it.unibz.inf.ontouml.vp.addPhaseStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MODE_KIND = "it.unibz.inf.ontouml.vp.addModeKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MIXIN = "it.unibz.inf.ontouml.vp.addMixinStereotype";
	public static final String ACTION_ADD_STEREOTYPE_KIND = "it.unibz.inf.ontouml.vp.addKindStereotype";
	public static final String ACTION_ADD_STEREOTYPE_COLLECTIVE = "it.unibz.inf.ontouml.vp.addCollectiveStereotype";
	public static final String ACTION_ADD_STEREOTYPE_CATEGORY = "it.unibz.inf.ontouml.vp.addCategoryStereotype";
	
	//Association stereotypes (ordered as in plugin.xml)

	public static final String ACTION_ADD_STEREOTYPE_INSTANTIATION = "it.unibz.inf.ontouml.vp.addInstantiationStereotype";
	
	public static final String ACTION_ADD_STEREOTYPE_TERMINATION = "it.unibz.inf.ontouml.vp.addTerminationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_PARTICIPATIONAL = "it.unibz.inf.ontouml.vp.addParticipationalStereotype";
	public static final String ACTION_ADD_STEREOTYPE_PARTICIPATION = "it.unibz.inf.ontouml.vp.addParticipationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_HISTORICAL_DEPENDENCE = "it.unibz.inf.ontouml.vp.addHistoricalDependenceStereotype";
	public static final String ACTION_ADD_STEREOTYPE_CREATION = "it.unibz.inf.ontouml.vp.addCreationStereotype";
	
	public static final String ACTION_ADD_STEREOTYPE_SUB_QUANTITY_OF = "it.unibz.inf.ontouml.vp.addSubQuantityStereotype";
	public static final String ACTION_ADD_STEREOTYPE_SUB_COLLECTION_OF = "it.unibz.inf.ontouml.vp.addSubCollectionStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MEMBER_OF = "it.unibz.inf.ontouml.vp.addMemberOfStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MEDIATION = "it.unibz.inf.ontouml.vp.addMediationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MATERIAL = "it.unibz.inf.ontouml.vp.addMaterialStereotype";
	public static final String ACTION_ADD_STEREOTYPE_HISTORICAL = "it.unibz.inf.ontouml.vp.addHistoricalStereotype";
	public static final String ACTION_ADD_STEREOTYPE_EXTERNAL_DEPENDENCE = "it.unibz.inf.ontouml.vp.addExternalDependenceStereotype";
	public static final String ACTION_ADD_STEREOTYPE_COMPONENT_OF = "it.unibz.inf.ontouml.vp.addComponentOfStereotype";
	public static final String ACTION_ADD_STEREOTYPE_COMPARATIVE = "it.unibz.inf.ontouml.vp.addComparativeStereotype";
	public static final String ACTION_ADD_STEREOTYPE_CHARACTERIZATION = "it.unibz.inf.ontouml.vp.addCharacterizationStereotype";

	//Attribute stereotypes (ordered as in plugin.xml)
	public static final String ACTION_ADD_STEREOTYPE_END = "it.unibz.inf.ontouml.vp.addEndStereotype";
	public static final String ACTION_ADD_STEREOTYPE_BEGIN = "it.unibz.inf.ontouml.vp.addBeginStereotype";


	// Default colors
	public static final Color COLOR_TYPE = new Color(211, 211, 252);
	public static final Color COLOR_POWERTYPE = new Color(211, 211, 252);
	
	public static final Color COLOR_EVENT = new Color(252, 252, 212);
	
	public static final Color COLOR_ENUMERATION = new Color(255, 255, 255);
	public static final Color COLOR_DATATYPE = new Color(255, 255, 255);
	
	public static final Color COLOR_FUNCTIONAL_COMPLEX_KIND = new Color(255, 218, 221);
	public static final Color COLOR_COLLECTIVE = new Color(255, 218, 221);
	public static final Color COLOR_QUANTITY = new Color(255, 218, 221);
	public static final Color COLOR_RELATOR_KIND = new Color(211, 255, 211);
	public static final Color COLOR_MODE_KIND = new Color(192, 237, 255);
	public static final Color COLOR_QUALITY_KIND = new Color(192, 237, 255);
	
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
		case ACTION_ADD_STEREOTYPE_TYPE:
			element.addStereotype(StereotypeUtils.STR_TYPE);
			paint(context, COLOR_TYPE);
			break;
		case ACTION_ADD_STEREOTYPE_POWERTYPE:
			element.addStereotype(StereotypeUtils.STR_POWERTYPE);
			paint(context, COLOR_POWERTYPE);
			break;
		case ACTION_ADD_STEREOTYPE_HISTORICAL_ROLE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_ROLE);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_EVENT:
			element.addStereotype(StereotypeUtils.STR_EVENT);
			paint(context, COLOR_EVENT);
			break;
		case ACTION_ADD_STEREOTYPE_ENUMERATION:
			element.addStereotype(StereotypeUtils.STR_ENUMERATION);
			paint(context, COLOR_ENUMERATION);
			break;
		case ACTION_ADD_STEREOTYPE_DATATYPE:
			element.addStereotype(StereotypeUtils.STR_DATATYPE);
			paint(context, COLOR_DATATYPE);
			break;
		case ACTION_ADD_STEREOTYPE_SUBKIND:
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_ROLE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_ROLE:
			element.addStereotype(StereotypeUtils.STR_ROLE);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_RELATOR_KIND:
			element.addStereotype(StereotypeUtils.STR_RELATOR_KIND);
			paint(context, COLOR_RELATOR_KIND);
			break;			
		case ACTION_ADD_STEREOTYPE_QUANTITY:
			element.addStereotype(StereotypeUtils.STR_QUANTITY);
			paint(context, COLOR_QUANTITY);
			break;
		case ACTION_ADD_STEREOTYPE_QUALITY_KIND:
			element.addStereotype(StereotypeUtils.STR_QUALITY_KIND);
			paint(context, COLOR_QUALITY_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_PHASE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_PHASE:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			paint(context, getSpecializedColor(context));
			break;
		case ACTION_ADD_STEREOTYPE_MODE_KIND:
			element.addStereotype(StereotypeUtils.STR_MODE_KIND);
			paint(context, COLOR_MODE_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_MIXIN:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		case ACTION_ADD_STEREOTYPE_KIND:
			element.addStereotype(StereotypeUtils.STR_KIND);
			paint(context, COLOR_FUNCTIONAL_COMPLEX_KIND);
			break;
		case ACTION_ADD_STEREOTYPE_COLLECTIVE:
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE);
			paint(context, COLOR_COLLECTIVE);
			break;
		case ACTION_ADD_STEREOTYPE_CATEGORY:
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
			setAbstract(element);
			paint(context, COLOR_NON_SORTAL);
			break;
		
		
		case ACTION_ADD_STEREOTYPE_INSTANTIATION:
			element.addStereotype(StereotypeUtils.STR_INSTANTIATION);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_TERMINATION:
			element.addStereotype(StereotypeUtils.STR_TERMINATION);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_PARTICIPATIONAL:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATIONAL);
			setAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_PARTICIPATION:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATION);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_HISTORICAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_CREATION:
			element.addStereotype(StereotypeUtils.STR_CREATION);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_MATERIAL:
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_COMPARATIVE:
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_HISTORICAL:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_MEDIATION:
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_CHARACTERIZATION:
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_EXTERNAL_DEPENDENCE:
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			removeAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_COMPONENT_OF:
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			setAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_MEMBER_OF:
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			setAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_SUB_COLLECTION_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			setAggregationKind(element);
			break;
		case ACTION_ADD_STEREOTYPE_SUB_QUANTITY_OF:
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			setAggregationKind(element);
			break;
		
		case ACTION_ADD_STEREOTYPE_BEGIN:
			element.addStereotype(StereotypeUtils.STR_BEGIN);
			break;
		case ACTION_ADD_STEREOTYPE_END:
			element.addStereotype(StereotypeUtils.STR_END);
			break;
		}
	}

	@Override
	public void update(VPAction action, VPContext context) {
	}

	/**
	 * 
	 * Paints the assigned diagram element with the assigned color. No effect
	 * whenever auto-coloring is disabled or color is <code>null</code>.
	 * 
	 * @param diagramElement
	 * @param color
	 */
	private void paint(VPContext context, Color color) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled() || color == null) {
			return;
		}
		
		
		final IModelElement _class = context.getModelElement();
		final IDiagramElement[] diagramElements =  _class.getDiagramElements();
		
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
	 * Returns first sortal color occurring on one of the generalizations of this
	 * class. If generalization with such color is found, returns <code>null</code>.
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

				if (superColor.equals(COLOR_FUNCTIONAL_COMPLEX_KIND)
						|| superColor.equals(COLOR_FUNCTIONAL_COMPLEX_SORTAL)) {
					return COLOR_FUNCTIONAL_COMPLEX_SORTAL;
				} else if (superColor.equals(COLOR_COLLECTIVE) || superColor.equals(COLOR_COLLECTIVE_SORTAL)) {
					return COLOR_COLLECTIVE_SORTAL;
				} else if (superColor.equals(COLOR_QUANTITY) || superColor.equals(COLOR_QUANTITY_SORTAL)) {
					return COLOR_QUANTITY_SORTAL;
				} else if (superColor.equals(COLOR_RELATOR_KIND) || superColor.equals(COLOR_RELATOR_SORTAL)) {
					return COLOR_RELATOR_SORTAL;
				} else if (superColor.equals(COLOR_MODE_KIND) || superColor.equals(COLOR_MODE_SORTAL)) {
					return COLOR_MODE_SORTAL;
				} else if (superColor.equals(COLOR_QUALITY_KIND) || superColor.equals(COLOR_QUALITY_SORTAL)) {
					return COLOR_QUALITY_SORTAL;
				}
			}
		}

		return null;
	}
	
	private void setAggregationKind(IModelElement element){
		IAssociation association = (IAssociation) element;
		IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) association.getToEnd();
		
		if(compositionToEnd.getAggregationKind().equals(IAssociationEnd.AGGREGATION_KIND_NONE)){
			compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
			compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		}
		
		return;
	}
	
	private void removeAggregationKind(IModelElement element){ 
		IAssociationEnd compositionFromEnd = (IAssociationEnd) ((IAssociation)element).getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) ((IAssociation)element).getToEnd();
		
		compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		
		return;
	}
	
	private void setAbstract(IModelElement element){
		((IClass)element).setAbstract(true);
		
		return;
	}
}
