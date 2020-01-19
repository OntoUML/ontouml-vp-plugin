package it.unibz.inf.ontouml.vp.controllers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.OntoUMLServerUtils;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

/**
 * Implementation of toolbar button action responsible for performing model
 * verification.
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public class ModelVerificationAction implements VPActionController {
	
	/**
	 * 
	 * Performs OntoUML model verification. 
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				ViewUtils.clearLog(ViewUtils.SCOPE_PLUGIN);

				try {
					ViewUtils.log("Initiating verification.", ViewUtils.SCOPE_PLUGIN);

					final String response = OntoUMLServerUtils
							.requestModelVerification(ModelElement.generateModel(true));

					ViewUtils.logVerificationResponse(response);
					ViewUtils.log("Verification terminated.", ViewUtils.SCOPE_PLUGIN);
				} catch (Exception e) {
					ViewUtils.log("Verification terminated with error.", ViewUtils.SCOPE_PLUGIN);
					ViewUtils.log(
							"Please share your log (including your model, if possible) with our developers at <https://github.com/OntoUML/ontouml-vp-plugin>.",
							ViewUtils.SCOPE_PLUGIN);
					e.printStackTrace();
				}
			}

		});
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