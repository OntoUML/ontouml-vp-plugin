package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.utils.ViewUtils;

/**
 * 
 * Implementation toolbar button action responsible for exporting OntoUML model in both XMI (according
 * UML's standard) and JSON (according to OntoUML Schema).
 * 
 * @author Claudenir Fonseca
 *
 */
public class ModelExportAction implements VPActionController {

	/**
	 * 
	 * Performs model export in XMI and JSON formats to user defined folder.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {
		// TODO Auto-generated method stub
		ViewUtils.log("Model Export feature yet not implemented", ViewUtils.SCOPE_DEVELOPMENT_LOG);
	}

	/**
	 * Called when the menu containing the button is accessed allowing for action
	 * manipulation, such as enable/disable or selecting the button.
	 * 
	 *  OBS: DOES NOT apply to this class.
	 */
	@Override
	public void update(VPAction action) {
	}

}
