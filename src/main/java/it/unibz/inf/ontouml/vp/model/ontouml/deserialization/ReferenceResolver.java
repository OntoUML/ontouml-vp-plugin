package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ConnectorView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.DiagramElement;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ReferenceResolver {
  public static void resolveReferences(Project project) {
    Map<String, OntoumlElement> elementMap = project.getElementMap();

    for (Property property : project.getAllProperties()) {
      resolvePropertyType(elementMap, property);
      resolveSubsettedProperties(elementMap, property);
      resolveRedefinedProperties(elementMap, property);
    }

    for (Generalization generalization : project.getAllGeneralizations()) {
      resolveGeneral(elementMap, generalization);
      resolveSpecific(elementMap, generalization);
    }

    for (GeneralizationSet gs : project.getAllGeneralizationSets()) {
      resolveCategorizer(elementMap, gs);
      resolveGeneralizations(elementMap, gs);
    }

    for (DiagramElement diagramElement : project.getAllDiagramElements()) {
      resolveModelElement(elementMap, diagramElement);
    }

    for (ConnectorView connectorView : project.getAllConnectorViews()) {
      resolveSource(elementMap, connectorView);
      resolveTarget(elementMap, connectorView);
    }
  }

  private static void resolveSource(Map<String, OntoumlElement> elementMap, ConnectorView element) {
    DiagramElement reference = element.getSource();

    if (reference == null) return;

    DiagramElement source = resolve(elementMap, reference, DiagramElement.class);
    element.setSource(source);
  }

  private static void resolveTarget(Map<String, OntoumlElement> elementMap, ConnectorView element) {
    DiagramElement reference = element.getTarget();

    if (reference == null) return;

    DiagramElement source = resolve(elementMap, reference, DiagramElement.class);
    element.setTarget(source);
  }

  private static void resolveModelElement(
      Map<String, OntoumlElement> elementMap, DiagramElement element) {
    ModelElement reference = element.getModelElement();

    if (reference == null) return;

    ModelElement source = resolve(elementMap, reference, ModelElement.class);
    element.setModelElement(source);
  }

  private static void resolveGeneralizations(
      Map<String, OntoumlElement> elementMap, GeneralizationSet gs) {

    Set<Generalization> sources = new HashSet<>();

    for (Generalization reference : gs.getGeneralizations())
      sources.add(resolve(elementMap, reference, Generalization.class));

    gs.setGeneralizations(sources);
  }

  private static void resolveCategorizer(
      Map<String, OntoumlElement> elementMap, GeneralizationSet gs) {
    Optional<Class> reference = gs.getCategorizer();

    if (reference.isEmpty()) return;

    Class source = resolve(elementMap, reference.get(), Class.class);
    gs.setCategorizer(source);
  }

  private static void resolveGeneral(
      Map<String, OntoumlElement> elementMap, Generalization generalization) {
    Optional<Classifier<?, ?>> reference = generalization.getGeneral();

    if (reference.isEmpty()) return;

    Classifier<?, ?> source = resolve(elementMap, reference.get(), Classifier.class);
    generalization.setGeneral(source);
  }

  private static void resolveSpecific(
      Map<String, OntoumlElement> elementMap, Generalization generalization) {
    Optional<Classifier<?, ?>> reference = generalization.getSpecific();

    if (reference.isEmpty()) return;

    Classifier<?, ?> source = resolve(elementMap, reference.get(), Classifier.class);
    generalization.setSpecific(source);
  }

  private static void resolvePropertyType(
      Map<String, OntoumlElement> elementMap, Property property) {
    Optional<Classifier<?, ?>> reference = property.getPropertyType();

    if (reference.isEmpty()) return;

    Classifier<?, ?> source = resolve(elementMap, reference.get(), Classifier.class);
    property.setPropertyType(source);
  }

  private static void resolveSubsettedProperties(
      Map<String, OntoumlElement> elementMap, Property property) {
    for (Property reference : property.getSubsettedProperties()) {
      Property source = resolve(elementMap, reference, Property.class);
      property.replaceSubsettedProperty(reference, source);
    }
  }

  private static void resolveRedefinedProperties(
      Map<String, OntoumlElement> elementMap, Property property) {
    for (Property reference : property.getRedefinedProperties()) {
      Property source = resolve(elementMap, reference, Property.class);
      property.replaceRedefinedProperty(reference, source);
    }
  }

  private static <T extends OntoumlElement> T resolve(
      Map<String, OntoumlElement> elementMap, T reference, java.lang.Class<T> referenceType) {

    OntoumlElement source = elementMap.get(reference.getId());

    if (source == null)
      throw new NullPointerException("Referenced element in property type does not exist!");

    if (!referenceType.isInstance(source))
      throw new NullPointerException("Referenced element in property type is not a classifier!");

    return referenceType.cast(source);
  }
}
