package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;

import java.io.IOException;

public class PackageSerializer extends ModelElementSerializer<Package> {

   @Override
   public void serialize(Package pkg, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
      super.serialize(pkg, jsonGen, provider);
      writeNullableArrayField("contents", pkg.getContents(), jsonGen);
      jsonGen.writeEndObject();
   }
}
