package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;

public class PaintModelAction implements VPActionController {
	
	ViewManager _viewManager = ApplicationManager.instance().getViewManager();	

	@Override
	public void performAction(VPAction arg0) {
		
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			_viewManager.showMessage("Smart Paint is NOT enabled! Please activate it on the plugin settings.");		
			return;
		}
		
		SmartColoring.smartPaint();
	}

	@Override
	public void update(VPAction arg0) {}

}
