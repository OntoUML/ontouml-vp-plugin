package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackageDeserializer extends JsonDeserializer<Package> {

  @Override
  public Package deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    Package pkg = new Package();
    ElementDeserializer.deserialize(pkg, root, codec);
    ModelElementDeserializer.deserialize(pkg, root, codec);
    deserializeContents(pkg, root, codec);

    return pkg;
  }

  private void deserializeContents(Package pkg, JsonNode root, ObjectCodec codec) {
    JsonNode contentsNode = root.get("contents");

    if (contentsNode != null && contentsNode.isArray()) {

      List<ModelElement> contents = new ArrayList<>();
      contentsNode
          .elements()
          .forEachRemaining(
              contentNode -> {
                if (!contentNode.isObject()) return;

                String type = contentNode.get("type").asText();
                java.lang.Class<? extends ModelElement> referenceType;

                switch (type) {
                  case "Package":
                    referenceType = Package.class;
                    break;
                  case "Class":
                    referenceType = Class.class;
                    break;
                  case "Relation":
                    referenceType = Relation.class;
                    break;
                  case "Generalization":
                    referenceType = Generalization.class;
                    break;
                  case "GeneralizationSet":
                    referenceType = GeneralizationSet.class;
                    break;
                  default:
                    return;
                }

                try {
                  ModelElement content = contentNode.traverse(codec).readValueAs(referenceType);
                  contents.add(content);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });

      pkg.setContents(contents);
    }
  }
}
