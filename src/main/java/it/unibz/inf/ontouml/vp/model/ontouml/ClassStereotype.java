package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ClassStereotype implements Stereotype {

   TYPE("type"),
   HISTORICAL_ROLE("historicalRole"),
   HISTORICAL_ROLE_MIXIN("historicalRoleMixin"),
   EVENT("event"),
   SITUATION("situation"),
   CATEGORY("category"),
   MIXIN("mixin"),
   ROLE_MIXIN("roleMixin"),
   PHASE_MIXIN("phaseMixin"),
   KIND("kind"),
   COLLECTIVE("collective"),
   QUANTITY("quantity"),
   RELATOR("relator"),
   QUALITY("quality"),
   MODE("mode"),
   SUBKIND("subkind"),
   ROLE("role"),
   PHASE("phase"),
   ENUMERATION("enumeration"),
   DATATYPE("datatype"),
   ABSTRACT("abstract");

   public static List<ClassStereotype> SORTALS = Arrays.asList(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE, SUBKIND, PHASE, ROLE, HISTORICAL_ROLE);
   public static List<ClassStereotype> ULTIMATE_SORTALS = Arrays.asList(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE);
   public static List<ClassStereotype> BASE_SORTALS = Arrays.asList(SUBKIND, PHASE, ROLE, HISTORICAL_ROLE);
   public static List<ClassStereotype> NON_SORTALS = Arrays.asList(CATEGORY, MIXIN, PHASE_MIXIN, ROLE_MIXIN, HISTORICAL_ROLE_MIXIN);
   public static List<ClassStereotype> RIGIDS = Arrays.asList(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE, SUBKIND, EVENT, SITUATION, TYPE, ABSTRACT, DATATYPE, ENUMERATION);
   public static List<ClassStereotype> ANTI_RIGIDS = Arrays.asList(ROLE, ROLE_MIXIN, PHASE, PHASE_MIXIN, HISTORICAL_ROLE, HISTORICAL_ROLE_MIXIN);
   public static List<ClassStereotype> SEMI_RIGIDS = Arrays.asList(MIXIN);
   public static List<ClassStereotype> MOMENTS = Arrays.asList(MODE, QUALITY, RELATOR);
   public static List<ClassStereotype> SUBSTANTIALS = Arrays.asList(KIND, QUANTITY, COLLECTIVE);
   public static List<ClassStereotype> ENDURANTS = Arrays.asList(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE, SUBKIND, PHASE, ROLE, HISTORICAL_ROLE, CATEGORY, MIXIN, PHASE_MIXIN, ROLE_MIXIN, HISTORICAL_ROLE_MIXIN);
   public static List<ClassStereotype> ABSTRACTS = Arrays.asList(ABSTRACT, DATATYPE, ENUMERATION);

   public final String stereotypeName;

   ClassStereotype(String name) {
      this.stereotypeName = name;
   }

   public boolean isNonSortal() {
      return NON_SORTALS.contains(this);
   }

   public boolean isSortal() {
      return SORTALS.contains(this);
   }

   public boolean isUltimateSortal() {
      return ULTIMATE_SORTALS.contains(this);
   }

   public boolean isBaseSortal() {
      return BASE_SORTALS.contains(this);
   }

   public boolean isRigid() {
      return RIGIDS.contains(this);
   }

   public boolean isAntiRigid() {
      return ANTI_RIGIDS.contains(this);
   }

   public boolean isSemiRigid() {
      return SEMI_RIGIDS.contains(this);
   }

   public boolean isAbstract() {
      return ABSTRACTS.contains(this);
   }

   public boolean isEndurant() {
      return ENDURANTS.contains(this);
   }

   public boolean isSubstantial() {
      return SUBSTANTIALS.contains(this);
   }

   public boolean isMoment() {
      return MOMENTS.contains(this);
   }

   public boolean isEvent() {
      return this.equals(EVENT);
   }

   public boolean isSituation() {
      return this.equals(SITUATION);
   }

   public boolean isType() {
      return this.equals(TYPE);
   }

   @Override
   public String getStereotypeName() {
      return stereotypeName;
   }

   public static Optional<ClassStereotype> findByName(String name) {
      return Stereotype.findByName(ClassStereotype.class, name);
   }


}
