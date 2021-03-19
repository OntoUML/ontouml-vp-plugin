package it.unibz.inf.ontouml.vp.model.ontouml.view;

public abstract class RectangularShape extends Shape {
  Point topLeft = new Point(0, 0);
  int width = 20;
  int height = 10;

  public RectangularShape(String id, int width, int height) {
    super(id);
    this.width = width;
    this.height = height;
  }

  public RectangularShape(int width, int height) {
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

  public int getX() {
    return topLeft.getX();
  }

  public void setX(Integer x) {
    topLeft.setX(x);
  }

  public int getY() {
    return topLeft.getY();
  }

  public void setY(Integer y) {
    topLeft.setY(y);
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = (width != null) ? width : 0;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = (height != null) ? height : 0;
  }
}
