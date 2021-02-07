package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.TextDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.TextSerializer;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = TextSerializer.class)
@JsonDeserialize(using = TextDeserializer.class)
public class Text extends RectangularShape {
  String value;

  public Text(String id, String value) {
    super(id);
    this.value = value;
  }

  public Text(String value) {
    this(null, value);
  }

  public Text() {
    this(null, null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return new ArrayList<>();
  }

  @Override
  public String getType() {
    return "Text";
  }
}
