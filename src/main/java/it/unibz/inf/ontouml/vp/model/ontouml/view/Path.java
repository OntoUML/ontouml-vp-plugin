package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.PathDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.PathSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonSerialize(using = PathSerializer.class)
@JsonDeserialize(using = PathDeserializer.class)
public class Path extends Shape {
  List<Point> points = new ArrayList<>();

  public Path(String id) {
    super(id);
  }

  public Path() {
    this(null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return Collections.emptyList();
  }

  @Override
  public String getType() {
    return "Path";
  }

  public void moveTo(int x, int y) {
    points.add(new Point(x, y));
  }

  public List<Point> getPoints() {
    return points;
  }

  public void setPoints(List<Point> points) {
    this.points.clear();
    if (points != null) addPoints(points);
  }

  public void addPoints(List<Point> points) {
    if (points != null) points.forEach(p -> addPoint(p));
  }

  public void addPoint(Point point) {
    if (point != null) points.add(point);
  }

  @Override
  public String toString() {
    return points.toString();
  }
}
