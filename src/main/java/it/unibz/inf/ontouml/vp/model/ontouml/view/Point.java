package it.unibz.inf.ontouml.vp.model.ontouml.view;

public class Point {
  int x;
  int y;

  public Point(Integer x, Integer y) {
    setX(x);
    setY(y);
  }

  public int getX() {
    return x;
  }

  public void setX(Integer x) {
    this.x = (x != null) ? x : 0;
  }

  public int getY() {
    return y;
  }

  public void setY(Integer y) {
    this.y = (y != null) ? y : 0;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ')';
  }
}
