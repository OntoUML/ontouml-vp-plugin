package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class SmartProperties {

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
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_COMPARATIVE:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("0..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("0..*");
			
			source.setReadOnly(false);
			target.setReadOnly(false);
			association.setDerived(true);
			return;
		case StereotypeUtils.STR_COMPONENT_OF:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_MATERIAL:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1..*");
			
			source.setReadOnly(false);
			target.setReadOnly(false);
			association.setDerived(true);
			return;
		case StereotypeUtils.STR_EXTERNAL_DEPENDENCE:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("0..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1..*");
			
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MEDIATION:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MEMBER_OF:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1..*");
			
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_SUB_COLLECTION_OF:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_SUB_QUANTITY_OF:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_CREATION:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_HISTORICAL_DEPENDENCE:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("0..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MANIFESTATION:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("0..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1..*");
			
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_PARTICIPATION:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("0..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1..*");
			
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_PARTICIPATIONAL:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_TERMINATION:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("1");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1");
			
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_INSTANTIATION:
			if (source.getMultiplicity() == null || source.getMultiplicity().equals("Unspecified"))
				source.setMultiplicity("0..*");
			if (target.getMultiplicity() == null || target.getMultiplicity().equals("Unspecified"))
				target.setMultiplicity("1..*");
			
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
