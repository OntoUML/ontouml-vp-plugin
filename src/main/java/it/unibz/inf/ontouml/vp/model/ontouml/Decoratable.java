package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Optional;

public abstract class Decoratable<S extends Stereotype> extends ModelElement {
   private String customStereotype;
   private S ontoumlStereotype;

   public Decoratable(String id, MultilingualText name, S ontoumlStereotype) {
      super(id, name);
      setOntoumlStereotype(ontoumlStereotype);
   }

   public Decoratable(String id, MultilingualText name, String stereotypeName) {
      super(id, name);
      setStereotype(stereotypeName);
   }

   public Optional<S> getOntoumlStereotype() {
      return Optional.ofNullable(ontoumlStereotype);
   }

   public void setOntoumlStereotype(S ontoumlStereotype) {
      this.ontoumlStereotype = ontoumlStereotype;
      this.customStereotype = null;
   }

   public boolean hasOntoumlStereotype() {
      return getOntoumlStereotype().isPresent();
   }

   public Optional<String> getCustomStereotype() {
      return Optional.ofNullable(customStereotype);
   }

   public void setCustomStereotype(String customStereotype) {
      this.ontoumlStereotype = null;
      this.customStereotype = customStereotype;
   }

   public boolean hasCustomStereotype() {
      return getCustomStereotype().isPresent();
   }

   public Optional<String> getStereotype() {
      String stereotype = hasOntoumlStereotype() ? ontoumlStereotype.getStereotypeName() : customStereotype;
      return Optional.ofNullable(stereotype);
   }

   /**
    * If the supplied stereotype string matches the name of an OntoUML stereotype, this method will set it. Otherwise, it will set a custom stereotype.
    */
   public abstract void setStereotype(String stereotypeName);

   public void removeStereotype() {
      ontoumlStereotype = null;
      customStereotype = null;
   }

   public boolean hasStereotype() {
      return hasOntoumlStereotype() || hasCustomStereotype();
   }

   public boolean hasStereotype(S stereotype) {
      return hasOntoumlStereotype() && this.ontoumlStereotype.equals(stereotype);
   }

   public boolean hasStereotype(String stereotypeName) {
      Optional<String> stereotype = getStereotype();
      return stereotype.isPresent() && stereotype.get().equals(stereotypeName);
   }

}
