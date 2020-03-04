package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectDiagramListener;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class ProjectDiagramListener implements IProjectDiagramListener {
	
	public ProjectDiagramListener() {}
	
	@Override
	public void diagramAdded(IProject project, IDiagramUIModel diagram) {
		diagram.addDiagramListener(OntoUMLPlugin.DIAGRAM_LISTENER);
	}

	@Override
	public void diagramRemoved(IProject project, IDiagramUIModel diagram) {}

}