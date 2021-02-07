package it.unibz.inf.ontouml.vp.model.ontouml.view;

public class Point {
  double x;
  double y;

  public Point(Double x, Double y) {
    setX(x);
    setY(y);
  }

  public double getX() {
    return x;
  }

  public void setX(Double x) {
    this.x = (x != null) ? x : 0;
  }

  public double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = (y != null) ? y : 0;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ')';
  }
}
