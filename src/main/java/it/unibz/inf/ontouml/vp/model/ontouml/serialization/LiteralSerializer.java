package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Literal;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;

import java.io.IOException;

public class LiteralSerializer extends ModelElementSerializer<Literal> {

   @Override
   public void serialize(
           Literal literal, JsonGenerator jsonGen, SerializerProvider provider)
           throws IOException {

      super.serialize(literal, jsonGen, provider);
      jsonGen.writeEndObject();
   }
}
