package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.OntoUMLPluginForVP;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ActivateOntoUMLPlugin implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {

		if (!OntoUMLPluginForVP.isPluginActive) {
			
			StereotypeUtils.removeAllModelSteryotypesButOntoUML(IModelElementFactory.MODEL_TYPE_CLASS);
			StereotypeUtils.removeAllModelSteryotypesButOntoUML(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
			StereotypeUtils.setUpOntoUMLStereotypes();
			OntoUMLPluginForVP.isPluginActive = true;
			
		}

	}

	@Override
	public void update(VPAction arg0) {
	}

}
