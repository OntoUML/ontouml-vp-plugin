package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ReloadPlugin implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		ApplicationManager.instance().reloadPluginClasses("it.unibz.inf.ontouml.vp");		
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}

}
