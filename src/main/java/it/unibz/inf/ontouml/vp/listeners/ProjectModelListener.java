package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class ProjectModelListener implements IProjectModelListener {

	@Override
	public void modelAdded(IProject arg0, IModelElement arg1) {
		
		arg1.addPropertyChangeListener(OntoUMLPlugin.MODEL_LISTENER);
		
	}

	@Override
	public void modelRemoved(IProject arg0, IModelElement arg1) {}

}
