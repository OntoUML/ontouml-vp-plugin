package it.unibz.inf.ontouml.vp.controllers;

import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.util.Arrays;
import java.util.HashMap;

public enum ApplyAssociationStereotypeId {
  INSTANTIATION(
      "it.unibz.inf.ontouml.vp.addInstantiationStereotype",
      Stereotype.INSTANTIATION,
      "Instantiation"),
  TERMINATION(
      "it.unibz.inf.ontouml.vp.addTerminationStereotype", Stereotype.TERMINATION, "Termination"),
  PARTICIPATIONAL(
      "it.unibz.inf.ontouml.vp.addParticipationalStereotype",
      Stereotype.PARTICIPATIONAL,
      "Participational"),
  PARTICIPATION(
      "it.unibz.inf.ontouml.vp.addParticipationStereotype",
      Stereotype.PARTICIPATION,
      "Participation"),
  HISTORICAL_DEPENDENCE(
      "it.unibz.inf.ontouml.vp.addHistoricalDependenceStereotype",
      Stereotype.HISTORICAL_DEPENDENCE,
      "HistoricalDependence"),
  CREATION("it.unibz.inf.ontouml.vp.addCreationStereotype", Stereotype.CREATION, "Creation"),
  MANIFESTATION(
      "it.unibz.inf.ontouml.vp.addManifestationStereotype",
      Stereotype.MANIFESTATION,
      "Manifestation"),
  BRINGS_ABOUT(
      "it.unibz.inf.ontouml.vp.addBringsAboutStereotype", Stereotype.BRINGS_ABOUT, "BringsAbout"),
  TRIGGERS("it.unibz.inf.ontouml.vp.addTriggersStereotype", Stereotype.TRIGGERS, "Triggers"),
  SUB_QUANTITY_OF(
      "it.unibz.inf.ontouml.vp.addSubQuantityStereotype",
      Stereotype.SUB_QUANTITY_OF,
      "SubQuantityOf"),
  SUB_COLLECTION_OF(
      "it.unibz.inf.ontouml.vp.addSubCollectionStereotype",
      Stereotype.SUB_COLLECTION_OF,
      "SubCollectionOf"),
  MEMBER_OF("it.unibz.inf.ontouml.vp.addMemberOfStereotype", Stereotype.MEMBER_OF, "MemberOf"),
  MEDIATION("it.unibz.inf.ontouml.vp.addMediationStereotype", Stereotype.MEDIATION, "Mediation"),
  MATERIAL("it.unibz.inf.ontouml.vp.addMaterialStereotype", Stereotype.MATERIAL, "Material"),
  EXTERNAL_DEPENDENCE(
      "it.unibz.inf.ontouml.vp.addExternalDependenceStereotype",
      Stereotype.EXTERNAL_DEPENDENCE,
      "ExternalDependence"),
  COMPONENT_OF(
      "it.unibz.inf.ontouml.vp.addComponentOfStereotype", Stereotype.COMPONENT_OF, "ComponentOf"),
  COMPARATIVE(
      "it.unibz.inf.ontouml.vp.addComparativeStereotype", Stereotype.COMPARATIVE, "Comparative"),
  CHARACTERIZATION(
      "it.unibz.inf.ontouml.vp.addCharacterizationStereotype",
      Stereotype.CHARACTERIZATION,
      "Characterization"),

