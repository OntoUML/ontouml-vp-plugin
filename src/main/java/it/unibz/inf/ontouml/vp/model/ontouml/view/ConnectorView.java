package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import java.util.ArrayList;
import java.util.List;

public abstract class ConnectorView<T extends ModelElement> extends ElementView<T, Path> {
  private ElementView<?, ?> source;
  private ElementView<?, ?> target;

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

  public ElementView<?, ?> getSource() {
    return source;
  }

  public void setSource(ElementView<?, ?> source) {
    this.source = source;
  }

  public ElementView<?, ?> getTarget() {
    return target;
  }

  public void setTarget(ElementView<?, ?> target) {
    this.target = target;
  }
}
