package it.unibz.inf.ontouml.vp.utils;

import java.util.ArrayList;
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

   public static final List<String> STEREOTYPES = Arrays.asList(STR_TYPE, STR_HISTORICAL_ROLE, STR_EVENT, STR_CATEGORY,
           STR_MIXIN, STR_ROLE_MIXIN, STR_PHASE_MIXIN, STR_KIND, STR_COLLECTIVE, STR_QUANTITY, STR_RELATOR,
           STR_QUALITY, STR_MODE, STR_SUBKIND, STR_ROLE, STR_PHASE, STR_ENUMERATION, STR_DATATYPE, STR_MATERIAL,
           STR_COMPARATIVE, STR_MEDIATION, STR_CHARACTERIZATION, STR_EXTERNAL_DEPENDENCE, STR_COMPONENT_OF,
           STR_MEMBER_OF, STR_SUB_COLLECTION_OF, STR_SUB_QUANTITY_OF, STR_INSTANTIATION, STR_TERMINATION,
           STR_PARTICIPATIONAL, STR_PARTICIPATION, STR_HISTORICAL_DEPENDENCE, STR_CREATION, STR_MANIFESTATION,
           STR_BEGIN, STR_END);

   // Ontological natures
   public static final String NATURE_FUNCTIONAL_COMPLEX = "functional-complex";
   public static final String NATURE_COLLECTIVE = "collective";
   public static final String NATURE_QUANTITY = "quantity";
   public static final String NATURE_RELATOR = "relator";
   public static final String NATURE_MODE = "mode";
   public static final String NATURE_QUALITY = "quality";
   public static final String NATURE_EVENT = "event";
   public static final String NATURE_TYPE = "type";
   public static final String NATURE_ABSTRACT = "abstract";

   // Meta-properties names
   public static final String PROPERTY_RESTRICTED_TO = "restrictedTo";
   public static final String PROPERTY_IS_EXTENSIONAL = "isExtensional";
   public static final String PROPERTY_IS_POWERTYPE = "isPowertype";
   public static final String PROPERTY_ORDER = "order";

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

   /**
    * Method to be called whenever a project is opened to properly install all
    * stereotypes.
    */
   public static void generate() {
      final Map<String, IStereotype> stereotypeElements = STEREOTYPE_ELEMENTS != null ?
              STEREOTYPE_ELEMENTS : new HashMap<>();
      final ApplicationManager app = ApplicationManager.instance();
      final IProject project = app.getProjectManager().getProject();
      final IModelElement[] installedStereotypesArray = project
              .toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
      final List<IModelElement> installedStereotypes = installedStereotypesArray != null
              ? Arrays.asList(installedStereotypesArray)
              : new ArrayList<>();

      // Retrieves IStereotype objects for OntoUML elements
      for (IModelElement installedStereotype : installedStereotypes) {
         if (STEREOTYPES.contains(installedStereotype.getName())) {
            stereotypeElements.put(installedStereotype.getName(), (IStereotype) installedStereotype);
         }
      }

      // Creates missing IStereotype objects for OntoUML classes
      for (String ontoUMLClassStereotype : getOntoUMLClassStereotypeNames()) {
         if (stereotypeElements.get(ontoUMLClassStereotype) == null) {
            final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
            newStereotypeElement.setName(ontoUMLClassStereotype);
            newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
            stereotypeElements.put(ontoUMLClassStereotype, newStereotypeElement);
         }
      }

      for (String ontoUMLAssociationStereotype : getOntoUMLAssociationStereotypeNames()) {
         if (stereotypeElements.get(ontoUMLAssociationStereotype) == null) {
            final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
            newStereotypeElement.setName(ontoUMLAssociationStereotype);
            newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
            stereotypeElements.put(ontoUMLAssociationStereotype, newStereotypeElement);
         }
      }

      for (String ontoUMLAttributeStereotype : getOntoUMLAttributeStereotypeNames()) {
         if (stereotypeElements.get(ontoUMLAttributeStereotype) == null) {
            final IStereotype newStereotypeElement = IModelElementFactory.instance().createStereotype();
            newStereotypeElement.setName(ontoUMLAttributeStereotype);
            newStereotypeElement.setBaseType(IModelElementFactory.MODEL_TYPE_ATTRIBUTE);
            stereotypeElements.put(ontoUMLAttributeStereotype, newStereotypeElement);
         }
      }

      // Checks and adds missing tagged value definitions to IStereotype objects
      final Set<String> taggedStereotypeNames = getOntoUMLClassStereotypeNames();

      for (String currentStereotypeName : taggedStereotypeNames) {
         final IStereotype currentStereotype = stereotypeElements.get(currentStereotypeName);
         ITaggedValueDefinitionContainer definitionsContainer = currentStereotype.getTaggedValueDefinitions();

         if (definitionsContainer == null) {
            definitionsContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
         }

         final ITaggedValueDefinition[] definitionsArray = definitionsContainer.toTaggedValueDefinitionArray();
         final Map<String, ITaggedValueDefinition> definitions = new HashMap<>();

         for (int j = 0; definitionsArray != null && j < definitionsArray.length; j++) {
            definitions.put(definitionsArray[j].getName(), definitionsArray[j]);
         }

         // Adds "restrictedTo" to all IStereotype objects
         if (!definitions.containsKey(PROPERTY_RESTRICTED_TO)) {
            final ITaggedValueDefinition restrictedTo = IModelElementFactory.instance().createTaggedValueDefinition();
            restrictedTo.setName(PROPERTY_RESTRICTED_TO);
            restrictedTo.setType(ITaggedValueDefinition.TYPE_TEXT);
            restrictedTo.setDefaultValue(getDefaultNature(currentStereotypeName));
            definitionsContainer.addTaggedValueDefinition(restrictedTo);
         }

         // Adds "isExtensional" to all STR_COLLECTIVE IStereotype
         if (currentStereotype.getName().equals(STR_COLLECTIVE) && !definitions.containsKey(PROPERTY_IS_EXTENSIONAL)) {
            final ITaggedValueDefinition isExtensional = IModelElementFactory.instance()
                    .createTaggedValueDefinition();
            isExtensional.setName(PROPERTY_IS_EXTENSIONAL);
            isExtensional.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
            isExtensional.setDefaultValue("false");
            definitionsContainer.addTaggedValueDefinition(isExtensional);
         }

         // Adds "isPowertype" to all STR_TYPE IStereotype
         if (currentStereotype.getName().equals(STR_TYPE) && !definitions.containsKey(PROPERTY_IS_POWERTYPE)) {
            final ITaggedValueDefinition isPowertype = IModelElementFactory.instance()
                    .createTaggedValueDefinition();
            isPowertype.setName(PROPERTY_IS_POWERTYPE);
            isPowertype.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
            isPowertype.setDefaultValue("false");
            definitionsContainer.addTaggedValueDefinition(isPowertype);
         }

         // Adds "order" to all STR_TYPE IStereotype
         if (currentStereotype.getName().equals(STR_TYPE) && !definitions.containsKey(PROPERTY_ORDER)) {
            final ITaggedValueDefinition order = IModelElementFactory.instance().createTaggedValueDefinition();
            order.setName(PROPERTY_ORDER);
            order.setType(ITaggedValueDefinition.TYPE_TEXT);
            order.setDefaultValue("2");
            definitionsContainer.addTaggedValueDefinition(order);
         }

         currentStereotype.setTaggedValueDefinitions(definitionsContainer);
      }

      if (STEREOTYPE_ELEMENTS == null) {
         STEREOTYPE_ELEMENTS = stereotypeElements;
      }
   }

   public static String getRestrictedNatures() {
      return toRestrictedNaturesString(NATURE_COLLECTIVE, NATURE_EVENT, NATURE_MODE, NATURE_FUNCTIONAL_COMPLEX,
              NATURE_QUALITY, NATURE_QUANTITY, NATURE_RELATOR, NATURE_TYPE, NATURE_ABSTRACT);
   }

   public static List<String> getRestrictedNaturesList() {
      return Arrays.asList(NATURE_COLLECTIVE, NATURE_EVENT, NATURE_MODE, NATURE_FUNCTIONAL_COMPLEX,
              NATURE_QUALITY, NATURE_QUANTITY, NATURE_RELATOR, NATURE_TYPE, NATURE_ABSTRACT);
   }

   public static String toRestrictedNaturesString(String... restrictedNatures) {
      if (restrictedNatures == null || restrictedNatures.length == 0)
         return "";

      StringBuilder restricted = new StringBuilder();

      for (int i = 0; i < restrictedNatures.length; i++) {
         restricted.append(i == 0 ? restrictedNatures[i] : ", " + restrictedNatures[i]);
      }

      return restricted.toString();
   }

   public static void applyStereotype(IModelElement element, String stereotypeName) {
      // Tries to retrieve the stereotype
      if (STEREOTYPE_ELEMENTS == null)
         generate();

      final IStereotype stereotype = STEREOTYPE_ELEMENTS.get(stereotypeName);

      // TODO: check the necessity of throwing some exception here
      if (stereotype == null || !element.getModelType().equals(stereotype.getBaseType()))
         return;

      final ITaggedValueContainer container = element.getTaggedValues();
      Map<String, Object> taggedValueMap = new HashMap<>();

      if (container != null) {
         ITaggedValue[] taggedValues = container.toTaggedValueArray();

         // 1. Saves and deletes tagged values associated to a stereotype
         for (ITaggedValue tv : taggedValues) {
            if (tv.getTagDefinition() != null) {
               taggedValueMap.put(tv.getName(), tv.getValue());
               tv.delete();
            }
         }
      }

      // 2. Removes old stereotypes
      for (IStereotype s : element.toStereotypeModelArray())
         element.removeStereotype(s);

      // 3. Adds new stereotype
      element.addStereotype(stereotype);

      // 4. Reapply tagged values
      ITaggedValue[] taggedValues = element.getTaggedValues().toTaggedValueArray();

      for (ITaggedValue taggedValue : taggedValues) {
         Object oldValue = taggedValueMap.get(taggedValue.getName());
         if (oldValue != null)
            taggedValue.setValue(oldValue.toString());
      }

      deleteTaggedValue(element, "allowed");
   }

   public static void setRestrictedTo(IModelElement element, String stereotypeName) {
      if (element.getTaggedValues() == null) {
         return;
      }

      Iterator<?> values = element.getTaggedValues().taggedValueIterator();

      while (values.hasNext()) {
         final ITaggedValue value = (ITaggedValue) values.next();
         final ITaggedValueDefinition definition = value != null ? value.getTagDefinition() : null;
         final IStereotype valueStereotype = definition != null ?
                 value.getTagDefinitionStereotype() : null;
         final String valueStereotypeName = valueStereotype != null ?
                 valueStereotype.getName() : null;

         if (
                 value.getName().equals(PROPERTY_RESTRICTED_TO) &&
                         stereotypeName.equals(valueStereotypeName)
         ) {
            final String defaultNature = getDefaultNature(stereotypeName);
            value.setValue(StereotypeUtils.toRestrictedNaturesString(defaultNature));
         }
      }
   }

   public static String getDefaultNature(String stereotype) {
      switch (stereotype) {
         case STR_TYPE:
            return NATURE_TYPE;
         case STR_HISTORICAL_ROLE:
         case STR_EVENT:
            return NATURE_EVENT;
         case STR_CATEGORY:
         case STR_MIXIN:
         case STR_ROLE_MIXIN:
         case STR_PHASE_MIXIN:
         case STR_KIND:
         case STR_SUBKIND:
         case STR_ROLE:
         case STR_PHASE:
            return NATURE_FUNCTIONAL_COMPLEX;
         case STR_COLLECTIVE:
            return NATURE_COLLECTIVE;
         case STR_QUANTITY:
            return NATURE_QUANTITY;
         case STR_RELATOR:
            return NATURE_RELATOR;
         case STR_QUALITY:
            return NATURE_QUALITY;
         case STR_MODE:
            return NATURE_MODE;
         case STR_ENUMERATION:
         case STR_DATATYPE:
            return NATURE_ABSTRACT;
         default:
            return "";
      }
   }

   public static ITaggedValue reapplyStereotypeAndGetTaggedValue(IClass _class, String taggedValueName) {
      // Delete allowed tagged value if exists
      deleteTaggedValue(_class, "allowed");

      ITaggedValue taggedValue = getTaggedValue(_class, taggedValueName);
      if (taggedValue == null) {
         boolean successfullyApplied = reapplyCurrentStereotype(_class);
         if (!successfullyApplied)
            return null;
      }

      //Retrieves desired tagged value
      return getTaggedValue(_class, taggedValueName);
   }

   public static boolean reapplyCurrentStereotype(IClass _class) {
      String stereotype = _class.toStereotypeArray() != null && _class.toStereotypeArray().length > 0 ?
              _class.toStereotypeArray()[0] : null;

      // Escape in case the stereotype is missing or incorrect
      if (stereotype == null || !getOntoUMLClassStereotypeNames().contains(stereotype))
         return false;

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
            if(tv.getName().equals(name))
               tv.delete();
         }
      }
   }

}