package it.unibz.inf.ontouml.vp.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class SmartModelling {

	public static void setAggregationKind(IModelElement element) {
		IAssociation association = (IAssociation) element;
		IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) association.getToEnd();

		if (compositionToEnd.getAggregationKind().equals(IAssociationEnd.AGGREGATION_KIND_NONE)) {
			compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
			compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		}

		return;
	}

	public static void removeAggregationKind(IModelElement element) {
		IAssociationEnd compositionFromEnd = (IAssociationEnd) ((IAssociation) element).getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) ((IAssociation) element).getToEnd();

		compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);

		return;
	}

	public static void setAbstract(IModelElement element) {
		((IClass) element).setAbstract(true);

		return;
	}

	private static boolean setCardinalityIfEmpty(IAssociationEnd end, String cardinality) {

		if (end.getMultiplicity() == null || end.getMultiplicity().equals("Unspecified")) {
			end.setMultiplicity(cardinality);
			return true;
		} else {
			return false;
		}
	}

	private static void setMetaProperties(IAssociation association) {
		IClass targetClass;
		final ArrayList<String> targetClassStereotypes;
		String targetStereotype="";
		
		if(association.getTo().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
			targetClass = (IClass) association.getTo();
			targetClassStereotypes = new ArrayList<String>(Arrays.asList(targetClass.toStereotypeArray()));
			
			if(targetClassStereotypes.size() == 1)
				targetStereotype = targetClassStereotypes.get(0);
		}

		IAssociationEnd source = (IAssociationEnd) association.getFromEnd();
		IAssociationEnd target = (IAssociationEnd) association.getToEnd();

		String[] stereotypes = association.toStereotypeArray();

		if (source == null || target == null)
			return;

		if (stereotypes.length != 1)
			return;

		switch (stereotypes[0]) {
		case StereotypeUtils.STR_CHARACTERIZATION:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_COMPARATIVE:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "0..*");
			association.setDerived(true);
			return;
		case StereotypeUtils.STR_COMPONENT_OF:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1");
			return;
		case StereotypeUtils.STR_MATERIAL:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1..*");
			association.setDerived(true);
			return;
		case StereotypeUtils.STR_EXTERNAL_DEPENDENCE:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1..*");
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MEDIATION:
			if(targetStereotype.equals(StereotypeUtils.STR_ROLE))
				setCardinalityIfEmpty(source, "1..*");
			else
				setCardinalityIfEmpty(source, "0..*");
			
			setCardinalityIfEmpty(target, "1");
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MEMBER_OF:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1..*");
			return;
		case StereotypeUtils.STR_SUB_COLLECTION_OF:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			return;
		case StereotypeUtils.STR_SUB_QUANTITY_OF:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			return;
		case StereotypeUtils.STR_CREATION:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_HISTORICAL_DEPENDENCE:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1");
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MANIFESTATION:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1..*");
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_PARTICIPATION:
			if(targetStereotype.equals(StereotypeUtils.STR_HISTORICAL_ROLE))
				setCardinalityIfEmpty(source, "1..*");
			else
				setCardinalityIfEmpty(source, "0");
			
			setCardinalityIfEmpty(target, "1..*");
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_PARTICIPATIONAL:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_TERMINATION:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_INSTANTIATION:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1..*");
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		}

	}

	/**
	 * Runs over the diagram to set default meta-properties
	 */
	public static void smartMetaProperties() {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final IModelElement[] modelElements = project
				.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

		for (int i = 0; modelElements != null && i < modelElements.length; i++)
			setMetaProperties((IAssociation) modelElements[i]);

	}

}
