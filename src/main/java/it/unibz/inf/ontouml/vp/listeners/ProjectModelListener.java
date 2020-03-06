package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class ProjectModelListener implements IProjectModelListener {

	@Override
	public void modelAdded(IProject project, IModelElement modelElement) {
		if(modelElement == null) { return ; }

		modelElement.addPropertyChangeListener(OntoUMLPlugin.MODEL_LISTENER);
	}

	@Override
	public void modelRemoved(IProject project, IModelElement modelElement) {}

}