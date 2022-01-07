package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ModelElement to handle IAssociation objects to be serialized as
 * ontouml-schema/Association
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class Association implements ModelElement {

  private final IAssociation sourceModelElement;

  @SerializedName("type")
  @Expose
  private final String type;

  @SerializedName("id")
  @Expose
  private final String id;

  @SerializedName("name")
  @Expose
  private String name;

  @SerializedName("description")
  @Expose
  private String description;

  @SerializedName("properties")
  @Expose
  private List<Property> properties;

  @SerializedName("propertyAssignments")
  @Expose
  private JsonObject propertyAssignments;

  @SerializedName("stereotypes")
  @Expose
  private List<String> stereotypes;

  @SerializedName("isAbstract")
  @Expose
  private boolean isAbstract;

  @SerializedName("isDerived")
  @Expose
  private boolean isDerived;

  public Association(IAssociation source) {
    this.sourceModelElement = source;

    this.type = ModelElement.TYPE_RELATION;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    addProperty(new Property((IAssociationEnd) source.getFromEnd()));
    addProperty(new Property((IAssociationEnd) source.getToEnd()));

    String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
    setAbstract(source.isAbstract());
    setDerived(source.isDerived());
  }

  public Association(IAssociation source, HashSet<String> modelElements) {
    this.sourceModelElement = source;

    this.type = ModelElement.TYPE_RELATION;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    if (modelElements.contains(source.getFromEnd().getId())) {
      addProperty(new Property((IAssociationEnd) source.getFromEnd()));
    }

    if (modelElements.contains(source.getToEnd().getId())) {
      addProperty(new Property((IAssociationEnd) source.getToEnd()));
    }

    String[] stereotypes = source.toStereotypeArray();
    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      addStereotype(stereotypes[i]);
    }

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
    setAbstract(source.isAbstract());
    setDerived(source.isDerived());
  }

  public static boolean holdsBetweenClasses(IAssociation association) {
    return association.getTo() instanceof IClass && association.getFrom() instanceof IClass;
  }

  @Override
  public String getId() {
    return getSourceModelElement().getId();
  }

  @Override
  public IAssociation getSourceModelElement() {
    return this.sourceModelElement;
  }

  @Override
  public String getOntoUMLType() {
    return this.type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = ModelElement.safeGetString(name);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = ModelElement.safeGetString(description);
  }

  public JsonObject getPropertyAssignments() {
    return propertyAssignments;
  }

  public void setPropertyAssignments(JsonObject propertyAssignments) {
    this.propertyAssignments = propertyAssignments;
  }

  public List<String> getStereotypes() {
    return this.stereotypes;
  }

  public void setStereotypes(List<String> stereotypes) {
    this.stereotypes = stereotypes;
  }

  public String getStereotype(int position) {
    return this.stereotypes.get(position);
  }

  public void addStereotype(String name) {
    if (this.stereotypes == null) {
      this.stereotypes = new ArrayList<String>();
    }

    this.stereotypes.add(name);
  }

  public void removeStereotype(String name) {

    if (this.stereotypes.contains(name)) {
      this.stereotypes.remove(name);
    }
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  public Property getProperty(int position) {
    return this.properties.get(position);
  }

  public void addProperty(Property property) {
    if (this.properties == null) {
      this.properties = new ArrayList<Property>();
    }

    this.properties.add(property);
  }

  public void removeProperty(Property property) {

    if (this.properties.contains(property)) {
      this.properties.remove(property);
    }
  }

  public boolean isAbstract() {
    return this.isAbstract;
  }

  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  public boolean isDerived() {
    return this.isDerived;
  }

  public void setDerived(boolean isDerived) {
    this.isDerived = isDerived;
  }

  public static IClass getSource(IAssociation association) {
    return (IClass) association.getFrom();
  }

  public static IClass getTarget(IAssociation association) {
    return (IClass) association.getTo();
  }

  public static void setSource(IAssociation association, IClass newSource) {
    association.setFrom(newSource);
  }

  public static void setTarget(IAssociation association, IClass newTarget) {
    association.setTo(newTarget);
  }

  public static IAssociationEnd getSourceEnd(IAssociation association) {
    return (IAssociationEnd) association.getFromEnd();
  }

  public static IAssociationEnd getTargetEnd(IAssociation association) {
    return (IAssociationEnd) association.getToEnd();
  }

  public static List<String> getSourceRestrictions(IAssociation association) {
    return Class.getRestrictedToList(getSource(association));
  }

  public static List<String> getTargetRestrictions(IAssociation association) {
    return Class.getRestrictedToList(getTarget(association));
  }

  public static void invertAssociation(
      IAssociation association, boolean keepAllAssociationEndPropertiesInPlace) {
    final IClass originalSource = getSource(association);
    final IClass originalTarget = getTarget(association);
    final IAssociationEnd originalSourceEnd = getSourceEnd(association);
    final IAssociationEnd originalTargetEnd = getTargetEnd(association);
    final PropertyDescription sourceEndDescription = new PropertyDescription(originalSourceEnd);
    final PropertyDescription targetEndDescription = new PropertyDescription(originalTargetEnd);
    final int readingDirection = association.getDirection();
    final List<AssociationModelDescription> originalAssociationsDescriptions =
        retrieveAndDeleteAssociationModels(association);

    setSource(association, originalTarget);
    setTarget(association, originalSource);

    if (keepAllAssociationEndPropertiesInPlace) {
      sourceEndDescription.copyTo(originalTargetEnd);
      targetEndDescription.copyTo(originalSourceEnd);
      association.setDirection(readingDirection == IAssociation.DIRECTION_FROM_TO ?
          IAssociation.DIRECTION_TO_FROM : IAssociation.DIRECTION_FROM_TO);
    } else {
      sourceEndDescription.partialCopyTo(originalTargetEnd);
      targetEndDescription.partialCopyTo(originalSourceEnd);
    }

    setNavigability(association);

    for (AssociationModelDescription originalAssociationsDescription :
        originalAssociationsDescriptions) {
      originalAssociationsDescription.recreateInvertedAssociationModel();
    }
  }

  private static List<AssociationModelDescription> retrieveAndDeleteAssociationModels(
      IAssociation association) {
    final IDiagramElement[] associationModels = association.getDiagramElements();
    final List<AssociationModelDescription> associationModelsDecriptions = new ArrayList<>();

    for (int i = 0; associationModels != null && i < associationModels.length; i++) {
      final IAssociationUIModel associationModel = (IAssociationUIModel) associationModels[i];
      final AssociationModelDescription associationModelDescription =
          new AssociationModelDescription(associationModel);

      associationModelsDecriptions.add(associationModelDescription);
      associationModelDescription.deleteAssociationModel();
    }

    return associationModelsDecriptions;
  }

  public static void setNavigability(IAssociation association) {
    if (!Association.isOntoumlAssociation(association)) {
      return;
    }

    final IAssociationEnd sourceEnd = getSourceEnd(association);
    final IAssociationEnd targetEnd = getTargetEnd(association);
    final String targetAgg = targetEnd.getAggregationKind().toLowerCase();

    sourceEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);

    if (IAssociationEnd.AGGREGATION_KIND_none.equals(targetAgg)) {
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAV_NAVIGABLE);
    } else {
      targetEnd.setNavigable(IAssociationEnd.NAVIGABLE_NAV_UNSPECIFIED);
    }
  }

  public static void setDefaultAggregationKind(IAssociation association, boolean forceOverride) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final IAssociationEnd sourceEnd = getSourceEnd(association);
    final IAssociationEnd targetEnd = getTargetEnd(association);
    String aggregationKind = IAssociationEnd.AGGREGATION_KIND_none;

    switch (stereotype) {
      case Stereotype.MEMBER_OF:
        aggregationKind = IAssociationEnd.AGGREGATION_KIND_shared;
        break;
      case Stereotype.COMPONENT_OF:
      case Stereotype.SUB_COLLECTION_OF:
      case Stereotype.SUB_QUANTITY_OF:
      case Stereotype.PARTICIPATIONAL:
        aggregationKind = IAssociationEnd.AGGREGATION_KIND_composite;
        break;
      default:
        if (hasOntoumlStereotype(association)) {
          // When there is a non-parthood stereotype we must remove any aggregation kind
          sourceEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
          targetEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
        }
        // We don't interfere where there is not stereotype
        return;
    }

    if (forceOverride) {
      sourceEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_none);
      targetEnd.setAggregationKind(aggregationKind);
    } else {
      // By not forcing override we keep user-defined aggregation (e.g., "shared")
      // The 16.3 release changes the aggregation kind's string and we must keep backwards
      // compatibility
      final String targetCurrentAggregation =
          targetEnd.getAggregationKind() != null
              ? targetEnd.getAggregationKind().toLowerCase()
              : "none";

      if (IAssociationEnd.AGGREGATION_KIND_none.equals(targetCurrentAggregation)) {
        targetEnd.setAggregationKind(aggregationKind);
      }
    }
  }

  public static void setDefaultMultiplicity(IAssociation association, boolean forceOverride) {
    final IClass source = getSource(association);
    final IClass target = getTarget(association);
    final IAssociationEnd sourceEnd = getSourceEnd(association);
    final IAssociationEnd targetEnd = getTargetEnd(association);
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String sourceStereotype = ModelElement.getUniqueStereotypeName(source);
    final String targetStereotype = ModelElement.getUniqueStereotypeName(target);
    String sourceMultiplicity = IAssociationEnd.MULTIPLICITY_UNSPECIFIED;
    String targetMultiplicity = IAssociationEnd.MULTIPLICITY_UNSPECIFIED;
    boolean sourceMustBeReadOnly = false;
    boolean targetMustBeReadOnly = false;

    switch (stereotype) {
      case Stereotype.CHARACTERIZATION:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: Characterized end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: Mode/Quality end
        targetMustBeReadOnly = true;
        break;

      case Stereotype.COMPARATIVE:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        break;

      case Stereotype.COMPONENT_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        break;

      case Stereotype.MATERIAL:
        if (Stereotype.ROLE.equals(targetStereotype)
            || Stereotype.ROLE_MIXIN.equals(targetStereotype)) {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        if (Stereotype.ROLE.equals(sourceStereotype)
            || Stereotype.ROLE_MIXIN.equals(sourceStereotype)) {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        break;

      case Stereotype.EXTERNAL_DEPENDENCE:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Source: Mode/Quality end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY; // Target: Dependee end
        targetMustBeReadOnly = true;
        break;

      case Stereotype.MEDIATION:
        if (Stereotype.ROLE.equals(targetStereotype)
            || Stereotype.ROLE_MIXIN.equals(targetStereotype)) {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.MEMBER_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        break;

      case Stereotype.SUB_COLLECTION_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        break;

      case Stereotype.SUB_QUANTITY_OF:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        sourceMustBeReadOnly = true;
        break;

      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: Endurant end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: Event end
        sourceMustBeReadOnly = true;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.HISTORICAL_DEPENDENCE:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Source: Depender end
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: Dependee end
        targetMustBeReadOnly = true;
        break;

      case Stereotype.MANIFESTATION:
        // Source: Mode/Quality/Relator end
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Target: Event end
        sourceMustBeReadOnly = true;
        break;

      case Stereotype.PARTICIPATION:
        // Source: Endurant end (participant)
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        // Target: Event end
        if (Stereotype.HISTORICAL_ROLE.equals(sourceStereotype)
            || Stereotype.HISTORICAL_ROLE_MIXIN.equals(sourceStereotype)) {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        } else {
          targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
        }
        sourceMustBeReadOnly = true;
        break;

      case Stereotype.PARTICIPATIONAL:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE;
        sourceMustBeReadOnly = true;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.INSTANTIATION:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY; // Source: lower order type
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY; // Target: higher order type
        break;

      case Stereotype.BRINGS_ABOUT:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: event
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Target: situation
        sourceMustBeReadOnly = true;
        targetMustBeReadOnly = true;
        break;

      case Stereotype.TRIGGERS:
        sourceMultiplicity = IAssociationEnd.MULTIPLICITY_ONE; // Source: situation
        targetMultiplicity = IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE; // Target: event
        sourceMustBeReadOnly = true;
        break;

      default:
        return;
    }

    if (forceOverride) {
      sourceEnd.setMultiplicity(sourceMultiplicity);
      targetEnd.setMultiplicity(targetMultiplicity);
    } else {
      if (IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(sourceEnd.getMultiplicity())) {
        sourceEnd.setMultiplicity(sourceMultiplicity);
      }
      if (IAssociationEnd.MULTIPLICITY_UNSPECIFIED.equals(targetEnd.getMultiplicity())) {
        targetEnd.setMultiplicity(targetMultiplicity);
      }
    }

    if (sourceMustBeReadOnly) {
      sourceEnd.setReadOnly(true);
    }

    if (targetMustBeReadOnly) {
      targetEnd.setReadOnly(true);
    }
  }

  public static String getDefaultSourceMultiplicity(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String targetStereotype =
        ModelElement.getUniqueStereotypeName(Association.getTarget(association));

    switch (stereotype != null ? stereotype : "") {
      case Stereotype.CHARACTERIZATION:
      case Stereotype.SUB_COLLECTION_OF:
      case Stereotype.SUB_QUANTITY_OF:
      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
      case Stereotype.BRINGS_ABOUT:
      case Stereotype.TRIGGERS:
        return IAssociationEnd.MULTIPLICITY_ONE;
      case Stereotype.COMPONENT_OF:
      case Stereotype.MEMBER_OF:
      case Stereotype.MANIFESTATION:
      case Stereotype.PARTICIPATION:
      case Stereotype.PARTICIPATIONAL:
        return IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
      case Stereotype.COMPARATIVE:
      case Stereotype.EXTERNAL_DEPENDENCE:
      case Stereotype.HISTORICAL_DEPENDENCE:
      case Stereotype.INSTANTIATION:
        return IAssociationEnd.MULTIPLICITY_MANY;
      case Stereotype.MATERIAL:
      case Stereotype.MEDIATION:
        return Stereotype.ROLE.equals(targetStereotype)
                || Stereotype.ROLE_MIXIN.equals(targetStereotype)
            ? IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            : IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
    }

    return IAssociationEnd.MULTIPLICITY_MANY;
  }

  public static String getDefaultTargetMultiplicity(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String sourceStereotype =
        ModelElement.getUniqueStereotypeName(Association.getSource(association));

    switch (stereotype != null ? stereotype : "") {
      case Stereotype.TRIGGERS:
        return IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE;
      case Stereotype.CHARACTERIZATION:
      case Stereotype.COMPONENT_OF:
      case Stereotype.MEDIATION:
      case Stereotype.SUB_COLLECTION_OF:
      case Stereotype.SUB_QUANTITY_OF:
      case Stereotype.CREATION:
      case Stereotype.TERMINATION:
      case Stereotype.HISTORICAL_DEPENDENCE:
      case Stereotype.PARTICIPATIONAL:
      case Stereotype.BRINGS_ABOUT:
        return IAssociationEnd.MULTIPLICITY_ONE;
      case Stereotype.EXTERNAL_DEPENDENCE:
      case Stereotype.MEMBER_OF:
      case Stereotype.INSTANTIATION:
        return IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
      case Stereotype.COMPARATIVE:
      case Stereotype.MANIFESTATION:
        return IAssociationEnd.MULTIPLICITY_MANY;
      case Stereotype.MATERIAL:
        return Stereotype.ROLE.equals(sourceStereotype)
                || Stereotype.ROLE_MIXIN.equals(sourceStereotype)
            ? IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            : IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
      case Stereotype.PARTICIPATION:
        return Stereotype.HISTORICAL_ROLE.equals(sourceStereotype)
                || Stereotype.HISTORICAL_ROLE_MIXIN.equals(sourceStereotype)
            ? IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            : IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
    }

    return IAssociationEnd.MULTIPLICITY_MANY;
  }

  public static boolean isOntoumlAssociation(IAssociation association) {
    boolean hasSource = getSource(association) != null;
    boolean hasTarget = getTarget(association) != null;
    boolean hasOntoumlStereotype = hasOntoumlStereotype(association);
    boolean hasOntoumlSource = hasSource && Class.isOntoumlClass(getSource(association));
    boolean hasOntoumlTarget = hasTarget && Class.isOntoumlClass(getTarget(association));

    return hasSource && hasTarget && (hasOntoumlStereotype || hasOntoumlSource || hasOntoumlTarget);
  }

  public static boolean hasOntoumlStereotype(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return Stereotype.getOntoUMLAssociationStereotypeNames().contains(stereotype);
  }

  public static boolean hasMereologyStereotype(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return Stereotype.getOntoUMLMereologyStereotypeNames().contains(stereotype);
  }

  public static boolean hasAggregationSetOnSource(IAssociation association) {
    var aggregationKind = getSourceEnd(association).getAggregationKind();

    // TODO: remove direct comparison to "Shared" once IAssociationEnd.AGGREGATION_KIND_shared is
    // fixed by VP
    return IAssociationEnd.AGGREGATION_KIND_shared.equals(aggregationKind)
        || IAssociationEnd.AGGREGATION_KIND_composite.equals(aggregationKind)
        || "Shared".equals(aggregationKind);
  }

  public static boolean hasAggregationSetOnTarget(IAssociation association) {
    var aggregationKind = getTargetEnd(association).getAggregationKind();

    // TODO: remove direct comparison to "Shared" once IAssociationEnd.AGGREGATION_KIND_shared is
    // fixed by VP
    return IAssociationEnd.AGGREGATION_KIND_shared.equals(aggregationKind)
        || IAssociationEnd.AGGREGATION_KIND_composite.equals(aggregationKind)
        || "Shared".equals(aggregationKind);
  }

  public static boolean isSourceAlwaysReadOnly(IAssociation association) {
    final Set<String> necessarySourceStereotypes =
        Set.of(
            Stereotype.SUB_QUANTITY_OF,
            Stereotype.CREATION,
            Stereotype.TERMINATION,
            Stereotype.MANIFESTATION,
            Stereotype.PARTICIPATION,
            Stereotype.PARTICIPATIONAL,
            Stereotype.BRINGS_ABOUT,
            Stereotype.TRIGGERS);
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return stereotype != null && necessarySourceStereotypes.contains(stereotype);
  }

  public static boolean isTargetAlwaysReadOnly(IAssociation association) {
    final Set<String> necessaryTargetStereotypes =
        Set.of(
            Stereotype.CHARACTERIZATION,
            Stereotype.EXTERNAL_DEPENDENCE,
            Stereotype.MEDIATION,
            Stereotype.CREATION,
            Stereotype.TERMINATION,
            Stereotype.HISTORICAL_DEPENDENCE,
            Stereotype.PARTICIPATIONAL,
            Stereotype.BRINGS_ABOUT);
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    return stereotype != null && necessaryTargetStereotypes.contains(stereotype);
  }

  public static String getDefaultAggregationKind(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final Set<String> sharedDefault = Set.of(Stereotype.MEMBER_OF);
    final Set<String> compositeDefault =
        Set.of(
            Stereotype.COMPONENT_OF,
            Stereotype.SUB_COLLECTION_OF,
            Stereotype.SUB_QUANTITY_OF,
            Stereotype.PARTICIPATIONAL);

    if (sharedDefault.contains(stereotype)) return IAssociationEnd.AGGREGATION_KIND_shared;
    if (compositeDefault.contains(stereotype)) return IAssociationEnd.AGGREGATION_KIND_composite;
    return IAssociationEnd.AGGREGATION_KIND_none;
  }
}
