package it.unibz.inf.ontouml.vp.controllers;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.OntoUMLServerUtils;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;

/**
 * Implementation of toolbar button Export to gUFO.
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public class ExportToGUFOAction implements VPActionController {

	@Override
	public void performAction(VPAction action) {

		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					final BufferedReader gufo = OntoUMLServerUtils
							.transformToGUFO(ModelElement.generateModel(true),
							"http://api.ontouml.org/", "turtle", "name");
					if(gufo != null) {
						saveFile(gufo);
					}
				} catch (Exception e) {
					// ViewUtils.exportToGUFOIssueDialog("Some error occurred while exporting the model");
					e.printStackTrace();
				}
			}

		});
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

	private void saveFile(BufferedReader buffer) throws IOException {
		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
		final FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
				"Choose destination", FileDialog.SAVE);

		String suggestedFolderPath = projectConfigurations.getExportGUFOFolderPath();
		String suggestedFileName = projectConfigurations.getExportGUFOFilename();

		if(suggestedFileName.isEmpty()){
			String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
			suggestedFileName = projectName+".ttl";
		}

		fd.setDirectory(suggestedFolderPath);
		fd.setFile(suggestedFileName);
		fd.setVisible(true);

		
		
		if (fd.getDirectory() != null && fd.getFile() != null) {
			final String fileDirectory = fd.getDirectory();
			final String fileName = !fd.getFile().endsWith(".ttl") ? fd.getFile() + ".ttl" : fd.getFile();
			final String output = buffer.lines().collect(Collectors.joining("\n"));

			Files.write(Paths.get(fileDirectory, fileName), output.getBytes());
			projectConfigurations.setExportGUFOFolderPath(fileDirectory);
			projectConfigurations.setExportGUFOFilename(fileName);
			configs.save();
		}
	}

}