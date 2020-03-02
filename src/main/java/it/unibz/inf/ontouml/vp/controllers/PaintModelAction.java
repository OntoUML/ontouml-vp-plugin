package it.unibz.inf.ontouml.vp.controllers;

import javax.swing.JOptionPane;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.SmartColoring;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class PaintModelAction implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()) {
			final int enableSmartPaint = ViewUtils.smartPaintEnableDialog();

			if(enableSmartPaint == JOptionPane.YES_OPTION) {
				Configurations.getInstance().getProjectConfigurations().setAutomaticColoringEnabled(true);
				SmartColoring.smartPaint();
			}
		}
		else {
			final int proceedSmartPaintAction = ViewUtils.smartPaintConfirmationDialog();

			if(proceedSmartPaintAction == JOptionPane.YES_OPTION) {
				SmartColoring.smartPaint();
			}
		}
	}

	@Override
	public void update(VPAction arg0) {}

}
