package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.Stereotype;
import java.io.IOException;

public class ClassifierSerializer<T extends Classifier<T, S>, S extends Stereotype>
    extends DecoratableSerializer<T, S> {
  @Override
  public void serialize(T classifier, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    super.serialize(classifier, jsonGen, provider);

    jsonGen.writeBooleanField("isAbstract", classifier.isAbstract());
    jsonGen.writeBooleanField("isDerived", classifier.isDerived());
    writeNullableArrayField("properties", classifier.getProperties(), jsonGen);
  }
}
