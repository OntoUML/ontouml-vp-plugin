package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.ClassViewSerializer;

@JsonSerialize(using = ClassViewSerializer.class)
public class ClassView extends NodeView<Class, Rectangle> {
  public ClassView(String id, Class clazz) {
    super(id, clazz);
  }

  public ClassView(Class clazz) {
    this(null, clazz);
  }

  public ClassView() {
    this(null, null);
  }

  @Override
  public String getType() {
    return "ClassView";
  }

  @Override
  Rectangle createShape() {
    return new Rectangle();
  }
}
