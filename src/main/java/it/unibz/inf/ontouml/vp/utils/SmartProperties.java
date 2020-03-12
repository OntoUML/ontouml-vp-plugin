package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class SmartProperties {

	private static boolean setCardinalityIfEmpty(IAssociationEnd end, String cardinality) {

		if (end.getMultiplicity() == null || end.getMultiplicity().equals("Unspecified")) {
			end.setMultiplicity("1");
			return true;
		} else {
			return false;
		}
	}

	private static void setMetaProperties(IAssociation association) {

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
			setCardinalityIfEmpty(source, "1..*");
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
			setCardinalityIfEmpty(source, "0..*");
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
			setCardinalityIfEmpty(target, "1*");
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
		final IModelElement[] modelElements = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

		for (int i = 0; modelElements != null && i < modelElements.length; i++)
			setMetaProperties((IAssociation) modelElements[i]);

	}

}
