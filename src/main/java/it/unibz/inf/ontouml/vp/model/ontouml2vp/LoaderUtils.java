package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.view.DiagramElement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoaderUtils {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  static IDiagramElement getIDiagramElement(IClassDiagramUIModel vpDiagram, DiagramElement view) {
    String targetId = view.getId();
    return vpDiagram.getDiagramElementById(targetId);
  }

  static <T extends IDiagramElement> T getIDiagramElement(
      IClassDiagramUIModel vpDiagram, DiagramElement view, java.lang.Class<T> vpType) {
    String targetId = view.getId();
    return vpType.cast(vpDiagram.getDiagramElementById(targetId));
  }

  static IModelElement getIModelElement(DiagramElement view) {
    IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();
    String modelElementId = view.getModelElement().getId();
    return vpProject.getModelElementById(modelElementId);
  }

  static String getIncompatibleMessage(
      DiagramElement fromView, IModelElement toModelElement, java.lang.Class<?> expected) {
    return "Skipped "
        + fromView.getType()
        + ": "
        + fromView.getId()
        + ". Incompatible model element (expected: "
        + expected.getSimpleName()
        + "; actual: "
        + (toModelElement != null ? toModelElement.getModelType() : null)
        + ")";
  }

  static String getModelElementImportingMessage(OntoumlElement fromElement) {
    return "Importing "
        + fromElement.getType()
        + ": "
        + fromElement.getFirstName().orElse("")
        + " ("
        + fromElement.getId()
        + ")";
  }

  static void logElementCreation(OntoumlElement element) {
    System.out.println(getModelElementImportingMessage(element));
  }

  static List<IDataType> getAllDatatypes() {
    return Stream.of(
            vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_DATA_TYPE))
        .filter(IDataType.class::isInstance)
        .map(IDataType.class::cast)
        .collect(Collectors.toList());
  }

  static IClass getToClass(Class fromClass) {
    IModelElement toClass = IAttributeLoader.vpProject.getModelElementById(fromClass.getId());

    if (toClass instanceof IClass) return (IClass) toClass;

    return null;
  }

  static void loadName(ModelElement fromElement, IModelElement toElement) {
    fromElement.getFirstName().ifPresent(name -> toElement.setName(name));
  }
}
