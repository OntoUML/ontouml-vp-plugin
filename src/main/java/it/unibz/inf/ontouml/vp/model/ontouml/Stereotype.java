package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.EnumSet;
import java.lang.Class;
import java.util.Optional;

public interface Stereotype {

   String getStereotypeName();

   static <S extends Enum<S> & Stereotype> Optional<S> findByName(Class<S> enumeration, String name) {
      return EnumSet.allOf(enumeration)
              .stream()
              .filter(value -> value.getStereotypeName().equals(name))
              .findFirst();
   }

}