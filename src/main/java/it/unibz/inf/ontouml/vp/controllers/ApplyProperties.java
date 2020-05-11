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
				this.setAllowedProperty(context);
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

	private ITaggedValue getValueToBeSet(VPContext context, String valueName){
		if(context.getModelElement() == null) {
			return null;
		}

		final IClass _class = (IClass) context.getModelElement();
		String currentStereotype = _class.toStereotypeArray() != null && _class.toStereotypeArray().length > 0 ?
			_class.toStereotypeArray() [0] :
			null;

		// Escape in case the stereotype is missing or incorrect
		if(
			currentStereotype == null 
			|| !StereotypeUtils.getOntoUMLClassStereotypeNames().contains(currentStereotype)
		){
			return null;
		}

		// Reapply stereotype making sure that the tagged values are there
		StereotypeUtils.applyStereotype(_class, currentStereotype);

		// Searches for tagged value
		ITaggedValueContainer container = _class.getTaggedValues();
		Iterator<?> values = container == null ? null : container.taggedValueIterator();
		while(values != null && values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();

			if(value.getName().equals(valueName)) {
				return value;
			}
		}

		return null;
	}

	private void setAllowedProperty(VPContext context) {
		final ITaggedValue allowedValue = getValueToBeSet(context, StereotypeUtils.PROPERTY_ALLOWED);

		if(allowedValue == null) {
			return ;
		}

		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final SelectMultipleOptionsDialog dialog = 
				new SelectMultipleOptionsDialog(allowedValue.getValueAsString());
		
		vm.showDialog(dialog);
		allowedValue.setValue(dialog.getSelectedValues());
	}

	private void setIsExtensionalProperty(VPAction action, VPContext context, ActionEvent event) {
		final ITaggedValue isExtensionalValue = getValueToBeSet(context, StereotypeUtils.PROPERTY_IS_EXTENSIONAL);

		if(isExtensionalValue == null) {
			return ;
		}

		isExtensionalValue.setValue(!isExtensionalValue.getValueAsString().toLowerCase().equals("true"));
	}

	private void setIsPowertypeProperty(VPAction action, VPContext context, ActionEvent event) {
		final ITaggedValue isPowertypeValue = getValueToBeSet(context, StereotypeUtils.PROPERTY_IS_POWERTYPE);

		if(isPowertypeValue == null) {
			return ;
		}

		isPowertypeValue.setValue(!isPowertypeValue.getValueAsString().toLowerCase().equals("true"));
	}

	private void setOrderProperty(VPAction action, VPContext context, ActionEvent event) {
		final ITaggedValue orderValue = getValueToBeSet(context, StereotypeUtils.PROPERTY_ORDER);

		if(orderValue == null) {
			return ;
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

					if(value.getName().equals(StereotypeUtils.PROPERTY_IS_EXTENSIONAL)) {
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

					if(value.getName().equals(StereotypeUtils.PROPERTY_IS_POWERTYPE)) {
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