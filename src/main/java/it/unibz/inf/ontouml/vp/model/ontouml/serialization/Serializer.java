package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import java.io.IOException;
import java.util.Collection;

public class Serializer {
  public static void writeNullableStringField(String fieldName, String value, JsonGenerator jsonGen)
      throws IOException {

    if (value != null) jsonGen.writeStringField(fieldName, value);
    else jsonGen.writeNullField(fieldName);
  }

  public static void writeNullableBooleanField(
      String fieldName, Boolean value, JsonGenerator jsonGen) throws IOException {

    if (value != null) jsonGen.writeBooleanField(fieldName, value);
    else jsonGen.writeNullField(fieldName);
  }

  public static void writeNullableNumberField(String fieldName, Number value, JsonGenerator jsonGen)
      throws IOException {

    if (value == null) {
      jsonGen.writeNullField(fieldName);
      return;
    }

    if (value instanceof Integer) jsonGen.writeNumberField(fieldName, (Integer) value);
    else if (value instanceof Double) jsonGen.writeNumberField(fieldName, (Double) value);
    else if (value instanceof Long) jsonGen.writeNumberField(fieldName, (Long) value);
    else if (value instanceof Float) jsonGen.writeNumberField(fieldName, (Float) value);
  }

  public static void writeNullableArrayField(
      String fieldName, Collection<?> list, JsonGenerator jsonGen) throws IOException {

    if (list != null && !list.isEmpty()) jsonGen.writeObjectField(fieldName, list);
    else jsonGen.writeNullField(fieldName);
  }

  public static void writeNullableReferenceField(
      String fieldName, OntoumlElement element, JsonGenerator jsonGen) throws IOException {

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
