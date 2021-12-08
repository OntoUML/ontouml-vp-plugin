package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import java.awt.event.ActionEvent;

/**
 * Implementation of context sensitive action of change OntoUML stereotypes in model elements.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ApplyStereotypeController implements VPContextActionController {

  @Override
  public void performAction(VPAction action, VPContext context, ActionEvent event) {
    ApplyStereotypeMenuManager manager = ApplyStereotypeMenuManager.create(action, context);
    manager.performAction();
  }

  /**
   * When the user right clicks on a diagram element, this method is invoked for each menu item of
   * the menu to apply stereotypes. It sets the enables/disables menu items and adjusts their
   * labels.
   */
  @Override
  public void update(VPAction action, VPContext context) {
    ApplyStereotypeMenuManager manager = ApplyStereotypeMenuManager.create(action, context);
    manager.update();
  }
}
