package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ProjectListener implements IProjectListener {

	public ProjectListener() {
	}

	@Override
	public void projectAfterOpened(IProject project) {
		System.out.println("After Opened... ----------------");

		if(project == null) { return ; }

		try {
			StereotypeUtils.generate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void projectNewed(IProject project) {
		System.out.println("Newed... ----------------");

		if(project == null) { return ; }

		project.addProjectDiagramListener(OntoUMLPlugin.PROJECT_DIAGRAM_LISTENER);
		project.addProjectModelListener(OntoUMLPlugin.PROJECT_MODEL_LISTENER);
	}

	@Override
	public void projectOpened(IProject project) {
		if(project == null) { return ; }

		project.addProjectDiagramListener(OntoUMLPlugin.PROJECT_DIAGRAM_LISTENER);
		project.addProjectModelListener(OntoUMLPlugin.PROJECT_MODEL_LISTENER);

		String[] elementTypes = { IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION };

		IModelElement[] modelElements = project.toAllLevelModelElementArray(elementTypes);

		for (int i = 0; i <= 1; i++) {
			for (int j = 0; modelElements != null && j < modelElements.length; j++) {
				IModelElement element = (IModelElement) modelElements[j];
				element.addPropertyChangeListener(OntoUMLPlugin.MODEL_LISTENER);
			}
		}
	}

	@Override
	public void projectPreSave(IProject project) {
	}

	@Override
	public void projectRenamed(IProject project) {
	}

	@Override
	public void projectSaved(IProject project) {
	}

}
