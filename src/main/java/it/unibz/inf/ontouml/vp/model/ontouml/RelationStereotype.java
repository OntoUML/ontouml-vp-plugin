package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum RelationStereotype implements Stereotype {
  MATERIAL("material"),
  DERIVATION("derivation"),
  COMPARATIVE("comparative"),
  MEDIATION("mediation"),
  CHARACTERIZATION("characterization"),
  EXTERNAL_DEPENDENCE("externalDependence"),
  COMPONENT_OF("componentOf"),
  MEMBER_OF("memberOf"),
  SUBCOLLECTION_OF("subCollectionOf"),
  SUBQUANTITY_OF("subQuantityOf"),
  INSTANTIATION("instantiation"),
  TERMINATION("termination"),
  PARTICIPATIONAL("participational"),
  PARTICIPATION("participation"),
  HISTORICAL_DEPENDENCE("historicalDependence"),
  CREATION("creation"),
  MANIFESTATION("manifestation"),
  BRINGS_ABOUT("bringsAbout"),
  TRIGGERS("triggers");

  public static List<RelationStereotype> EXISTENTIAL_DEPENDENCY_ON_SOURCE =
      Arrays.asList(
          BRINGS_ABOUT,
          CREATION,
          MANIFESTATION,
          PARTICIPATION,
          PARTICIPATIONAL,
          TERMINATION,
          TRIGGERS);

  public static List<RelationStereotype> EXISTENTIAL_DEPENDENCY_ON_TARGET =
      Arrays.asList(
          BRINGS_ABOUT,
          CHARACTERIZATION,
          CREATION,
          EXTERNAL_DEPENDENCE,
          HISTORICAL_DEPENDENCE,
          MEDIATION,
          PARTICIPATIONAL);

  public static List<RelationStereotype> EXISTENTIAL_DEPENDENCY =
      Arrays.asList(
          BRINGS_ABOUT,
          CHARACTERIZATION,
          CREATION,
          EXTERNAL_DEPENDENCE,
          HISTORICAL_DEPENDENCE,
          MANIFESTATION,
          MEDIATION,
          PARTICIPATION,
          PARTICIPATIONAL,
          TERMINATION,
          TRIGGERS);

  public static List<RelationStereotype> ALLOWS_HIGHER_ARITY = Arrays.asList(MATERIAL);

  public final String stereotypeName;

  RelationStereotype(String name) {
    this.stereotypeName = name;
  }

  @Override
  public String getStereotypeName() {
    return stereotypeName;
  }

  public static Optional<RelationStereotype> findByName(String name) {
    return Stereotype.findByName(RelationStereotype.class, name);
  }

  public boolean isExistentialDependency() {
    return EXISTENTIAL_DEPENDENCY.contains(this);
  }

  public boolean isBinaryOnly() {
    return !ALLOWS_HIGHER_ARITY.contains(this);
  }
}
