package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Literal;
import java.io.IOException;
import java.util.List;

public class ClassDeserializer extends JsonDeserializer<Class> {

  @Override
  public Class deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Class clazz = new Class();

    ElementDeserializer.deserialize(clazz, root, codec);
    ModelElementDeserializer.deserialize(clazz, root, codec);
    DecoratableDeserializer.deserialize(clazz, root, codec);
    ClassifierDeserializer.deserialize(clazz, root, codec);

    Boolean isExtensional = deserializeNullableBooleanField(root, "isExtensional");
    clazz.setExtensional(isExtensional);

    Boolean isPowertype = deserializeNullableBooleanField(root, "isPowertype");
    clazz.setPowertype(isPowertype);

    Integer order = deserializeNullableIntegerField(root, "order");
    clazz.setOrder(order);

    String[] restrictedTo = deserializeNullableStringArrayField(root, "restrictedTo", codec);
    if (restrictedTo != null) {
      clazz.setRestrictedTo(restrictedTo);
    }

    List<Literal> literals = deserializeObjectArray(root, "literals", Literal.class, codec);
    if (literals != null) {
      clazz.setLiterals(literals);
    }

    return clazz;
  }
}
