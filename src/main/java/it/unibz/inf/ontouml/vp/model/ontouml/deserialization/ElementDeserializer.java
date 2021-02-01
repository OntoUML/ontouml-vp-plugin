package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.Element;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import java.io.IOException;

public class ElementDeserializer {

  public static void deserialize(Element element, JsonNode root, ObjectCodec codec)
      throws IOException {

    String id = root.get("id").asText();
    element.setId(id);

    JsonNode nameNode = root.get("name");
    if (nameNode != null) {
      MultilingualText name = nameNode.traverse(codec).readValueAs(MultilingualText.class);
      element.setName(name);
    }

    JsonNode descNode = root.get("description");
    if (descNode != null) {
      MultilingualText desc = descNode.traverse(codec).readValueAs(MultilingualText.class);
      element.setDescription(desc);
    }
  }
}
