package it.unibz.inf.ontouml.vp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.listeners.ProjectListener;

public class OntoUMLPluginForVP implements VPPlugin {

	public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
	public static final String PLUGIN_NAME = "OntoUML Plugin";
	public static IModelElement[] allModelElements;
	public static boolean isPluginActive;
	
//	private static final String VERIFICATION_SERVICE_URL = "http://localhost:3000/v1/verification";
	public static final String DEFAULT_SERVER_URL = "https://ontouml.herokuapp.com/v1/verification";
	
	private static Configurations config;

	@Override
	public void loaded(VPPluginInfo arg0) {
		ProjectManager pm = ApplicationManager.instance().getProjectManager();
		IProject p = pm.getProject();
		OntoUMLPluginForVP.allModelElements = pm.getSelectableStereotypesForModelType(IModelElementFactory.MODEL_TYPE_CLASS, p, true);
		
		loadConfigurations();
		ApplicationManager.instance().getProjectManager().getProject().addProjectListener(new ProjectListener());
	}

	@Override
	public void unloaded() {
		saveConfigurations();
	}
	
	public static Configurations getConfigurations() {
		if(config == null) {
			config = new Configurations();
		}
		
		return config;
	}
	
	public static ProjectConfigurations checkProjectConfigurations(IProject source) {
		ProjectConfigurations project = OntoUMLPluginForVP.getConfigurations().getProject(source.getId());
		
		if(project == null) {
			project = new ProjectConfigurations(source);
			OntoUMLPluginForVP.getConfigurations().addProject(project);
		}
		
		return project;
	}
	
	public static ProjectConfigurations getCurrentPorjectConfigurations() {
		IProject p = ApplicationManager.instance().getProjectManager().getProject();
		return checkProjectConfigurations(p);
	}
	
	public static Configurations loadConfigurations() {
		ViewManager vm = ApplicationManager.instance().getViewManager();
		File configurationsFile = new File(ApplicationManager.instance().getWorkspaceLocation(), ".ontouml-config.json");
		String json = "";
		
		try {
			if(!configurationsFile.exists()) {
				configurationsFile.createNewFile();
			}
			json = new String(Files.readAllBytes(configurationsFile.toPath()));
		}
		catch (IOException e) {
            e.printStackTrace();
            vm.showMessage("Unable to load " + OntoUMLPluginForVP.PLUGIN_NAME + " configurations.\n" + 
            		"Please share your log (including your model, if possible) with our developers at <https://github.com/OntoUML/ontouml-vp-plugin>.");
        }
		
		Gson gson = new Gson();
		config = gson.fromJson(json, Configurations.class);
		
		if(config == null) {
			config = new Configurations();
		}
		
		return config;
	}
	
	public static void saveConfigurations() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		String json = gson.toJson(getConfigurations());
		
		ViewManager vm = ApplicationManager.instance().getViewManager();
		File configurationsFile = new File(ApplicationManager.instance().getWorkspaceLocation(), ".ontouml-config.json");
		FileWriter fw = null;
		
        try {
            fw = new FileWriter(configurationsFile);
            fw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
            vm.showMessage("Unable to save " + OntoUMLPluginForVP.PLUGIN_NAME + " configurations.\n" + 
            		"Please share your log (including your model, if possible) with our developers at <https://github.com/OntoUML/ontouml-vp-plugin>.");
        }finally{
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
}
