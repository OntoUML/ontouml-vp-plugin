package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Collections;
import java.util.List;

public class Diagram extends OntoumlElement {

   public Diagram(OntoumlElement container, String id, MultilingualText name) {
      super(container, id, name);
   }

   @Override
   public List<OntoumlElement> getContents() {
      return Collections.emptyList();
   }

   @Override
   public String getType() {
      return "Diagram";
   }

}
