package it.unibz.inf.ontouml.vp.controllers;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.SmartModelling;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.awt.event.ActionEvent;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

public class ApplyStereotypeFixedMenu implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {

		IDiagramElement[] diagramElements = ApplicationManager.instance().getDiagramManager().getActiveDiagram().getSelectedDiagramElement();

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

		final IStereotype[] stereotypes = element.toStereotypeModelArray();

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}

		switch (action.getActionId()) {
		case ActionIds.TYPE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_TYPE);
			break;
		case ActionIds.HISTORICAL_ROLE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_ROLE);
			break;
		case ActionIds.EVENT_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_EVENT);
			break;
		case ActionIds.ENUMERATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_ENUMERATION);
			break;
		case ActionIds.DATATYPE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_DATATYPE);
			break;
		case ActionIds.SUBKIND_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			break;
		case ActionIds.ROLE_MIXIN_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
			break;
		case ActionIds.ROLE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_ROLE);
			break;
		case ActionIds.RELATOR_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_RELATOR);
			break;
		case ActionIds.QUANTITY_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_QUANTITY);
			break;
		case ActionIds.QUALITY_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_QUALITY);
			break;
		case ActionIds.PHASE_MIXIN_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
			break;
		case ActionIds.PHASE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_PHASE);
			break;
		case ActionIds.MODE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_MODE);
			break;
		case ActionIds.MIXIN_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_MIXIN);
			break;
		case ActionIds.KIND_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_KIND);
			break;
		case ActionIds.COLLECTIVE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE);
			break;
		case ActionIds.CATEGORY_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
			break;
		case ActionIds.INSTANTIATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_INSTANTIATION);
			break;
		case ActionIds.TERMINATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_TERMINATION);
			break;
		case ActionIds.PARTICIPATIONAL_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATIONAL);
			break;
		case ActionIds.PARTICIPATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_PARTICIPATION);
			break;
		case ActionIds.HISTORICAL_DEPENDENCE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_HISTORICAL_DEPENDENCE);
			break;
		case ActionIds.CREATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_CREATION);
			break;
		case ActionIds.MANIFESTATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_MANIFESTATION);
			break;
		case ActionIds.MATERIAL_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			break;
		case ActionIds.COMPARATIVE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			break;
		case ActionIds.MEDIATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			break;
		case ActionIds.CHARACTERIZATION_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			break;
		case ActionIds.EXTERNAL_DEPENDENCE_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			break;
		case ActionIds.COMPONENT_OF_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			break;
		case ActionIds.MEMBER_OF_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			break;
		case ActionIds.SUB_COLLECTION_OF_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			break;
		case ActionIds.SUB_QUANTITY_OF_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			break;
		case ActionIds.BEGIN_FIXEDMENU:
			element.addStereotype(StereotypeUtils.STR_BEGIN);
			break;
		case ActionIds.END_FIXEDMENU:
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

}
