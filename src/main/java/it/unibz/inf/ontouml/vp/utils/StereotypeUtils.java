package it.unibz.inf.ontouml.vp.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

public class StereotypeUtils {

   public static Map<String, IStereotype> STEREOTYPE_ELEMENTS = null;

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

   // Ontological natures
   public static final String RESTRICTED_TO_FUNCTIONAL_COMPLEX = "functional-complex";
   public static final String RESTRICTED_TO_COLLECTIVE = "collective";
   public static final String RESTRICTED_TO_QUANTITY = "quantity";
   public static final String RESTRICTED_TO_RELATOR = "relator";
   public static final String RESTRICTED_TO_MODE = "mode";
   public static final String RESTRICTED_TO_QUALITY = "quality";
   public static final String RESTRICTED_TO_EVENT = "event";
   public static final String RESTRICTED_TO_TYPE = "type";
   public static final String RESTRICTED_TO_ABSTRACT = "abstract";

   // Meta-properties names
   public static final String PROPERTY_RESTRICTED_TO = "restrictedTo";
   public static final String PROPERTY_IS_EXTENSIONAL = "isExtensional";
   public static final String PROPERTY_IS_POWERTYPE = "isPowertype";
   public static final String PROPERTY_ORDER = "order";

   public static List<String> getOntoUMLTaggedValues() {
      return Arrays.asList(PROPERTY_RESTRICTED_TO, PROPERTY_IS_EXTENSIONAL, PROPERTY_IS_POWERTYPE, PROPERTY_ORDER);
   }

   public static void removeAllModelStereotypes(String modelType) {
      ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
      IProject project = projectManager.getProject();
      IModelElement[] allModels = projectManager.getSelectableStereotypesForModelType(modelType, project, true);

      for (IModelElement model : allModels)
         model.delete();
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
   }

   public static void setDefaultStereotypes(IModelElement[] elements) {
      for (IModelElement model : elements) {
         IStereotype stereotype = IModelElementFactory.instance().createStereotype();
         stereotype.setName(model.getName());
         stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
      }
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
      final Set<String> str_names = new HashSet<>();

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
      final Set<String> str_names = new HashSet<>();

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

   public static Set<String> getOntoUMLAttributeStereotypeNames() {
      final Set<String> str_names = new HashSet<>();

      str_names.add(STR_BEGIN);
      str_names.add(STR_END);

      return str_names;
   }

   public static Set<String> getNonSortalStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_CATEGORY);
		str_names.add(STR_MIXIN);
		str_names.add(STR_ROLE_MIXIN);
		str_names.add(STR_PHASE_MIXIN);

