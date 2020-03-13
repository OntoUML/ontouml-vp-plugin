package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
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

	private static String getTypeStereotype(IAssociationEnd associationEnd){
		String noStereotype = "";
		
		try {
			final IModelElement type = associationEnd.getTypeAsElement();
			
			if(!type.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
				return noStereotype;
			
			final String[] stereotypes = ((IClass) type).toStereotypeArray();

			if(stereotypes!=null && stereotypes.length==1)
				return stereotypes[0];
			
			return noStereotype;
		}
		catch(Exception e){
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

		if (stereotypes==null || stereotypes.length!=1)
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
				if(targetStereotype.equals(StereotypeUtils.STR_ROLE))
					setCardinalityIfEmpty(source, "1..*");
				else
					setCardinalityIfEmpty(source, "0..*");
					
				if(sourceStereotype.equals(StereotypeUtils.STR_ROLE))
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
				if(targetStereotype.equals(StereotypeUtils.STR_ROLE))
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
				if(targetStereotype.equals(StereotypeUtils.STR_HISTORICAL_ROLE))
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
		if(_class==null)
			return;
			
		String[] stereotypes = _class.toStereotypeArray();

		if (stereotypes==null || stereotypes.length!=1)
			return;

		switch (stereotypes[0]) {
			// case StereotypeUtils.STR_KIND:
			// 	break;
			// case StereotypeUtils.STR_COLLECTIVE:
			// 	break;
			// case StereotypeUtils.STR_QUANTITY:
			// 	break;
			// case StereotypeUtils.STR_MODE:
			// 	break;
			// case StereotypeUtils.STR_QUALITY:
			// 	break;
			// case StereotypeUtils.STR_RELATOR:
			// 	break;
			// case StereotypeUtils.STR_SUBKIND:
			// 	break;
			// case StereotypeUtils.STR_ROLE:
			// 	break;
			// case StereotypeUtils.STR_PHASE:
			// 	break;
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
			// case StereotypeUtils.STR_EVENT:
			// 	break;
			// case StereotypeUtils.STR_HISTORICAL_ROLE:
			// 	break;
			// case StereotypeUtils.STR_TYPE:
			// 	break;
			// case StereotypeUtils.STR_ENUMERATION:
			// 	break;
			// case StereotypeUtils.STR_DATATYPE:
			// 	break;
		}
	}

}
