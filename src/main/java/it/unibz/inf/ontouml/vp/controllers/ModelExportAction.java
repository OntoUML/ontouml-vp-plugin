package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 * Implementation toolbar button action responsible for exporting OntoUML model
 * in  JSON (according to OntoUML Schema).
 * 
 * @author Claudenir Fonseca
 *
 */
public class ModelExportAction implements VPActionController {

	/**
	 * 
	 * Performs model export in JSON format.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {
		final ProjectConfigurations projectConfigurations = Configurations.getInstance().getProjectConfigurations();
		final Configurations configs = Configurations.getInstance();

		FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
				"Choose destination", FileDialog.SAVE);
		
		
		String suggestedFilename;
		fd.setDirectory(projectConfigurations.getExportFolderPath());
		
		if(projectConfigurations.getExportFilename().length() == 0){
			String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();		
			suggestedFilename = projectName;
			fd.setFile(suggestedFilename);
		}else{
			suggestedFilename = projectConfigurations.getExportFilename();
			fd.setFile(suggestedFilename);
		}
		
		fd.setVisible(true);

		String fileDirectory = fd.getDirectory();

		String fileName = fd.getFile();
		if(!fileName.endsWith(".json"))
			fileName+=".json";

		if (fileDirectory != null) {
			try {
				final String jsonModel = ModelElement.generateModel(true);
				Files.write(Paths.get(fileDirectory, fileName), jsonModel.getBytes());
				projectConfigurations.setExportFolderPath(fileDirectory);
				projectConfigurations.setExportFilename(fileName);
				configs.save();
			} catch (IOException e) {
				ViewUtils.log(
						"Export Failed. Please submit your Visual Paradigm's log and the time of the error our developers.",
						ViewUtils.SCOPE_PLUGIN);
				e.printStackTrace();
			}
		}

	}

	/**
	 * Called when the menu containing the button is accessed allowing for action
	 * manipulation, such as enable/disable or selecting the button.
	 * 
	 * OBS: DOES NOT apply to this class.
	 */
	@Override
	public void update(VPAction action) {
	}

}