package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;

public class IDiagramLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();
  static DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

  public static void load(Diagram fromDiagram) {

    IClassDiagramUIModel toDiagram = createIDiagram(fromDiagram);
    transferDiagramProperties(fromDiagram, toDiagram);

    fromDiagram
        .getAllClassViews()
        .forEach(fromClassView -> IClassUIModelLoader.load(toDiagram, fromClassView));

    fromDiagram.getAllRelationViews().stream()
        .filter(view -> view.getModelElement() != null)
        .filter(view -> view.getModelElement().holdsBetweenClasses())
        .forEach(fromRelationView -> IAssociationUIModelLoader.load(toDiagram, fromRelationView));

    fromDiagram.getAllRelationViews().stream()
        .filter(view -> view.getModelElement() != null)
        .filter(view -> !view.getModelElement().holdsBetweenClasses())
        .forEach(fromRelationView -> IAssociationUIModelLoader.load(toDiagram, fromRelationView));

    fromDiagram
        .getAllGeneralizationViews()
        .forEach(fromGenView -> IGeneralizationUIModelLoader.load(toDiagram, fromGenView));

    fromDiagram
        .getAllGeneralizationSetViews()
        .forEach(fromGsView -> IGeneralizationSetUIModelLoader.load(toDiagram, fromGsView));
  }

  private static void transferDiagramProperties(Diagram fromDiagram, IDiagramUIModel toDiagram) {
    String fromOwnerId = fromDiagram.getOwner().getId();
    IModelElement toOwner = vpProject.getModelElementById(fromOwnerId);

    if (toOwner != null) toOwner.addSubDiagram(toDiagram);

    String name = fromDiagram.getFirstName().orElse("Unnamed diagram");
    toDiagram.setName(name);
  }

  private static IClassDiagramUIModel createIDiagram(Diagram fromDiagram) {
    IDiagramUIModel vpDiagram = vpProject.getDiagramById(fromDiagram.getId());

    if (vpDiagram != null) {
      System.out.println("Diagram " + fromDiagram.getId() + " exists! Let's override it!");
      vpDiagram.delete();
    } else {
      System.out.println("Diagram " + fromDiagram.getId() + " not found! Let's create it");
    }

    IClassDiagramUIModel toDiagram =
        (IClassDiagramUIModel)
            diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
    fromDiagram.setId(toDiagram.getId());

    return toDiagram;
  }
}