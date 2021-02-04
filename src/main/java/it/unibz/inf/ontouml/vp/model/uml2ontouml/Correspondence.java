package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;

public class Correspondence {
  Object source;
  OntoumlElement target;

  public Correspondence(Object source, OntoumlElement target) {
    this.source = source;
    this.target = target;
  }

  public Object getSource() {
    return source;
  }

  public OntoumlElement getTarget() {
    return target;
  }
}