  INSTANTIATION_FIXED(
      "it.unibz.inf.ontouml.vp.addInstantiationStereotype.fixedMenu",
      Stereotype.INSTANTIATION,
      "Instantiation"),
  TERMINATION_FIXED(
      "it.unibz.inf.ontouml.vp.addTerminationStereotype.fixedMenu",
      Stereotype.TERMINATION,
      "Termination"),
  PARTICIPATIONAL_FIXED(
      "it.unibz.inf.ontouml.vp.addParticipationalStereotype.fixedMenu",
      Stereotype.PARTICIPATIONAL,
      "Participational"),
  PARTICIPATION_FIXED(
      "it.unibz.inf.ontouml.vp.addParticipationStereotype.fixedMenu",
      Stereotype.PARTICIPATION,
      "Participation"),
  HISTORICAL_DEPENDENCE_FIXED(
      "it.unibz.inf.ontouml.vp.addHistoricalDependenceStereotype.fixedMenu",
      Stereotype.HISTORICAL_DEPENDENCE,
      "HistoricalDependence"),
  CREATION_FIXED(
      "it.unibz.inf.ontouml.vp.addCreationStereotype.fixedMenu", Stereotype.CREATION, "Creation"),
  MANIFESTATION_FIXED(
      "it.unibz.inf.ontouml.vp.addManifestationStereotype.fixedMenu",
      Stereotype.MANIFESTATION,
      "Manifestation"),
  BRINGS_ABOUT_FIXED(
      "it.unibz.inf.ontouml.vp.addBringsAboutStereotype.fixedMenu",
      Stereotype.BRINGS_ABOUT,
      "BringsAbout"),
  TRIGGERS_FIXED(
      "it.unibz.inf.ontouml.vp.addTriggersStereotype.fixedMenu", Stereotype.TRIGGERS, "Triggers"),
  SUB_QUANTITY_OF_FIXED(
      "it.unibz.inf.ontouml.vp.addSubQuantityStereotype.fixedMenu",
      Stereotype.SUB_QUANTITY_OF,
      "SubQuantityOf"),
  SUB_COLLECTION_OF_FIXED(
      "it.unibz.inf.ontouml.vp.addSubCollectionStereotype.fixedMenu",
      Stereotype.SUB_COLLECTION_OF,
      "SubCollectionOf"),
  MEMBER_OF_FIXED(
      "it.unibz.inf.ontouml.vp.addMemberOfStereotype.fixedMenu", Stereotype.MEMBER_OF, "MemberOf"),
  MEDIATION_FIXED(
      "it.unibz.inf.ontouml.vp.addMediationStereotype.fixedMenu",
      Stereotype.MEDIATION,
      "Mediation"),
  MATERIAL_FIXED(
      "it.unibz.inf.ontouml.vp.addMaterialStereotype.fixedMenu", Stereotype.MATERIAL, "Material"),
  EXTERNAL_DEPENDENCE_FIXED(
      "it.unibz.inf.ontouml.vp.addExternalDependenceStereotype.fixedMenu",
      Stereotype.EXTERNAL_DEPENDENCE,
      "ExternalDependence"),
  COMPONENT_OF_FIXED(
      "it.unibz.inf.ontouml.vp.addComponentOfStereotype.fixedMenu",
      Stereotype.COMPONENT_OF,
      "ComponentOf"),
  COMPARATIVE_FIXED(
      "it.unibz.inf.ontouml.vp.addComparativeStereotype.fixedMenu",
      Stereotype.COMPARATIVE,
      "Comparative"),
  CHARACTERIZATION_FIXED(
      "it.unibz.inf.ontouml.vp.addCharacterizationStereotype.fixedMenu",
      Stereotype.CHARACTERIZATION,
      "Characterization");

  private final String actionId;
  private final String stereotype;
  private final String defaultLabel;

  private static HashMap<String, ApplyAssociationStereotypeId> actionIdMap;

  private ApplyAssociationStereotypeId(String actionId, String stereotype, String defaultLabel) {
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

  public static ApplyAssociationStereotypeId getFromActionId(String actionId) {
    if (actionIdMap == null || actionIdMap.isEmpty()) {
      actionIdMap = new HashMap<String, ApplyAssociationStereotypeId>();
      Arrays.stream(ApplyAssociationStereotypeId.values())
          .forEach(
              applyAssociationStereotypeId ->
                  actionIdMap.put(
                      applyAssociationStereotypeId.getActionId(), applyAssociationStereotypeId));
    }

    return actionIdMap.get(actionId);
  }
}
