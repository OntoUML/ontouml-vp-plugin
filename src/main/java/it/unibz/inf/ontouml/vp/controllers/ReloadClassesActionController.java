package it.unibz.inf.ontouml.vp.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.vpuml.plugin.umlpluginmodel.modelui.DiagramUIModel;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.features.constraints.AssociationConstraints;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.GitHubUtils;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ReloadClassesActionController implements VPActionController {

	@Override
	public void performAction(VPAction action) {
//      OntoUMLPlugin.reload();
//		generateModel();
		accessGitHub();
	}

	@Override
	public void update(VPAction action) {
	}
	
	private void accessGitHub() {
		try {
			JsonArray releases = GitHubUtils.getReleases(OntoUMLPlugin.PLUGIN_REPO_NAME);
			Configurations.getInstance().setReleases(releases);
			Configurations.getInstance().save();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void generateModel() {		
		final ApplicationManager app = ApplicationManager.instance();
		final DiagramManager dm = app.getDiagramManager();
		final IDiagramUIModel diagram = dm.getActiveDiagram();
		final IPackage pkg = (IPackage) diagram.getParentModel();
		final IModelElementFactory factory = IModelElementFactory.instance();
		
		if(diagram == null || pkg == null) { return ; }		
		
		final Set<String> stereotypes = StereotypeUtils.getOntoUMLClassStereotypeNames();
		
		for (String sourceStereotype : stereotypes) {
			final IPackage sourcePkg = factory.createPackage();
			sourcePkg.setName(sourceStereotype);
			pkg.addChild(sourcePkg);
			
			for (String targetStereotype : stereotypes) {
				final List<String> allowedAssociations = 
						AssociationConstraints.getAllowedAssociations(sourceStereotype, targetStereotype);
				
				if(!allowedAssociations.isEmpty()) {
					final IClass source = factory.createClass();
					final IClass target = factory.createClass();
					
					source.setName(sourceStereotype + " as Source");
					target.setName(targetStereotype + " as Target");
					
					source.addStereotype(sourceStereotype);
					target.addStereotype(targetStereotype);
					
					sourcePkg.addChild(source);
					sourcePkg.addChild(target);
					
					for (String associationStereotype : allowedAssociations) {
						final IAssociation association = factory.createAssociation();
						
						association.addStereotype(associationStereotype);
						association.setFrom(source);
						association.setTo(target);
						
						sourcePkg.addChild(association);
					}
				}
			}
		}
	}

}