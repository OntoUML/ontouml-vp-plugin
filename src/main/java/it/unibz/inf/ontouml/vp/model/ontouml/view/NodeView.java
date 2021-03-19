package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import java.util.Arrays;
import java.util.List;

public abstract class NodeView<T extends ModelElement, S extends RectangularShape>
    extends ElementView<T, S> {

  public NodeView(String id, T element) {
    super(id, element);
  }

  public NodeView(T element) {
    this(null, element);
  }

  public NodeView() {
    this(null, null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Arrays.asList(shape);
  }

  public int getX() {
    return shape.getX();
  }

  public void setX(int x) {
    shape.setX(x);
  }

  public int getY() {
    return shape.getY();
  }

  public void setY(int y) {
    shape.setY(y);
  }

  public int getWidth() {
    return shape.width;
  }

  public void setWidth(int width) {
    shape.setWidth(width);
  }

  public int getHeight() {
    return shape.height;
  }

  public void setHeight(int height) {
    shape.setHeight(height);
  }
}
