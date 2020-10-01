package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.util.List;

public class ReloadClassesController implements VPActionController {

  @Override
  public void performAction(VPAction action) {
    //      OntoUMLPlugin.reload();
    //		generateModel();
    reverseDirection(action);
  }

  @Override
  public void update(VPAction action) {}

  private void reverseDirection(VPAction action) {
    ApplicationManager app = ApplicationManager.instance();
    app.reloadPluginClasses(OntoUMLPlugin.PLUGIN_ID);

    //		DiagramManager dm = app.getDiagramManager();
    //		IDiagramElement[] selectedElements = dm.getSelectedDiagramElements();
    //
    //		for (int i = 0; selectedElements != null && i < selectedElements.length; i++) {
    //			IDiagramElement diagramElement = selectedElements[i];
    //			IModelElement element = diagramElement.getModelElement();
    //
    //			if (element == null ||
    // !IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(element.getModelType())) {
    //				continue;
    //			}
    //
    //			IAssociation association = (IAssociation) element;
    //			Association.invertAssociation(association);
    //			IAssociationUIModel originalAssociationView = (IAssociationUIModel) diagramElement;
    //			IClass originalFrom = (IClass) association.getFrom();
    //			IClass originalTo = (IClass) association.getTo();
    //			IAssociationEnd originalFromEnd = (IAssociationEnd) association.getFromEnd();
    //			IAssociationEnd originalToEnd = (IAssociationEnd) association.getToEnd();
    //
    //			association.setFrom(originalTo);
    //			association.setTo(originalFrom);
    //
    //			Point[] points = originalAssociationView.getPoints();
    //			IDiagramUIModel diagram = diagramElement.getDiagramUIModel();
    //			IAssociationUIModel reverseAssociationView = (IAssociationUIModel)
    // dm.createConnector(diagram, association, originalAssociationView.getToShape(),
    //					originalAssociationView.getFromShape(), null);
    //			diagramElement.deleteViewOnly();
    //
    //			if(points != null) {
    //				for (int j = points.length - 1; j >= 0; j--) {
    //					reverseAssociationView.addPoint(points[j]);
    //				}
    //			}
    //			reverseAssociationView.resetCaption();
    //			IConnectorUIModel reverseAssociationView = (IConnectorUIModel)
    // dm.createDiagramElement(diagram, association);
    //			reverseAssociationView.set
    //		}
  }

  @SuppressWarnings("unused")
  private void generateModel() {
    final ApplicationManager app = ApplicationManager.instance();
    final DiagramManager dm = app.getDiagramManager();
    final IDiagramUIModel diagram = dm.getActiveDiagram();
    final IPackage pkg = (IPackage) diagram.getParentModel();
    final IModelElementFactory factory = IModelElementFactory.instance();

    if (diagram == null || pkg == null) {
      return;
    }

    final List<String> stereotypes = Stereotype.getOntoUMLClassStereotypeNames();

    for (String sourceStereotype : stereotypes) {
      final IPackage sourcePkg = factory.createPackage();
      sourcePkg.setName(sourceStereotype);
      pkg.addChild(sourcePkg);

      for (String targetStereotype : stereotypes) {
        //				// Method getAllowedAssociations() was removed
        //				final List<String> allowedAssociations =
        //						OntoUMLConstraintsManager.getAllowedAssociations(sourceStereotype,
        // targetStereotype);

        //				if(!allowedAssociations.isEmpty()) {
        //					final IClass source = factory.createClass();
        //					final IClass target = factory.createClass();
        //
        //					source.setName(sourceStereotype + " as Source");
        //					target.setName(targetStereotype + " as Target");
        //
        //					source.addStereotype(sourceStereotype);
        //					target.addStereotype(targetStereotype);
        //
        //					sourcePkg.addChild(source);
        //					sourcePkg.addChild(target);
        //
        //					for (String associationStereotype : allowedAssociations) {
        //						final IAssociation association = factory.createAssociation();
        //
        //						association.addStereotype(associationStereotype);
        //						association.setFrom(source);
        //						association.setTo(target);
        //
        //						sourcePkg.addChild(association);
        //					}
        //				}
      }
    }
  }
}
