package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Rectangle;
import java.io.IOException;

public class ClassViewDeserializer extends JsonDeserializer<ClassView> {

  @Override
  public ClassView deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    ObjectCodec codec = parser.getCodec();
    JsonNode root = parser.readValueAsTree();

    ClassView classView = new ClassView();

    DiagramElementDeserializer.deserialize(classView, root, codec);
    DeserializerUtils.deserializeObject(root, "modelElement", Class.class, codec);
    DeserializerUtils.deserializeObject(root, "shape", Rectangle.class, codec);

    return classView;
  }
}
