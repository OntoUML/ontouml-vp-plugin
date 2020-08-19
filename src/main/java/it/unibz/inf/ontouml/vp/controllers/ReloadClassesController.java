package it.unibz.inf.ontouml.vp.controllers;

import java.util.Set;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.StereotypesManager;

public class ReloadClassesController implements VPActionController {

	@Override
	public void performAction(VPAction action) {
//      OntoUMLPlugin.reload();
//		generateModel();
	}

	@Override
	public void update(VPAction action) {
	}
	
	@SuppressWarnings("unused")
	private void generateModel() {		
		final ApplicationManager app = ApplicationManager.instance();
		final DiagramManager dm = app.getDiagramManager();
		final IDiagramUIModel diagram = dm.getActiveDiagram();
		final IPackage pkg = (IPackage) diagram.getParentModel();
		final IModelElementFactory factory = IModelElementFactory.instance();
		
		if(diagram == null || pkg == null) { return ; }		
		
		final Set<String> stereotypes = StereotypesManager.getOntoUMLClassStereotypeNames();
		
		for (String sourceStereotype : stereotypes) {
			final IPackage sourcePkg = factory.createPackage();
			sourcePkg.setName(sourceStereotype);
			pkg.addChild(sourcePkg);
			
			for (String targetStereotype : stereotypes) {
//				// Method getAllowedAssociations() was removed 
//				final List<String> allowedAssociations = 
//						OntoUMLConstraintsManager.getAllowedAssociations(sourceStereotype, targetStereotype);
				
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