package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ElementView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Shape;

public class IDiagramElementTransformer {

  public static <T extends ModelElement, S extends Shape> void transform(
          IDiagramElement source, ElementView<T, S> target, Class<T> type) {

    String id = source.getId();
    target.setId(id);

    T modelElement = getModelElement(source, type);
    target.setModelElement(modelElement);
  }

  private static <T extends ModelElement> T getModelElement(IDiagramElement view, Class<T> type) {
    IModelElement modelElement = view.getModelElement();
    ModelElement stub = ReferenceTransformer.transformStub(modelElement);

    return (type.isInstance(stub) ? type.cast(stub) : null);
  }
}
