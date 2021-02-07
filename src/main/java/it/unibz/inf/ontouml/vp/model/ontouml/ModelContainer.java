package it.unibz.inf.ontouml.vp.model.ontouml;

import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ModelContainer {

  List<OntoumlElement> getContents();

  List<OntoumlElement> getAllContents();

  default <T> List<T> getAllContentsByType(java.lang.Class<T> type) {
    return getAllContents().stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(Collectors.toList());
  }

  default List<ModelElement> getAllModelElements() {
    return getAllContentsByType(ModelElement.class);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Package> getAllPackages() {
    return getAllContentsByType(it.unibz.inf.ontouml.vp.model.ontouml.model.Package.class);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllClasses() {
    return getAllContentsByType(it.unibz.inf.ontouml.vp.model.ontouml.model.Class.class);
  }

  default List<Property> getAllProperties() {
    return getAllContentsByType(Property.class);
  }

  default List<Property> getAllAttributes() {
    return getAllProperties().stream().filter(Property::isAttribute).collect(Collectors.toList());
  }

  default List<Literal> getAllLiterals() {
    return getAllContentsByType(Literal.class);
  }

  default List<Relation> getAllRelations() {
    return getAllContentsByType(Relation.class);
  }

  default List<Property> getAllRelationEnds() {
    return getAllContentsByType(Property.class).stream()
        .filter(Property::isRelationEnd)
        .collect(Collectors.toList());
  }

  default List<Generalization> getAllGeneralizations() {
    return getAllContentsByType(Generalization.class);
  }

  default List<GeneralizationSet> getAllGeneralizationSets() {
    return getAllContentsByType(GeneralizationSet.class);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getClassesByStereotype(
      ClassStereotype stereotype) {
    if (stereotype == null) throw new NullPointerException("Input stereotype cannot be null!");

    return getAllClasses().stream()
        .filter(
            c ->
                c.getOntoumlStereotype().isPresent()
                    && c.getOntoumlStereotype().get() == stereotype)
        .collect(Collectors.toList());
  }

  default List<Relation> getRelationsByStereotype(RelationStereotype stereotype) {
    if (stereotype == null) throw new NullPointerException("Input stereotype cannot be null!");

    return getAllRelations().stream()
        .filter(
            c ->
                c.getOntoumlStereotype().isPresent()
                    && c.getOntoumlStereotype().get() == stereotype)
        .collect(Collectors.toList());
  }

  default List<Property> getAttributesByStereotype(PropertyStereotype stereotype) {
    if (stereotype == null) throw new NullPointerException("Input stereotype cannot be null!");

    return getAllAttributes().stream()
        .filter(
            c ->
                c.getOntoumlStereotype().isPresent()
                    && c.getOntoumlStereotype().get() == stereotype)
        .collect(Collectors.toList());
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllTypes() {
    return this.getClassesByStereotype(ClassStereotype.TYPE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllHistoricalRoles() {
    return this.getClassesByStereotype(ClassStereotype.HISTORICAL_ROLE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllHistoricalRoleMixins() {
    return this.getClassesByStereotype(ClassStereotype.HISTORICAL_ROLE_MIXIN);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllEvents() {
    return this.getClassesByStereotype(ClassStereotype.EVENT);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllSituations() {
    return this.getClassesByStereotype(ClassStereotype.SITUATION);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllCategories() {
    return this.getClassesByStereotype(ClassStereotype.CATEGORY);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllMixins() {
    return this.getClassesByStereotype(ClassStereotype.MIXIN);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllRoleMixins() {
    return this.getClassesByStereotype(ClassStereotype.ROLE_MIXIN);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllPhaseMixin() {
    return this.getClassesByStereotype(ClassStereotype.PHASE_MIXIN);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllKinds() {
    return this.getClassesByStereotype(ClassStereotype.KIND);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllCollectives() {
    return this.getClassesByStereotype(ClassStereotype.COLLECTIVE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllQuantities() {
    return this.getClassesByStereotype(ClassStereotype.QUANTITY);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllRelators() {
    return this.getClassesByStereotype(ClassStereotype.RELATOR);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllQualities() {
    return this.getClassesByStereotype(ClassStereotype.QUALITY);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllModes() {
    return this.getClassesByStereotype(ClassStereotype.MODE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllSubkinds() {
    return this.getClassesByStereotype(ClassStereotype.SUBKIND);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllRoles() {
    return this.getClassesByStereotype(ClassStereotype.ROLE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllPhases() {
    return this.getClassesByStereotype(ClassStereotype.PHASE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllEnumerations() {
    return this.getClassesByStereotype(ClassStereotype.ENUMERATION);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllDatatypes() {
    return this.getClassesByStereotype(ClassStereotype.DATATYPE);
  }

  default List<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getAllAbstracts() {
    return this.getClassesByStereotype(ClassStereotype.ABSTRACT);
  }

  default <T extends OntoumlElement> Optional<T> getElementById(
      String id, java.lang.Class<T> type) {

    List<OntoumlElement> elements =
        getAllContents().stream()
            .filter(e -> type.isInstance(e) && id.equals(e.getId()))
            .collect(Collectors.toList());

    if (elements.size() == 1) return Optional.of(type.cast(elements.get(0)));

    if (elements.size() == 0) return Optional.empty();

    throw new IllegalStateException(
        "There is more than one instance of " + type.getName() + " with the same id!");
  }

  default Optional<it.unibz.inf.ontouml.vp.model.ontouml.model.Class> getClassById(String id) {
    return getElementById(id, it.unibz.inf.ontouml.vp.model.ontouml.model.Class.class);
  }

  default Optional<Relation> getRelationById(String id) {
    return getElementById(id, Relation.class);
  }

  default Optional<Generalization> getGeneralizationById(String id) {
    return getElementById(id, Generalization.class);
  }

  default Optional<GeneralizationSet> getGeneralizationSetById(String id) {
    return getElementById(id, GeneralizationSet.class);
  }

  default Optional<Property> getPropertyById(String id) {
    return getElementById(id, Property.class);
  }

  default Optional<it.unibz.inf.ontouml.vp.model.ontouml.model.Package> getPackageById(String id) {
    return getElementById(id, Package.class);
  }

  default Map<String, OntoumlElement> getElementMap() throws IllegalStateException {
    Map<String, OntoumlElement> map = new HashMap<>();

    for (OntoumlElement element : getAllContents()) {
      String key = element.getId();
      if (map.containsKey(key)) {
        throw new IllegalStateException("Duplicate ids!");
      }
      map.put(key, element);
    }

    return map;
  }
}
