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
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_COMPARATIVE:
			association.setDerived(true);
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_COMPONENT_OF:
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_MATERIAL:
			association.setDerived(true);
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_EXTERNAL_DEPENDENCE:
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MEDIATION:
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MEMBER_OF:
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_SUB_COLLECTION_OF:
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_SUB_QUANTITY_OF:
			source.setReadOnly(false);
			target.setReadOnly(false);
			return;
		case StereotypeUtils.STR_CREATION:
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_HISTORICAL_DEPENDENCE:
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_MANIFESTATION:
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_PARTICIPATION:
			source.setReadOnly(false);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_PARTICIPATIONAL:
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_TERMINATION:
			source.setReadOnly(true);
			target.setReadOnly(true);
			return;
		case StereotypeUtils.STR_INSTANTIATION:
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
