package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class AddOntoUMLStereotype implements VPContextActionController {
	
	public static final String ACTION_ADD_STEREOTYPE_KIND = "it.unibz.inf.ontouml.vp.addKindStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_COLLECTIVE_KIND = "it.unibz.inf.ontouml.vp.addCollectiveKindStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_QUANTITY_KIND = "it.unibz.inf.ontouml.vp.addQuantityKindStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_RELATOR_KIND = "it.unibz.inf.ontouml.vp.addRelatorKindStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_MODE_KIND = "it.unibz.inf.ontouml.vp.addModeKindStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_QUALITY_KIND = "it.unibz.inf.ontouml.vp.addQualityKindStereotype";
	
	public static final String ACTION_ADD_STEREOTYPE_SUBKIND = "it.unibz.inf.ontouml.vp.addSubkindStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_PHASE = "it.unibz.inf.ontouml.vp.addPhaseStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_ROLE = "it.unibz.inf.ontouml.vp.addRoleStereotype"; 
	
	public static final String ACTION_ADD_STEREOTYPE_CATEGORY = "it.unibz.inf.ontouml.vp.addCategoryStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_ROLE_MIXIN = "it.unibz.inf.ontouml.vp.addRoleMixinStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_PHASE_MIXIN = "it.unibz.inf.ontouml.vp.addPhaseMixinStereotype";
	public static final String ACTION_ADD_STEREOTYPE_MIXIN = "it.unibz.inf.ontouml.vp.addMixinStereotype"; 
	
	public static final String ACTION_ADD_STEREOTYPE_MATERIAL = "it.unibz.inf.ontouml.vp.addMaterialStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_COMPARATIVE = "it.unibz.inf.ontouml.vp.addComparativeStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_HISTORICAL = "it.unibz.inf.ontouml.vp.addHistoricalStereotype"; 
	
	public static final String ACTION_ADD_STEREOTYPE_MEDIATION = "it.unibz.inf.ontouml.vp.addMediationStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_CHARACTERIZATION = "it.unibz.inf.ontouml.vp.addCharacterizationStereotype";
	public static final String ACTION_ADD_STEREOTYPE_EXTERNAL_DEPENDENCE = "it.unibz.inf.ontouml.vp.addExternalDependenceStereotype"; 
	
	public static final String ACTION_ADD_STEREOTYPE_COMPONENT_OF = "it.unibz.inf.ontouml.vp.addComponentOfStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_MEMBER_OF = "it.unibz.inf.ontouml.vp.addMemberOfStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_SUB_COLLECTION_OF = "it.unibz.inf.ontouml.vp.addSubCollectionStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_SUB_QUANTITY_OF = "it.unibz.inf.ontouml.vp.addSubQuantityStereotype"; 
	public static final String ACTION_ADD_STEREOTYPE_SUM = "it.unibz.inf.ontouml.vp.addSumStereotype"; 

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
//		System.out.println("It's me! A Mario!");
//		System.out.println("VPAction Label: "+arg0.getLabel());
//		System.out.println("VPAction ID: "+arg0.getActionId());
//		System.out.println("VPContext Type: "+arg1.getContextType());
		
		final IModelElement element = context.getModelElement();
		
		final IStereotype[] stereotypes = element.toStereotypeModelArray();
		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			element.removeStereotype(stereotypes[i]);
		}
		
		switch(action.getActionId()) {
		case ACTION_ADD_STEREOTYPE_KIND :
			element.addStereotype(StereotypeUtils.STR_KIND);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(204,255,153));
			break;
		case ACTION_ADD_STEREOTYPE_COLLECTIVE_KIND :
			element.addStereotype(StereotypeUtils.STR_COLLECTIVE_KIND);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(204,255,153));
			break;
		case ACTION_ADD_STEREOTYPE_QUANTITY_KIND :
			element.addStereotype(StereotypeUtils.STR_QUANTITY_KIND);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(204,255,153));
			break;
		case ACTION_ADD_STEREOTYPE_RELATOR_KIND :
			element.addStereotype(StereotypeUtils.STR_RELATOR_KIND);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(255,255,153));
			break;
		case ACTION_ADD_STEREOTYPE_MODE_KIND :
			element.addStereotype(StereotypeUtils.STR_MODE_KIND);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(255,204,153));
			break;
		case ACTION_ADD_STEREOTYPE_QUALITY_KIND :
			element.addStereotype(StereotypeUtils.STR_QUALITY_KIND);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(255,153,153));
			break;
		case ACTION_ADD_STEREOTYPE_SUBKIND :
			element.addStereotype(StereotypeUtils.STR_SUBKIND);
			break;
		case ACTION_ADD_STEREOTYPE_ROLE :
			element.addStereotype(StereotypeUtils.STR_ROLE);
			break;
		case ACTION_ADD_STEREOTYPE_PHASE :
			element.addStereotype(StereotypeUtils.STR_PHASE);
			break;
		case ACTION_ADD_STEREOTYPE_CATEGORY :
			element.addStereotype(StereotypeUtils.STR_CATEGORY);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(224,224,224));
			break;
		case ACTION_ADD_STEREOTYPE_ROLE_MIXIN :
			element.addStereotype(StereotypeUtils.STR_ROLE_MIXIN);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(224,224,224));
			break;
		case ACTION_ADD_STEREOTYPE_PHASE_MIXIN :
			element.addStereotype(StereotypeUtils.STR_PHASE_MIXIN);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(224,224,224));
			break;
		case ACTION_ADD_STEREOTYPE_MIXIN :
			element.addStereotype(StereotypeUtils.STR_MIXIN);
//			((IShapeUIModel) context.getDiagramElement()).getFillColor().setColor1(new Color(224,224,224));
			break;
		case ACTION_ADD_STEREOTYPE_MATERIAL :
			element.addStereotype(StereotypeUtils.STR_MATERIAL);
			break;
		case ACTION_ADD_STEREOTYPE_COMPARATIVE :
			element.addStereotype(StereotypeUtils.STR_COMPARATIVE);
			break;
		case ACTION_ADD_STEREOTYPE_HISTORICAL :
			element.addStereotype(StereotypeUtils.STR_HISTORICAL);
			break;
		case ACTION_ADD_STEREOTYPE_MEDIATION :
			element.addStereotype(StereotypeUtils.STR_MEDIATION);
			break;
		case ACTION_ADD_STEREOTYPE_CHARACTERIZATION :
			element.addStereotype(StereotypeUtils.STR_CHARACTERIZATION);
			break;
		case ACTION_ADD_STEREOTYPE_EXTERNAL_DEPENDENCE :
			element.addStereotype(StereotypeUtils.STR_EXTERNAL_DEPENDENCE);
			break;
		case ACTION_ADD_STEREOTYPE_COMPONENT_OF :
			element.addStereotype(StereotypeUtils.STR_COMPONENT_OF);
			break;
		case ACTION_ADD_STEREOTYPE_MEMBER_OF :
			element.addStereotype(StereotypeUtils.STR_MEMBER_OF);
			break;
		case ACTION_ADD_STEREOTYPE_SUB_COLLECTION_OF :
			element.addStereotype(StereotypeUtils.STR_SUB_COLLECTION_OF);
			break;
		case ACTION_ADD_STEREOTYPE_SUB_QUANTITY_OF :
			element.addStereotype(StereotypeUtils.STR_SUB_QUANTITY_OF);
			break;
		case ACTION_ADD_STEREOTYPE_SUM :
			element.addStereotype(StereotypeUtils.STR_SUM);
			break;
		}
	}

	@Override
	public void update(VPAction action, VPContext context) {
	}

}
