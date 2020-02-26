package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class ProjectListener implements IProjectListener {

	public ProjectListener() {
	}

	@Override
	public void projectAfterOpened(IProject arg0) {
	}

	@Override
	public void projectNewed(IProject arg0) {

		arg0.addProjectDiagramListener(OntoUMLPlugin.PROJECT_DIAGRAM_LISTENER);
		arg0.addProjectModelListener(OntoUMLPlugin.PROJECT_MODEL_LISTENER);

	}

	@Override
	public void projectOpened(IProject arg0) {

		arg0.addProjectDiagramListener(OntoUMLPlugin.PROJECT_DIAGRAM_LISTENER);
		arg0.addProjectModelListener(OntoUMLPlugin.PROJECT_MODEL_LISTENER);

		String[] elementTypes = { IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION };

		IModelElement[] modelElements = arg0.toAllLevelModelElementArray(elementTypes);

		for (int i = 0; i <= 1; i++) {
			for (int j = 0; modelElements != null && j < modelElements.length; j++) {
				IModelElement element = (IModelElement) modelElements[j];
				element.addPropertyChangeListener(OntoUMLPlugin.MODEL_LISTENER);
			}
		}
	}

	@Override
	public void projectPreSave(IProject arg0) {
	}

	@Override
	public void projectRenamed(IProject arg0) {
	}

	@Override
	public void projectSaved(IProject arg0) {
	}

}
