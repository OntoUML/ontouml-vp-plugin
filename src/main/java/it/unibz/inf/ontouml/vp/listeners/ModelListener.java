package it.unibz.inf.ontouml.vp.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModelListener implements PropertyChangeListener {

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    try {
      ModelElementEventManager eventManager = ModelElementEventManager.create(event);
      if(eventManager != null) eventManager.processEvent();
    } catch (Exception e) {
      System.err.println("An error occurred while processing a change event on model element.");
      e.printStackTrace();
    }
  }

}
