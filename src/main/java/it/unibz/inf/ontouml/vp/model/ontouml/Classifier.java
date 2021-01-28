package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class Classifier<T extends Classifier<T, S>, S extends Stereotype>
    extends Decoratable<S> {
  boolean isAbstract;
  boolean isDerived;
  List<Property> properties = new ArrayList<>();

  public Classifier(String id, MultilingualText name, S ontoumlStereotype) {
    super(id, name, ontoumlStereotype);
  }

  public Classifier(String id, MultilingualText name, String stereotypeName) {
    super(id, name, stereotypeName);
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract(boolean anAbstract) {
    isAbstract = anAbstract;
  }

  public boolean isDerived() {
    return isDerived;
  }

  public void setDerived(boolean derived) {
    isDerived = derived;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(Collection<Property> properties) {
    if (properties == null)
      throw new NullPointerException("Cannot set a null value to the properties list.");

    this.properties.addAll(properties);
  }

  public boolean hasProperties() {
    return properties.size() > 0;
  }

  public List<Generalization> getGeneralizations() {
    return null;
  }

  public List<GeneralizationSet> getGeneralizationSets() {
    return null;
  }

  public List<Generalization> getGeneralizationsWhereGeneral() {
    return null;
  }

  public List<Generalization> getGeneralizationsWhereSpecific() {
    return null;
  }

  public List<GeneralizationSet> getGeneralizationSetsWhereGeneral() {
    return null;
  }

  public List<GeneralizationSet> getGeneralizationSetsWhereSpecific() {
    return null;
  }

  public List<T> getParents() {
    return null;
  }

  public List<T> getChildren() {
    return null;
  }

  public List<T> getAncestors() {
    return null;
  }

  public List<T> getDescendants() {
    return null;
  }

  public List<T> getFilteredAncestors(Predicate<T> filter) {
    return null;
  }

  public List<T> getFilteredDescendants(Predicate<T> filter) {
    return null;
  }

  public static boolean areAbstract(Collection<? extends Classifier<?, ?>> classifiers) {
    return classifiers.stream().allMatch(c -> c.isAbstract());
  }

  public static boolean areDerived(Collection<? extends Classifier<?, ?>> classifiers) {
    return classifiers.stream().allMatch(c -> c.isDerived());
  }
}
