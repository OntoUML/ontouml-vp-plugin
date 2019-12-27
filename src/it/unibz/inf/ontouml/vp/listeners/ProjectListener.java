package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

import it.unibz.inf.ontouml.vp.OntoUMLPluginForVP;
import it.unibz.inf.ontouml.vp.ProjectConfigurations;

public class ProjectListener implements IProjectListener {

	@Override
	public void projectAfterOpened(IProject project) {
		// TODO Auto-generated method stub

	}

	@Override
	public void projectNewed(IProject project) {
		OntoUMLPluginForVP.checkProjectConfigurations(project);

	}

	@Override
	public void projectOpened(IProject project) {
		OntoUMLPluginForVP.checkProjectConfigurations(project);
	}

	@Override
	public void projectPreSave(IProject project) {
		OntoUMLPluginForVP.saveConfigurations();
	}

	@Override
	public void projectRenamed(IProject project) {
		// TODO Auto-generated method stub

	}

	@Override
	public void projectSaved(IProject project) {
		// TODO Auto-generated method stub

	}

}
