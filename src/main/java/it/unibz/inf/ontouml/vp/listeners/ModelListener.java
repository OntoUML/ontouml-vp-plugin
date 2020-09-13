package it.unibz.inf.ontouml.vp.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;

import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.Generalization;
import it.unibz.inf.ontouml.vp.utils.SmartColoringUtils;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;

public class ModelListener implements PropertyChangeListener {

	// Property change names of interest events
	final public static String PCN_MODEL_VIEW_ADDED = "modelViewAdded";
	// Events on classes
	final public static String PCN_STEREOTYPES = "stereotypes";
	final public static String PCN_RESTRICTED_TO = StereotypesManager.PROPERTY_RESTRICTED_TO;
	final public static String PCN_FROM_RELATIONSHIP_ADDED = "fromRelationshipAdded";
	final public static String PCN_FROM_RELATIONSHIP_REMOVED = "fromRelationshipRemoved";
	final public static String PCN_TO_RELATIONSHIP_ADDED = "toRelationshipAdded";
	final public static String PCN_TO_RELATIONSHIP_REMOVED = "toRelationshipRemoved";
	// Events on generalizations
	final public static String PCN_FROM_MODEL = "fromModel";
	final public static String PCN_TO_MODEL = "toModel";

	final private List<String> interestStereotypes;

	public ModelListener() {
		interestStereotypes = Stereotype.getSortalStereotypeNames();
		interestStereotypes.addAll(Stereotype.getUltimateSortalStereotypeNames());
		interestStereotypes.add(Stereotype.TYPE);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		try {
			final Object source = event.getSource();

			if (source instanceof IClass) {
				processClassEvent(event);
			} else if (source instanceof IGeneralization) {
				processGeneralizationEvent(event);
			}
		} catch (Exception e) {
			System.err.println("An error ocurred while processing a change event on model element.");
			e.printStackTrace();
		}
	}

	private void processClassEvent(PropertyChangeEvent event) {
		final boolean isSmartModelingEnabled = Configurations.getInstance().getProjectConfigurations()
				.isSmartModellingEnabled();

		switch (event.getPropertyName()) {
		case PCN_STEREOTYPES:
			if (isSmartModelingEnabled) {
				enforceAndPropagateRestrictedTo(event);
			}
			break;
		case PCN_RESTRICTED_TO:
			if (isSmartModelingEnabled) {
				enforceAndPropagateRestrictedTo(event);
			}
			smartPaint(event);
			break;
		case PCN_MODEL_VIEW_ADDED:
			smartPaint(event);
			break;
		}
	}

	private void processGeneralizationEvent(PropertyChangeEvent event) {
		final boolean isSmartModelingEnabled = Configurations.getInstance().getProjectConfigurations()
				.isSmartModellingEnabled();

		if (!isSmartModelingEnabled) {
			return;
		}

		final IGeneralization sourceGen = event.getSource() instanceof IGeneralization
				? (IGeneralization) event.getSource()
				: null;

		switch (event.getPropertyName()) {
		case PCN_TO_MODEL:
			lookupAndPropagateRestrictedTo(event.getOldValue());
			lookupAndPropagateRestrictedTo(event.getNewValue());
			break;
		case PCN_FROM_MODEL:
			lookupAndPropagateRestrictedTo(event.getOldValue());
			lookupAndPropagateRestrictedTo(event.getNewValue());
			lookupAndPropagateRestrictedTo(Generalization.getSpecific(sourceGen));
			break;
		}
	}

