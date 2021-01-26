package it.unibz.inf.ontouml.vp.model.ontouml;

public enum AggregationKind {
   NONE("NONE"),
   SHARED("SHARED"),
   COMPOSITE("COMPOSITE");

   String name;

   AggregationKind(String name) {
      this.name = name;
   }
}
