package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Collections;
import java.util.List;

public abstract class DiagramElement extends OntoumlElement {

  public DiagramElement(OntoumlElement container, String id, MultilingualText name) {
    super(container, id, name);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }
}
