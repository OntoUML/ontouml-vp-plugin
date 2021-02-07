package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;

/**
 * A graphical representation of a {@link ModelElement} within a {@link Diagram} .
 *
 * <p>Each instance of a DiagramElement exists in one and only one Diagram.
 *
 * <p>Diagrams only have instances of DiagramElement as direct content (children).
 */
public abstract class DiagramElement<T extends ModelElement, S extends Shape> extends ViewElement {

  T modelElement;
  S shape;

  public DiagramElement(String id, T modelElement) {
    super(id);
    this.shape = createShape();
    this.modelElement = modelElement;
  }

  public DiagramElement(T modelElement) {
    this(null, modelElement);
  }

  public T getModelElement() {
    return modelElement;
  }

  public void setModelElement(T modelElement) {
    this.modelElement = modelElement;
  }

  public S getShape() {
    return shape;
  }

  public void setShape(S shape) {
    if (shape != null) this.shape = shape;
  }

  abstract S createShape();
}
