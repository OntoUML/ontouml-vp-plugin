package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Element;

public class IModelElementTransformer {

  public static void transform(IModelElement source, Element target) {
    String id = source.getId();
    target.setId(id);

    String name = source.getName();
    target.addName(name);

    String description = source.getDescription();
    target.addDescription(description.isEmpty() ? null : description);
  }
}
