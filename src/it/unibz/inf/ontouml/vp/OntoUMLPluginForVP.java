package it.unibz.inf.ontouml.vp;

import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class OntoUMLPluginForVP implements VPPlugin {

	public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
	public static final String PLUGIN_NAME = "OntoUML Plugin";

	@Override
	public void loaded(VPPluginInfo arg0) {
		StereotypeUtils.removeAllModelSteryotypes(IModelElementFactory.MODEL_TYPE_CLASS);
		StereotypeUtils.removeAllModelSteryotypes(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
		StereotypeUtils.setUpOntoUMLStereotypes();
	}

	@Override
	public void unloaded() {
	}

}
