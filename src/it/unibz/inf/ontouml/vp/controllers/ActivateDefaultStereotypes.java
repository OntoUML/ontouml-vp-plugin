package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.OntoUMLPluginForVP;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ActivateDefaultStereotypes implements VPActionController {
	
	@Override
	public void performAction(VPAction arg0) {
	
		StereotypeUtils.setDefaultStereotypes(OntoUMLPluginForVP.allModelElements);
		
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}
	
}