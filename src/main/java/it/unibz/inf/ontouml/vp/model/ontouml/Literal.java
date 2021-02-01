package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.LiteralDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.LiteralSerializer;
import java.util.Collections;
import java.util.List;

@JsonSerialize(using = LiteralSerializer.class)
@JsonDeserialize(using = LiteralDeserializer.class)
public class Literal extends ModelElement {

  public Literal(String id, MultilingualText name) {
    super(id, name);
  }

  public Literal(String name) {
    this(null, new MultilingualText(name));
  }

  public Literal() {
    this(null);
  }

  @Override
  public String getType() {
    return "Literal";
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }
}
