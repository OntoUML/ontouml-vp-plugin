package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.ITaggedValueDefinitionContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StereotypeUtils {

	// Class stereotypes
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
	public static final String STR_RELATOR = "relator";
	public static final String STR_QUALITY = "quality";
	public static final String STR_MODE = "mode";
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
	public static final String STR_MANIFESTATION = "manifestation";

	// Attribute stereotypes
	public static final String STR_BEGIN = "begin";
	public static final String STR_END = "end";

	public static final List<String> STEREOTYPES = Arrays.asList(STR_TYPE, STR_HISTORICAL_ROLE, STR_EVENT, STR_CATEGORY,
			STR_MIXIN, STR_ROLE_MIXIN, STR_PHASE_MIXIN, STR_KIND, STR_COLLECTIVE, STR_QUANTITY, STR_RELATOR,
			STR_QUALITY, STR_MODE, STR_SUBKIND, STR_ROLE, STR_PHASE, STR_ENUMERATION, STR_DATATYPE, STR_MATERIAL,
			STR_COMPARATIVE, STR_MEDIATION, STR_CHARACTERIZATION, STR_EXTERNAL_DEPENDENCE, STR_COMPONENT_OF,
			STR_MEMBER_OF, STR_SUB_COLLECTION_OF, STR_SUB_QUANTITY_OF, STR_INSTANTIATION, STR_TERMINATION,
			STR_PARTICIPATIONAL, STR_PARTICIPATION, STR_HISTORICAL_DEPENDENCE, STR_CREATION, STR_MANIFESTATION,
			STR_BEGIN, STR_END);

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

			if (!(classStereotypes.contains(model.getName()) || associationStereotypes.contains(model.getName())))
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

		// str_names.add(STR_POWERTYPE);
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
		str_names.add(STR_COLLECTIVE);
		str_names.add(STR_QUANTITY);
		str_names.add(STR_RELATOR);
		str_names.add(STR_QUALITY);
		str_names.add(STR_MODE);
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
		str_names.add(STR_MANIFESTATION);

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

	/**
	 * Method to be called whenever a project is opened to properly install all
	 * stereotypes.
	 */
	public static void generate() {
		final ApplicationManager app = ApplicationManager.instance();
		final IProject project = app.getProjectManager().getProject();
		final IModelElement[] installedStereotypesArray = project
				.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
		final List<IModelElement> installedStereotypes = installedStereotypesArray != null
				? Arrays.asList(installedStereotypesArray)
				: new ArrayList<IModelElement>();
		final List<String> ontoUMLClassStereotypes = new ArrayList<String>();
		final Map<String, IStereotype> stereotypesMap = new HashMap<String, IStereotype>();

		ontoUMLClassStereotypes.addAll(getOntoUMLClassStereotypeNames());
		ontoUMLClassStereotypes.remove(STR_DATATYPE);
		ontoUMLClassStereotypes.remove(STR_ENUMERATION);

		System.out.println("Executing generate()");

		System.out.println("Retrieving installed stereotypes");
		// Retrieves IStereotype objects for OntoUML classes
		for (IModelElement installedStereotype : installedStereotypes) {
			if (ontoUMLClassStereotypes.contains(installedStereotype.getName())) {
				stereotypesMap.put(installedStereotype.getName(), (IStereotype) installedStereotype);
			}
		}

		System.out.println("Creating missing stereotypes");
		// Creates missing IStereotype objects for OntoUML classes
		for (String ontoUMLClassStereotype : ontoUMLClassStereotypes) {
			if (stereotypesMap.get(ontoUMLClassStereotype) == null) {
				final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
				newStereotypeElement.setName(ontoUMLClassStereotype);
				newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
				stereotypesMap.put(ontoUMLClassStereotype, newStereotypeElement);
			}
		}

		System.out.println("Adding missing tagged values");
		// Checks and adds missing tagged value definitions to IStereotype objects
		for (IStereotype current : stereotypesMap.values()) {
			ITaggedValueDefinitionContainer container = current.getTaggedValueDefinitions();

			if (container == null) {
				System.out.println("Missing container detected");
				container = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
			}

			final ITaggedValueDefinition[] definitionsArray = container.toTaggedValueDefinitionArray();
			final Map<String, ITaggedValueDefinition> definitions = new HashMap<String, ITaggedValueDefinition>();

			for (int j = 0; definitionsArray != null && j < definitionsArray.length; j++) {
				definitions.put(definitionsArray[j].getName(), definitionsArray[j]);
			}

			// Adds "allowed" natures to all IStereotype objects
			if (!definitions.containsKey("allowed")) {
				System.out.println("Adding 'allowed' to the container");
				final ITaggedValueDefinition allowed = IModelElementFactory.instance().createTaggedValueDefinition();
				allowed.setName("allowed");
				allowed.setType(ITaggedValueDefinition.TYPE_TEXT);
				allowed.setDefaultValue(getAllowedNatures());
				container.addTaggedValueDefinition(allowed);
			}

			// Adds "isExtensional" to all STR_COLLECTIVE IStereotype
			if (current.getName().equals(STR_COLLECTIVE) && !definitions.containsKey("isExtensional")) {
				System.out.println("Adding 'isExtensional' to the container");
				final ITaggedValueDefinition isExtensional = IModelElementFactory.instance()
						.createTaggedValueDefinition();
				isExtensional.setName("isExtensional");
				isExtensional.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
				isExtensional.setDefaultValue("false");
				container.addTaggedValueDefinition(isExtensional);
			}

			// Adds "isPowertype" to all STR_TYPE IStereotype
			if (current.getName().equals(STR_TYPE) && !definitions.containsKey("isPowertype")) {
				System.out.println("Adding 'isPowertype' to the container");
				final ITaggedValueDefinition isPowertype = IModelElementFactory.instance()
						.createTaggedValueDefinition();
				isPowertype.setName("isPowertype");
				isPowertype.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
				isPowertype.setDefaultValue("false");
				container.addTaggedValueDefinition(isPowertype);
			}

			// Adds "order" to all STR_TYPE IStereotype
			if (current.getName().equals(STR_TYPE) && !definitions.containsKey("order")) {
				System.out.println("Adding 'order' to the container");
				final ITaggedValueDefinition order = IModelElementFactory.instance().createTaggedValueDefinition();
				order.setName("order");
				order.setType(ITaggedValueDefinition.TYPE_TEXT);
				order.setDefaultValue("2");
				container.addTaggedValueDefinition(order);
			}

			current.setTaggedValueDefinitions(container);
		}
	}

	public static String getAllowedNatures() {
		return "[ \"object\", \"collective\", \"quantity\", \"relator\",\"mode\", \"quality\", \"type\", \"event\" ]";
	}
}