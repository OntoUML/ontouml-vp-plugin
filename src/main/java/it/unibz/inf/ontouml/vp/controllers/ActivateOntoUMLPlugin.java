package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ActivateOntoUMLPlugin implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {

		if (!OntoUMLPlugin.isPluginActive) {
			
//			StereotypeUtils.removeAllModelStereotypesButOntoUML(IModelElementFactory.MODEL_TYPE_CLASS);
//			StereotypeUtils.removeAllModelStereotypesButOntoUML(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
			StereotypeUtils.setUpOntoUMLStereotypes();
			OntoUMLPlugin.isPluginActive = true;
			
		}

	}

	@Override
	public void update(VPAction arg0) {
	}

}
