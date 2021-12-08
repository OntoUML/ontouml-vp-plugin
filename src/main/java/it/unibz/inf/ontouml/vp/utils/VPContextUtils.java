package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.uml.Diagram;
import java.util.Set;

public class VPContextUtils {

  /** @return a set including selected model elements of the same type */
  public static Set<IModelElement> getModelElements(VPContext context) {
    if(isDiagramContext(context)) {
      return Diagram.getSelectedModelElements(context.getDiagram(), getModelType(context));
    }
    return Set.of(context.getModelElement());
  }

  public static boolean isDiagramContext(VPContext context) {
    return context.getDiagram() != null;
  }

  public static String getModelType(VPContext context) {
    IModelElement modelElement = context.getModelElement();
    return modelElement != null ? modelElement.getModelType() : "";
  }

}
