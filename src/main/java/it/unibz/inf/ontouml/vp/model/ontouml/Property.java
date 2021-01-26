package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Property extends Decoratable<PropertyStereotype> {
  private Cardinality cardinality = new Cardinality();
  private Classifier<?, ?> propertyType;
  private List<Property> subsettedProperties = new ArrayList<>();
  private List<Property> redefinedProperties = new ArrayList<>();
  private AggregationKind aggregationKind;
  private boolean isDerived;
  private boolean isOrdered;
  private boolean isReadOnly;

  public Property(
      String id,
      MultilingualText name,
      PropertyStereotype ontoumlStereotype,
      Classifier<?, ?> type) {
    super(id, name, ontoumlStereotype);
    setPropertyType(type);
  }

  public Property(String id, MultilingualText name, String stereotypeName, Classifier<?, ?> type) {
    super(id, name, stereotypeName);
    setPropertyType(type);
  }

  public Property(String id, MultilingualText name, Classifier<?, ?> type) {
    this(id, name, (PropertyStereotype) null, type);
  }

  public Property(String id, String name, Classifier<?, ?> type) {
    this(id, new MultilingualText(name), (PropertyStereotype) null, type);
  }

  public Property(String name, Classifier<?, ?> type) {
    this(null, name, type);
  }

  public Property(Classifier<?, ?> type) {
    this(null, null, (PropertyStereotype) null, type);

    if (type != null && type.getFirstName().isPresent()) {
      String propertyName = type.getFirstName().get().trim().toLowerCase();

      addName(propertyName);
    }
  }

  @Override
  public String getType() {
    return "Property";
  }

  @Override
  public void setStereotype(String stereotypeName) {
    Optional<PropertyStereotype> stereotype = PropertyStereotype.findByName(stereotypeName);

    stereotype.ifPresentOrElse(
        s -> setOntoumlStereotype(stereotype.get()), () -> setCustomStereotype(stereotypeName));
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }

  public Cardinality getCardinality() {
    return cardinality;
  }

  public void setCardinality(Cardinality cardinality) {
    this.cardinality = cardinality;
  }

  public void setPropertyType(Classifier<?, ?> propertyType) {
    this.propertyType = propertyType;
  }

  public List<Property> getSubsettedProperties() {
    return subsettedProperties;
  }

  public void setSubsettedProperties(List<Property> subsettedProperties) {
    this.subsettedProperties = subsettedProperties;
  }

  public List<Property> getRedefinedProperties() {
    return redefinedProperties;
  }

  public void setRedefinedProperties(List<Property> redefinedProperties) {
    this.redefinedProperties = redefinedProperties;
  }

  public AggregationKind getAggregationKind() {
    return aggregationKind;
  }

  public void setAggregationKind(AggregationKind aggregationKind) {
    this.aggregationKind = aggregationKind;
  }

  public boolean isDerived() {
    return isDerived;
  }

  public void setDerived(boolean derived) {
    isDerived = derived;
  }

  public boolean isOrdered() {
    return isOrdered;
  }

  public void setOrdered(boolean ordered) {
    isOrdered = ordered;
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }

  public void setReadOnly(boolean readOnly) {
    isReadOnly = readOnly;
  }

  public Optional<Classifier<?, ?>> getPropertyType() {
    return Optional.ofNullable(propertyType);
  }

  public boolean isPropertyTypeClass() {
    Optional<Classifier<?, ?>> type = getPropertyType();
    return type.isPresent() && type.get() instanceof Class;
  }

  public boolean isPropertyTypeRelation() {
    Optional<Classifier<?, ?>> type = getPropertyType();
    return type.isPresent() && type.get() instanceof Relation;
  }

  public Class getPropertyTypeAsClass() {
    if (!isPropertyTypeClass()) throw new IllegalCallerException("Property type is not a class.");

    return (Class) propertyType;
  }

  public Relation getPropertyTypeAsRelation() {
    if (!isPropertyTypeRelation())
      throw new IllegalCallerException("Property type is not a relation.");

    return (Relation) propertyType;
  }

  public boolean isAggregationEnd() {
    return aggregationKind == AggregationKind.COMPOSITE
        || aggregationKind == AggregationKind.SHARED;
  }

  public boolean isAttribute() {
    return getContainer().orElse(null) instanceof Class;
  }

  public boolean isRelationEnd() {
    return getContainer().orElse(null) instanceof Relation;
  }
}
