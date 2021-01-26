package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ModelContainer {

   List<OntoumlElement> getContents();

   List<OntoumlElement> getContents(Predicate<OntoumlElement> filter);

   List<OntoumlElement> getAllContents();

   List<OntoumlElement> getAllContents(Predicate<OntoumlElement> filter);

   default <T> List<T> getAllContentsByType(java.lang.Class<T> type) {
      return getAllContents(type::isInstance)
              .stream()
              .map(type::cast)
              .collect(Collectors.toList());
   }

   default List<ModelElement> getAllModelElements() {
      return getAllContentsByType(ModelElement.class);
   }

   default List<Package> getAllPackages() {
      return getAllContentsByType(Package.class);
   }

   default List<Class> getAllClasses() {
      return getAllContentsByType(Class.class);
   }

   default List<Property> getAllAttributes() {
      return getAllContentsByType(Property.class)
              .stream()
              .filter(Property::isAttribute)
              .collect(Collectors.toList());
   }

   default List<Literal> getAllLiterals() {
      return getAllContentsByType(Literal.class);
   }

   default List<Relation> getAllRelations() {
      return getAllContentsByType(Relation.class);
   }

   default List<Property> getAllRelationEnds() {
      return getAllContentsByType(Property.class)
              .stream()
              .filter(Property::isRelationEnd)
              .collect(Collectors.toList());
   }

   default List<Generalization> getAllGeneralizations() {
      return getAllContentsByType(Generalization.class);
   }

   default List<GeneralizationSet> getAllGeneralizationSets() {
      return getAllContentsByType(GeneralizationSet.class);
   }

   default List<Class> getClassesByStereotype(ClassStereotype stereotype) {
      if (stereotype == null)
         throw new NullPointerException("Input stereotype cannot be null!");

      return getAllClasses()
              .stream()
              .filter(c -> c.getOntoumlStereotype().isPresent() && c.getOntoumlStereotype().get() == stereotype)
              .collect(Collectors.toList());
   }

   default List<Relation> getRelationsByStereotype(RelationStereotype stereotype) {
      if (stereotype == null)
         throw new NullPointerException("Input stereotype cannot be null!");

      return getAllRelations()
              .stream()
              .filter(c -> c.getOntoumlStereotype().isPresent() && c.getOntoumlStereotype().get() == stereotype)
              .collect(Collectors.toList());
   }

   default List<Property> getAttributesByStereotype(PropertyStereotype stereotype) {
      if (stereotype == null)
         throw new NullPointerException("Input stereotype cannot be null!");

      return getAllAttributes()
              .stream()
              .filter(c -> c.getOntoumlStereotype().isPresent() && c.getOntoumlStereotype().get() == stereotype)
              .collect(Collectors.toList());
   }

   default List<Class> getAllTypes() {
      return this.getClassesByStereotype(ClassStereotype.TYPE);
   }

   default List<Class> getAllHistoricalRoles() {
      return this.getClassesByStereotype(ClassStereotype.HISTORICAL_ROLE);
   }

   default List<Class> getAllHistoricalRoleMixins() {
      return this.getClassesByStereotype(ClassStereotype.HISTORICAL_ROLE_MIXIN);
   }

   default List<Class> getAllEvents() {
      return this.getClassesByStereotype(ClassStereotype.EVENT);
   }

   default List<Class> getAllSituations() {
      return this.getClassesByStereotype(ClassStereotype.SITUATION);
   }

   default List<Class> getAllCategories() {
      return this.getClassesByStereotype(ClassStereotype.CATEGORY);
   }

   default List<Class> getAllMixins() {
      return this.getClassesByStereotype(ClassStereotype.MIXIN);
   }

   default List<Class> getAllRoleMixins() {
      return this.getClassesByStereotype(ClassStereotype.ROLE_MIXIN);
   }

   default List<Class> getAllPhaseMixin() {
      return this.getClassesByStereotype(ClassStereotype.PHASE_MIXIN);
   }

   default List<Class> getAllKinds() {
      return this.getClassesByStereotype(ClassStereotype.KIND);
   }

   default List<Class> getAllCollectives() {
      return this.getClassesByStereotype(ClassStereotype.COLLECTIVE);
   }

   default List<Class> getAllQuantities() {
      return this.getClassesByStereotype(ClassStereotype.QUANTITY);
   }

   default List<Class> getAllRelators() {
      return this.getClassesByStereotype(ClassStereotype.RELATOR);
   }

   default List<Class> getAllQualities() {
      return this.getClassesByStereotype(ClassStereotype.QUALITY);
   }

   default List<Class> getAllModes() {
      return this.getClassesByStereotype(ClassStereotype.MODE);
   }

   default List<Class> getAllSubkinds() {
      return this.getClassesByStereotype(ClassStereotype.SUBKIND);
   }

   default List<Class> getAllRoles() {
      return this.getClassesByStereotype(ClassStereotype.ROLE);
   }

   default List<Class> getAllPhases() {
      return this.getClassesByStereotype(ClassStereotype.PHASE);
   }

   default List<Class> getAllEnumerations() {
      return this.getClassesByStereotype(ClassStereotype.ENUMERATION);
   }

   default List<Class> getAllDatatypes() {
      return this.getClassesByStereotype(ClassStereotype.DATATYPE);
   }

   default List<Class> getAllAbstracts() {
      return this.getClassesByStereotype(ClassStereotype.ABSTRACT);
   }

}
