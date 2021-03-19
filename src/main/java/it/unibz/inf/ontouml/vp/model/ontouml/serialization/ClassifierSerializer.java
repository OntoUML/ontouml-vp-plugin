package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import java.io.IOException;

public class ClassifierSerializer extends JsonSerializer<Classifier<?, ?>> {

  @Override
  public void serialize(
      Classifier<?, ?> classifier, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(classifier, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(Classifier<?, ?> classifier, JsonGenerator jsonGen)
      throws IOException {
    DecoratableSerializer.serializeFields(classifier, jsonGen);
    jsonGen.writeBooleanField("isAbstract", classifier.isAbstract());
    jsonGen.writeBooleanField("isDerived", classifier.isDerived());
    Serializer.writeNullableArrayField("properties", classifier.getProperties(), jsonGen);
  }
}
