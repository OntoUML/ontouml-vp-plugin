package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Property;
import it.unibz.inf.ontouml.vp.model.ontouml.PropertyStereotype;

import java.io.IOException;

public class PropertySerializer extends DecoratableSerializer<Property, PropertyStereotype> {
   @Override
   public void serialize(Property property, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
      super.serialize(property, jsonGen, provider);

      jsonGen.writeBooleanField("isDerived", property.isDerived());
      jsonGen.writeBooleanField("isReadOnly", property.isReadOnly());
      jsonGen.writeBooleanField("isOrdered", property.isOrdered());
      writeNullableStringField("cardinality", property.getCardinality().getValue().orElse(null), jsonGen);
      writeNullableReferenceField("propertyType", property.getPropertyType().orElse(null), jsonGen);
      writeNullableReferenceArray("subsettedProperties", property.getSubsettedProperties(), jsonGen);
      writeNullableReferenceArray("redefinedProperties", property.getRedefinedProperties(), jsonGen);

      if (property.getAggregationKind().isPresent())
         jsonGen.writeStringField("aggregationKind", property.getAggregationKind().get().getName());
      else
         jsonGen.writeNullField("aggregationKind");

      jsonGen.writeEndObject();
   }
}

