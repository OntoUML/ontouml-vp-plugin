package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.features.constraints.AssociationConstraints;
import it.unibz.inf.ontouml.vp.features.constraints.ClassConstraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.AbstractMap.SimpleEntry;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.factory.IModelElementFactory;

public class SmartModelling {

	public static void setAggregationKind(IModelElement element) {
		IAssociation association = (IAssociation) element;
		IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) association.getToEnd();

		if (compositionToEnd.getAggregationKind().equals(IAssociationEnd.AGGREGATION_KIND_NONE)) {
			compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
		}

		compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);

		return;
	}

	public static void removeAggregationKind(IModelElement element) {
		IAssociationEnd compositionFromEnd = (IAssociationEnd) ((IAssociation) element).getFromEnd();
		IAssociationEnd compositionToEnd = (IAssociationEnd) ((IAssociation) element).getToEnd();

		compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);
		compositionToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_NONE);

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

	private static String getTypeStereotype(IAssociationEnd associationEnd) {
		String noStereotype = "";

		try {
			final IModelElement type = associationEnd.getTypeAsElement();

			if (!type.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
				return noStereotype;

			final String[] stereotypes = ((IClass) type).toStereotypeArray();

			if (stereotypes != null && stereotypes.length == 1)
				return stereotypes[0];

			return noStereotype;
		} catch (Exception e) {
			return noStereotype;
		}
	}

	public static void setAssociationMetaProperties(IAssociation association) {

		IAssociationEnd source = (IAssociationEnd) association.getFromEnd();
		IAssociationEnd target = (IAssociationEnd) association.getToEnd();

		if (source == null || target == null)
			return;

		String sourceStereotype = getTypeStereotype(source);
		String targetStereotype = getTypeStereotype(target);

		String[] stereotypes = association.toStereotypeArray();

		if (stereotypes == null || stereotypes.length != 1)
			return;

		switch (stereotypes[0]) {
		case StereotypeUtils.STR_CHARACTERIZATION:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_COMPARATIVE:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "0..*");
			association.setDerived(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_COMPONENT_OF:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1");
			setAggregationKind(association);
			return;
		case StereotypeUtils.STR_MATERIAL:
			if (targetStereotype.equals(StereotypeUtils.STR_ROLE))
				setCardinalityIfEmpty(source, "1..*");
			else
				setCardinalityIfEmpty(source, "0..*");

			if (sourceStereotype.equals(StereotypeUtils.STR_ROLE))
				setCardinalityIfEmpty(target, "1..*");
			else
				setCardinalityIfEmpty(target, "0..*");

			association.setDerived(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_EXTERNAL_DEPENDENCE:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1..*");
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_MEDIATION:
			if (targetStereotype.equals(StereotypeUtils.STR_ROLE))
				setCardinalityIfEmpty(source, "1..*");
			else
				setCardinalityIfEmpty(source, "0..*");

			setCardinalityIfEmpty(target, "1");
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_MEMBER_OF:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1..*");
			setAggregationKind(association);
			return;
		case StereotypeUtils.STR_SUB_COLLECTION_OF:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			setAggregationKind(association);
			return;
		case StereotypeUtils.STR_SUB_QUANTITY_OF:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			setAggregationKind(association);
			return;
		case StereotypeUtils.STR_CREATION:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_HISTORICAL_DEPENDENCE:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1");
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_MANIFESTATION:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1..*");
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_PARTICIPATION:
			if (targetStereotype.equals(StereotypeUtils.STR_HISTORICAL_ROLE))
				setCardinalityIfEmpty(source, "1..*");
			else
				setCardinalityIfEmpty(source, "0..*");

			setCardinalityIfEmpty(target, "1..*");
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_PARTICIPATIONAL:
			setCardinalityIfEmpty(source, "1..*");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			target.setReadOnly(true);
			setAggregationKind(association);
			return;
		case StereotypeUtils.STR_TERMINATION:
			setCardinalityIfEmpty(source, "1");
			setCardinalityIfEmpty(target, "1");
			source.setReadOnly(true);
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		case StereotypeUtils.STR_INSTANTIATION:
			setCardinalityIfEmpty(source, "0..*");
			setCardinalityIfEmpty(target, "1..*");
			source.setReadOnly(true);
			target.setReadOnly(true);
			removeAggregationKind(association);
			return;
		}
	}

	public static void setClassMetaProperties(IClass _class) {
		if (_class == null)
			return;

		String[] stereotypes = _class.toStereotypeArray();

		if (stereotypes == null || stereotypes.length != 1)
			return;

		switch (stereotypes[0]) {
		case StereotypeUtils.STR_CATEGORY:
			_class.setAbstract(true);
			break;
		case StereotypeUtils.STR_ROLE_MIXIN:
			_class.setAbstract(true);
			break;
		case StereotypeUtils.STR_PHASE_MIXIN:
			_class.setAbstract(true);
			break;
		case StereotypeUtils.STR_MIXIN:
			_class.setAbstract(true);
			break;
		}
	}

	public static void manageAssociationStereotypes(IAssociation association, VPAction action) {

		if (!association.getFrom().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
			return;

		if (!association.getTo().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
			return;

		final IClass source = (IClass) association.getFrom();
		final IClass target = (IClass) association.getTo();

		final ArrayList<String> sourceStereotypes = new ArrayList<String>(Arrays.asList(source.toStereotypeArray()));
		final ArrayList<String> targetStereotypes = new ArrayList<String>(Arrays.asList(target.toStereotypeArray()));

		if (sourceStereotypes.size() != 1 || targetStereotypes.size() != 1) {
			// if any end has more than 1 stereotypes nothing is allowed
			action.setEnabled(false);
			return;
		}

		// continue if both ends has ONLY ONE stereotype in both ends
		final String sourceStereotype = sourceStereotypes.get(0);
		final String targetStereotype = targetStereotypes.get(0);
		final ArrayList<String> allowedCombinations = AssociationConstraints.allowedCombinations.get(new SimpleEntry<String, String>(sourceStereotype, targetStereotype));

		if (allowedCombinations == null || !allowedCombinations.contains(action.getActionId()))
			action.setEnabled(false);

		return;
	}

	public static void manageClassStereotypes(IClass _class, VPAction action) {

		final ISimpleRelationship[] relationshipsTo = _class.toToRelationshipArray();
		final ISimpleRelationship[] relationshipsFrom = _class.toFromRelationshipArray();

		for (int i = 0; relationshipsTo != null && i < relationshipsTo.length; i++) {
			final ISimpleRelationship relationshipTo = relationshipsTo[i];
			final String relationshipTypeTo = relationshipTo.getModelType();
			final String superClassType = relationshipTo.getFrom() != null ? relationshipTo.getFrom().getModelType() : "";

			if (!(relationshipTypeTo.equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) || !(superClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS)))
				continue;

			final IClass superClass = (IClass) relationshipTo.getFrom();
			final ArrayList<String> superClassStereotypes = new ArrayList<String>(Arrays.asList(superClass.toStereotypeArray()));

			if (superClassStereotypes.size()==0)
				continue;

			if (superClassStereotypes.size() > 1)
				action.setEnabled(false);
			
			final String superStereotype = superClassStereotypes.get(0);
			final ArrayList<String> allowedCombinationsSub = ClassConstraints.allowedSubCombinations.get(superStereotype);

			if (allowedCombinationsSub == null || !allowedCombinationsSub.contains(action.getActionId()))
				action.setEnabled(false);
		}

		for (int i = 0; relationshipsFrom != null && i < relationshipsFrom.length; i++) {
			final ISimpleRelationship relationshipFrom = relationshipsFrom[i];
			final String relationshipTypeFrom = relationshipFrom.getModelType();
			final String subClassType = relationshipFrom.getTo() != null ? relationshipFrom.getFrom().getModelType() : "";

			if (!(relationshipTypeFrom.equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) || !(subClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS)))
				continue;

			final IClass subClass = (IClass) relationshipFrom.getTo();
			final ArrayList<String> subClassStereotypes = new ArrayList<String>(Arrays.asList(subClass.toStereotypeArray()));

			if (subClassStereotypes.size()==0)
				continue;

			if (subClassStereotypes.size() > 1)
				action.setEnabled(false);

			final String subStereotype = subClassStereotypes.get(0);
			final ArrayList<String> allowedCombinationsSuper = ClassConstraints.allowedSuperCombinations.get(subStereotype);

			if (allowedCombinationsSuper == null || !allowedCombinationsSuper.contains(action.getActionId()))
				action.setEnabled(false);
		}
		
		return;
	}

}
