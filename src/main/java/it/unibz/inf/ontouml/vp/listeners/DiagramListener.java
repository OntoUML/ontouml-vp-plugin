package it.unibz.inf.ontouml.vp.listeners;

import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.factory.IModelElementFactory;

public class DiagramListener implements IDiagramListener {
	
	public DiagramListener() {}

	@Override
	public void diagramElementAdded(IDiagramUIModel arg0, IDiagramElement arg1) {

		if (arg1.getModelElement().getName().equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
			IGeneralization generalization = (IGeneralization) arg1.getModelElement();

			String fromType = generalization.getFrom().getModelType();
			boolean isFromClass = fromType.equals(IModelElementFactory.MODEL_TYPE_CLASS);

			String toType = generalization.getTo().getModelType();
			boolean isToClass = toType.equals(IModelElementFactory.MODEL_TYPE_CLASS);

			if ((isFromClass) && (isToClass))
				SmartColoring.paint((IClass) generalization.getFrom());

		}

	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel arg0, IDiagramElement arg1) {}

	@Override
	public void diagramUIModelLoaded(IDiagramUIModel arg0) {}

	@Override
	public void diagramUIModelPropertyChanged(IDiagramUIModel arg0, String arg1, Object arg2, Object arg3) {}

	@Override
	public void diagramUIModelRenamed(IDiagramUIModel arg0) {}

}