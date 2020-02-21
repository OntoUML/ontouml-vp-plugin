package it.unibz.inf.ontouml.vp.listeners;

import java.util.Iterator;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

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

		Iterator<?> diagramIter = arg0.diagramIterator();
		while (diagramIter.hasNext()) {
			IDiagramUIModel diagram = (IDiagramUIModel) diagramIter.next();
			diagram.addDiagramListener(OntoUMLPlugin.DIAGRAM_LISTENER);
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
