package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.SmartModelling;
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

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		StereotypeUtils.generate();

		final IModelElement element = context.getModelElement();
		final IStereotype[] originalStereotypes = element.toStereotypeModelArray();
		String addedStereotype = "";

		switch (action.getActionId()) {
			case ActionIds.TYPE:
				addedStereotype = StereotypeUtils.STR_TYPE;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_TYPE));
				break;
			case ActionIds.HISTORICAL_ROLE:
				addedStereotype = StereotypeUtils.STR_HISTORICAL_ROLE;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString());
				// setAllowed(element, addedStereotype, "[]");
				break;
			case ActionIds.EVENT:
				addedStereotype = StereotypeUtils.STR_EVENT;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_EVENT));
				// setAllowed(element, addedStereotype, "[\"event\"]");
				break;
			case ActionIds.ENUMERATION:
				addedStereotype = StereotypeUtils.STR_ENUMERATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.DATATYPE:
				addedStereotype = StereotypeUtils.STR_DATATYPE;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.SUBKIND:
				addedStereotype = StereotypeUtils.STR_SUBKIND;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString());
				// setAllowed(element, addedStereotype, "[]");
				break;
			case ActionIds.ROLE_MIXIN:
				addedStereotype = StereotypeUtils.STR_ROLE_MIXIN;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.ROLE:
				addedStereotype = StereotypeUtils.STR_ROLE;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString());
				// setAllowed(element, addedStereotype, "[]");
				break;
			case ActionIds.RELATOR:
				addedStereotype = StereotypeUtils.STR_RELATOR;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_RELATOR));
				// setAllowed(element, addedStereotype, "[\"relator\"]");
				break;
			case ActionIds.QUANTITY:
				addedStereotype = StereotypeUtils.STR_QUANTITY;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_QUANTITY));
				// setAllowed(element, addedStereotype, "[\"quantity\"]");
				break;
			case ActionIds.QUALITY:
				addedStereotype = StereotypeUtils.STR_QUALITY;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_QUALITY));
				// setAllowed(element, addedStereotype, "[\"quality\"]");
				break;
			case ActionIds.PHASE_MIXIN:
				addedStereotype = StereotypeUtils.STR_PHASE_MIXIN;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.PHASE:
				addedStereotype = StereotypeUtils.STR_PHASE;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString());
				// setAllowed(element, addedStereotype, "[]");
				break;
			case ActionIds.MODE:
				addedStereotype = StereotypeUtils.STR_MODE;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_MODE));
				// setAllowed(element, addedStereotype, "[\"mode\"]");
				break;
			case ActionIds.MIXIN:
				addedStereotype = StereotypeUtils.STR_MIXIN;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.KIND:
				addedStereotype = StereotypeUtils.STR_KIND;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_OBJECT));
				// setAllowed(element, addedStereotype, "[\"object\"]");
				break;
			case ActionIds.COLLECTIVE:
				addedStereotype = StereotypeUtils.STR_COLLECTIVE;
				element.addStereotype(addedStereotype);
				setAllowed(element, addedStereotype, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_COLLECTIVE));
				// setAllowed(element, addedStereotype, "[\"collective\"]");
				break;
			case ActionIds.CATEGORY:
				addedStereotype = StereotypeUtils.STR_CATEGORY;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.INSTANTIATION:
				addedStereotype = StereotypeUtils.STR_INSTANTIATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.TERMINATION:
				addedStereotype = StereotypeUtils.STR_TERMINATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.PARTICIPATIONAL:
				addedStereotype = StereotypeUtils.STR_PARTICIPATIONAL;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.PARTICIPATION:
				addedStereotype = StereotypeUtils.STR_PARTICIPATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.HISTORICAL_DEPENDENCE:
				addedStereotype = StereotypeUtils.STR_HISTORICAL_DEPENDENCE;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.CREATION:
				addedStereotype = StereotypeUtils.STR_CREATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.MANIFESTATION:
				addedStereotype = StereotypeUtils.STR_MANIFESTATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.MATERIAL:
				addedStereotype = StereotypeUtils.STR_MATERIAL;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.COMPARATIVE:
				addedStereotype = StereotypeUtils.STR_COMPARATIVE;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.MEDIATION:
				addedStereotype = StereotypeUtils.STR_MEDIATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.CHARACTERIZATION:
				addedStereotype = StereotypeUtils.STR_CHARACTERIZATION;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.EXTERNAL_DEPENDENCE:
				addedStereotype = StereotypeUtils.STR_EXTERNAL_DEPENDENCE;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.COMPONENT_OF:
				addedStereotype = StereotypeUtils.STR_COMPONENT_OF;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.MEMBER_OF:
				addedStereotype = StereotypeUtils.STR_MEMBER_OF;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.SUB_COLLECTION_OF:
				addedStereotype = StereotypeUtils.STR_SUB_COLLECTION_OF;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.SUB_QUANTITY_OF:
				addedStereotype = StereotypeUtils.STR_SUB_QUANTITY_OF;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.BEGIN:
				addedStereotype = StereotypeUtils.STR_BEGIN;
				element.addStereotype(addedStereotype);
				break;
			case ActionIds.END:
				addedStereotype = StereotypeUtils.STR_END;
				element.addStereotype(addedStereotype);
				break;
		}

		for (int i = 0; originalStereotypes != null && i < originalStereotypes.length; i++) {
			final IStereotype originalStereotype = originalStereotypes[i];
			final ITaggedValueContainer container = originalStereotype.getTaggedValues();
			final Iterator<?> values = container != null ? container.taggedValueIterator() : null;

			if (!originalStereotype.getName().equals(addedStereotype)) {
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();
					final ITaggedValueDefinition definition = value != null ? value.getTagDefinition() : null;
					final IStereotype stereotype = definition != null ? definition.getTagDefStereotype() : null;
					
					if(stereotype != null && stereotype.equals(originalStereotype)) {
						container.removeTaggedValue(value);
						value.delete();
					}
				}

				// element.removeStereotype(originalStereotype);
			}
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

	@Override
	public void update(VPAction action, VPContext context) {
		if (Configurations.getInstance().getProjectConfigurations().isSmartModellingEnabled()) {
			final IModelElement element = context.getModelElement();

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
		} else {
			action.setEnabled(true);
		}

	}

	public void setAllowed(IModelElement element, String stereotypeName, String allowed) {
		if(element.getTaggedValues() == null) { return ; }

		Iterator<?> values = element.getTaggedValues().taggedValueIterator();
			
		while(values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();
			final ITaggedValueDefinition definition = value != null ? value.getTagDefinition() : null;
			final IStereotype str = definition != null ? definition.getTagDefStereotype() : null;
			final String strName = str != null ? str.getName() : null;
			
			if(
				value.getName().equals("allowed") && 
				stereotypeName.equals(strName)
			) {
				value.setValue(allowed);
			}
		}
	}

}
