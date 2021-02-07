package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;
import it.unibz.inf.ontouml.vp.model.ontouml.view.DiagramElement;
import java.util.Arrays;

public class IClassDiagramTransformer {

  public static Diagram transform(IDiagramUIModel sourceElement) {
    if (!(sourceElement instanceof IClassDiagramUIModel)) return null;

    IClassDiagramUIModel source = (IClassDiagramUIModel) sourceElement;

    System.out.println(source.getType() + ": " + source.getName());

    Diagram target = new Diagram();

    String id = source.getId();
    target.setId(id);

    String name = source.getName();
    target.addName(name);

    String description = source.getDocumentation();
    target.addDescription(description);

    ModelElement owner = getOwner(source);
    target.setOwner(owner);

    Arrays.stream(source.toDiagramElementArray())
        .map(e -> transfromIDiagramElement(e))
        .forEach(e -> target.addElement(e));

    return target;
  }

  private static ModelElement getOwner(IClassDiagramUIModel source) {
    IModelElement container = source.getParentModel();
    return (ModelElement) ReferenceTransformer.transformStub(container);
  }

  public static DiagramElement<?, ?> transfromIDiagramElement(IDiagramElement source) {
    DiagramElement<?, ?> target = null;

    if (source instanceof IClassUIModel) {
      target = IClassUIModelTransformer.transform(source);
    } else if (source instanceof IAssociationUIModel) {
      target = IAssociationUIModelTransformer.transform(source);
    } else if (source instanceof IAssociationClassUIModel) {
      target = IAssociationClassUIModelTransformer.transform(source);
    } else if (source instanceof IGeneralizationUIModel) {
      target = IGeneralizationUIModelTransformer.transform(source);
    } else if (source instanceof IGeneralizationSetUIModel) {
      target = IGeneralizationSetUIModelTransformer.transform(source);
    } else if (source instanceof IPackageUIModel || source instanceof IModelUIModel) {
      target = IPackageUIModelTransformer.transform(source);
    }

    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }
}
