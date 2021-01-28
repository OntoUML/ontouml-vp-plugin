package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.GeneralizationSerializer;
import java.util.Collections;
import java.util.List;

@JsonSerialize(using = GeneralizationSerializer.class)
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

  @Override
  public String getType() {
    return "Generalization";
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }

  public Classifier<?, ?> getGeneral() {
    return general;
  }

  public void setGeneral(Classifier<?, ?> general) {
    if (general == null) throw new NullPointerException("Cannot set general to null!");

    this.general = general;
  }

  public Classifier<?, ?> getSpecific() {
    return specific;
  }

  public void setSpecific(Classifier<?, ?> specific) {
    if (specific == null) throw new NullPointerException("Cannot set specific to null!");

    this.specific = specific;
  }

  public boolean involvesClasses() {
    return specific instanceof Class && general instanceof Class;
  }

  public boolean involvesRelations() {
    return specific instanceof Relation && general instanceof Relation;
  }
}
