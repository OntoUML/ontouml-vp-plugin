package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import java.awt.*;

/** Element defined in the concrete syntax of the language */
public abstract class ViewElement extends OntoumlElement {

  public ViewElement(String id, MultilingualText name) {
    super(id, name);
  }

  public ViewElement(String id) {
    super(id, null);
  }
}
