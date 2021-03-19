package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import java.io.IOException;
import java.util.Map;

public class MultilingualTextDeserializer extends JsonDeserializer<MultilingualText> {

  @Override
  public MultilingualText getNullValue(DeserializationContext ctxt) throws JsonMappingException {
    return new MultilingualText();
  }

  @Override
  public MultilingualText deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode node = codec.readTree(parser);

    if (node.isTextual()) return new MultilingualText(node.asText());

    if (node.isObject()) {
      Map<String, String> textMap =
          node.traverse(codec).readValueAs(new TypeReference<Map<String, String>>() {});

      return new MultilingualText(textMap);
    }

    if (node.isNull()) return new MultilingualText();

    throw new JsonMappingException(
        parser, "Multilingual text mus be either a string, null, or an object.");
  }
}
