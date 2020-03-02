package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class PaintModelAction implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			
			if(ViewUtils.enableSmartPaintDialog()) {
				Configurations.getInstance().getProjectConfigurations().setAutomaticColoringEnabled(true);
				SmartColoring.smartPaint();
			}
			
			return;
		}
		
		SmartColoring.smartPaint();
	}

	@Override
	public void update(VPAction arg0) {}

}
