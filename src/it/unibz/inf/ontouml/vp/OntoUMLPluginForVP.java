package it.unibz.inf.ontouml.vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class OntoUMLPluginForVP implements VPPlugin {

	public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
	public static final String PLUGIN_NAME = "OntoUML Plugin";
	public static IModelElement[] allModelElements;
	public static boolean isPluginActive;

	@Override
	public void loaded(VPPluginInfo arg0) {
		
		ProjectManager pm = ApplicationManager.instance().getProjectManager();
		IProject p = pm.getProject();
		OntoUMLPluginForVP.allModelElements = pm.getSelectableStereotypesForModelType(IModelElementFactory.MODEL_TYPE_CLASS, p, true);
		
	}

	@Override
	public void unloaded() {}

}
