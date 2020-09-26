package it.unibz.inf.ontouml.vp.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestrictedTo {

  // Ontological natures
  public static final String FUNCTIONAL_COMPLEX = "functional-complex";
  public static final String COLLECTIVE = "collective";
  public static final String QUANTITY = "quantity";
  public static final String RELATOR = "relator";
  public static final String INTRINSIC_MODE = "intrinsic-mode";
  public static final String EXTRINSIC_MODE = "extrinsic-mode";
  public static final String QUALITY = "quality";
  public static final String EVENT = "event";
  public static final String SITUATION = "situation";
  public static final String TYPE = "type";
  public static final String ABSTRACT = "abstract";

  public static List<String> getRestrictionsList() {
    return Arrays.asList(
        COLLECTIVE,
        EVENT,
        SITUATION,
        INTRINSIC_MODE,
        EXTRINSIC_MODE,
        FUNCTIONAL_COMPLEX,
        QUALITY,
        QUANTITY,
        RELATOR,
        TYPE,
        ABSTRACT);
  }

  public static List<String> possibleRestrictedToValues(String stereotype) {
    if (stereotype == null) return Collections.emptyList();

    switch (stereotype) {
      case Stereotype.TYPE:
        return Collections.singletonList(TYPE);
      case Stereotype.EVENT:
        return Collections.singletonList(EVENT);
      case Stereotype.SITUATION:
        return Collections.singletonList(SITUATION);
      case Stereotype.KIND:
        return Collections.singletonList(FUNCTIONAL_COMPLEX);
      case Stereotype.COLLECTIVE:
        return Collections.singletonList(COLLECTIVE);
      case Stereotype.QUANTITY:
        return Collections.singletonList(QUANTITY);
      case Stereotype.RELATOR:
        return Collections.singletonList(RELATOR);
      case Stereotype.QUALITY:
        return Collections.singletonList(QUALITY);
      case Stereotype.MODE:
        return Arrays.asList(INTRINSIC_MODE, EXTRINSIC_MODE);
      case Stereotype.ENUMERATION:
      case Stereotype.DATATYPE:
      case Stereotype.ABSTRACT:
        return Collections.singletonList(ABSTRACT);
      case Stereotype.CATEGORY:
      case Stereotype.MIXIN:
      case Stereotype.ROLE_MIXIN:
      case Stereotype.PHASE_MIXIN:
      case Stereotype.HISTORICAL_ROLE_MIXIN:
      case Stereotype.SUBKIND:
      case Stereotype.ROLE:
      case Stereotype.PHASE:
      case Stereotype.HISTORICAL_ROLE:
        return Arrays.asList(
            FUNCTIONAL_COMPLEX,
            COLLECTIVE,
            QUANTITY,
            INTRINSIC_MODE,
            EXTRINSIC_MODE,
            QUALITY,
            RELATOR);
      default:
        return Collections.emptyList();
    }
  }

  public static boolean shouldOverrideRestrictedTo(String stereotype, String restrictedTo) {
    switch (stereotype) {
      case Stereotype.TYPE:
      case Stereotype.EVENT:
      case Stereotype.SITUATION:
      case Stereotype.KIND:
      case Stereotype.COLLECTIVE:
      case Stereotype.QUANTITY:
      case Stereotype.RELATOR:
      case Stereotype.QUALITY:
      case Stereotype.MODE:
      case Stereotype.ENUMERATION:
      case Stereotype.DATATYPE:
      case Stereotype.ABSTRACT:
        return true;
      case Stereotype.CATEGORY:
      case Stereotype.MIXIN:
      case Stereotype.ROLE_MIXIN:
      case Stereotype.PHASE_MIXIN:
      case Stereotype.HISTORICAL_ROLE_MIXIN:
        return (restrictedTo.contains(ABSTRACT)
            || restrictedTo.contains(EVENT)
            || restrictedTo.contains(TYPE)
            || restrictedTo.contains(SITUATION));
      case Stereotype.SUBKIND:
      case Stereotype.ROLE:
      case Stereotype.PHASE:
      case Stereotype.HISTORICAL_ROLE:
      default:
        return false;
    }
  }

  public static String getDefaultRestrictedTo(String stereotype) {
    switch (stereotype) {
      case Stereotype.TYPE:
        return TYPE;
      case Stereotype.EVENT:
        return EVENT;
      case Stereotype.SITUATION:
        return SITUATION;
      case Stereotype.CATEGORY:
      case Stereotype.MIXIN:
      case Stereotype.ROLE_MIXIN:
      case Stereotype.PHASE_MIXIN:
      case Stereotype.HISTORICAL_ROLE_MIXIN:
      case Stereotype.KIND:
        return FUNCTIONAL_COMPLEX;
      case Stereotype.COLLECTIVE:
        return COLLECTIVE;
      case Stereotype.QUANTITY:
        return QUANTITY;
      case Stereotype.RELATOR:
        return RELATOR;
      case Stereotype.QUALITY:
        return QUALITY;
      case Stereotype.MODE:
        return INTRINSIC_MODE;
      case Stereotype.ENUMERATION:
      case Stereotype.DATATYPE:
      case Stereotype.ABSTRACT:
        return ABSTRACT;
      case Stereotype.SUBKIND:
      case Stereotype.ROLE:
      case Stereotype.PHASE:
      case Stereotype.HISTORICAL_ROLE:
      default:
        return "";
    }
  }

  public static boolean isRestrictedToEditable(String stereotype) {
    final List<String> customizableRestrictedToStereotypes =
        Arrays.asList(
            Stereotype.CATEGORY,
            Stereotype.MIXIN,
            Stereotype.MODE,
            Stereotype.PHASE_MIXIN,
            Stereotype.ROLE_MIXIN,
            Stereotype.HISTORICAL_ROLE_MIXIN);

    return customizableRestrictedToStereotypes.contains(stereotype);
  }
}
