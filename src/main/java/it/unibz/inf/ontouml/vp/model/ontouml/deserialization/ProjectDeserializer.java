package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ProjectDeserializer extends JsonDeserializer<Project> {

  @Override
  public Project deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Project project = new Project();

    ElementDeserializer.deserialize(project, root, codec);
    Package model = deserializeObject(root, "model", Package.class, codec);
    project.setModel(model);

    try {
      resolveReferences(project);
    } catch (Exception e) {
      throw new JsonParseException(parser, "Cannot deserialize project", e);
    }

    return project;
  }

  private void resolveReferences(Project project) throws Exception {
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
  }

  private void resolveGeneralizations(Map<String, OntoumlElement> elementMap, GeneralizationSet gs)
      throws Exception {

    Set<Generalization> sources = new HashSet<>();

    for (Generalization reference : gs.getGeneralizations())
      sources.add(getSource(elementMap, reference, Generalization.class));

    gs.setGeneralizations(sources);
  }

  private void resolveCategorizer(Map<String, OntoumlElement> elementMap, GeneralizationSet gs)
      throws Exception {
    Optional<Class> reference = gs.getCategorizer();

    if (reference.isEmpty()) return;

    Class source = getSource(elementMap, reference.get(), Class.class);
    gs.setCategorizer(source);
  }

  private void resolveGeneral(Map<String, OntoumlElement> elementMap, Generalization generalization)
      throws Exception {
    Optional<Classifier<?, ?>> reference = generalization.getGeneral();

    if (reference.isEmpty()) return;

    Classifier<?, ?> source = getSource(elementMap, reference.get(), Classifier.class);
    generalization.setGeneral(source);
  }

  private void resolveSpecific(
      Map<String, OntoumlElement> elementMap, Generalization generalization) throws Exception {
    Optional<Classifier<?, ?>> reference = generalization.getSpecific();

    if (reference.isEmpty()) return;

    Classifier<?, ?> source = getSource(elementMap, reference.get(), Classifier.class);
    generalization.setSpecific(source);
  }

  private void resolvePropertyType(Map<String, OntoumlElement> elementMap, Property property)
      throws Exception {
    Optional<Classifier<?, ?>> reference = property.getPropertyType();

    if (reference.isEmpty()) return;

    Classifier<?, ?> source = getSource(elementMap, reference.get(), Classifier.class);
    property.setPropertyType(source);
  }

  private void resolveSubsettedProperties(Map<String, OntoumlElement> elementMap, Property property)
      throws Exception {
    for (Property reference : property.getSubsettedProperties()) {
      Property source = getSource(elementMap, reference, Property.class);
      property.replaceSubsettedProperty(reference, source);
    }
  }

  private void resolveRedefinedProperties(Map<String, OntoumlElement> elementMap, Property property)
      throws Exception {
    for (Property reference : property.getRedefinedProperties()) {
      Property source = getSource(elementMap, reference, Property.class);
      property.replaceRedefinedProperty(reference, source);
    }
  }

  private <T extends OntoumlElement> T getSource(
      Map<String, OntoumlElement> elementMap, T reference, java.lang.Class<T> referenceType)
      throws Exception {

    OntoumlElement source = elementMap.get(reference.getId());

    if (source == null) throw new Exception("Referenced element in property type does not exist!");

    if (!referenceType.isInstance(source))
      throw new Exception("Referenced element in property type is not a classifier!");

    return referenceType.cast(source);
  }
}
