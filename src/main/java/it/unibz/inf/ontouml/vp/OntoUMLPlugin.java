package it.unibz.inf.ontouml.vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.listeners.DiagramListener;
import it.unibz.inf.ontouml.vp.listeners.ModelListener;
import it.unibz.inf.ontouml.vp.listeners.ProjectDiagramListener;
import it.unibz.inf.ontouml.vp.listeners.ProjectListener;
import it.unibz.inf.ontouml.vp.listeners.ProjectModelListener;

/**
 * 
 * Implementation of VPPlugin responsible for configuring OntoUML Plugin's
 * behaviour when loading and unload.
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public class OntoUMLPlugin implements VPPlugin {

	public static final String PLUGIN_VERSION_RELEASE = "0.2.0";
	public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
	public static final String PLUGIN_NAME = "OntoUML Plugin";

	public static IModelElement[] allModelElements;
	public static boolean isPluginActive;
	
	public static ModelListener MODEL_LISTENER = new ModelListener();
	public static DiagramListener DIAGRAM_LISTENER = new DiagramListener();
	public static ProjectModelListener PROJECT_MODEL_LISTENER = new ProjectModelListener();
	public static ProjectDiagramListener PROJECT_DIAGRAM_LISTENER = new ProjectDiagramListener();
	
	private static boolean isExportToGUFOWindowOpen;
	private static boolean isConfigWindowOpen;

	/**
	 * 
	 * OntoUMLPlugin constructor. Declared to make explicit Open API requirements.
	 * 
	 */
	public OntoUMLPlugin() {
		// The constructor of a VPPlugin MUST NOT have parameters.
		isExportToGUFOWindowOpen = false;
		isConfigWindowOpen = false;
	}

	/**
	 * 
	 * Called by Visual Paradigm when the plugin is loaded.
	 * 
	 * @param pluginInfo
	 * 
	 */
	@Override
	public void loaded(VPPluginInfo pluginInfo) {
	
		ProjectManager pm = ApplicationManager.instance().getProjectManager();
		ProjectListener projectListener = new ProjectListener();
		IProject p = pm.getProject();
		p.addProjectListener(projectListener);
		p.addProjectDiagramListener(OntoUMLPlugin.PROJECT_DIAGRAM_LISTENER);
		p.addProjectModelListener(OntoUMLPlugin.PROJECT_MODEL_LISTENER);
		
		OntoUMLPlugin.allModelElements = pm.getSelectableStereotypesForModelType(IModelElementFactory.MODEL_TYPE_CLASS,
				p, true);
	
	}

	/**
	 * 
	 * Called by Visual Paradigm when the plugin is unloaded (i.e., Visual Paradigm
	 * will be exited). This method is not called when the plugin is reloaded.
	 * 
	 */
	@Override
	public void unloaded() {
	}
	
	public static void setExportToGUFOWindowOpen(boolean open) {
		isExportToGUFOWindowOpen = open;
		
	}
	
	public static boolean getExportToGUFOWindowOpen() {
		return isExportToGUFOWindowOpen;
	}
	
	public static void setConfigWindowOpen(boolean open) {
		isConfigWindowOpen = open;
		
	}
	
	public static boolean getConfigWindowOpen() {
		return isConfigWindowOpen;
	}

}
