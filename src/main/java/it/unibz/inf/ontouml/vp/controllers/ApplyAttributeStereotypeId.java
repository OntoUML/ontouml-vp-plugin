package it.unibz.inf.ontouml.vp.controllers;

import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.util.Arrays;
import java.util.HashMap;

public enum ApplyAttributeStereotypeId {
  END("it.unibz.inf.ontouml.vp.addEndStereotype", Stereotype.END, "End"),
  BEGIN("it.unibz.inf.ontouml.vp.addBeginStereotype", Stereotype.BEGIN, "Begin"),

  END_FIXED("it.unibz.inf.ontouml.vp.addEndStereotype.fixedMenu", Stereotype.END, "End"),
  BEGIN_FIXED("it.unibz.inf.ontouml.vp.addBeginStereotype.fixedMenu", Stereotype.BEGIN, "Begin");

  private final String actionId;
  private final String stereotype;
  private final String defaultLabel;

  private static HashMap<String, ApplyAttributeStereotypeId> actionIdMap;

  private ApplyAttributeStereotypeId(String actionId, String stereotype, String defaultLabel) {
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

  public static ApplyAttributeStereotypeId getFromActionId(String actionId) {
    if (actionIdMap == null || actionIdMap.isEmpty()) {
      actionIdMap = new HashMap<String, ApplyAttributeStereotypeId>();
      Arrays.stream(ApplyAttributeStereotypeId.values())
          .forEach(
              applyAttributeStereotypeId ->
                  actionIdMap.put(
                      applyAttributeStereotypeId.getActionId(), applyAttributeStereotypeId));
    }

    return actionIdMap.get(actionId);
  }

  public boolean isFixed() {
    return getActionId().contains(".fixedMenu");
  }
}
