package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

abstract public class ApplyStereotypeMenuManager {

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
        throw new RuntimeException("Attempt to apply stereotype to unexpected model element of type '"  + modelType + "'");
    }
  }

  abstract public void performAction();

  abstract public void update();
}
