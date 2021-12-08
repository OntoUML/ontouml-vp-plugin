package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.model.uml.Class;

public class ApplyClassStereotypeMenuManager extends ApplyStereotypeMenuManager {

  private final ApplyClassStereotypeId classStereotypeId;

  ApplyClassStereotypeMenuManager(VPAction action, VPContext context) {
    this.action = action;
    this.context = context;
    this.classStereotypeId = ApplyClassStereotypeId.getFromActionId(action.getActionId());
  }

  @Override
  public void performAction() {
    VPContextUtils.getModelElements(context).forEach(element -> {
      StereotypesManager.applyStereotype(element, classStereotypeId.getStereotype());
      if (classStereotypeId.hasKnownNature()) {
        Class.setDefaultRestrictedTo((IClass) element);
      }
    });
  }

  @Override
  public void update() {
    if(classStereotypeId.isFixed()) {
      action.setEnabled(true);
    } else {
      boolean shouldEnable = isStereotypeAllowedForAllSelectedElements(classStereotypeId);
      action.setEnabled(shouldEnable);
    }
  }

  private boolean isStereotypeAllowedForAllSelectedElements(ApplyClassStereotypeId classStereotypeId) {
    String stereotype = classStereotypeId.getStereotype();
    return VPContextUtils.getModelElements(context).stream()
        .allMatch(
            element -> OntoUMLConstraintsManager.isStereotypeAllowed((IClass) element, stereotype));
  }
}
