package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.GeneralizationSet;

import java.io.IOException;

public class GeneralizationSetSerializer extends ModelElementSerializer<GeneralizationSet> {

   @Override
   public void serialize(
           GeneralizationSet gs, JsonGenerator jsonGen, SerializerProvider provider)
           throws IOException {

      super.serialize(gs, jsonGen, provider);

      jsonGen.writeBooleanField("isDisjoint", gs.isDisjoint());
      jsonGen.writeBooleanField("isComplete", gs.isComplete());
      writeNullableReferenceField("categorizer", gs.getCategorizer().orElse(null), jsonGen);
      writeNullableReferenceArray("generalizations", gs.getGeneralizations(), jsonGen);

      jsonGen.writeEndObject();
   }
}