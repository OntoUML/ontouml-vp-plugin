package it.unibz.inf.ontouml.vp.model.ontouml;

public enum AggregationKind {
   NONE("NONE"),
   SHARED("SHARED"),
   COMPOSITE("COMPOSITE");

   final String name;

   AggregationKind(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
