package it.unibz.inf.ontouml.vp.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;

import it.unibz.inf.ontouml.vp.model.Class;
import it.unibz.inf.ontouml.vp.model.Generalization;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ModelListener implements PropertyChangeListener {

	// Property change names of interest events
	// Events on classes
	final public static String PCN_STEREOTYPES = "stereotypes";
	final public static String PCN_RESTRICTED_TO = "restrictedTo";
	final public static String PCN_FROM_RELATIONSHIP_ADDED = "fromRelationshipAdded";
	final public static String PCN_FROM_RELATIONSHIP_REMOVED = "fromRelationshipRemoved";
	final public static String PCN_TO_RELATIONSHIP_ADDED = "toRelationshipAdded";
	final public static String PCN_TO_RELATIONSHIP_REMOVED = "toRelationshipRemoved";
	// Events on generalizations
	final public static String PCN_FROM_MODEL = "fromModel";
	final public static String PCN_TO_MODEL = "toModel";

	final private Set<String> interestStereotypes;

	public ModelListener() {
		interestStereotypes = StereotypeUtils.getSortalStereotypeNames();
		interestStereotypes.addAll(StereotypeUtils.getUltimateSortalStereotypeNames());
		interestStereotypes.add(StereotypeUtils.STR_TYPE);
		interestStereotypes.add(StereotypeUtils.STR_HISTORICAL_ROLE);
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

	/**
	 * Processes changes on the stereotype or restriction (tagged value restrictedTo)
	 * of an instance of IClass.
	 * 
	 * Changes to stereotypes or the tagged value trigger the verification and
	 * (in certain cases) propagation of the restrictions down the hierarchy.
	 * 
	 * Changes to the tagged value trigger the smart painting.
	 */
	private void processClassEvent(PropertyChangeEvent event) {
		switch(event.getPropertyName()) {
			case PCN_STEREOTYPES:
				enforceAndPropagateRestrictedTo(event);
				break;
			case PCN_RESTRICTED_TO:
				enforceAndPropagateRestrictedTo(event);
				smartPaint(event);
				break;
		}
	}

	/**
	 * Triggers the verification of the restrictions on new and old values
	 * when changing the general or specific of a generalization.
	 * 
	 * In case of change on general, also trigger the verification on the
	 * specific.
	 */
	private void processGeneralizationEvent(PropertyChangeEvent event) {
		final IGeneralization sourceGen = event.getSource() instanceof IGeneralization ?
				(IGeneralization) event.getSource() : null;

		switch(event.getPropertyName()) {
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
		final IClass _class = event.getSource() instanceof IClass ?
				(IClass) event.getSource() : null;
		final Object newRestriction = event.getNewValue();
		final Object oldRestriction = event.getOldValue();

		if(_class != null && !newRestriction.equals(oldRestriction)) {
			SmartColoring.paint(_class);
		}
	}

	/**
	 * Verifies and makes sure that entities have their correct restrictions.
	 * 
	 * The fixed restrictions are simply enforced.
	 * 
	 * The restrictions of «type» and kinds is enforced and propagated to
	 * their descendants.
	 * 
	 * The restrictions of sortals and historical roles is updated based on 
	 * their sortal parents and propagated (if there is any update).
	 * 
	 * Non-sortals and classes without a proper stereotype are ignored.
	 */
	private void enforceAndPropagateRestrictedTo(PropertyChangeEvent event) {
		final IClass _class = event.getSource() instanceof IClass ?
				(IClass) event.getSource() : null;
		final String stereotype = StereotypeUtils.getUniqueStereotype(_class);
		final Object newValue = event.getNewValue();
		final Object oldValue = event.getOldValue();

		if(stereotype == null) { return ; }

		switch (stereotype) {
			case StereotypeUtils.STR_EVENT:
			case StereotypeUtils.STR_DATATYPE:
			case StereotypeUtils.STR_ENUMERATION:
				// if(!newValue.equals(oldValue)) {
				// 	Class.setDefaultRestrictedTo(_class);
				// }
				// break;
			case StereotypeUtils.STR_KIND:
			case StereotypeUtils.STR_COLLECTIVE:
			case StereotypeUtils.STR_QUANTITY:
			case StereotypeUtils.STR_RELATOR:
			case StereotypeUtils.STR_MODE:
			case StereotypeUtils.STR_QUALITY:
			case StereotypeUtils.STR_TYPE:
				if(!newValue.equals(oldValue)) {
					Class.setDefaultRestrictedTo(_class);
					propagateRestrictionsToDescendants(_class);
				}
				break;
			case StereotypeUtils.STR_CATEGORY:
			case StereotypeUtils.STR_ROLE_MIXIN:
			case StereotypeUtils.STR_PHASE_MIXIN:
			case StereotypeUtils.STR_MIXIN:
				if(!newValue.equals(oldValue)) {
					propagateRestrictionsToDescendants(_class);
				}
				break;
			case StereotypeUtils.STR_SUBKIND:
			case StereotypeUtils.STR_ROLE:
			case StereotypeUtils.STR_PHASE:
				final Set<IClass> sortalParents = getSortalParents(_class);
				final String parentsRestrictions = Class.getRestrictedTo(sortalParents);
				String currentRestrictions = Class.getRestrictedTo(_class);
				
				currentRestrictions = currentRestrictions == null ? 
						"" : currentRestrictions;
				
				if(!currentRestrictions.equals(parentsRestrictions)){
					if (
						StereotypeUtils.STR_HISTORICAL_ROLE.equals(stereotype) &&
						sortalParents.isEmpty()
					) {
						propagateRestrictionsToDescendants(_class);
					} else {
						Class.setRestrictedTo(_class, parentsRestrictions);
						propagateRestrictionsToDescendants(_class);
					}
				}
				break;
		}
	}

	/**
	 * For subkinds, phases, roles, and historical roles, update the value
	 * of restrictedTo based on the of its sortal parents and triggers
	 * the update of sortal descendants.
	 */
	private void lookupAndPropagateRestrictedTo(Object classObject) {
		final IClass _class = classObject instanceof IClass ?
				(IClass) classObject : null ;
		final String stereotype = _class != null ?
				StereotypeUtils.getUniqueStereotype(_class) : null;

		if(_class == null || stereotype == null) { return ; }

		switch (stereotype) {
			case StereotypeUtils.STR_SUBKIND:
			case StereotypeUtils.STR_ROLE:
			case StereotypeUtils.STR_PHASE:
			case StereotypeUtils.STR_HISTORICAL_ROLE:
				final Set<IClass> interestParents = getSortalParents(_class);
				final String parentsRestrictions = Class.getRestrictedTo(interestParents);
				
				String currentRestrictions = Class.getRestrictedTo(_class);
				currentRestrictions = currentRestrictions == null ? 
						"" : currentRestrictions;
				
				if(!currentRestrictions.equals(parentsRestrictions)){
					Class.setRestrictedTo(_class, parentsRestrictions);
					propagateRestrictionsToDescendants(_class);
				}
				break;
		}
	}

	/**
	 * Moves through the sortals and historical roles (kinds excluded) that
	 * are descendants of _class updating their values of restrictedTo.
	 * 
	 * The value of restrictedTo is updated if it is different from the current.
	 * 
	 * It goes recursively and stops if the descendent is not a sortal or a
	 * historical role or if restrictedTo is not updated.
	 */
	private void propagateRestrictionsToDescendants(IClass _class) {
		Class.applyOnDescendants(_class, descendent -> {
			String descendentStereotype = 
					StereotypeUtils.getUniqueStereotype(descendent);
			
			if(!interestStereotypes.contains(descendentStereotype)) {
				return false;
			}

			final Set<IClass> descendentParents = getSortalParents(descendent);

			final String newRestriction = Class.getRestrictedTo(descendentParents);
			String currentRestriction = Class.getRestrictedTo(descendent);
			currentRestriction = currentRestriction == null ? "" : currentRestriction;

			if(!currentRestriction.equals(newRestriction)) {
				Class.setRestrictedTo(descendent, newRestriction);
				return true;
			}

			return false;
		});
	}

	private Set<IClass> getSortalParents(IClass _class) {
		final Set<IClass> sortalParents = Class.getParents(_class);
		sortalParents.removeIf(parent -> {
			final String parentStereotype = StereotypeUtils.getUniqueStereotype(parent);
			return !interestStereotypes.contains(parentStereotype);
		});

		return sortalParents;
	}

}