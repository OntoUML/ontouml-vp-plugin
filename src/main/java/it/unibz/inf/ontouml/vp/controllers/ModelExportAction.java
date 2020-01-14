package it.unibz.inf.ontouml.vp.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;

import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

/**
 * 
 * Implementation toolbar button action responsible for exporting OntoUML model in both XMI (according
 * UML's standard) and JSON (according to OntoUML Schema).
 * 
 * @author Claudenir Fonseca
 *
 */
public class ModelExportAction implements VPActionController {

	/**
	 * 
	 * Performs model export in XMI and JSON formats to user defined folder.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();
		
		/*1if(!configurations.isModelExportEnabled()) {
			ViewUtils.log("Model Export feature is not enabled.", ViewUtils.SCOPE_DEVELOPMENT_LOG);
			return ;
		} */
		
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final String jsonModel = ModelElement.generateModel(true);
		final File outputXMLFolder = new File(configurations.getExportFolderPath());
		
		try {
			ApplicationManager.instance().getModelConvertionManager().showExportXMLDialog(outputXMLFolder);
            Files.write(Paths.get(configurations.getExportFolderPath(), project.getName() + ".json"), 
            		jsonModel.getBytes());
            ViewUtils.log("JSON model successfully exported.", ViewUtils.SCOPE_PLUGIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Called when the menu containing the button is accessed allowing for action
	 * manipulation, such as enable/disable or selecting the button.
	 * 
	 *  OBS: DOES NOT apply to this class.
	 */
	@Override
	public void update(VPAction action) {
	}

}
