package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Property;
import java.io.IOException;

public class PropertySerializer extends JsonSerializer<Property> {
  @Override
  public void serialize(Property property, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(property, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Property property, JsonGenerator jsonGen) throws IOException {
    DecoratableSerializer.serializeFields(property, jsonGen);
    jsonGen.writeBooleanField("isDerived", property.isDerived());
    jsonGen.writeBooleanField("isReadOnly", property.isReadOnly());
    jsonGen.writeBooleanField("isOrdered", property.isOrdered());
    Serializer.writeNullableStringField(
        "cardinality", property.getCardinality().getValue().orElse(null), jsonGen);
    Serializer.writeNullableReferenceField(
        "propertyType", property.getPropertyType().orElse(null), jsonGen);
    Serializer.writeNullableReferenceArray(
        "subsettedProperties", property.getSubsettedProperties(), jsonGen);
    Serializer.writeNullableReferenceArray(
        "redefinedProperties", property.getRedefinedProperties(), jsonGen);

    if (property.getAggregationKind().isPresent())
      jsonGen.writeStringField("aggregationKind", property.getAggregationKind().get().getName());
    else jsonGen.writeNullField("aggregationKind");
  }
}
