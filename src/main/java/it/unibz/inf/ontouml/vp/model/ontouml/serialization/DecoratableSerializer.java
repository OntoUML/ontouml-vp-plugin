package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Decoratable;
import it.unibz.inf.ontouml.vp.model.ontouml.Stereotype;

import java.io.IOException;

public class DecoratableSerializer<T extends Decoratable<S>, S extends Stereotype> extends ModelElementSerializer<T> {

   @Override
   public void serialize(T element, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
      super.serialize(element, jsonGen, provider);
      jsonGen.writeObjectField("stereotype", element.getStereotype().orElse(null));
   }
}
