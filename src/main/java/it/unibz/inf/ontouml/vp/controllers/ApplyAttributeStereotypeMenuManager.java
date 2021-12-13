package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IAttribute;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;

public class ApplyAttributeStereotypeMenuManager extends ApplyStereotypeMenuManager {

  private final ApplyAttributeStereotypeId attributeStereotypeId;

  ApplyAttributeStereotypeMenuManager(VPAction action, VPContext context) {
    this.action = action;
    this.context = context;
    this.attributeStereotypeId = ApplyAttributeStereotypeId.getFromActionId(action.getActionId());
  }

  @Override
  public void performAction() {
    IAttribute att = (IAttribute) context.getModelElement();
    StereotypesManager.applyStereotype(att, attributeStereotypeId.getStereotype());
  }

  @Override
  public void update() {
    action.setEnabled(true);
  }
}
