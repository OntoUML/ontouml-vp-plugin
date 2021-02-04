package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import static it.unibz.inf.ontouml.vp.model.ontouml.deserialization.ReferenceResolver.resolveReferences;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;
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
        .map(Uml2OntoumlTransformer::transformModelElement)
        .forEach(element -> resolveContainer(element, root));

    resolveReferences(target);

    return target;
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
}
