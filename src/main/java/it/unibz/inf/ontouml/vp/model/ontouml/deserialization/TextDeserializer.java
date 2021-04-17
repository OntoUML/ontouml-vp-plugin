package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DeserializerUtils.deserializeNullableStringField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Text;
import java.io.IOException;

public class TextDeserializer extends JsonDeserializer<Text> {
  @Override
  public Text deserialize(JsonParser parser, DeserializationContext context) throws IOException {

    JsonNode root = parser.readValueAsTree();

    Text text = new Text();

    String value = deserializeNullableStringField(root, "value");
    text.setValue(value);

    RectangularShapeDeserializer.deserialize(text, root);

    return text;
  }
}
