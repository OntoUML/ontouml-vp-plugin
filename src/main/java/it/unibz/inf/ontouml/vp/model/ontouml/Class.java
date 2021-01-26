package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Class extends Classifier<Class, ClassStereotype> {
  protected Boolean isExtensional;
  protected Boolean isPowertype;
  protected Integer order;
  protected Set<Nature> restrictedTo = new TreeSet<>();
  protected List<Literal> literals = new ArrayList<>();

  public Class(String id, MultilingualText name, ClassStereotype ontoumlStereotype) {
    super(id, name, ontoumlStereotype);
  }

  public Class(String id, MultilingualText name, String stereotypeName) {
    this(id, name, (ClassStereotype) null);
    setStereotype(stereotypeName);
  }

  public Class(String id, String name, ClassStereotype stereotype) {
    this(id, new MultilingualText(name), stereotype);
  }

  public Class(String id, String name, String stereotype) {
    this(id, new MultilingualText(name), stereotype);
  }

  public Class(String name, ClassStereotype stereotype) {
    this(null, name, stereotype);
  }

  public Class(String name, String stereotype) {
    this(null, name, stereotype);
  }

  public Class(ClassStereotype stereotype) {
    this(null, (MultilingualText) null, stereotype);
  }

  public Class(String name) {
    this(null, name, (ClassStereotype) null);
  }

  public Class() {
    this(null, (MultilingualText) null, (ClassStereotype) null);
  }

  @Override
  public String getType() {
    return "Class";
  }

  public boolean isExtensional() {
    return isExtensional;
  }

  public void setExtensional(boolean value) {
    isExtensional = value;
  }

  public boolean isPowertype() {
    return isPowertype;
  }

  public void setPowertype(boolean value) {
    isPowertype = value;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int value) {
    order = value;
  }

  public Set<Nature> getRestrictedTo() {
    return restrictedTo;
  }

  public void setRestrictedTo(Collection<Nature> restrictedTo) {
    this.restrictedTo.clear();
    this.restrictedTo.addAll(restrictedTo);
  }

  public List<Literal> getLiterals() {
    return literals;
  }

  public boolean hasLiterals() {
    return literals != null && literals.size() > 0;
  }

  public List<Literal> createLiterals(String[] names) {
    return Stream.of(names).map(name -> createLiteral(name)).collect(Collectors.toList());
  }

  public Literal createLiteral(String name) {
    if (!isEnumeration())
      throw new IllegalStateException("Cannot create a literal on a non-enumeration class.");

    if (literals == null) literals = new ArrayList<>();

    Literal literal = new Literal(this, name);
    literals.add(literal);
    return literal;
  }

  public void addLiteral(Literal literal) {
    if (!isEnumeration())
      throw new IllegalStateException("Cannot add a literal to a non-enumeration class.");

    if (literals == null) literals = new ArrayList<>();

    literal.setContainer(this);
    literals.add(literal);
  }

  public void setLiterals(Collection<Literal> literals) {
    if (literals == null) throw new NullPointerException("Cannot set a null literal list.");

    if (!isEnumeration())
      throw new IllegalStateException("Cannot set literals on a non-enumeration class.");

    literals.forEach(l -> l.setContainer(this));
    this.literals.clear();
    this.literals.addAll(literals);
  }

  public boolean hasAttributes() {
    return hasProperties();
  }

  public List<Property> getAttributes() {
    return getProperties();
  }

  public void addAttribute(Property p) {}

  public void setAttributes(Collection<Property> attributes) {}

  public Property createAttribute(String name, Classifier<?, ?> type) {
    return createAttribute(null, name, type);
  }

  public Property createAttribute(String id, String name, Classifier<?, ?> type) {
    Property attribute = new Property(id, name, type);
    attribute.setContainer(this);
    properties.add(attribute);

    return attribute;
  }

  @Override
  public void setStereotype(String stereotypeName) {
    Optional<ClassStereotype> stereotype = ClassStereotype.findByName(stereotypeName);

    stereotype.ifPresentOrElse(
        s -> setOntoumlStereotype(stereotype.get()), () -> setCustomStereotype(stereotypeName));
  }

  @Override
  public List<OntoumlElement> getContents() {
    List<OntoumlElement> contents = new ArrayList<>();

    if (hasAttributes()) OntoumlUtils.addIfNotNull(contents, getAttributes());

    if (hasLiterals()) OntoumlUtils.addIfNotNull(contents, getLiterals());

    return contents;
  }

  public boolean restrictedToOverlaps(List<Nature> natures) {
    if (natures == null) return false;

    TreeSet<Nature> natureSet = new TreeSet<>(natures);
    natureSet.retainAll(restrictedTo);
    return natureSet.size() > 0;
  }

  public boolean restrictedToContainedIn(List<Nature> natures) {
    if (natures == null) return false;

    return natures.containsAll(restrictedTo);
  }

  public boolean restrictedToContains(Nature nature) {
    return restrictedToContains(Collections.singletonList(nature));
  }

  public boolean restrictedToContains(List<Nature> natures) {
    if (natures == null) return false;

    return restrictedTo.containsAll(natures);
  }

  public boolean restrictedToEquals(Nature nature) {
    return restrictedToEquals(Collections.singletonList(nature));
  }

  public boolean restrictedToEquals(List<Nature> natures) {
    if (natures == null) return false;

    if (restrictedTo.size() != natures.size()) return false;

    TreeSet<Nature> naturesSet = new TreeSet<>(natures);
    return restrictedTo.equals(naturesSet);
  }

  public boolean isRestrictedToMoments() {
    return restrictedTo.stream().allMatch(Nature::isMoment);
  }

  public boolean isRestrictedToSubstantials() {
    return restrictedTo.stream().allMatch(Nature::isSubstantial);
  }

  public boolean isEnumeration() {
    return hasStereotype(ClassStereotype.ENUMERATION);
  }

  public boolean isRestrictedToEndurants() {
    return restrictedTo.stream().allMatch(Nature::isEndurant);
  }

  public boolean isRestrictedToFunctionalComplexes() {
    return restrictedToEquals(Nature.FUNCTIONAL_COMPLEX);
  }

  public boolean isRestrictedToCollectives() {
    return restrictedToEquals(Nature.COLLECTIVE);
  }

  public boolean isRestrictedToQuantity() {
    return restrictedToEquals(Nature.QUANTITY);
  }

  public boolean isRestrictedToIntrinsicMoments() {
    return restrictedTo.stream().allMatch(Nature::isIntrinsicMoment);
  }

  public boolean isRestrictedToExtrinsicMoments() {
    return restrictedTo.stream().allMatch(Nature::isExtrinsicMoment);
  }

  public boolean isRestrictedToRelators() {
    return restrictedToEquals(Nature.RELATOR);
  }

  public boolean isRestrictedToIntrinsicModes() {
    return restrictedToEquals(Nature.INTRINSIC_MODE);
  }

  public boolean isRestrictedToExtrinsicModes() {
    return restrictedToEquals(Nature.EXTRINSIC_MODE);
  }

  public boolean isRestrictedToQualities() {
    return restrictedToEquals(Nature.QUALITY);
  }

  public boolean isRestrictedToEvents() {
    return restrictedToEquals(Nature.EVENT);
  }

  public boolean isRestrictedToSituations() {
    return restrictedToEquals(Nature.SITUATION);
  }

  public boolean isRestrictedToTypes() {
    return restrictedToEquals(Nature.TYPE);
  }

  public boolean isRestrictedToAbstract() {
    return restrictedToEquals(Nature.ABSTRACT);
  }

  public static Class createEnumeration(String id, String name, String... literals) {
    Class enumeration = new Class(id, name, ClassStereotype.ENUMERATION);
    enumeration.createLiterals(literals);
    return enumeration;
  }
}
