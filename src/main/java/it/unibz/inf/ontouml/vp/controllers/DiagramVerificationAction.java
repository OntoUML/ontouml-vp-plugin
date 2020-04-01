package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;
import java.util.concurrent.ExecutorService;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.OntoUMLServerUtils;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import it.unibz.inf.ontouml.vp.views.ProgressPanel;

/**
 * Implementation of toolbar button action responsible for performing diagram verification.
 *
 */
public class DiagramVerificationAction implements VPActionController {

	private ProgressPanel progressPanel;
	private ProgressDialog loading;
	private IDialog mainDialog;
	ExecutorService executor;
	DiagramVerificationRequest request;
	Thread thread;

	/**
	 * 
	 * Performs OntoUML diagram verification.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {

		ViewUtils.clearLog(ViewUtils.SCOPE_PLUGIN);

		if (!hasOpenedClassDiagram()) {
			ViewUtils.simpleDialog("Diagram Verification", "Please open a diagram before running this command.");
			return;
		}

		request = new DiagramVerificationRequest();

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

	private static boolean hasOpenedClassDiagram() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject().toDiagramArray();

		if (diagramArray == null)
			return false;

		for (IDiagramUIModel diagram : diagramArray)
			if (diagram instanceof IClassDiagramUIModel && diagram.isOpened())
				return true;

		return false;
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

	public class DiagramVerificationRequest implements Runnable {

		private boolean doStop = false;

		public synchronized void doStop() {
			this.doStop = true;
		}

		private synchronized boolean keepRunning() {
			return this.doStop == false;
		}

		@Override
		public void run() {
			while (keepRunning()) {
				try {
					final String response = OntoUMLServerUtils.requestModelVerification(ModelElement.generateModel(true));

					if (keepRunning()) {
						if (response != null) {
							ViewUtils.logDiagramVerificationResponse(response);
							request.doStop();
							mainDialog.close();
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