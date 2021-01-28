package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.ClassSerializer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonSerialize(using = ClassSerializer.class)
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

  public Optional<Boolean> isExtensional() {
    return Optional.ofNullable(isExtensional);
  }

  public void setExtensional(Boolean value) {
    isExtensional = value;
  }

  public Optional<Boolean> isPowertype() {
    return Optional.ofNullable(isPowertype);
  }

  public void setPowertype(Boolean value) {
    isPowertype = value;
  }

  public Optional<Integer> getOrder() {
    return Optional.ofNullable(order);
  }

  public void setOrder(Integer value) {
    order = value;
  }

  public Set<Nature> getRestrictedTo() {
    return restrictedTo;
  }

  public void setRestrictedTo(Collection<Nature> restrictedTo) {
    this.restrictedTo.clear();
    this.restrictedTo.addAll(restrictedTo);
  }

  public void setRestrictedTo(Nature... restrictedTo) {
    this.restrictedTo.clear();
    this.restrictedTo.addAll(Arrays.asList(restrictedTo));
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

  public static Class createKind(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.KIND);
    clas.setRestrictedTo(Nature.FUNCTIONAL_COMPLEX);
    return clas;
  }

  public static Class createCollective(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.COLLECTIVE);
    clas.setRestrictedTo(Nature.COLLECTIVE);
    return clas;
  }

  public static Class createQuantity(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.QUANTITY);
    clas.setRestrictedTo(Nature.QUANTITY);
    return clas;
  }

  public static Class createRelator(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.RELATOR);
    clas.setRestrictedTo(Nature.RELATOR);
    return clas;
  }

  public static Class createMode(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.MODE);
    clas.setRestrictedTo(Nature.INTRINSIC_MODE, Nature.EXTRINSIC_MODE);
    return clas;
  }

  public static Class createQuality(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.QUALITY);
    clas.setRestrictedTo(Nature.QUALITY);
    return clas;
  }

  public static Class createSubkind(String id, String name) {
    return new Class(id, name, ClassStereotype.SUBKIND);
  }

  public static Class createRole(String id, String name) {
    return new Class(id, name, ClassStereotype.ROLE);
  }

  public static Class createPhase(String id, String name) {
    return new Class(id, name, ClassStereotype.PHASE);
  }

  public static Class createHistoricalRole(String id, String name) {
    return new Class(id, name, ClassStereotype.HISTORICAL_ROLE);
  }

  public static Class createMixin(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.MIXIN);
    clas.setAbstract(true);
    clas.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clas;
  }

  public static Class createCategory(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.CATEGORY);
    clas.setAbstract(true);
    clas.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clas;
  }

  public static Class createRoleMixin(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.ROLE_MIXIN);
    clas.setAbstract(true);
    clas.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clas;
  }

  public static Class createPhaseMixin(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.PHASE_MIXIN);
    clas.setAbstract(true);
    clas.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clas;
  }

  public static Class createHistoricalRoleMixin(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.HISTORICAL_ROLE_MIXIN);
    clas.setAbstract(true);
    clas.setRestrictedTo(Nature.SUBSTANTIAL_NATURES);
    return clas;
  }

  public static Class createEvent(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.EVENT);
    clas.setRestrictedTo(Nature.EVENT);
    return clas;
  }

  public static Class createSituation(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.SITUATION);
    clas.setRestrictedTo(Nature.TYPE);
    return clas;
  }

  public static Class createType(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.TYPE);
    clas.setRestrictedTo(Nature.FUNCTIONAL_COMPLEX);
    return clas;
  }

  public static Class createAbstract(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.ABSTRACT);
    clas.setRestrictedTo(Nature.ABSTRACT);
    return clas;
  }

  public static Class createDatatype(String id, String name) {
    Class clas = new Class(id, name, ClassStereotype.DATATYPE);
    clas.setRestrictedTo(Nature.ABSTRACT);
    return clas;
  }

  public static Class createEnumeration(String id, String name, String... literals) {
    Class enumeration = new Class(id, name, ClassStereotype.ENUMERATION);
    enumeration.setRestrictedTo(Nature.ABSTRACT);
    enumeration.createLiterals(literals);
    return enumeration;
  }
}
