package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.*;

import java.io.IOException;

public class RelationSerializer extends ClassifierSerializer<Relation, RelationStereotype> {

   @Override
   public void serialize(Relation clas, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
      super.serialize(clas, jsonGen, provider);
      jsonGen.writeEndObject();
   }
}
