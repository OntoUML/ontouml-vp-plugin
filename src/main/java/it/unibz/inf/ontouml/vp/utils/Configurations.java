package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * OntoUML Plugin configuration utility, allows persisting 
 * user preferences on the functionalities enabled by the plugin. 
 * 
 * @author Claudenir Fonseca
 *
 */
public class Configurations {
	
	private static final String CONFIG_FILE_NAME = ".ontouml.config.json";
	
	private static Configurations instance;
	
	@SerializedName("projects")
	@Expose()
	private List<ProjectConfigurations> projects;
	
	private Configurations() {
		this.projects = new ArrayList<ProjectConfigurations>();
	}
	
	private List<ProjectConfigurations> getProjectConfigurationsList() {
		return projects;
	}
	
	private boolean addProjectConfigurations(ProjectConfigurations projectConfigurations) {
		return getProjectConfigurationsList().add(projectConfigurations);
	}
	
	/**
	 * 
	 * Returns singleton instance of the class.
	 * 
	 * @return configurations
	 * 
	 */
	public static Configurations getInstance() {
		if(instance == null) {
			final ApplicationManager application = ApplicationManager.instance(); 
			final File workspace = application.getWorkspaceLocation();
			final File configurationsFile = new File(workspace, CONFIG_FILE_NAME);
			
			if(configurationsFile.exists()) {
				String json = "";
				
				try {
					json = new String(Files.readAllBytes(configurationsFile.toPath()));
				}
				catch (IOException e) {
					e.printStackTrace();
					application.getViewManager().showMessage("Unable to load " 
							+ OntoUMLPlugin.PLUGIN_NAME + " configurations.\n" + 
							"Please contact our team at <https://github.com/OntoUML/ontouml-vp-plugin>.");
				}
				
				Gson gson = new Gson();
				instance = gson.fromJson(json, Configurations.class);
			}
			else {
				instance = new Configurations();
			}
		}

		return instance;
	}
	
	/**
	 * 
	 * Persists user preferences on Visual Paradigm's workspace.
	 *  
	 */
	public void save() {
		final ApplicationManager application = ApplicationManager.instance(); 
		final File workspace = application.getWorkspaceLocation();
		final File configurationsFile = new File(workspace, CONFIG_FILE_NAME);
		final FileWriter fw;
		final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		final String json = gson.toJson(this);
		
		try {
			if(!configurationsFile.exists()) {
				configurationsFile.createNewFile();
			}
		
			fw = new FileWriter(configurationsFile);
			fw.write(json);
			fw.close();
		} catch (IOException e) {
			application.getViewManager().showMessage("Unable save " 
					+ OntoUMLPlugin.PLUGIN_NAME 
					+ " configurations.");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Retrieves the configurations of the current project.
	 * 
	 * @return current project's configurations.
	 * 
	 */
	public ProjectConfigurations getProjectConfigurations() {
		final IProject current = ApplicationManager.instance().getProjectManager().getProject();
		
		return getProjectConfigurations(current.getId());
	}
	
	/**
	 * 
	 * Retrieves the project configurations given the provided ID. 
	 * If no previous configurations is present, a new instance of 
	 * <code>ProjectConfigurations</code> is returned with default
	 * settings.
	 * 
	 * @param projectId - Interest project's ID.
	 * 
	 * @return current project's configurations.
	 * 
	 * 
	 */
	public ProjectConfigurations getProjectConfigurations(String projectId) {
		for (ProjectConfigurations projectConfigurations : getProjectConfigurationsList()) {
			if(projectConfigurations.getId().equals(projectId)) {
				return projectConfigurations;
			}
		}
		
		ProjectConfigurations projectConfigurations = new ProjectConfigurations(projectId);
		addProjectConfigurations(projectConfigurations);
		
		return projectConfigurations;
	}
	
}
