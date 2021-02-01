package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Generalization;
import java.io.IOException;

public class GeneralizationSerializer extends ModelElementSerializer<Generalization> {

  @Override
  public void serialize(
      Generalization generalization, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {

    super.serialize(generalization, jsonGen, provider);

    writeNullableReferenceField("general", generalization.getGeneral().orElse(null), jsonGen);
    writeNullableReferenceField("specific", generalization.getSpecific().orElse(null), jsonGen);

    jsonGen.writeEndObject();
  }
}
