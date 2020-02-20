package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectDiagramListener;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class ProjectDiagramListener implements IProjectDiagramListener {
	
	public ProjectDiagramListener() {
	}
	
	@Override
	public void diagramAdded(IProject arg0, IDiagramUIModel arg1) {
		//System.out.println("Diagram " + arg1.getType() + " : " + arg1.getName() + " added.");
		arg1.addDiagramListener(OntoUMLPlugin.DIAGRAM_LISTENER);
	}

	@Override
	public void diagramRemoved(IProject arg0, IDiagramUIModel arg1) {
		//System.out.println("Diagram " + arg1.getType() + " : " + arg1.getName() + " removed.");		
	}

}