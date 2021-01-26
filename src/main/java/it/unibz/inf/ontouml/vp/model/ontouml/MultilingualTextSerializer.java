package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class MultilingualTextSerializer extends StdSerializer<MultilingualText> {

  public MultilingualTextSerializer() {
    super(MultilingualText.class);
  }

  protected MultilingualTextSerializer(java.lang.Class<MultilingualText> t) {
    super(t);
  }

  @Override
  public void serialize(MultilingualText text, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {

    if (text.isEmpty() || text.getText().isEmpty()) {
      jsonGen.writeNull();
      return;
    }

    if (text.size() == 1) {
      jsonGen.writeString(text.getText().get());
      return;
    }

    jsonGen.writeObject(text.getMap());
  }
}
