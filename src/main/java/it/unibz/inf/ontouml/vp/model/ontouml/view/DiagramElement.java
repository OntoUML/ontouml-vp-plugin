package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import java.awt.*;

/** Element defined in the concrete syntax of the language */
public abstract class DiagramElement extends OntoumlElement {

  public DiagramElement(String id, MultilingualText name) {
    super(id, name);
  }

  public DiagramElement(String id) {
    super(id, null);
  }
}
