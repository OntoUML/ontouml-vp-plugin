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
	}

	@Override
	public void update(VPAction action) {}
	
}