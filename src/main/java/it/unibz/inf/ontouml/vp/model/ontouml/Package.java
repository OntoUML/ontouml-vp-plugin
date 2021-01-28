package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.PackageSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@JsonSerialize(using = PackageSerializer.class)
public class Package extends ModelElement implements ModelContainer {

  List<ModelElement> contents = new ArrayList<>();

  public Package(String id, MultilingualText name) {
    super(id, name);
  }

  public Package(String id, String name) {
    this(id, new MultilingualText(name));
  }

  public Package(String name) {
    this(null, name);
  }

  public Package() {
    this(null, (MultilingualText) null);
  }

  @Override
  public String getType() {
    return "Package";
  }

  @Override
  public List<OntoumlElement> getContents() {
    List<OntoumlElement> copiedContents = new ArrayList<>();

    if (contents == null) return copiedContents;

    OntoumlUtils.addIfNotNull(copiedContents, contents);
    return copiedContents;
  }

  public <T extends ModelElement> T addContent(T child) {
    child.setContainer(this);
    contents.add(child);
    return child;
  }

  public boolean hasContents() {
    return contents != null && contents.size() > 0;
  }

  public Package createPackage() {
    return createPackage(null, null);
  }

  public Package createPackage(String name) {
    return createPackage(null, name);
  }

  public Package createPackage(String id, String name) {
    return addContent(new Package(id, name));
  }

  public Class createClass() {
    return createClass(null, null, (String) null);
  }

  public Class createClass(String name) {
    return createClass(null, name, (String) null);
  }

  public Class createClass(String id, String name, ClassStereotype stereotype) {
    return addContent(new Class(id, name, stereotype));
  }

  public Class createClass(String id, String name, String customStereotype) {
    return addContent(new Class(id, name, customStereotype));
  }

  public Class createKind(String name) {
    return createKind(null, name);
  }

  public Class createKind(String id, String name) {
    return addContent(Class.createKind(id, name));
  }

  public Class createCollective(String name) {
    return createCollective(null, name);
  }

  public Class createCollective(String id, String name) {
    return addContent(Class.createCollective(id, name));
  }

  public Class createQuantity(String name) {
    return createQuantity(null, name);
  }

  public Class createQuantity(String id, String name) {
    return addContent(Class.createQuantity(id, name));
  }

  public Class createRelator(String name) {
    return createRelator(null, name);
  }

  public Class createRelator(String id, String name) {
    return addContent(Class.createRelator(id, name));
  }

  public Class createQuality(String name) {
    return createQuality(null, name);
  }

  public Class createQuality(String id, String name) {
    return addContent(Class.createQuality(id, name));
  }

  public Class createMode(String name) {
    return createMode(null, name);
  }

  public Class createMode(String id, String name) {
    return addContent(Class.createMode(id, name));
  }

  public Class createSubkind(String name) {
    return createSubkind(null, name);
  }

  public Class createSubkind(String id, String name) {
    return addContent(Class.createSubkind(id, name));
  }

  public Class createRole(String name) {
    return createRole(null, name);
  }

  public Class createRole(String id, String name) {
    return addContent(Class.createRole(id, name));
  }

  public Class createPhase(String name) {
    return createPhase(null, name);
  }

  public Class createPhase(String id, String name) {
    return addContent(Class.createPhase(id, name));
  }

  public Class createRoleMixin(String name) {
    return createRoleMixin(null, name);
  }

  public Class createRoleMixin(String id, String name) {
    return addContent(Class.createRoleMixin(id, name));
  }

  public Class createPhaseMixin(String name) {
    return createPhaseMixin(null, name);
  }

  public Class createPhaseMixin(String id, String name) {
    return addContent(Class.createPhaseMixin(id, name));
  }

  public Class createMixin(String name) {
    return createMixin(null, name);
  }

  public Class createMixin(String id, String name) {
    return addContent(Class.createMixin(id, name));
  }

  public Class createCategory(String name) {
    return createCategory(null, name);
  }

  public Class createCategory(String id, String name) {
    return addContent(Class.createCategory(id, name));
  }

  public Class createEvent(String name) {
    return createEvent(null, name);
  }

  public Class createEvent(String id, String name) {
    return addContent(Class.createEvent(id, name));
  }

  public Class createSituation(String name) {
    return createSituation(null, name);
  }

  public Class createSituation(String id, String name) {
    return addContent(Class.createSituation(id, name));
  }

  public Class createHistoricalRole(String name) {
    return createHistoricalRole(null, name);
  }

  public Class createHistoricalRole(String id, String name) {
    return addContent(Class.createHistoricalRole(id, name));
  }

  public Class createHistoricalRoleMixin(String name) {
    return createHistoricalRoleMixin(null, name);
  }

  public Class createHistoricalRoleMixin(String id, String name) {
    return addContent(Class.createHistoricalRoleMixin(id, name));
  }

  public Class createType(String name) {
    return createType(null, name);
  }

  public Class createType(String id, String name) {
    return addContent(Class.createType(id, name));
  }

  public Class createAbstract(String name) {
    return createAbstract(null, name);
  }

  public Class createAbstract(String id, String name) {
    return addContent(Class.createAbstract(id, name));
  }

  public Class createDatatype(String name) {
    return createDatatype(null, name);
  }

  public Class createDatatype(String id, String name) {
    return addContent(Class.createDatatype(id, name));
  }

  public Class createEnumeration(String[] literals) {
    return createEnumeration(null, null, literals);
  }

  public Class createEnumeration(String name, String[] literals) {
    return createEnumeration(null, name, literals);
  }

  public Class createEnumeration(String id, String name, String[] literals) {
    return addContent(Class.createEnumeration(id, name, literals));
  }

  public Relation createRelation(Classifier<?, ?> source, Classifier<?, ?> target) {
    return createRelation(null, null, null, source, target);
  }

  public Relation createRelation(String name, Classifier<?, ?> source, Classifier<?, ?> target) {
    return createRelation(null, name, null, source, target);
  }

  public Relation createRelation(
      String id,
      String name,
      RelationStereotype stereotype,
      Classifier<?, ?> source,
      Classifier<?, ?> target) {
    return addContent(new Relation(id, name, stereotype, source, target));
  }

  public <T extends Classifier<T, S>, S extends Stereotype> Generalization createGeneralization(
      Classifier<T, S> specific, Classifier<T, S> general) {
    return createGeneralization(null, specific, general);
  }

  public <T extends Classifier<T, S>, S extends Stereotype> Generalization createGeneralization(
      String id, Classifier<T, S> specific, Classifier<T, S> general) {
    return addContent(new Generalization(id, specific, general));
  }

  public GeneralizationSet createGeneralizationSet(Generalization... generalizations) {
    return createGeneralizationSet(null, null, null, Arrays.asList(generalizations));
  }

  public GeneralizationSet createGeneralizationSet(Collection<Generalization> generalizations) {
    return createGeneralizationSet(null, null, null, generalizations);
  }

  public GeneralizationSet createGeneralizationSet(
      String name, Collection<Generalization> generalizations) {
    return createGeneralizationSet(null, name, null, generalizations);
  }

  public GeneralizationSet createGeneralizationSet(
      String name, Class categorizer, Collection<Generalization> generalizations) {
    return createGeneralizationSet(null, name, categorizer, generalizations);
  }

  public GeneralizationSet createGeneralizationSet(
      String id, String name, Class categorizer, Collection<Generalization> generalizations) {
    return addContent(new GeneralizationSet(id, name, categorizer, generalizations));
  }
}
