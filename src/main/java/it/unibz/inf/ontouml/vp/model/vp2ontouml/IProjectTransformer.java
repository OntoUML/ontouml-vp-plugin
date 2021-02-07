package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.ReferenceResolver.resolveReferences;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IProjectTransformer {

  public static Project transform(IProject source) {
    Project target = new Project();
    Trace.getInstance().put(source.getId(), source, target);

    String name = source.getName();
    target.addName(source.getName());

    String id = source.getId();
    target.setId(id);

    Package root = target.createModel(id + "_root", name);

    getElementStream(source)
        .map(element -> transformModelElement(element))
        .forEach(element -> resolveContainer(element, root));

    resolveReferences(target);

    List<Diagram> diagrams = transformDiagrams(source);
    target.setDiagrams(diagrams);

    return target;
  }

  private static List<Diagram> transformDiagrams(IProject source) {
    return Stream.of(source.toDiagramArray())
        .filter(IClassDiagramUIModel.class::isInstance)
        .map(IClassDiagramTransformer::transform)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static Stream<IModelElement> getElementStream(IProject source) {
    final String[] elementTypes = {
      IModelElementFactory.MODEL_TYPE_PACKAGE,
      IModelElementFactory.MODEL_TYPE_MODEL,
      IModelElementFactory.MODEL_TYPE_CLASS,
      IModelElementFactory.MODEL_TYPE_DATA_TYPE,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS,
    };

    IModelElement[] sourceContents = source.toAllLevelModelElementArray(elementTypes);
    return Stream.of(sourceContents);
  }

  private static void resolveContainer(ModelElement targetElement, Package root) {
    Package targetContainer = getContainer(targetElement, root);
    targetContainer.addContent(targetElement);
  }

  private static Package getContainer(ModelElement targetElement, Package root) {
    Object sourceElement = Trace.getInstance().getSource(targetElement.getId());
    IModelElement sourceParent = getSourceParent(sourceElement);

    if (sourceParent == null) {
      return root;
    }

    OntoumlElement targetParent = Trace.getInstance().getTarget(sourceParent);
    return (targetParent instanceof Package) ? (Package) targetParent : root;
  }

  private static IModelElement getSourceParent(Object element) {
    // VP puts an association, associationclass and generalization inside the package of its source
    if (element instanceof IRelationship) {
      IModelElement from = ((IRelationship) element).getFrom();
      return from != null ? from.getParent() : null;
    }

    if (element instanceof IModelElement) {
      return ((IModelElement) element).getParent();
    }

    return null;
  }

  public static ModelElement transformModelElement(IModelElement source) {
    ModelElement target = null;

    if (source instanceof IClass || source instanceof IDataType) {
      target = IClassTransformer.transform(source);
    } else if (source instanceof IAssociation) {
      target = IAssociationTransformer.transform(source);
    } else if (source instanceof IAssociationClass) {
      target = IAssociationClassTransformer.transform(source);
    } else if (source instanceof IPackage || source instanceof IModel) {
      target = IPackageTransformer.transform(source);
    } else if (source instanceof IAttribute || source instanceof IAssociationEnd) {
      target = IPropertyTransformer.transform(source);
    } else if (source instanceof IGeneralization) {
      target = IGeneralizationTransformer.transform(source);
    } else if (source instanceof IGeneralizationSet) {
      target = IGeneralizationSetTransformer.transform(source);
    }

    Trace.getInstance().put(source.getId(), source, target);

    return target;
  }
}
