package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.utils.ConfigurationsUtils;
import it.unibz.inf.ontouml.vp.utils.RestrictedTo;
import it.unibz.inf.ontouml.vp.utils.SmartColoringUtils;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.beans.PropertyChangeEvent;
import java.util.Set;

public class ClassEventManager extends ModelElementEventManager {

  // Names of property change events to be handled
  private static final String RESTRICTED_TO_CHANGE_EVENT =
      StereotypesManager.PROPERTY_RESTRICTED_TO;
  private static final String IS_EXTENSIONAL_CHANGE_EVENT =
      StereotypesManager.PROPERTY_IS_EXTENSIONAL;

  // Other constants
  private static final Set<String> STEREOTYPES_WITH_FIXED_RESTRICTED_TO_VALUE =
      Set.of(
          Stereotype.EVENT,
          Stereotype.SITUATION,
          Stereotype.TYPE,
          Stereotype.ABSTRACT,
          Stereotype.DATATYPE,
          Stereotype.ENUMERATION,
          Stereotype.KIND,
          Stereotype.COLLECTIVE,
          Stereotype.QUANTITY,
          Stereotype.RELATOR,
          Stereotype.MODE,
          Stereotype.QUALITY);
  private static final Set<String> STEREOTYPES_WITH_MULTIPLE_RESTRICTED_TO_VALUES =
      Set.of(
          Stereotype.CATEGORY,
          Stereotype.MIXIN,
          Stereotype.PHASE_MIXIN,
          Stereotype.ROLE_MIXIN,
          Stereotype.HISTORICAL_ROLE_MIXIN);
  private static final Set<String> STEREOTYPES_WITH_INHERITED_RESTRICTED_TO_VALUE =
      Set.of(Stereotype.SUBKIND, Stereotype.ROLE, Stereotype.PHASE, Stereotype.HISTORICAL_ROLE);

  protected final IClass source;

  ClassEventManager(PropertyChangeEvent event) {
    this.event = event;
    this.source = (IClass) event.getSource();
    this.changeEvent = event.getPropertyName();
    this.newValue = event.getNewValue();
    this.oldValue = event.getOldValue();
  }

  @Override
  public void processEvent() {
    switch (changeEvent) {
      case STEREOTYPE_CHANGE_EVENT:
        processStereotypeChange();
        break;
      case RESTRICTED_TO_CHANGE_EVENT:
        processRestrictedToChange();
        break;
      case IS_EXTENSIONAL_CHANGE_EVENT:
        processIsExtensionalChange();
        break;
      case VIEW_ADDED_EVENT:
        processViewAdded();
        break;
    }
  }

  private void processStereotypeChange() {
    if (ConfigurationsUtils.isSmartModelingEnabled()) {
      enforceAndPropagateRestrictedTo();
    }
  }

  private void processRestrictedToChange() {
    if (ConfigurationsUtils.isSmartModelingEnabled()) {
      enforceAndPropagateRestrictedTo();
      resetIsExtensionalValueForNonCollectiveSource(event);
    }
    paint();
  }

  private void processIsExtensionalChange() {
    if (ConfigurationsUtils.isSmartModelingEnabled()) {
      preventManualChangeToIsExtensionalForNonCollectiveSource();
    }
  }

  private void processViewAdded() {
    paint();
  }

  private void paint() {
    if (hasEventChangedValues() && ConfigurationsUtils.isAutomaticColoringEnabled()) {
      SmartColoringUtils.paint(source);
    }
  }

  private void resetIsExtensionalValueForNonCollectiveSource(PropertyChangeEvent event) {
    if (hasEventChangedValues() && !RestrictedTo.COLLECTIVE.equals(newValue)) {
      Class.isExtensional(source, false);
    }
  }

  private void preventManualChangeToIsExtensionalForNonCollectiveSource() {
    if (hasEventChangedValues() && !Class.isRestrictedTo(source, Set.of(RestrictedTo.COLLECTIVE))) {
      Class.isExtensional(source, false);
    }
  }

  private void enforceAndPropagateRestrictedTo() {
    if (!Class.isOntoumlClass(source)) {
      return;
    }

    if (Class.doesItHaveFixedRestrictions(source) && hasEventChangedValues()) {
      enforceFixedRestrictions();
      Class.propagateRestrictionsToDescendants(source);
      return;
    }

    if (Class.doesItInheritItsRestrictions(source) && hasEventChangedValues()) {
      Class.propagateRestrictionsToDescendants(source);
      return;
    }

    if (Class.canItHaveMultipleRestrictions(source) && hasEventChangedValues()) {
      Class.propagateRestrictionsToDescendants(source);
      return;
    }

    if (!Class.doesItHaveFixedRestrictions(source)
        && !Class.doesItInheritItsRestrictions(source)
        && !Class.canItHaveMultipleRestrictions(source)) {
      throwUnaccountedOntoumlStereotype();
    }
  }

  private void throwUnaccountedOntoumlStereotype() {
    throw new RuntimeException("OntoUML not processed due to unaccounted stereotype");
  }

  private void enforceFixedRestrictions() {
    Class.setDefaultRestrictedTo(source);
  }
}
