package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.utils.SmartColoring;

public class PaintModelAction implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		
		SmartColoring.smartPaint();
		
	}

	@Override
	public void update(VPAction arg0) {}

}
