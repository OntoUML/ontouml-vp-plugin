package it.unibz.inf.ontouml.vp.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;

public class ModelListener implements PropertyChangeListener {

	public ModelListener() {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO: Removing before merging into `development`
		final StringBuilder msg = new StringBuilder();
		msg.append("[MODEL ELEMENT PROPERTY CHANGE]");
		msg.append(" EVENT | ");
		msg.append(event);

		System.out.println(msg.toString());

		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			return;
		}

		final Object changeSource = event.getSource();
		try {
			if (changeSource instanceof IModelElement) {
				final IModelElement modelElement = (IModelElement) changeSource;

				if (modelElement.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
					SmartColoring.paint((IClass) modelElement);
					SmartColoring.smartPaint();
				}

				if (modelElement.getModelType().equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION)) {
					final IGeneralization generalization = (IGeneralization) modelElement;
					final String sourceType = generalization.getFrom() != null ? generalization.getFrom().getModelType()
							: null;
					final String targetType = generalization.getTo() != null ? generalization.getTo().getModelType()
							: null;

					if (sourceType != null && targetType != null
							&& sourceType.equals(IModelElementFactory.MODEL_TYPE_CLASS)
							&& targetType.equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
						SmartColoring.paint((IClass) generalization.getFrom());
						SmartColoring.paint((IClass) generalization.getTo());
					}
				}

			}
		} catch (Exception e) {
			System.out.println("Exception caught while answering to a property change.");
			System.out.println("Source: " + changeSource);
			e.printStackTrace();
		}
	}

}