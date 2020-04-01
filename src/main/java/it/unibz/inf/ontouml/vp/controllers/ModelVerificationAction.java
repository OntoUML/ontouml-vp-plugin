package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.OntoUMLServerUtils;
import it.unibz.inf.ontouml.vp.utils.ServerRequest;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import it.unibz.inf.ontouml.vp.views.ProgressPanel;

/**
 * Implementation of toolbar button action responsible for performing model verification.
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public class ModelVerificationAction implements VPActionController {

	private ProgressPanel progressPanel;
	private ProgressDialog loading;
	private IDialog mainDialog;
	ModelVerificationRequest request;

	/**
	 * 
	 * Performs OntoUML model verification.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {
		
		request = new ModelVerificationRequest();
		
		loading = new ProgressDialog();
		ApplicationManager.instance().getViewManager().showDialog(loading);

		Thread thread = new Thread(request);
		thread.start();
	}

	/**
	 * Called when the menu containing the button is accessed allowing for action manipulation, such as enable/disable or selecting the button.
	 * 
	 * OBS: DOES NOT apply to this class.
	 */
	@Override
	public void update(VPAction action) {
	}

	protected class ProgressDialog implements IDialogHandler {

		@Override
		public Component getComponent() {
			progressPanel = new ProgressPanel(request);
			return progressPanel;
		}

		@Override
		public void prepare(IDialog dialog) {
			mainDialog = dialog;
			mainDialog.setTitle("Verification Service");
			mainDialog.setModal(false);
			mainDialog.setResizable(false);
			dialog.setSize(progressPanel.getWidth(), progressPanel.getHeight() + 20);
			progressPanel.setContainerDialog(mainDialog);
		}

		@Override
		public void shown() {
		}

		@Override
		public boolean canClosed() {
			request.doStop();
			mainDialog.close();
			ViewUtils.cleanAndShowMessage("Request cancelled by the user.");
			return true;
		}

	}

	public class ModelVerificationRequest extends ServerRequest {

		@Override
		public void run() {
			while (keepRunning()) {
				try {
					final String response = OntoUMLServerUtils.requestModelVerification(ModelElement.generateModel(true));

					if (keepRunning()) {
						if (response != null) {
							mainDialog.close();
							ViewUtils.logDiagramVerificationResponse(response);
							request.doStop();
						}
					} else {
						loading.canClosed();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}