		return str_names;
	}

	public static Set<String> getUltimateSortalStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_KIND);
		str_names.add(STR_COLLECTIVE);
		str_names.add(STR_QUANTITY);
		str_names.add(STR_RELATOR);
		str_names.add(STR_QUALITY);
		str_names.add(STR_MODE);

		return str_names;
	}

	public static Set<String> getSortalStereotypeNames() {
		final Set<String> str_names = new HashSet<String>();

		str_names.add(STR_SUBKIND);
		str_names.add(STR_ROLE);
		str_names.add(STR_PHASE);

		return str_names;
	}

   /**
    * Method to be called whenever a project is opened to properly install all
    * stereotypes.
    */
    public static void generate() {
      final IProject project = ApplicationManager.instance().getProjectManager().getProject();
      final IModelElement[] installedStereotypes = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
      final Map<String, IStereotype> stereotypesMap = STEREOTYPE_ELEMENTS != null ?
            STEREOTYPE_ELEMENTS : new HashMap<>();
      final Set<String> allStereotypeNames = getOntoUMLAssociationStereotypeNames();

      allStereotypeNames.addAll(getOntoUMLAttributeStereotypeNames());
      allStereotypeNames.addAll(getOntoUMLClassStereotypeNames());

      // Retrieves IStereotype objects for OntoUML elements
      for (IModelElement stereotype : installedStereotypes){
         if (allStereotypeNames.contains(stereotype.getName())) {
            stereotypesMap.put(stereotype.getName(), (IStereotype) stereotype);
         }
      }

      // Creates missing IStereotype objects for OntoUML classes
      for (String ontoUMLClassStereotype : getOntoUMLClassStereotypeNames()) {
         if (stereotypesMap.get(ontoUMLClassStereotype) == null) {
            final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
            newStereotypeElement.setName(ontoUMLClassStereotype);
            newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
            stereotypesMap.put(ontoUMLClassStereotype, newStereotypeElement);
         }
      }

      for (String ontoUMLAssociationStereotype : getOntoUMLAssociationStereotypeNames()) {
         if (stereotypesMap.get(ontoUMLAssociationStereotype) == null) {
            final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
            newStereotypeElement.setName(ontoUMLAssociationStereotype);
            newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
            stereotypesMap.put(ontoUMLAssociationStereotype, newStereotypeElement);
         }
      }

      for (String ontoUMLAttributeStereotype : getOntoUMLAttributeStereotypeNames()) {
         if (stereotypesMap.get(ontoUMLAttributeStereotype) == null) {
            final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
            newStereotypeElement.setName(ontoUMLAttributeStereotype);
            newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_ATTRIBUTE);
            stereotypesMap.put(ontoUMLAttributeStereotype, newStereotypeElement);
         }
      }

      // Checks and adds missing tagged value definitions to IStereotype objects
      final Set<String> taggedStereotypeNames = getOntoUMLClassStereotypeNames();

      for (String stereotypeName : taggedStereotypeNames) {
         final IStereotype stereotype = stereotypesMap.get(stereotypeName);
         ITaggedValueDefinitionContainer definitionsContainer = stereotype.getTaggedValueDefinitions();

         if (definitionsContainer == null) {
            definitionsContainer = IModelElementFactory
               .instance().createTaggedValueDefinitionContainer();
         }

         final ITaggedValueDefinition[] definitionsArray = definitionsContainer.toTaggedValueDefinitionArray();
         final Map<String, ITaggedValueDefinition> definitions = new HashMap<>();

         for (int j = 0; definitionsArray != null && j < definitionsArray.length; j++) {
            definitions.put(definitionsArray[j].getName(), definitionsArray[j]);
         }

         // Removes deprecated references to "allowed"
         if (definitions.containsKey("allowed")) {
            final ITaggedValueDefinition allowed = definitions.get("allowed");
            allowed.delete();
         }

         // Adds "restrictedTo" to all IStereotype objects
         if (!definitions.containsKey(PROPERTY_RESTRICTED_TO)) {
            final ITaggedValueDefinition restrictedTo = IModelElementFactory.instance().createTaggedValueDefinition();
            restrictedTo.setName(PROPERTY_RESTRICTED_TO);
            restrictedTo.setType(ITaggedValueDefinition.TYPE_TEXT);
            restrictedTo.setDefaultValue(getDefaultRestrictedTo(stereotypeName));
            restrictedTo.setTagDefStereotype(stereotype);
            definitionsContainer.addTaggedValueDefinition(restrictedTo);
         }

         // Adds "isExtensional" to all STR_COLLECTIVE IStereotype
         if (
            stereotype.getName().equals(STR_COLLECTIVE) && 
            !definitions.containsKey(PROPERTY_IS_EXTENSIONAL)
         ) {
            final ITaggedValueDefinition isExtensional = IModelElementFactory.instance()
                    .createTaggedValueDefinition();
            isExtensional.setName(PROPERTY_IS_EXTENSIONAL);
            isExtensional.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
            isExtensional.setDefaultValue("false");
            isExtensional.setTagDefStereotype(stereotype);
            definitionsContainer.addTaggedValueDefinition(isExtensional);
         }

         // Adds "isPowertype" to all STR_TYPE IStereotype
         if (
            stereotype.getName().equals(STR_TYPE) && 
            !definitions.containsKey(PROPERTY_IS_POWERTYPE)
         ) {
            final ITaggedValueDefinition isPowertype = IModelElementFactory.instance()
                    .createTaggedValueDefinition();
            isPowertype.setName(PROPERTY_IS_POWERTYPE);
            isPowertype.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
            isPowertype.setDefaultValue("false");
            isPowertype.setTagDefStereotype(stereotype);
            definitionsContainer.addTaggedValueDefinition(isPowertype);
         }

         // Adds "order" to all STR_TYPE IStereotype
         if (
            stereotype.getName().equals(STR_TYPE) && 
            !definitions.containsKey(PROPERTY_ORDER)
         ) {
            final ITaggedValueDefinition order = IModelElementFactory.instance().createTaggedValueDefinition();
            order.setName(PROPERTY_ORDER);
            order.setType(ITaggedValueDefinition.TYPE_TEXT);
            order.setDefaultValue("2");
            order.setTagDefStereotype(stereotype);
            definitionsContainer.addTaggedValueDefinition(order);
         }

         stereotype.setTaggedValueDefinitions(definitionsContainer);
      }

      // generated() is called when any project is "newed" (which includes opening)
      // and may trigger a NullPointerException when the project is in fact new
      // so we have to be sure that the map is null until it is properly updated
      if(!stereotypesMap.isEmpty()) {
         STEREOTYPE_ELEMENTS = stereotypesMap;
      } else {
         STEREOTYPE_ELEMENTS = null;
      }
   }

   public static String getRestrictions() {
      return getRestrictionsList()
         .stream()
         .sorted()
         .collect(Collectors.joining(" "));
   }

   public static List<String> getRestrictionsList() {
      return Arrays.asList(RESTRICTED_TO_COLLECTIVE,
         RESTRICTED_TO_EVENT, RESTRICTED_TO_MODE,
         RESTRICTED_TO_FUNCTIONAL_COMPLEX, RESTRICTED_TO_QUALITY,
         RESTRICTED_TO_QUANTITY, RESTRICTED_TO_RELATOR,
         RESTRICTED_TO_TYPE, RESTRICTED_TO_ABSTRACT);
   }

   public static void applyStereotype(IModelElement element, String stereotypeName) {
      // Tries to retrieve the stereotype
      if (STEREOTYPE_ELEMENTS == null)
         generate();

      final IStereotype stereotype = STEREOTYPE_ELEMENTS.get(stereotypeName);

      // TODO: check the necessity of throwing some exception here
      if (stereotype == null || !element.getModelType().equals(stereotype.getBaseType())){
         return;
      }

      System.out.println("\nStereotype: " + stereotype.getName());
      ITaggedValueDefinitionContainer definitionContainer = stereotype.getTaggedValueDefinitions();
      if (definitionContainer != null) {
         Iterator<?> iterator = definitionContainer.taggedValueDefinitionIterator();
         while (iterator != null && iterator.hasNext()) {
            ITaggedValueDefinition definition = (ITaggedValueDefinition) iterator.next();
            System.out.println("\tDefined tagged value: " + definition.getName());
         }
      }

      IModelElementFactory factory = IModelElementFactory.instance();
      ITaggedValueContainer container = element.getTaggedValues();
      Map<String, Object> taggedValueMap = new HashMap<>();

      if (container == null) {
         container = factory.createTaggedValueContainer();
         element.setTaggedValues(container);
      }

      if (element instanceof IClass) {
         int count = element.getTaggedValues().taggedValueCount();
         System.out.println("Initial number of tagged values: " + count);
         ITaggedValue[] taggedValues = container.toTaggedValueArray();

         // 1. Saves and deletes tagged values associated to a stereotype
         for (ITaggedValue tv : taggedValues) {
            final boolean isAllowedTag = tv.getName().equals("allowed");
            final boolean isOntoUMLTag = getOntoUMLTaggedValues().contains(tv.getName());
            final boolean isAssociatedToStereotype = tv.getTagDefinition() != null;

            if (isAllowedTag) {
               System.out.println(tv.getName() + ": allowed");
               taggedValueMap.put(tv.getName(), tv.getValue());
               container.removeTaggedValue(tv);
               tv.delete();
            } else if (isOntoUMLTag) {
               // It does not remove non-OntoUML tags
               System.out.println(tv.getName() + ": has tag definition");

               if (isAssociatedToStereotype){
                  taggedValueMap.put(tv.getName(), tv.getValue());
               }

               container.removeTaggedValue(tv);
               tv.delete();
            }
         }

      }

      // 2. Removes old stereotypes
      if (element.stereotypeCount() > 0) {
         for (IStereotype s : element.toStereotypeModelArray()) {
            element.removeStereotype(s);
         }
      }

      // 3. Adds new stereotype
      element.addStereotype(stereotype);

      // 4. Reapply values of tagged values related to the stereotypes (e.g. restrictedTo, order, isPowertype, isExtensional)
      if (element.getTaggedValues() != null) {
         int count = element.getTaggedValues().taggedValueCount();
         System.out.println("Number of tagged values: " + count);
         ITaggedValue[] taggedValues = element.getTaggedValues().toTaggedValueArray();

         for (ITaggedValue taggedValue : taggedValues) {
            Object oldValue = taggedValueMap.get(taggedValue.getName());
            if (oldValue != null) {
               taggedValue.setValue(oldValue.toString());
            }
         }

         System.out.println("Number of tagged values: " + count);
      }
   }

   public static String getDefaultRestrictedTo(String stereotype) {
      switch (stereotype) {
         case STR_TYPE:
            return RESTRICTED_TO_TYPE;
         case STR_HISTORICAL_ROLE:
         case STR_EVENT:
            return RESTRICTED_TO_EVENT;
         case STR_CATEGORY:
         case STR_MIXIN:
         case STR_ROLE_MIXIN:
         case STR_PHASE_MIXIN:
            return RESTRICTED_TO_FUNCTIONAL_COMPLEX;
         case STR_KIND:
            return RESTRICTED_TO_FUNCTIONAL_COMPLEX;
         case STR_COLLECTIVE:
            return RESTRICTED_TO_COLLECTIVE;
         case STR_QUANTITY:
            return RESTRICTED_TO_QUANTITY;
         case STR_RELATOR:
            return RESTRICTED_TO_RELATOR;
         case STR_QUALITY:
            return RESTRICTED_TO_QUALITY;
         case STR_MODE:
            return RESTRICTED_TO_MODE;
         case STR_ENUMERATION:
         case STR_DATATYPE:
            return RESTRICTED_TO_ABSTRACT;
         case STR_SUBKIND:
         case STR_ROLE:
         case STR_PHASE:
         default:
            return "";
      }
   }

   public static ITaggedValue reapplyStereotypeAndGetTaggedValue(IClass _class, String taggedValueName) {
      reapplyCurrentStereotype(_class);

      ITaggedValue taggedValue = getTaggedValue(_class, taggedValueName);
      if (taggedValue == null)
         return null;

      //Retrieves desired tagged value
      return getTaggedValue(_class, taggedValueName);
   }

   public static boolean reapplyCurrentStereotype(IClass _class) {
      String stereotype = _class.toStereotypeArray() != null && _class.toStereotypeArray().length > 0 ?
              _class.toStereotypeArray()[0] : null;

      // Escape in case the stereotype is missing or incorrect
      if (stereotype == null || !getOntoUMLClassStereotypeNames().contains(stereotype))
         return false;

      System.out.println("Reapplying " + stereotype + " to " + _class.getName());
      // Reapply stereotype making sure that the tagged values are there
      applyStereotype(_class, stereotype);

      return true;
   }

   public static ITaggedValue getTaggedValue(IModelElement element, String name) {
      ITaggedValueContainer container = element.getTaggedValues();

      if (container == null)
         return null;

      try {
         return container.getTaggedValueByName(name);
      } catch (Exception e) {
         return null;
      }
   }

   public static void deleteTaggedValue(IModelElement element, String name) {
      ITaggedValueContainer container = element.getTaggedValues();

      if (container != null) {
         ITaggedValue[] taggedValues = container.toTaggedValueArray();
         for (ITaggedValue tv : taggedValues) {
            if (tv.getName().equals(name))
               tv.delete();
         }
      }
   }

   public static String getUniqueStereotypeName(IModelElement element) {
		return element.stereotypeCount() == 1 ?
			element.toStereotypeModelArray()[0].getName() :
			null;
   }
   
   public static IStereotype getUniqueStereotype(IModelElement element) {
		return element.stereotypeCount() == 1 ?
			element.toStereotypeModelArray()[0] :
			null;
	}

}