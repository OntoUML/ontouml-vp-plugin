package it.unibz.inf.ontouml.vp.model.ontouml;

public enum Nature {
  FUNCTIONAL_COMPLEX("functional-complex", true, true, false, false),
  COLLECTIVE("collective", true, true, false, false),
  QUANTITY("quantity", true, true, false, false),
  RELATOR("relator", true, false, false, true),
  INTRINSIC_MODE("intrinsic-mode", false, false, true, false),
  EXTRINSIC_MODE("extrinsic-mode", false, false, false, true),
  QUALITY("quality", true, false, true, false),
  EVENT("event", false, false, false, false),
  SITUATION("situation", false, false, false, false),
  TYPE("type", false, false, false, false),
  ABSTRACT("abstract", false, false, false, false);

  public final String name;
  public final boolean isEndurant, isSubstantial, isMoment, isIntrinsicMoment, isExtrinsicMoment;

  Nature(
      String name,
      boolean isEndurant,
      boolean isSubstantial,
      boolean isIntrinsicMoment,
      boolean isExtrinsicMoment) {
    this.name = name;
    this.isEndurant = isEndurant;
    this.isSubstantial = isSubstantial;
    this.isMoment = isIntrinsicMoment || isExtrinsicMoment;
    this.isIntrinsicMoment = isIntrinsicMoment;
    this.isExtrinsicMoment = isExtrinsicMoment;
  }

  public String getName() {
    return name;
  }

  public boolean isEndurant() {
    return isEndurant;
  }

  public boolean isSubstantial() {
    return isSubstantial;
  }

  public boolean isMoment() {
    return isMoment;
  }

  public boolean isIntrinsicMoment() {
    return isIntrinsicMoment;
  }

  public boolean isExtrinsicMoment() {
    return isExtrinsicMoment;
  }
}
