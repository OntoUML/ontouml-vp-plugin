package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IAssociation;
import java.beans.PropertyChangeEvent;

public class AssociationEventManager extends ModelElementEventManager {

  private final IAssociation source;

  AssociationEventManager(PropertyChangeEvent event) {
    this.event = event;
    this.source = (IAssociation) event.getSource();
    this.changeEvent = event.getPropertyName();
    this.newValue = event.getNewValue();
    this.oldValue = event.getOldValue();
  }

  @Override
  public void processEvent() {}
}
