package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import java.io.IOException;
import java.util.Map;

public class ModelElementDeserializer {

  public static void deserialize(ModelElement element, JsonNode root, ObjectCodec codec)
      throws IOException {

    JsonNode node = root.get("propertyAssignments");

    if (node != null && node.isObject()) {
      Map<String, Object> propertyMap =
          node.traverse(codec).readValueAs(new TypeReference<Map<String, Object>>() {});

      element.setPropertyAssignments(propertyMap);
    }
  }
}
