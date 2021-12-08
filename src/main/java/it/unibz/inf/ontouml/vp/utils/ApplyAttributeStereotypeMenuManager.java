package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;

public class ApplyAttributeStereotypeMenuManager extends ApplyStereotypeMenuManager {

  private final ApplyAttributeStereotypeId attributeStereotypeId;

  ApplyAttributeStereotypeMenuManager(VPAction action, VPContext context) {
    this.action = action;
    this.context = context;
    this.attributeStereotypeId = ApplyAttributeStereotypeId.getFromActionId(action.getActionId());
  }

  @Override
  public void performAction() {
    VPContextUtils.getModelElements(context).forEach(element -> {
      StereotypesManager.applyStereotype(element, attributeStereotypeId.getStereotype());
    });
  }

  @Override
  public void update() {
    action.setEnabled(true);
  }
}
