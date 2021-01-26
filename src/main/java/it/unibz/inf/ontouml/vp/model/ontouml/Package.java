package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    return createClass(null, null, null);
  }

  public Class createClass(String name) {
    return createClass(null, name, null);
  }

  public Class createClass(String id, String name, ClassStereotype stereotype) {
    return addContent(new Class(id, name, stereotype));
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
