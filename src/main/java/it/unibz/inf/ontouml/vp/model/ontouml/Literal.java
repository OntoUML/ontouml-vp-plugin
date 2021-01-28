package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.LiteralSerializer;

import java.util.Collections;
import java.util.List;

@JsonSerialize(using = LiteralSerializer.class)
public class Literal extends ModelElement {

   public Literal(Class enumeration, String id, MultilingualText name) {
      super(enumeration, id, name);

      if (enumeration!=null && !enumeration.isEnumeration())
         throw new IllegalArgumentException("The container of a literal must be an Enumeration.");
   }

   public Literal(Class enumeration, String name) {
      this(enumeration, null, new MultilingualText(name));
   }

   @Override
   public String getType() {
      return "Literal";
   }

   @Override
   public List<OntoumlElement> getContents() {
      return Collections.emptyList();
   }

}
