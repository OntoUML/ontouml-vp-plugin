package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OntoUMLConstraintsManager {

  private static final Type mapOfListsType =
      new TypeToken<Map<String, List<String>>>() {
      }.getType();

  private static String GENERALIZATION_CONSTRAINTS_FILENAME = "generalization_constraints.json";
  private static String ASSOCIATION_CONSTRAINTS_FILENAME = "association_constraints_nature.json";

  private static String ALLOWED_SUBCLASSES = "allowedSubClassesFor";
  private static String ALLOWED_SUPERCLASSES = "allowedSuperClassesFor";

  private static Map<String, List<String>> allowedSuperClassesFor, allowedSubClassesFor;

  private static Map<String, List<String>> associationConstraints;

  private static Map<String,List<String>> getAllowedSuperClassesFor() {
    if(!areConstraintsLoaded()) loadConstraints();
    return allowedSuperClassesFor;
  }

  private static Map<String,List<String>> getAllowedSubClassesFor() {
    if(!areConstraintsLoaded()) loadConstraints();
    return allowedSubClassesFor;
  }

  private static Map<String,List<String>> getAssociationConstraints() {
    if(!areConstraintsLoaded()) loadConstraints();
    return associationConstraints;
  }

  private static void loadConstraints() {
    try {
      final ApplicationManager app = ApplicationManager.instance();
      final VPPluginInfo pluginInfo = app.getPluginInfo(OntoUMLPlugin.PLUGIN_ID);

      File generalizationsConstraintsFile =
          new File(pluginInfo.getPluginDir(), GENERALIZATION_CONSTRAINTS_FILENAME);
      File associationConstraintsFile =
          new File(pluginInfo.getPluginDir(), ASSOCIATION_CONSTRAINTS_FILENAME);

      final JsonParser parser = new JsonParser();
      final JsonObject gConstraintsObject =
          parser.parse(new FileReader(generalizationsConstraintsFile)).getAsJsonObject();
      final JsonObject aConstraintsObject =
          parser.parse(new FileReader(associationConstraintsFile)).getAsJsonObject();

      final Gson gson = new Gson();
      allowedSubClassesFor =
          gson.fromJson(gConstraintsObject.getAsJsonObject(ALLOWED_SUBCLASSES), mapOfListsType);
      allowedSuperClassesFor =
          gson.fromJson(gConstraintsObject.getAsJsonObject(ALLOWED_SUPERCLASSES), mapOfListsType);
      associationConstraints = gson.fromJson(aConstraintsObject, mapOfListsType);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  // TODO: remove
  public static List<String> getAllowedActionsBasedOnParent(IClass parrentClass) {
    if (allowedSuperClassesFor == null || allowedSubClassesFor == null) {
      loadConstraints();
    }

    final String parentClassStereotype = ModelElement.getUniqueStereotypeName(parrentClass);
    List<String> allowedStereotypes = allowedSubClassesFor.get(parentClassStereotype);

    if (allowedStereotypes == null) {
      allowedStereotypes = new ArrayList<>();
    }

    return allowedStereotypes.stream()
        .map(allowedStereotype -> ActionIdManager.classStereotypeToActionID(allowedStereotype))
        .collect(Collectors.toList());
  }

  // TODO: remove
  public static List<String> getAllowedActionsBasedOnChild(IClass childClass) {
    if (allowedSuperClassesFor == null || allowedSubClassesFor == null) {
      loadConstraints();
    }

    final String childClassStereotype = ModelElement.getUniqueStereotypeName(childClass);
    List<String> allowedStereotypes = allowedSuperClassesFor.get(childClassStereotype);

    if (allowedStereotypes == null) {
      allowedStereotypes = new ArrayList<>();
    }

    return allowedStereotypes.stream()
        .map(allowedStereotype -> ActionIdManager.classStereotypeToActionID(allowedStereotype))
        .collect(Collectors.toList());
  }

  // TODO: remove
  public static List<String> getAllowedActionsBasedOnSourceAndTarget(
      IClass sourceClass, IClass targetClass) {
    if (associationConstraints == null) {
      loadConstraints();
    }

    final List<String> sourceRestrictions = Class.getRestrictedToList(sourceClass);
    final List<String> targetRestrictions = Class.getRestrictedToList(targetClass);
    final List<List<String>> listsOfAllowedStereotypes = new ArrayList<>();

    for (String sourceRestriction : sourceRestrictions) {
      for (String targetRestriction : targetRestrictions) {
        final List<String> allowedStereotypes =
            associationConstraints.get(sourceRestriction + ":" + targetRestriction);

        if (allowedStereotypes != null) {
          listsOfAllowedStereotypes.add(allowedStereotypes);
        }
      }
    }

    List<String> intersectingAllowedStereotypes = new ArrayList<>();

    for (List<String> allowedStereotypes : listsOfAllowedStereotypes) {
      if (allowedStereotypes.isEmpty()) {
        return allowedStereotypes;
      }

      if (intersectingAllowedStereotypes.isEmpty()) {
        intersectingAllowedStereotypes.addAll(allowedStereotypes);
      } else {
        intersectingAllowedStereotypes =
            intersectingAllowedStereotypes.stream()
                .filter(allowedStereotypes::contains)
                .distinct()
                .collect(Collectors.toList());
      }
    }

    return intersectingAllowedStereotypes.stream()
        .map(
            allowedStereotype -> ActionIdManager.associationStereotypeToActionID(allowedStereotype))
        .collect(Collectors.toList());
  }

  public static boolean isStereotypeAllowed(IClass _class, String stereotype) {
    return isStereotypeAllowedBasedOnChildClasses(_class, stereotype)
        && isStereotypeAllowedBasedOnParentClasses(_class, stereotype);
  }

  public static boolean isStereotypeAllowed(IAssociation association, String stereotype) {
    final List<String> sourceRestrictions = Association.getSourceRestrictions(association);
    final List<String> targetRestrictions = Association.getTargetRestrictions(association);
    boolean isAllowed = false;

    for (var sourceRestriction : sourceRestrictions) {
      for (var targetRestriction : targetRestrictions) {
        isAllowed = getAssociationConstraints().get(sourceRestriction + ":" + targetRestriction)
            .contains(stereotype);

        if(isAllowed) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean isStereotypeAllowedIfInverted(IAssociation association, String stereotype) {
    final List<String> sourceRestrictions = Association.getSourceRestrictions(association);
    final List<String> targetRestrictions = Association.getTargetRestrictions(association);
    boolean isAllowed = false;

    for (var sourceRestriction : sourceRestrictions) {
      for (var targetRestriction : targetRestrictions) {
        isAllowed = associationConstraints.get(targetRestriction + ":" + sourceRestriction)
            .contains(stereotype);
        if(isAllowed) {
          return true;
        }
      }
    }

    return false;
  }

  private static boolean areConstraintsLoaded() {
    return allowedSuperClassesFor != null && allowedSubClassesFor != null && associationConstraints != null
        && !allowedSuperClassesFor.isEmpty() && !allowedSubClassesFor.isEmpty() && !associationConstraints.isEmpty();
  }

  private static boolean isStereotypeAllowedBasedOnChildClasses(IClass _class, String stereotype) {
    return Class.getChildren(_class).stream()
        .map(ModelElement::getUniqueStereotypeName)
        .allMatch(
            childStereotype -> childStereotype == null || getAllowedSubClassesFor().get(stereotype)
                .contains(childStereotype));
  }

  private static boolean isStereotypeAllowedBasedOnParentClasses(IClass _class, String stereotype) {
    return Class.getParents(_class).stream()
        .map(ModelElement::getUniqueStereotypeName)
        .allMatch(
            parentStereotype -> parentStereotype == null || getAllowedSuperClassesFor().get(stereotype)
                .contains(parentStereotype));
  }
}
