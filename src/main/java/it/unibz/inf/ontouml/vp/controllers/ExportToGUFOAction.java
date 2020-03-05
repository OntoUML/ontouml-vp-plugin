package it.unibz.inf.ontouml.vp.controllers;

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
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import java.awt.*;

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
							.transformToGUFO(ModelElement.generateModel(true));
					saveFile(gufo);
				} catch (IOException e) {
					ViewUtils.exportToGUFOIssueDialog("Some error occurred while exporting the model");
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					ViewUtils.exportToGUFOIssueDialog("Some error occurred while exporting the model");
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

		String suggestedFolderPath = projectConfigurations.getExportFolderPath();
		String suggestedFileName = projectConfigurations.getExportFilename();

		if(suggestedFileName.isEmpty()){
			String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
			suggestedFileName = projectName+".ttl";
		}

		fd.setDirectory(suggestedFolderPath);
		fd.setFile(suggestedFileName);
		fd.setVisible(true);

		String fileDirectory = fd.getDirectory();
		String fileName = fd.getFile();

		if(!fileName.endsWith(".ttl"))
			fileName+=".ttl";

		if (fileDirectory != null) {
			// final String str = buffer.lines().collect(Collectors.joining());
			final String str = "@prefix : <https://example.com#>.\n" +
			"@prefix gufo: <http://purl.org/nemo/gufo#>.\n" +
			"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n" +
			"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n" +
			"@prefix owl: <http://www.w3.org/2002/07/owl#>.\n" +
			"@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n" +
			"\n" +
			"<https://example.com> rdf:type owl:Ontology;\n" +
			"   owl:imports gufo:.\n" +
			":qJdeWA6AUB0UtAWm rdf:type owl:Class, owl:NamedIndividual;\n" +
			"   rdfs:label \"Person\";\n" +
			"   rdfs:subClassOf gufo:FunctionalComplex;\n" +
			"   rdf:type gufo:Kind.\n";
			Files.write(Paths.get(fileDirectory, fileName), str.getBytes());
			projectConfigurations.setExportGUFOFolderPath(fileDirectory);
			projectConfigurations.setExportGUFOFilename(fileName);
			configs.save();
		}
	}

}