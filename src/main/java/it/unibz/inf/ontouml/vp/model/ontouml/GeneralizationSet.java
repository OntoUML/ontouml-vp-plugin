package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.GeneralizationSetDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.GeneralizationSetSerializer;
import java.util.*;

@JsonSerialize(using = GeneralizationSetSerializer.class)
@JsonDeserialize(using = GeneralizationSetDeserializer.class)
public class GeneralizationSet extends ModelElement {

  private boolean isDisjoint;
  private boolean isComplete;
  private Class categorizer;
  private Set<Generalization> generalizations = new HashSet<>();

  public GeneralizationSet(
      String id,
      MultilingualText name,
      Class categorizer,
      Collection<Generalization> generalizations) {
    super(id, name);
    OntoumlUtils.addIfNotNull(this.generalizations, generalizations);
    this.categorizer = categorizer;
  }

  public GeneralizationSet(
      String id, String name, Class categorizer, Collection<Generalization> generalizations) {
    this(id, new MultilingualText(name), categorizer, generalizations);
  }

  public GeneralizationSet(String id, String name, Collection<Generalization> generalizations) {
    this(id, new MultilingualText(name), null, generalizations);
  }

  public GeneralizationSet(String id, String name, Generalization... generalizations) {
    this(id, new MultilingualText(name), null, Arrays.asList(generalizations));
  }

  public GeneralizationSet(
      MultilingualText name, Class categorizer, Collection<Generalization> generalizations) {
    this(null, name, categorizer, generalizations);
  }

  public GeneralizationSet(Class categorizer, Collection<Generalization> generalizations) {
    this(null, (MultilingualText) null, categorizer, generalizations);
  }

  public GeneralizationSet(String name, Collection<Generalization> generalizations) {
    this(null, new MultilingualText(name), null, generalizations);
  }

  public GeneralizationSet(Collection<Generalization> generalizations) {
    this(null, (MultilingualText) null, null, generalizations);
  }

  public GeneralizationSet(String name, Generalization... generalizations) {
    this(null, new MultilingualText(name), null, Arrays.asList(generalizations));
  }

  public GeneralizationSet(Generalization... generalizations) {
    this(null, generalizations);
  }

  public boolean isDisjoint() {
    return isDisjoint;
  }

  public void setDisjoint(boolean disjoint) {
    isDisjoint = disjoint;
  }

  public boolean isComplete() {
    return isComplete;
  }

  public void setComplete(boolean complete) {
    isComplete = complete;
  }

  public Optional<Class> getCategorizer() {
    return Optional.ofNullable(categorizer);
  }

  public void setCategorizer(Class categorizer) {
    this.categorizer = categorizer;
  }

  public Set<Generalization> getGeneralizations() {
    return new HashSet<>(generalizations);
  }

  public void setGeneralizations(Collection<Generalization> generalizations) {
    this.generalizations.clear();
    OntoumlUtils.addIfNotNull(this.generalizations, generalizations);
  }

  public void addGeneralization(Generalization generalization) {
    if (generalization == null)
      throw new NullPointerException("Cannot add a null generalization to the generalization set.");

    this.generalizations.add(generalization);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }

  @Override
  public String getType() {
    return "GeneralizationSet";
  }
}
