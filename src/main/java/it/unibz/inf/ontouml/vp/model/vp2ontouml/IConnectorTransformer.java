package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ConnectorView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ElementView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Path;
import java.util.Arrays;

public class IConnectorTransformer {

  public static void transform(IConnectorUIModel source, ConnectorView<?> target) {

    Path path = new Path();
    path.setId(source.getId() + "_path");
    Arrays.stream(source.getPoints())
        .forEachOrdered(p -> path.moveTo((int) p.getX(), (int) p.getY()));
    target.setPath(path);

    IDiagramElement connectorSource = null;
    if (source.getFromShape() != null) connectorSource = source.getFromShape();
    else if (source.getFromConnector() != null) connectorSource = source.getFromConnector();

    ElementView<?, ?> connectorSourceStub = ReferenceTransformer.transformStub(connectorSource);
    target.setSource(connectorSourceStub);

    IDiagramElement connectorTarget = null;
    if (source.getToShape() != null) connectorTarget = source.getToShape();
    else if (source.getToConnector() != null) connectorTarget = source.getToConnector();

    ElementView<?, ?> connectorTargetStub = ReferenceTransformer.transformStub(connectorTarget);
    target.setTarget(connectorTargetStub);
  }
}
