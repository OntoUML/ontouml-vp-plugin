package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.*;

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

   public static final Collection<ClassStereotype> SORTALS = Set.of(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE, SUBKIND, PHASE, ROLE, HISTORICAL_ROLE);
   public static final Collection<ClassStereotype> ULTIMATE_SORTALS = Set.of(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE);
   public static final Collection<ClassStereotype> BASE_SORTALS = Set.of(SUBKIND, PHASE, ROLE, HISTORICAL_ROLE);
   public static final Collection<ClassStereotype> NON_SORTALS = Set.of(CATEGORY, MIXIN, PHASE_MIXIN, ROLE_MIXIN, HISTORICAL_ROLE_MIXIN);
   public static final Collection<ClassStereotype> RIGIDS = Set.of(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE, SUBKIND, EVENT, SITUATION, TYPE, ABSTRACT, DATATYPE, ENUMERATION);
   public static final Collection<ClassStereotype> ANTI_RIGIDS = Set.of(ROLE, ROLE_MIXIN, PHASE, PHASE_MIXIN, HISTORICAL_ROLE, HISTORICAL_ROLE_MIXIN);
   public static final Collection<ClassStereotype> SEMI_RIGIDS = Set.of(MIXIN);
   public static final Collection<ClassStereotype> MOMENTS = Set.of(MODE, QUALITY, RELATOR);
   public static final Collection<ClassStereotype> SUBSTANTIALS = Set.of(KIND, QUANTITY, COLLECTIVE);
   public static final Collection<ClassStereotype> ENDURANTS = Set.of(KIND, QUANTITY, COLLECTIVE, RELATOR, QUALITY, MODE, SUBKIND, PHASE, ROLE, HISTORICAL_ROLE, CATEGORY, MIXIN, PHASE_MIXIN, ROLE_MIXIN, HISTORICAL_ROLE_MIXIN);
   public static final Collection<ClassStereotype> ABSTRACTS = Set.of(ABSTRACT, DATATYPE, ENUMERATION);

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
