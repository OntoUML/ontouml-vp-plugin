package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

import it.unibz.inf.ontouml.vp.features.constraints.ActionIds;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import it.unibz.inf.ontouml.vp.views.SelectMultipleOptionsDialog;

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
		final ITaggedValueContainer container = _class.getTaggedValues();
		final Iterator<?> values = container == null ? null : container.taggedValueIterator();

		switch(action.getActionId()) {
			case ActionIds.PROPERTY_SET_ALLOWED:
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("allowed")) {
						final ViewManager vm = ApplicationManager.instance().getViewManager();
						final SelectMultipleOptionsDialog dialog = 
								new SelectMultipleOptionsDialog(value.getValueAsString());
						
						vm.showDialog(dialog);
						value.setValue(dialog.getSelectedValues());

						return;
					}
				}
				action.setEnabled(false);
				break;
			case ActionIds.PROPERTY_SET_IS_POWERTYPE:
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("isPowertype")) {
						value.setValue(action.isSelected());
						return;
					}
				}
				break;
			case ActionIds.PROPERTY_SET_ORDER:
				// Consider using a JSpinner
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("order")) {
						value.setValue(ViewUtils.setOrderDialog(value.getValueAsString()));
						return ;
					}
				}
				break;
		}
	}

	@Override
	public void update(VPAction action, VPContext context) {
		if(context.getModelElement() == null) {
			return ;
		}

		final IClass _class = (IClass) context.getModelElement();
		final ITaggedValueContainer container = _class.getTaggedValues();
		final Iterator<?> values = container == null ? null : container.taggedValueIterator();
		
		switch(action.getActionId()) {
			case ActionIds.PROPERTY_SET_ALLOWED:
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("allowed")) {
						action.setEnabled(true);
						return ;
					}
				}

				action.setEnabled(false);
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

				action.setEnabled(false);
				action.setSelected(false);
				break;
			case ActionIds.PROPERTY_SET_ORDER:
				while(values != null && values.hasNext()) {
					final ITaggedValue value = (ITaggedValue) values.next();

					if(value.getName().equals("order")) {
						action.setEnabled(true);
						return ;
					}
				}

				action.setEnabled(false);
				break;
		}
	}
}