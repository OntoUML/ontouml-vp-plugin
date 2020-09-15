package it.unibz.inf.ontouml.vp.controllers;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ActionIdManager;
import it.unibz.inf.ontouml.vp.utils.RestrictedTo;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import it.unibz.inf.ontouml.vp.views.SelectRestrictionsView;
import it.unibz.inf.ontouml.vp.views.SetOrderView;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in
 * model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyPropertiesController implements VPContextActionController {

	@Override
	public void performAction(VPAction action, VPContext context, ActionEvent event) {
		if (context.getModelElement() == null || !(context.getModelElement() instanceof IClass)) {
			return;
		}

		final IClass clickedClass = (IClass) context.getModelElement();

		switch (action.getActionId()) {
		case ActionIdManager.PROPERTY_SET_RESTRICTED_TO:
			this.setRestrictedTo(context, clickedClass);
			break;

		case ActionIdManager.PROPERTY_SET_IS_ABSTRACT:
			final boolean isAbstract = clickedClass.isAbstract();
			forEachSelectedClass(context, cla -> cla.setAbstract(!isAbstract));
			break;

		case ActionIdManager.PROPERTY_SET_IS_DERIVED:
			final boolean isDerived = ModelElement.getIsDerived(clickedClass);
			forEachSelectedClass(context, selected -> {
				ModelElement.setIsDerived(selected, !isDerived);
			});
			break;
		case ActionIdManager.PROPERTY_SET_IS_EXTENSIONAL:
			setBooleanTaggedValue(context, clickedClass, StereotypesManager.PROPERTY_IS_EXTENSIONAL);
			break;

		case ActionIdManager.PROPERTY_SET_IS_POWERTYPE:
			setBooleanTaggedValue(context, clickedClass, StereotypesManager.PROPERTY_IS_POWERTYPE);
			break;

		case ActionIdManager.PROPERTY_SET_ORDER:
			setOrderProperty(context, clickedClass);
			break;
		}
	}

	@Override
	public void update(VPAction action, VPContext context) {
		if (context.getModelElement() == null || !(context.getModelElement() instanceof IClass)) {
			return;
		}

		final IClass _class = (IClass) context.getModelElement();
		final String stereotype = Class.getUniqueStereotypeName(_class);
		final List<String> allClassStereotypes = Stereotype.getOntoUMLClassStereotypeNames();

		switch (action.getActionId()) {
		case ActionIdManager.PROPERTY_SET_RESTRICTED_TO:
			if (allClassStereotypes.contains(stereotype)) {
				final boolean isSmartModelingEnabled = Configurations.getInstance().getProjectConfigurations()
						.isSmartModellingEnabled();
				final List<String> nonFixedRestrictedTo = Arrays.asList(Stereotype.CATEGORY, Stereotype.MIXIN,
						Stereotype.MODE, Stereotype.PHASE_MIXIN, Stereotype.ROLE_MIXIN,
						Stereotype.HISTORICAL_ROLE_MIXIN);

				action.setEnabled(!isSmartModelingEnabled || nonFixedRestrictedTo.contains(stereotype));
			} else {
				action.setEnabled(false);
			}
			break;
		case ActionIdManager.PROPERTY_SET_IS_ABSTRACT:
			action.setEnabled(true);
			action.setSelected(_class.isAbstract());
			break;
		case ActionIdManager.PROPERTY_SET_IS_DERIVED:
			action.setEnabled(true);
			action.setSelected(ModelElement.getIsDerived(_class));
			break;
		case ActionIdManager.PROPERTY_SET_IS_EXTENSIONAL:
			action.setEnabled(Stereotype.COLLECTIVE.equals(stereotype)
					|| RestrictedTo.COLLECTIVE.equals(Class.getRestrictedTo(_class)));
			action.setSelected(Class.getIsExtensional(_class));
			break;
		case ActionIdManager.PROPERTY_SET_IS_POWERTYPE:
			action.setEnabled(Stereotype.TYPE.equals(stereotype));
			action.setSelected(Class.getIsPowertype(_class));
			break;
		case ActionIdManager.PROPERTY_SET_ORDER:
			action.setEnabled(_class.hasStereotype(Stereotype.TYPE));
			break;
		}
	}

	private void forEachSelectedClass(VPContext context, Consumer<IClass> consumer) {
		if (!(context.getModelElement() instanceof IClass))
			return;

		final IDiagramUIModel diagram = context.getDiagram();
		final IClass _class = (IClass) context.getModelElement();

		if (diagram == null) {
			consumer.accept(_class);
			return;
		}

		final IDiagramElement[] diagramElements = context.getDiagram().getSelectedDiagramElement();

		Arrays.stream(diagramElements)
				.filter(e -> e.getModelElement().getModelType().equals(IModelElementFactory.MODEL_TYPE_CLASS))
				.map(e -> (IClass) e.getModelElement()).forEach(consumer);
	}

	private void setBooleanTaggedValue(VPContext context, IClass clickedClass, String metaProperty) {
		final ITaggedValue booleanTaggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(clickedClass,
				metaProperty);
		final boolean value = booleanTaggedValue != null && Boolean.parseBoolean(booleanTaggedValue.getValueAsString());

		forEachSelectedClass(context, cla -> {
			ITaggedValue taggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(cla, metaProperty);

			if (taggedValue == null)
				return;

			taggedValue.setValue(!value);
		});
	}

	private void setOrderProperty(VPContext context, IClass clickedClass) {
		final ITaggedValue baseTaggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(clickedClass,
				StereotypesManager.PROPERTY_ORDER);

		if (baseTaggedValue == null)
			return;

		final SetOrderView dialog = new SetOrderView(baseTaggedValue.getValueAsString());
		ApplicationManager.instance().getViewManager().showDialog(dialog);
		final String order = dialog.getOrder();

		forEachSelectedClass(context, cla -> {
			ITaggedValue taggedValue = StereotypesManager.reapplyStereotypeAndGetTaggedValue(cla,
					StereotypesManager.PROPERTY_ORDER);

			if (taggedValue == null)
				return;

			taggedValue.setValue(order);
		});
	}

	private void setRestrictedTo(VPContext context, IClass clickedClass) {
		String currentRestrictions = Class.getRestrictedTo(clickedClass);
		currentRestrictions = currentRestrictions == null ? "" : currentRestrictions;

		String stereotype = Class.getUniqueStereotypeName(clickedClass);
		List<String> selectableRestrictedTo = RestrictedTo.possibleRestrictedToValues(stereotype);

		final SelectRestrictionsView dialog = new SelectRestrictionsView(currentRestrictions, selectableRestrictedTo);
		ApplicationManager.instance().getViewManager().showDialog(dialog);
		final String newRestrictions = dialog.getSelectedValues();

		forEachSelectedClass(context, cla -> {
			Class.setRestrictedTo(cla, newRestrictions);
		});
	}

}