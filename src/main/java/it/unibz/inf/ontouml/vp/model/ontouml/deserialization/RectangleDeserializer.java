package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class RectangleDeserializer extends JsonDeserializer<Rectangle> {
  @Override
  public Rectangle deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
    return null;
  }
}
