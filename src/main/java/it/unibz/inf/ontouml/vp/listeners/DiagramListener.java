package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;

public class DiagramListener implements IDiagramListener {

	public DiagramListener() {
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagram, IDiagramElement shape) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			return;
		}

		try {
			final IModelElement shapeElement = shape.getModelElement();

			if (shapeElement != null && shapeElement.getModelType().equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
				final IGeneralization generalization = (IGeneralization) shapeElement;
				final String sourceType = generalization.getFrom() != null ? generalization.getFrom().getModelType()
						: null;
				final String targetType = generalization.getFrom() != null ? generalization.getFrom().getModelType()
						: null;

				if (sourceType != null && targetType != null && sourceType.equals(IModelElementFactory.MODEL_TYPE_CLASS)
						&& targetType.equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
					SmartColoring.paint((IClass) generalization.getFrom());
					SmartColoring.paint((IClass) generalization.getTo());
				}

				// TODO: check if this id indeed necessary
				// SmartColoring.smartPaint();
			}
		} catch (Exception e) {
			System.out.println("Exception caught while adding element to diagram.");
			e.printStackTrace();
		}
	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel diagram, IDiagramElement shape) {
		if (Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			SmartColoring.smartPaint();
		}
	}

	@Override
	public void diagramUIModelLoaded(IDiagramUIModel diagram) {
		if (Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			SmartColoring.smartPaint();
		}
	}

	@Override
	public void diagramUIModelPropertyChanged(IDiagramUIModel diagram, String propertyName, Object originalProperty,
			Object modifiedProperty) {
		// TODO: Removing before merging into `development`
		final StringBuilder msg = new StringBuilder();
		msg.append("[DIAGRAM PROPERTY CHANGE]");
		msg.append(" PROPERTY | ");
		msg.append(propertyName);
		msg.append(" ORIGINAL | ");
		msg.append(originalProperty);
		msg.append(" MODIFIED | ");
		msg.append(modifiedProperty);

		System.out.println(msg.toString());
	}

	@Override
	public void diagramUIModelRenamed(IDiagramUIModel diagram) {
	}

}