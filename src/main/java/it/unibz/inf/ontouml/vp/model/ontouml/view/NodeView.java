package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import java.util.Arrays;
import java.util.List;

public abstract class NodeView<T extends ModelElement, S extends RectangularShape>
    extends DiagramElement<T, S> {

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

  public double getX() {
    return shape.getX();
  }

  public void setX(double x) {
    shape.setX(x);
  }

  public double getY() {
    return shape.getY();
  }

  public void setY(double y) {
    shape.setY(y);
  }

  public double getWidth() {
    return shape.width;
  }

  public void setWidth(double width) {
    shape.setWidth(width);
  }

  public double getHeight() {
    return shape.height;
  }

  public void setHeight(double height) {
    shape.setHeight(height);
  }
}
