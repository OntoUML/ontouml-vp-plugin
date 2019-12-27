package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ActivateDefaultStereotypes implements VPActionController {
	
	@Override
	public void performAction(VPAction action) {
		
		ViewManager vm = ApplicationManager.instance().getViewManager();
		vm.showMessage("Action \"" + action.getLabel() + "\" is currently disabled. No stereotypes are being removed.");
		
//		StereotypeUtils.removeAllModelStereotypesButOntoUML(IModelElementFactory.MODEL_TYPE_CLASS);
//		StereotypeUtils.removeAllModelStereotypesButOntoUML(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

//		TODO This won't help returning to the original set of stereotypes 
		
//		StereotypeUtils.setDefaultStereotypes(OntoUMLPluginForVP.allModelElements);
//		OntoUMLPluginForVP.isPluginActive = false;
		
	}

	@Override
	public void update(VPAction action) {
		// TODO Auto-generated method stub
		
	}
	
}