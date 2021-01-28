package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Optional;

public class Cardinality {

   private String cardinality;
   private String lowerBound;
   private String upperBound;

   public Cardinality(String lowerBound, String upperBound) {
      String cardinalityString = lowerBound.equals(upperBound) ? lowerBound : lowerBound + ".." + upperBound;
      setValue(cardinalityString);
   }

   public Cardinality(String cardinality) {
      setValue(cardinality);
   }

   public Cardinality() {
      setValue("1");
   }

   public void setValue(String cardinality) {
      if (cardinality == null)
         throw new NullPointerException("Cannot set null cardinality");

      this.cardinality = cardinality;
      setBounds();
   }

   public Optional<String> getValue() {
      return Optional.ofNullable(cardinality);
   }

   public Optional<String> getLowerBound() {
      return Optional.ofNullable(lowerBound);
   }

   public Optional<String> getUpperBound() {
      return Optional.ofNullable(upperBound);
   }

   public int getLowerBoundAsInt() {
      return Integer.parseInt(lowerBound);
   }

   public int getUpperBoundAsInt() {
      return isUnbounded() ? Integer.MAX_VALUE : Integer.parseInt(upperBound);
   }

   private void setBounds() {
      if (cardinality.equals("*")) {
         lowerBound = "0";
         upperBound = "*";
         return;
      }

      String[] bounds = cardinality.split("\\.\\.");

      if (bounds.length == 1) {
         lowerBound = upperBound = cardinality;
         return;
      }

      if (bounds.length == 2) {
         lowerBound = bounds[0];
         upperBound = bounds[1];
         return;
      }

      lowerBound = upperBound = null;
   }

   public boolean isLowerBoundValid() {
      try {
         return getLowerBoundAsInt() >= 0;
      } catch (Exception ignored) {
         return false;
      }
   }

   public boolean isUpperBoundValid() {
      try {
         return isUnbounded() || getUpperBoundAsInt() >= 1;
      } catch (Exception ignored) {
         return false;
      }
   }

   public boolean isUnbounded() {
      return "*".equals(upperBound);
   }

   public boolean isValid() {
      try {
         return isLowerBoundValid() &&
                 isUpperBoundValid() &&
                 getLowerBoundAsInt() <= getUpperBoundAsInt();
      } catch (Exception ignored) {
         return false;
      }
   }

}