	private void smartPaint(PropertyChangeEvent event) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			return;
		}

		final IClass _class = event.getSource() instanceof IClass ? (IClass) event.getSource() : null;
		final Object newRestriction = event.getNewValue();
		final Object oldRestriction = event.getOldValue();

		if (_class != null && !newRestriction.equals(oldRestriction)) {
			SmartColoringUtils.paint(_class);
		}
	}

	private void enforceAndPropagateRestrictedTo(PropertyChangeEvent event) {
		final IClass _class = event.getSource() instanceof IClass ? (IClass) event.getSource() : null;
		final String stereotype = Class.getUniqueStereotypeName(_class);
		final Object newValue = event.getNewValue();
		final Object oldValue = event.getOldValue();

		if (stereotype == null) {
			return;
		}

		switch (stereotype) {
		case Stereotype.EVENT:
		case Stereotype.DATATYPE:
		case Stereotype.ENUMERATION:
		case Stereotype.KIND:
		case Stereotype.COLLECTIVE:
		case Stereotype.QUANTITY:
		case Stereotype.RELATOR:
		case Stereotype.QUALITY:
		case Stereotype.TYPE:
			if (!newValue.equals(oldValue)) {
				Class.setDefaultRestrictedTo(_class);
				propagateRestrictionsToDescendants(_class);
			}
			break;
		case Stereotype.MODE:
		case Stereotype.CATEGORY:
		case Stereotype.ROLE_MIXIN:
		case Stereotype.PHASE_MIXIN:
		case Stereotype.MIXIN:
		case Stereotype.HISTORICAL_ROLE_MIXIN:
			if (!newValue.equals(oldValue)) {
				propagateRestrictionsToDescendants(_class);
			}
			break;
		case Stereotype.SUBKIND:
		case Stereotype.ROLE:
		case Stereotype.PHASE:
		case Stereotype.HISTORICAL_ROLE:
			final Set<IClass> sortalParents = getSortalParents(_class);
			final String parentsRestrictions = Class.getRestrictedTo(sortalParents);
			String currentRestrictions = Class.getRestrictedTo(_class);

			currentRestrictions = currentRestrictions == null ? "" : currentRestrictions;

			if (!currentRestrictions.equals(parentsRestrictions)) {
				Class.setRestrictedTo(_class, parentsRestrictions);
				propagateRestrictionsToDescendants(_class);
			}
			break;
		}
	}

	private void lookupAndPropagateRestrictedTo(Object classObject) {
		final IClass _class = classObject instanceof IClass ? (IClass) classObject : null;
		final String stereotype = _class != null ? Class.getUniqueStereotypeName(_class) : null;

		if (_class == null || stereotype == null) {
			return;
		}

		switch (stereotype) {
		case Stereotype.SUBKIND:
		case Stereotype.ROLE:
		case Stereotype.PHASE:
		case Stereotype.HISTORICAL_ROLE:
			final Set<IClass> interestParents = getSortalParents(_class);
			final String parentsRestrictions = Class.getRestrictedTo(interestParents);

			String currentRestrictions = Class.getRestrictedTo(_class);
			currentRestrictions = currentRestrictions == null ? "" : currentRestrictions;

			if (!currentRestrictions.equals(parentsRestrictions)) {
				Class.setRestrictedTo(_class, parentsRestrictions);
				propagateRestrictionsToDescendants(_class);
			}
			break;
		}
	}

	private void propagateRestrictionsToDescendants(IClass _class) {
		Class.applyOnDescendants(_class, descendent -> {
			String descendentStereotype = Class.getUniqueStereotypeName(descendent);

			if (!interestStereotypes.contains(descendentStereotype)) {
				return false;
			}

			final Set<IClass> descendentParents = getSortalParents(descendent);

			final String newRestriction = Class.getRestrictedTo(descendentParents);
			String currentRestriction = Class.getRestrictedTo(descendent);
			currentRestriction = currentRestriction == null ? "" : currentRestriction;

			if (!currentRestriction.equals(newRestriction)) {
				Class.setRestrictedTo(descendent, newRestriction);
				return true;
			}

			return false;
		});
	}

	private Set<IClass> getSortalParents(IClass _class) {
		final Set<IClass> sortalParents = Class.getParents(_class);
		sortalParents.removeIf(parent -> {
			final String parentStereotype = Class.getUniqueStereotypeName(parent);
			return !interestStereotypes.contains(parentStereotype);
		});

		return sortalParents;
	}

}