package it.unibz.inf.ontouml.vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

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

	public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
	public static final String PLUGIN_NAME = "OntoUML Plugin";

	public static IModelElement[] allModelElements;
	public static boolean isPluginActive;

	/**
	 * 
	 * OntoUMLPlugin constructor. Declared to make explicit Open API requirements.
	 * 
	 */
	public OntoUMLPlugin() {
		// The constructor of a VPPlugin MUST NOT have parameters.
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
		// TODO Reevaluate the need for the following code
		ProjectManager pm = ApplicationManager.instance().getProjectManager();
		IProject p = pm.getProject();
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

}
