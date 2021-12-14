package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.utils.VPContextUtils;

public abstract class ApplyStereotypeMenuManager {

  protected VPAction action;
  protected VPContext context;

  public static ApplyStereotypeMenuManager create(VPAction action, VPContext context) {
    String modelType = VPContextUtils.getModelType(context);
    switch (modelType) {
      case IModelElementFactory.MODEL_TYPE_CLASS:
        return new ApplyClassStereotypeMenuManager(action, context);
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        return new ApplyAttributeStereotypeMenuManager(action, context);
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return new ApplyAssociationStereotypeMenuManager(action, context);
      default:
        throw new RuntimeException(
            "Attempt to apply stereotype to unexpected model element of type '" + modelType + "'");
    }
  }

  public abstract void performAction();

  public abstract void update();
}
