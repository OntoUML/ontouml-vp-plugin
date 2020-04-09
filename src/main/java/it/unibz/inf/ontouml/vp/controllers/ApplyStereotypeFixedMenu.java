package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.SmartModelling;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ApplyStereotypeFixedMenu implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {

		IDiagramElement[] diagramElements = ApplicationManager.instance().getDiagramManager().getActiveDiagram()
				.getSelectedDiagramElement();

		if (diagramElements == null) {
			applyStereotype(action, context.getModelElement());
			return;
		}

		for (IDiagramElement diagramElement : diagramElements) {
			if (diagramElement.getModelElement().getModelType().equals(context.getModelElement().getModelType()))
				applyStereotype(action, diagramElement.getModelElement());
		}

	}

	@Override
	public void update(VPAction arg0, VPContext arg1) {

	}

	private void applyStereotype(VPAction action, IModelElement element) {
		
		switch (action.getActionId()) {
			case ActionIds.TYPE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TYPE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_TYPE, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_TYPE));
				break;
			case ActionIds.HISTORICAL_ROLE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_ROLE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.EVENT_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EVENT);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_EVENT, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_EVENT));
				break;
			case ActionIds.ENUMERATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ENUMERATION);
				break;
			case ActionIds.DATATYPE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_DATATYPE);
				break;
			case ActionIds.SUBKIND_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUBKIND);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_SUBKIND, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.ROLE_MIXIN_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE_MIXIN);
				break;
			case ActionIds.ROLE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_ROLE, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.RELATOR_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_RELATOR);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_RELATOR, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_RELATOR));
				break;
			case ActionIds.QUANTITY_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUANTITY);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_QUANTITY, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_QUANTITY));
				break;
			case ActionIds.QUALITY_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUALITY);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_QUALITY, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_QUALITY));
				break;
			case ActionIds.PHASE_MIXIN_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE_MIXIN);
				break;
			case ActionIds.PHASE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_PHASE, StereotypeUtils.toAllowedNaturesString());
				break;
			case ActionIds.MODE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MODE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_MODE, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_MODE));
				break;
			case ActionIds.MIXIN_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MIXIN);
				break;
			case ActionIds.KIND_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_KIND);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_KIND, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_OBJECT));
				break;
			case ActionIds.COLLECTIVE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COLLECTIVE);
				StereotypeUtils.setAllowed(element, StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.toAllowedNaturesString(StereotypeUtils.ALLOWED_COLLECTIVE));
				break;
			case ActionIds.CATEGORY_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CATEGORY);
				break;
			case ActionIds.INSTANTIATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_INSTANTIATION);
				break;
			case ActionIds.TERMINATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TERMINATION);
				break;
			case ActionIds.PARTICIPATIONAL_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATIONAL);
				break;
			case ActionIds.PARTICIPATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATION);
				break;
			case ActionIds.HISTORICAL_DEPENDENCE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
				break;
			case ActionIds.CREATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CREATION);
				break;
			case ActionIds.MANIFESTATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MANIFESTATION);
				break;
			case ActionIds.MATERIAL_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MATERIAL);
				break;
			case ActionIds.COMPARATIVE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPARATIVE);
				break;
			case ActionIds.MEDIATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEDIATION);
				break;
			case ActionIds.CHARACTERIZATION_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CHARACTERIZATION);
				break;
			case ActionIds.EXTERNAL_DEPENDENCE_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
				break;
			case ActionIds.COMPONENT_OF_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPONENT_OF);
				break;
			case ActionIds.MEMBER_OF_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEMBER_OF);
				break;
			case ActionIds.SUB_COLLECTION_OF_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_COLLECTION_OF);
				break;
			case ActionIds.SUB_QUANTITY_OF_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_QUANTITY_OF);
				break;
			case ActionIds.BEGIN_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_BEGIN);
				break;
			case ActionIds.END_FIXEDMENU:
				StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_END);
				break;
		}

		// switch (action.getActionId()) {
		// 	case ActionIds.TYPE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TYPE);
		// 		break;
		// 	case ActionIds.HISTORICAL_ROLE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_ROLE);
		// 		break;
		// 	case ActionIds.EVENT_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EVENT);
		// 		break;
		// 	case ActionIds.ENUMERATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ENUMERATION);
		// 		break;
		// 	case ActionIds.DATATYPE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_DATATYPE);
		// 		break;
		// 	case ActionIds.SUBKIND_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUBKIND);
		// 		break;
		// 	case ActionIds.ROLE_MIXIN_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE_MIXIN);
		// 		break;
		// 	case ActionIds.ROLE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_ROLE);
		// 		break;
		// 	case ActionIds.RELATOR_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_RELATOR);
		// 		break;
		// 	case ActionIds.QUANTITY_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUANTITY);
		// 		break;
		// 	case ActionIds.QUALITY_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_QUALITY);
		// 		break;
		// 	case ActionIds.PHASE_MIXIN_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE_MIXIN);
		// 		break;
		// 	case ActionIds.PHASE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PHASE);
		// 		break;
		// 	case ActionIds.MODE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MODE);
		// 		break;
		// 	case ActionIds.MIXIN_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MIXIN);
		// 		break;
		// 	case ActionIds.KIND_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_KIND);
		// 		break;
		// 	case ActionIds.COLLECTIVE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COLLECTIVE);
		// 		break;
		// 	case ActionIds.CATEGORY_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CATEGORY);
		// 		break;
		// 	case ActionIds.INSTANTIATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_INSTANTIATION);
		// 		break;
		// 	case ActionIds.TERMINATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_TERMINATION);
		// 		break;
		// 	case ActionIds.PARTICIPATIONAL_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATIONAL);
		// 		break;
		// 	case ActionIds.PARTICIPATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_PARTICIPATION);
		// 		break;
		// 	case ActionIds.HISTORICAL_DEPENDENCE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
		// 		break;
		// 	case ActionIds.CREATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CREATION);
		// 		break;
		// 	case ActionIds.MANIFESTATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MANIFESTATION);
		// 		break;
		// 	case ActionIds.MATERIAL_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MATERIAL);
		// 		break;
		// 	case ActionIds.COMPARATIVE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPARATIVE);
		// 		break;
		// 	case ActionIds.MEDIATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEDIATION);
		// 		break;
		// 	case ActionIds.CHARACTERIZATION_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_CHARACTERIZATION);
		// 		break;
		// 	case ActionIds.EXTERNAL_DEPENDENCE_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
		// 		break;
		// 	case ActionIds.COMPONENT_OF_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_COMPONENT_OF);
		// 		break;
		// 	case ActionIds.MEMBER_OF_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_MEMBER_OF);
		// 		break;
		// 	case ActionIds.SUB_COLLECTION_OF_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_COLLECTION_OF);
		// 		break;
		// 	case ActionIds.SUB_QUANTITY_OF_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_SUB_QUANTITY_OF);
		// 		break;
		// 	case ActionIds.BEGIN_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_BEGIN);
		// 		break;
		// 	case ActionIds.END_FIXEDMENU:
		// 		StereotypeUtils.applyStereotype(element, StereotypeUtils.STR_END);
		// 		break;
		// }

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

}
