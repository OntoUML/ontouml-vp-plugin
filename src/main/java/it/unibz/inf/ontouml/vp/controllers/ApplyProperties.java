package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;
import it.unibz.inf.ontouml.vp.views.SelectMultipleOptionsDialog;
import it.unibz.inf.ontouml.vp.views.SetOrderDialog;

/**
 * 
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
 *
 */
public class ApplyProperties implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();

		switch(action.getActionId()) {
			case ActionIds.PROPERTY_SET_ALLOWED:
				this.setAllowedProperty(action,context,event);
				break;
			case ActionIds.PROPERTY_SET_IS_ABSTRACT:
				_class.setAbstract(!_class.isAbstract());
				break;
			case ActionIds.PROPERTY_SET_IS_DERIVED:
				if (_class.getName().trim().startsWith("/")) {
					_class.setName(_class.getName().trim().substring(1));
				} else {
					_class.setName("/"+_class.getName());
				}
				break;
			case ActionIds.PROPERTY_SET_IS_EXTENSIONAL:
				this.setIsExtensionalProperty(action,context,event);
				break;
			case ActionIds.PROPERTY_SET_IS_POWERTYPE:
				this.setIsPowertypeProperty(action,context,event);
				break;
			case ActionIds.PROPERTY_SET_ORDER:
				this.setOrderProperty(action,context,event);
				break;
		}
	}

	private void setAllowedProperty(VPAction action, VPContext context, ActionEvent event) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();
		ITaggedValueContainer container = _class.getTaggedValues();
		Iterator<?> values = container == null ? null : container.taggedValueIterator();
		String currentStereotype = _class.toStereotypeArray() != null && _class.toStereotypeArray().length > 0 ?
			_class.toStereotypeArray() [0] :
			null;

		if(currentStereotype == null || !StereotypeUtils.getOntoUMLClassStereotypeNames().contains(currentStereotype)){
			return ;
		}

		// Searches for tagged value
		ITaggedValue allowedValue = null;
		while(values != null && values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();

			if(value.getName().equals("allowed")) {
				allowedValue = value;
				break ;
			}
		}

		// Adds missing tagged value
		if(allowedValue == null) {
			// Reset stereotype's tagged values
			if(StereotypeUtils.STEREOTYPE_ELEMENTS == null) { 
				StereotypeUtils.generate(); 
			}
			
			if(_class.getTaggedValues() != null) { 
				ITaggedValueContainer c = _class.getTaggedValues();
				_class.setTaggedValues(null);
				c.delete();
			}
			
			_class.removeStereotype(currentStereotype);
			_class.addStereotype(StereotypeUtils.STEREOTYPE_ELEMENTS.get(currentStereotype));
			StereotypeUtils.setAllowed(_class, currentStereotype);

			// Searches for the tagged value just added
			container = _class.getTaggedValues();
			values = container == null ? null : container.taggedValueIterator();

			while(values != null && values.hasNext()) {
				final ITaggedValue value = (ITaggedValue) values.next();

				if(value.getName().equals("allowed")) {
					allowedValue = value;
					break ;
				}
			}
		}

		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final SelectMultipleOptionsDialog dialog = 
				new SelectMultipleOptionsDialog(allowedValue.getValueAsString());
		
		vm.showDialog(dialog);
		allowedValue.setValue(dialog.getSelectedValues());
	}

	private void setIsExtensionalProperty(VPAction action, VPContext context, ActionEvent event) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();
		ITaggedValueContainer container = _class.getTaggedValues();
		Iterator<?> values = container == null ? null : container.taggedValueIterator();

		if(!_class.hasStereotype(StereotypeUtils.STR_COLLECTIVE)){
			return ;
		}

		// Searches for tagged value
		ITaggedValue isExtensionalValue = null;
		while(values != null && values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();

			if(value.getName().equals("isExtensional")) {
				isExtensionalValue = value;
				break ;
			}
		}

		// Adds missing tagged value
		if(isExtensionalValue == null) {
			// Reset stereotype's tagged values
			if(StereotypeUtils.STEREOTYPE_ELEMENTS == null) { 
				StereotypeUtils.generate(); 
			}
			
			if(_class.getTaggedValues() != null) { 
				ITaggedValueContainer c = _class.getTaggedValues();
				_class.setTaggedValues(null);
				c.delete();
			}
			
			_class.removeStereotype(StereotypeUtils.STR_COLLECTIVE);
			_class.addStereotype(StereotypeUtils.STEREOTYPE_ELEMENTS.get(StereotypeUtils.STR_COLLECTIVE));
			StereotypeUtils.setAllowed(_class, StereotypeUtils.STR_COLLECTIVE);

			// Searches for the tagged value just added
			container = _class.getTaggedValues();
			values = container == null ? null : container.taggedValueIterator();

			while(values != null && values.hasNext()) {
				final ITaggedValue value = (ITaggedValue) values.next();

				if(value.getName().equals("isExtensional")) {
					isExtensionalValue = value;
					break ;
				}
			}
		}

		isExtensionalValue.setValue(!isExtensionalValue.getValueAsString().toLowerCase().equals("true"));
	}

	private void setIsPowertypeProperty(VPAction action, VPContext context, ActionEvent event) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();
		ITaggedValueContainer container = _class.getTaggedValues();
		Iterator<?> values = container == null ? null : container.taggedValueIterator();

		if(!_class.hasStereotype(StereotypeUtils.STR_TYPE)){
			return ;
		}

		// Searches for tagged value
		ITaggedValue isPowertypeValue = null;
		while(values != null && values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();

			if(value.getName().equals("isPowertype")) {
				isPowertypeValue = value;
				break ;
			}
		}

		// Adds missing tagged value
		if(isPowertypeValue == null) {
			// Reset stereotype's tagged values
			if(StereotypeUtils.STEREOTYPE_ELEMENTS == null) { StereotypeUtils.generate(); }

			if(_class.getTaggedValues() != null) { 
				ITaggedValueContainer c = _class.getTaggedValues();
				_class.setTaggedValues(null);
				c.delete();
			}
			
			_class.removeStereotype(StereotypeUtils.STR_TYPE);
			_class.addStereotype(StereotypeUtils.STEREOTYPE_ELEMENTS.get(StereotypeUtils.STR_TYPE));
			
			StereotypeUtils.setAllowed(_class, StereotypeUtils.STR_TYPE);
			
			// Searches for the tagged value just added
			container = _class.getTaggedValues();
			values = container == null ? null : container.taggedValueIterator();

			while(values != null && values.hasNext()) {
				final ITaggedValue value = (ITaggedValue) values.next();

				if(value.getName().equals("isPowertype")) {
					isPowertypeValue = value;
					break ;
				}
			}
		}

		isPowertypeValue.setValue(!isPowertypeValue.getValueAsString().toLowerCase().equals("true"));
	}

	private void setOrderProperty(VPAction action, VPContext context, ActionEvent event) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();
		ITaggedValueContainer container = _class.getTaggedValues();
		Iterator<?> values = container == null ? null : container.taggedValueIterator();

		if(!_class.hasStereotype(StereotypeUtils.STR_TYPE)){
			return ;
		}

		// Searches for tagged value
		ITaggedValue orderValue = null;
		while(values != null && values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();

			if(value.getName().equals("order")) {
				orderValue = value;
				break ;
			}
		}

		// Adds missing tagged value
		if(orderValue == null) {
			// Reset stereotype's tagged values
			if(StereotypeUtils.STEREOTYPE_ELEMENTS == null) { StereotypeUtils.generate(); }

			if(_class.getTaggedValues() != null) { 
				ITaggedValueContainer c = _class.getTaggedValues();
				_class.setTaggedValues(null);
				c.delete();
			}
			
			_class.removeStereotype(StereotypeUtils.STR_TYPE);
			_class.addStereotype(StereotypeUtils.STEREOTYPE_ELEMENTS.get(StereotypeUtils.STR_TYPE));
			
			StereotypeUtils.setAllowed(_class, StereotypeUtils.STR_TYPE);

			// Searches for the tagged value just added
			container = _class.getTaggedValues();
			values = container == null ? null : container.taggedValueIterator();

			while(values != null && values.hasNext()) {
				final ITaggedValue value = (ITaggedValue) values.next();

				if(value.getName().equals("order")) {
					orderValue = value;
					break ;
				}
			}
		}

		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final SetOrderDialog dialog = 
				new SetOrderDialog(orderValue.getValueAsString());
		
		vm.showDialog(dialog);
		orderValue.setValue(dialog.getOrder());
	}

	@Override
	public void update(VPAction action, VPContext context) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();
		final ITaggedValueContainer container = _class.getTaggedValues();
		final Iterator<?> values = container == null ? null : container.taggedValueIterator();
		final Iterator<?> stereotypes = _class.stereotypeModelIterator();
		final Set<String> allStereotypes = StereotypeUtils.getOntoUMLClassStereotypeNames();
		
		switch(action.getActionId()) {
			case ActionIds.PROPERTY_SET_ALLOWED:
				
				while(stereotypes != null && stereotypes.hasNext()) {
					final IStereotype stereotype = (IStereotype) stereotypes.next();

					if(allStereotypes.contains(stereotype.getName())) {
						action.setEnabled(true);
						return ;
					}
				}

				action.setEnabled(false);
				break;
			case ActionIds.PROPERTY_SET_IS_ABSTRACT:
				action.setEnabled(true);
				action.setSelected(_class.isAbstract());
				break;
			case ActionIds.PROPERTY_SET_IS_DERIVED:
				action.setEnabled(true);
				action.setSelected(_class.getName().trim().startsWith("/"));
				break;
			case ActionIds.PROPERTY_SET_IS_EXTENSIONAL:
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("isExtensional")) {
						action.setEnabled(true);
						action.setSelected(value.getValueAsString().toLowerCase().equals("true"));
						return;
					}
				}

				action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_COLLECTIVE));
				action.setSelected(false);
				break;
			case ActionIds.PROPERTY_SET_IS_POWERTYPE:
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("isPowertype")) {
						action.setEnabled(true);
						action.setSelected(value.getValueAsString().toLowerCase().equals("true"));
						return;
					}
				}

				action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_TYPE));
				action.setSelected(false);
				break;
			case ActionIds.PROPERTY_SET_ORDER:
				action.setEnabled(_class.hasStereotype(StereotypeUtils.STR_TYPE));
				break;
		}
	}
}