package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ActivateOntoUMLPlugin implements VPActionController {
	
	@Override
	public void performAction(VPAction arg0) {
		
		StereotypeUtils.removeAllModelSteryotypesButOntoUML(IModelElementFactory.MODEL_TYPE_CLASS);
		StereotypeUtils.removeAllModelSteryotypesButOntoUML(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
		StereotypeUtils.setUpOntoUMLStereotypes();
		
	}

	@Override
	public void update(VPAction arg0) {}
	
}
