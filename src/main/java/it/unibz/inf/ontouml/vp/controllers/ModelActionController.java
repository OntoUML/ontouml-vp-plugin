package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ActionIdManager;
import java.awt.event.ActionEvent;

public class ModelActionController implements VPContextActionController {

  @Override
  public void performAction(VPAction action, VPContext context, ActionEvent event) {
    final IModelElement clickedElement = context.getModelElement();
    final String clickedElementType = clickedElement.getModelType();

    if (!IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)) {
      return;
    }

    final IAssociation clickedAssociation =
        IModelElementFactory.MODEL_TYPE_ASSOCIATION.equals(clickedElementType)
            ? (IAssociation) context.getModelElement()
            : null;

    switch (action.getActionId()) {
      case ActionIdManager.ASSOCIATION_ACTION_INVERT_ASSOCIATION:
        ModelElement.forEachSelectedElement(
            clickedAssociation,
            selectedAssociation -> Association.invertAssociation(selectedAssociation, false));
        break;
    }
  }

  @Override
  public void update(VPAction action, VPContext context) {}
}
