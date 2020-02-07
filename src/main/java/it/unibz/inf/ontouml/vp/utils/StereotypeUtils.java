package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StereotypeUtils {

	// Class stereotypes
	public static final String STR_POWERTYPE = "powertype";
	public static final String STR_TYPE = "type";

	public static final String STR_HISTORICAL_ROLE = "historicalRole";
	public static final String STR_EVENT = "event";
	
	public static final String STR_CATEGORY = "category";
	public static final String STR_MIXIN = "mixin";
	public static final String STR_ROLE_MIXIN = "roleMixin";
	public static final String STR_PHASE_MIXIN = "phaseMixin";
	public static final String STR_KIND = "kind";
	public static final String STR_COLLECTIVE = "collective";
	public static final String STR_QUANTITY = "quantity";
	public static final String STR_RELATOR_KIND = "relatorKind";
	public static final String STR_QUALITY_KIND = "qualityKind";
	public static final String STR_MODE_KIND = "modeKind";
	public static final String STR_SUBKIND = "subkind";
	public static final String STR_ROLE = "role";
	public static final String STR_PHASE = "phase";
	
	public static final String STR_ENUMERATION = "enumeration";
	public static final String STR_DATATYPE = "datatype";

	// Association stereotypes
	public static final String STR_MATERIAL = "material";
	public static final String STR_DERIVATION = "derivation";
	public static final String STR_COMPARATIVE = "comparative";

	public static final String STR_MEDIATION = "mediation";
	public static final String STR_CHARACTERIZATION = "characterization";
	public static final String STR_EXTERNAL_DEPENDENCE = "externalDependence";
	
	public static final String STR_COMPONENT_OF = "componentOf";
	public static final String STR_MEMBER_OF = "memberOf";
	public static final String STR_SUB_COLLECTION_OF = "subCollectionOf";
	public static final String STR_SUB_QUANTITY_OF = "subQuantityOf";
	
	public static final String STR_INSTANTIATION = "instantiation";
	
	public static final String STR_TERMINATION = "termination";
	public static final String STR_PARTICIPATIONAL = "participational";
	public static final String STR_PARTICIPATION = "participation";
	public static final String STR_HISTORICAL_DEPENDENCE = "historicalDependence";
	public static final String STR_CREATION = "creation";
	
	// Attribute stereotypes
	public static final String STR_BEGIN = "begin";
	public static final String STR_END = "end";

	public static final List<String> STEREOTYPES = Arrays.asList(STR_POWERTYPE, STR_TYPE, STR_HISTORICAL_ROLE, STR_EVENT, STR_CATEGORY,
	STR_MIXIN, STR_ROLE_MIXIN, STR_PHASE_MIXIN, STR_KIND,	STR_COLLECTIVE,	STR_QUANTITY,	STR_RELATOR_KIND,	STR_QUALITY_KIND,	STR_MODE_KIND,
	STR_SUBKIND,	STR_ROLE, STR_PHASE, STR_ENUMERATION, STR_DATATYPE, STR_MATERIAL, STR_COMPARATIVE, STR_MEDIATION,
	STR_CHARACTERIZATION, STR_EXTERNAL_DEPENDENCE, STR_COMPONENT_OF, STR_MEMBER_OF, STR_SUB_COLLECTION_OF, STR_SUB_QUANTITY_OF, 
	STR_INSTANTIATION, STR_TERMINATION, STR_PARTICIPATIONAL, STR_PARTICIPATION, STR_HISTORICAL_DEPENDENCE, STR_CREATION, STR_BEGIN, STR_END);

	public static void removeAllModelStereotypes(String modelType) {

		ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
		IProject project = projectManager.getProject();
		IModelElement[] allModels = projectManager.getSelectableStereotypesForModelType(modelType, project, true);

		for (IModelElement model : allModels)
			model.delete();
	
		return;
	}

	public static void removeAllModelStereotypesButOntoUML(String modelType) {

		ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
		IProject project = projectManager.getProject();
		IModelElement[] allModels = projectManager.getSelectableStereotypesForModelType(modelType, project, true);

		final Set<String> classStereotypes = getOntoUMLClassStereotypeNames();
		final Set<String> associationStereotypes = getOntoUMLAssociationStereotypeNames();
	
		for (IModelElement model : allModels) {
			
			if(!(classStereotypes.contains(model.getName()) || associationStereotypes.contains(model.getName())))
				model.delete();
		}

		return;
	}

	public static void setDefaultStereotypes(IModelElement[] elements) {

		for (IModelElement model : elements) {

			IStereotype stereotype = IModelElementFactory.instance().createStereotype();
			stereotype.setName(model.getName());
			stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

		}

		return;
	}

	public static void setUpOntoUMLStereotypes() {

		System.out.println("Checking stereotypes...");

		final Set<String> classStereotypes = getOntoUMLClassStereotypeNames();

		for (String ontoUML_stereotype : classStereotypes) {

			System.out.println("Generating class stereotype «" + ontoUML_stereotype + "»");
			final IStereotype s = IModelElementFactory.instance().createStereotype();
			s.setName(ontoUML_stereotype);
			s.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

		}

		final Set<String> associationStereotypes = getOntoUMLAssociationStereotypeNames();

		for (String missing_str_name : associationStereotypes) {

			System.out.println("Generating association stereotype «" + missing_str_name + "»");
			final IStereotype s = IModelElementFactory.instance().createStereotype();
			s.setName(missing_str_name);
			s.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

		}

		System.out.println("All OntoUML stereotypes are available.");
	}

	public static Set<String> getOntoUMLClassStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_POWERTYPE);
		str_names.add(STR_TYPE);
		
		str_names.add(STR_HISTORICAL_ROLE);
		str_names.add(STR_EVENT);

		str_names.add(STR_ENUMERATION);
		str_names.add(STR_DATATYPE);

		str_names.add(STR_CATEGORY);
		str_names.add(STR_MIXIN);
		str_names.add(STR_ROLE_MIXIN);
		str_names.add(STR_PHASE_MIXIN);
		str_names.add(STR_KIND);
		str_names.add(STR_QUANTITY);
		str_names.add(STR_RELATOR_KIND);
		str_names.add(STR_QUALITY_KIND);
		str_names.add(STR_MODE_KIND);
		str_names.add(STR_SUBKIND);
		str_names.add(STR_ROLE);
		str_names.add(STR_PHASE);

		return str_names;
	}

	public static Set<String> getOntoUMLAssociationStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_INSTANTIATION);
		
		str_names.add(STR_TERMINATION);
		str_names.add(STR_PARTICIPATIONAL);
		str_names.add(STR_PARTICIPATION);
		str_names.add(STR_HISTORICAL_DEPENDENCE);
		str_names.add(STR_CREATION);

		str_names.add(STR_MATERIAL);
		str_names.add(STR_MEDIATION);
		str_names.add(STR_COMPARATIVE);
		str_names.add(STR_CHARACTERIZATION);
		str_names.add(STR_EXTERNAL_DEPENDENCE);
		str_names.add(STR_COMPONENT_OF);
		str_names.add(STR_MEMBER_OF);
		str_names.add(STR_SUB_COLLECTION_OF);
		str_names.add(STR_SUB_QUANTITY_OF);

		return str_names;
	}
}