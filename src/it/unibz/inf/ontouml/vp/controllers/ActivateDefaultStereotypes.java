package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.OntoUMLPluginForVP;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ActivateDefaultStereotypes implements VPActionController {
	
	@Override
	public void performAction(VPAction arg0) {
		
		StereotypeUtils.removeAllModelSteryotypesButOntoUML(IModelElementFactory.MODEL_TYPE_CLASS);
		StereotypeUtils.removeAllModelSteryotypesButOntoUML(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
		StereotypeUtils.setDefaultStereotypes(OntoUMLPluginForVP.allModelElements);
		OntoUMLPluginForVP.isPluginActive = false;
		
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}
	
}