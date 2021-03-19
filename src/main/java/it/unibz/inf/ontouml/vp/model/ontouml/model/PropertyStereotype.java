package it.unibz.inf.ontouml.vp.model.ontouml.model;

import java.util.Optional;

public enum PropertyStereotype implements Stereotype {
  BEGIN("begin"),
  END("end");

  public final String stereotypeName;

  PropertyStereotype(String name) {
    this.stereotypeName = name;
  }

  @Override
  public String getStereotypeName() {
    return stereotypeName;
  }

  public static Optional<PropertyStereotype> findByName(String name) {
    return Stereotype.findByName(PropertyStereotype.class, name);
  }
}
