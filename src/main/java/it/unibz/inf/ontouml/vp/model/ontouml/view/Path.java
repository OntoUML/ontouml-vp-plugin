package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.PathSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonSerialize(using = PathSerializer.class)
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

  public void moveTo(double x, double y) {
    points.add(new Point(x, y));
  }

  public List<Point> getPoints() {
    return points;
  }

  @Override
  public String toString() {
    return points.toString();
  }
}
