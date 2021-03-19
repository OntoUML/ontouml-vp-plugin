package it.unibz.inf.ontouml.vp.model.ontouml.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.GeneralizationDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.GeneralizationSerializer;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JsonSerialize(using = GeneralizationSerializer.class)
@JsonDeserialize(using = GeneralizationDeserializer.class)
public class Generalization extends ModelElement {

  private Classifier<?, ?> general;
  private Classifier<?, ?> specific;

  public <T extends Classifier<T, S>, S extends Stereotype> Generalization(
      String id, MultilingualText name, Classifier<T, S> specific, Classifier<T, S> general) {
    super(id, name);
    setGeneral(general);
    setSpecific(specific);
  }

  public <T extends Classifier<T, S>, S extends Stereotype> Generalization(
      String id, String name, Classifier<T, S> specific, Classifier<T, S> general) {
    this(id, new MultilingualText(name), specific, general);
  }

  public <T extends Classifier<T, S>, S extends Stereotype> Generalization(
      String id, Classifier<T, S> specific, Classifier<T, S> general) {
    this(id, (MultilingualText) null, specific, general);
  }

  public <T extends Classifier<T, S>, S extends Stereotype> Generalization(
      Classifier<T, S> specific, Classifier<T, S> general) {
    this(null, (MultilingualText) null, specific, general);
  }

  public Generalization() {
    super(null, null);
  }

  @Override
  public String getType() {
    return "Generalization";
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }

  public Optional<Classifier<?, ?>> getGeneral() {
    return Optional.ofNullable(general);
  }

  public void setGeneral(Classifier<?, ?> general) {
    this.general = general;
  }

  public Optional<Classifier<?, ?>> getSpecific() {
    return Optional.ofNullable(specific);
  }

  public void setSpecific(Classifier<?, ?> specific) {
    this.specific = specific;
  }

  public boolean involvesClasses() {
    return specific instanceof Class && general instanceof Class;
  }

  public boolean involvesRelations() {
    return specific instanceof Relation && general instanceof Relation;
  }
}
