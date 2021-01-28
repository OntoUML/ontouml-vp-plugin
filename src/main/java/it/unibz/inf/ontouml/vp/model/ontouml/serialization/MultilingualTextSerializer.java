package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;

import java.io.IOException;

public class MultilingualTextSerializer extends JsonSerializer<MultilingualText> {

   @Override
   public void serialize(
           MultilingualText text, JsonGenerator jsonGen, SerializerProvider provider)
           throws IOException {

      if (text.isEmpty() || text.getText().isEmpty()) {
         jsonGen.writeNull();
         return;
      }

      if (text.size() == 1) {
         jsonGen.writeString(text.getText().get());
         return;
      }

      jsonGen.writeObject(text.getMap());
   }
}
