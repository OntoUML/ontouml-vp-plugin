package it.unibz.inf.ontouml.vp.model.ontouml.view;

public abstract class RectangularShape extends Shape {
  Point topLeft = new Point(0.0, 0.0);
  double width = 20;
  double height = 10;

  public RectangularShape(String id, double width, double height) {
    super(id);
    this.width = width;
    this.height = height;
  }

  public RectangularShape(double width, double height) {
    this(null, width, height);
    this.width = width;
    this.height = height;
  }

  public RectangularShape(String id) {
    super(id);
  }

  public RectangularShape() {
    super(null);
  }

  public double getX() {
    return topLeft.getX();
  }

  public void setX(Double x) {
    topLeft.setX(x);
  }

  public double getY() {
    return topLeft.getY();
  }

  public void setY(Double y) {
    topLeft.setY(y);
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(Double width) {
    this.width = (width != null) ? width : 0;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(Double height) {
    this.height = (height != null) ? height : 0;
  }
}
