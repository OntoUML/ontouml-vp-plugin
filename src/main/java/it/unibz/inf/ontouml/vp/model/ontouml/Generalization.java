package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Collections;
import java.util.List;

public class Generalization extends ModelElement {

   private Classifier<?, ?> general;
   private Classifier<?, ?> specific;


   public <T extends Classifier<T, S>, S extends Stereotype> Generalization(String id, MultilingualText name, Classifier<T, S> specific, Classifier<T, S> general) {
      super(id, name);
      this.general = general;
      this.specific = specific;
   }

   public <T extends Classifier<T, S>, S extends Stereotype> Generalization(String id, String name, Classifier<T, S> specific, Classifier<T, S> general) {
      this(id, new MultilingualText(name), specific, general);
   }

   public <T extends Classifier<T, S>, S extends Stereotype> Generalization(String id, Classifier<T, S> specific, Classifier<T, S> general) {
      this(id, (MultilingualText) null, specific, general);
   }

   public <T extends Classifier<T, S>, S extends Stereotype> Generalization(Classifier<T, S> specific, Classifier<T, S> general) {
      this(null, (MultilingualText) null, specific, general);
   }

   @Override
   public String getType() {
      return "Generalization";
   }

   @Override
   public List<OntoumlElement> getContents() {
      return Collections.emptyList();
   }

   public Classifier<?, ?> getGeneral() {
      return general;
   }

   public void setGeneral(Classifier<?, ?> general) {
      this.general = general;
   }

   public Classifier<?, ?> getSpecific() {
      return specific;
   }

   public void setSpecific(Classifier<?, ?> specific) {
      this.specific = specific;
   }

   public boolean involvesClasses() {
      return specific instanceof Class && general instanceof Class;
   }

   public boolean involvesRelations() {
      return specific instanceof Relation && general instanceof Relation;
   }
}
