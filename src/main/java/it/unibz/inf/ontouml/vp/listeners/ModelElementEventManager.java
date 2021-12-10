package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.beans.PropertyChangeEvent;

abstract public class ModelElementEventManager {

  // Names of property change events to be handled
  protected static final String STEREOTYPE_CHANGE_EVENT = "stereotypes";
  protected static final String VIEW_ADDED_EVENT = "modelViewAdded";

  protected String changeEvent;
  protected Object newValue;
  protected Object oldValue;

  protected PropertyChangeEvent event;

  abstract public void processEvent();

  protected boolean hasEventChangedValues() {
    return newValue != null ? !newValue.equals(oldValue) : newValue != null;
  }

  public static ModelElementEventManager create(PropertyChangeEvent event) {
    if(!hasModelElementSource(event)) throwUnexpectedEventSource();

    String modelType = ((IModelElement) event.getSource()).getModelType();

    switch(modelType) {
      case IModelElementFactory.MODEL_TYPE_CLASS:
        return new ClassEventManager(event);
      case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
        return new GeneralizationEventManager(event);
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return new AssociationEventManager(event);
      default:
        throwUnexpectedEventSource(modelType);
    }

    return null;
  }

  public static boolean hasModelElementSource(PropertyChangeEvent event) {
    return event.getSource() instanceof IModelElement;
  }

  public static void throwUnexpectedEventSource() {
    throw new RuntimeException("Unexpected source in received property change event.");
  }

  public static void throwUnexpectedEventSource(String modelType) {
    throw new RuntimeException("Unexpected source in received property change event of type '"+modelType+"'");
  }

}
