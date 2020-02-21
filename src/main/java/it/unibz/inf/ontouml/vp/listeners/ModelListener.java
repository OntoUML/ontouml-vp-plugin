package it.unibz.inf.ontouml.vp.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.SmartColoring;

public class ModelListener implements PropertyChangeListener {

	public ModelListener() {
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object changeSource = evt.getSource();
		if (changeSource instanceof IModelElement) {
			IModelElement model = (IModelElement) changeSource;

			if (model.getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS)) {
				SmartColoring.paint((IClass) model);
			}
		}
	}

}
