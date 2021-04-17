package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IShapeUIModel;
import it.unibz.inf.ontouml.vp.model.ontouml.view.NodeView;

public class IShapeTransformer {

  public static void transform(IShapeUIModel source, NodeView<?, ?> target) {
    target.setX(source.getX());
    target.setY(source.getY());

    target.setWidth(source.getWidth());
    target.setHeight(source.getHeight());

    target.getShape().setId(source.getId() + "_shape");
  }
}
