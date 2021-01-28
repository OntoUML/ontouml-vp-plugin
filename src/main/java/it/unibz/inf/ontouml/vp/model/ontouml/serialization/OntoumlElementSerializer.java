package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;

import java.io.IOException;
import java.util.Collection;

public class OntoumlElementSerializer<T extends OntoumlElement> extends ElementSerializer<T> {

   @Override
   public void serialize(
           T element, JsonGenerator jsonGen, SerializerProvider provider)
           throws IOException {

      super.serialize(element, jsonGen, provider);
      jsonGen.writeStringField("type", element.getType());

   }

   public static void writeNullableStringField(String fieldName, String value, JsonGenerator jsonGen)
           throws IOException {

      if (value != null)
         jsonGen.writeStringField(fieldName, value);
      else
         jsonGen.writeNullField(fieldName);

   }

   public static void writeNullableBooleanField(String fieldName, Boolean value, JsonGenerator jsonGen)
           throws IOException {

      if (value != null)
         jsonGen.writeBooleanField(fieldName, value);
      else
         jsonGen.writeNullField(fieldName);

   }

   public static void writeNullableIntegerField(String fieldName, Integer value, JsonGenerator jsonGen)
           throws IOException {

      if (value != null)
         jsonGen.writeNumberField(fieldName, value);
      else
         jsonGen.writeNullField(fieldName);

   }

   public static void writeNullableArrayField(String fieldName, Collection<?> list, JsonGenerator jsonGen)
           throws IOException {

      if (list != null && !list.isEmpty())
         jsonGen.writeObjectField(fieldName, list);
      else
         jsonGen.writeNullField(fieldName);

   }

   public static void writeNullableReferenceField(String fieldName, OntoumlElement element, JsonGenerator jsonGen)
           throws IOException {

      if (element != null) {
         jsonGen.writeObjectFieldStart(fieldName);
         jsonGen.writeStringField("id", element.getId());
         jsonGen.writeStringField("type", element.getType());
         jsonGen.writeEndObject();
      } else {
         jsonGen.writeNullField(fieldName);
      }

   }

   public static void writeNullableReferenceArray(
           String fieldName, Collection<? extends OntoumlElement> elements, JsonGenerator jsonGen)
           throws IOException {

      if (elements != null && !elements.isEmpty()) {
         jsonGen.writeArrayFieldStart(fieldName);
         for (OntoumlElement element : elements) {
            jsonGen.writeStartObject();
            jsonGen.writeStringField("id", element.getId());
            jsonGen.writeStringField("type", element.getType());
            jsonGen.writeEndObject();
         }
         jsonGen.writeEndArray();
      } else {
         jsonGen.writeNullField(fieldName);
      }
   }
}
