package it.unibz.inf.ontouml.vp.controllers;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

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
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();

		final String jsonModel = ModelElement.generateModel(true);

		FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
				"Choose destination", FileDialog.SAVE);

		fd.setDirectory(configurations.getExportFolderPath());
		fd.setFile("*.json");
		fd.setVisible(true);

		String fileDirectory = fd.getDirectory();
		String filename = fd.getFile() + ".json";

		if (fileDirectory != null) {
			try {
				Files.write(Paths.get(fileDirectory, filename), jsonModel.getBytes());
				configurations.setExportFolderPath(fileDirectory);
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
