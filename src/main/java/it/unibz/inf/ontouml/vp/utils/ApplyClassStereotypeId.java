package it.unibz.inf.ontouml.vp.utils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public enum ApplyClassStereotypeId {

  TYPE("it.unibz.inf.ontouml.vp.addTypeStereotype", Stereotype.TYPE, "Type"),
  HISTORICAL_ROLE("it.unibz.inf.ontouml.vp.addHistoricalRoleStereotype", Stereotype.HISTORICAL_ROLE,
      "HistoricalRole"),
  HISTORICAL_ROLE_MIXIN("it.unibz.inf.ontouml.vp.addHistoricalRoleMixinStereotype",
      Stereotype.HISTORICAL_ROLE_MIXIN, "HistoricalRoleMixin"),
  EVENT("it.unibz.inf.ontouml.vp.addEventStereotype", Stereotype.EVENT, "Event"),
  SITUATION("it.unibz.inf.ontouml.vp.addSituationStereotype", Stereotype.SITUATION, "Situation"),
  ENUMERATION("it.unibz.inf.ontouml.vp.addEnumerationStereotype", Stereotype.ENUMERATION,
      "Enumeration"),
  DATATYPE("it.unibz.inf.ontouml.vp.addDatatypeStereotype", Stereotype.DATATYPE, "Datatype"),
  ABSTRACT("it.unibz.inf.ontouml.vp.addAbstractStereotype", Stereotype.ABSTRACT, "Abstract"),
  SUBKIND("it.unibz.inf.ontouml.vp.addSubkindStereotype", Stereotype.SUBKIND, "Subkind"),
  ROLE_MIXIN("it.unibz.inf.ontouml.vp.addRoleMixinStereotype", Stereotype.ROLE_MIXIN, "Role"),
  ROLE("it.unibz.inf.ontouml.vp.addRoleStereotype", Stereotype.ROLE, "RoleMixin"),
  RELATOR("it.unibz.inf.ontouml.vp.addRelatorStereotype", Stereotype.RELATOR, "Relator"),
  QUANTITY("it.unibz.inf.ontouml.vp.addQuantityStereotype", Stereotype.QUANTITY, "Quantity"),
  QUALITY("it.unibz.inf.ontouml.vp.addQualityStereotype", Stereotype.QUALITY, "Quality"),
  PHASE_MIXIN("it.unibz.inf.ontouml.vp.addPhaseMixinStereotype", Stereotype.PHASE_MIXIN,
      "PhaseMixin"),
  PHASE("it.unibz.inf.ontouml.vp.addPhaseStereotype", Stereotype.PHASE, "Phase"),
  MODE("it.unibz.inf.ontouml.vp.addModeStereotype", Stereotype.MODE, "Mode"),
  MIXIN("it.unibz.inf.ontouml.vp.addMixinStereotype", Stereotype.MIXIN, "Mixin"),
  KIND("it.unibz.inf.ontouml.vp.addKindStereotype", Stereotype.KIND, "Kind"),
  COLLECTIVE("it.unibz.inf.ontouml.vp.addCollectiveStereotype", Stereotype.COLLECTIVE,
      "Collective"),
  CATEGORY("it.unibz.inf.ontouml.vp.addCategoryStereotype", Stereotype.CATEGORY, "Category"),

  TYPE_FIXED("it.unibz.inf.ontouml.vp.addTypeStereotype.fixedMenu", Stereotype.TYPE, "Type"),
  HISTORICAL_ROLE_FIXED("it.unibz.inf.ontouml.vp.addHistoricalRoleStereotype.fixedMenu",
      Stereotype.HISTORICAL_ROLE, "HistoricalRole"),
  HISTORICAL_ROLE_MIXIN_FIXED("it.unibz.inf.ontouml.vp.addHistoricalRoleMixinStereotype.fixedMenu",
      Stereotype.HISTORICAL_ROLE_MIXIN, "HistoricalRoleMixin"),
  EVENT_FIXED("it.unibz.inf.ontouml.vp.addEventStereotype.fixedMenu", Stereotype.EVENT, "Event"),
  SITUATION_FIXED("it.unibz.inf.ontouml.vp.addSituationStereotype.fixedMenu", Stereotype.SITUATION,
      "Situation"),
  ENUMERATION_FIXED("it.unibz.inf.ontouml.vp.addEnumerationStereotype.fixedMenu",
      Stereotype.ENUMERATION, "Enumeration"),
  DATATYPE_FIXED("it.unibz.inf.ontouml.vp.addDatatypeStereotype.fixedMenu", Stereotype.DATATYPE,
      "Datatype"),
  ABSTRACT_FIXED("it.unibz.inf.ontouml.vp.addAbstractStereotype.fixedMenu", Stereotype.ABSTRACT,
      "Abstract"),
  SUBKIND_FIXED("it.unibz.inf.ontouml.vp.addSubkindStereotype.fixedMenu", Stereotype.SUBKIND,
      "Subkind"),
  ROLE_MIXIN_FIXED("it.unibz.inf.ontouml.vp.addRoleMixinStereotype.fixedMenu",
      Stereotype.ROLE_MIXIN, "Role"),
  ROLE_FIXED("it.unibz.inf.ontouml.vp.addRoleStereotype.fixedMenu", Stereotype.ROLE, "RoleMixin"),
  RELATOR_FIXED("it.unibz.inf.ontouml.vp.addRelatorStereotype.fixedMenu", Stereotype.RELATOR,
      "Relator"),
  QUANTITY_FIXED("it.unibz.inf.ontouml.vp.addQuantityStereotype.fixedMenu", Stereotype.QUANTITY,
      "Quantity"),
  QUALITY_FIXED("it.unibz.inf.ontouml.vp.addQualityStereotype.fixedMenu", Stereotype.QUALITY,
      "Quality"),
  PHASE_MIXIN_FIXED("it.unibz.inf.ontouml.vp.addPhaseMixinStereotype.fixedMenu",
      Stereotype.PHASE_MIXIN, "PhaseMixin"),
  PHASE_FIXED("it.unibz.inf.ontouml.vp.addPhaseStereotype.fixedMenu", Stereotype.PHASE, "Phase"),
  MODE_FIXED("it.unibz.inf.ontouml.vp.addModeStereotype.fixedMenu", Stereotype.MODE, "Mode"),
  MIXIN_FIXED("it.unibz.inf.ontouml.vp.addMixinStereotype.fixedMenu", Stereotype.MIXIN, "Mixin"),
  KIND_FIXED("it.unibz.inf.ontouml.vp.addKindStereotype.fixedMenu", Stereotype.KIND, "Kind"),
  COLLECTIVE_FIXED("it.unibz.inf.ontouml.vp.addCollectiveStereotype.fixedMenu",
      Stereotype.COLLECTIVE, "Collective"),
  CATEGORY_FIXED("it.unibz.inf.ontouml.vp.addCategoryStereotype.fixedMenu", Stereotype.CATEGORY,
      "Category");

  private final String actionId;
  private final String stereotype;
  private final String defaultLabel;

  private static HashMap<String, ApplyClassStereotypeId> actionIdMap;

  private ApplyClassStereotypeId(String actionId, String stereotype, String defaultLabel) {
    this.actionId = actionId;
    this.stereotype = stereotype;
    this.defaultLabel = defaultLabel;
  }

  public String getActionId() {
    return actionId;
  }

  public String getDefaultLabel() {
    return defaultLabel;
  }

  public String getStereotype() {
    return stereotype;
  }

  public boolean isFixed() {
    return getActionId().contains(".fixedMenu");
  }

  public static ApplyClassStereotypeId getFromActionId(String actionId) {
    if (actionIdMap == null || actionIdMap.isEmpty()) {
      actionIdMap = new HashMap<String, ApplyClassStereotypeId>();
      Arrays.stream(ApplyClassStereotypeId.values())
          .forEach(applyClassStereotypeId -> actionIdMap
              .put(applyClassStereotypeId.getActionId(), applyClassStereotypeId));
    }

    return actionIdMap.get(actionId);
  }

  public boolean hasKnownNature() {
    Set<?> knownNatureStereotypeActions = Set.of(
        TYPE, TYPE_FIXED, EVENT, EVENT_FIXED, SITUATION, SITUATION_FIXED, ENUMERATION,
        ENUMERATION_FIXED, DATATYPE, DATATYPE_FIXED, ABSTRACT, ABSTRACT_FIXED, RELATOR,
        RELATOR_FIXED, QUANTITY, QUANTITY_FIXED, QUALITY, QUALITY_FIXED, MODE, MODE_FIXED, KIND,
        KIND_FIXED, COLLECTIVE, COLLECTIVE_FIXED);

    return knownNatureStereotypeActions.contains(this);
  }
}
