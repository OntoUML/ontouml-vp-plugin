package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import it.unibz.inf.ontouml.vp.model.ontouml.view.ConnectorView;
import java.awt.*;

public class IConnectorUIModelLoader {

  public static Point[] loadPoints(ConnectorView fromView) {
    return fromView.getPath().getPoints().stream()
        .map(p -> new Point((int) p.getX(), (int) p.getY()))
        .toArray(Point[]::new);
  }
}
