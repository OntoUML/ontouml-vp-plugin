package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import java.util.ArrayList;
import java.util.List;

public abstract class ConnectorView<T extends ModelElement> extends DiagramElement<T, Path> {
  private DiagramElement<?, ?> source;
  private DiagramElement<?, ?> target;

  public ConnectorView(String id, T connector) {
    super(id, connector);
  }

  public ConnectorView(T connector) {
    this(null, connector);
  }

  public ConnectorView() {
    this(null, null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    List<OntoumlElement> contents = new ArrayList<>();
    contents.add(getPath());
    return contents;
  }

  @Override
  Path createShape() {
    return new Path();
  }

  public Path getPath() {
    return getShape();
  }

  public void setPath(Path path) {
    setShape(path);
  }

  public DiagramElement<?, ?> getSource() {
    return source;
  }

  public void setSource(DiagramElement<?, ?> source) {
    this.source = source;
  }

  public DiagramElement<?, ?> getTarget() {
    return target;
  }

  public void setTarget(DiagramElement<?, ?> target) {
    this.target = target;
  }
}
