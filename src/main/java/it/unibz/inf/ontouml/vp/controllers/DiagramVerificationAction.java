package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

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
	private IDialog mainDialog;

	/**
	 * 
	 * Performs OntoUML diagram verification.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {

		if (!hasOpenedClassDiagram()) {
			ViewUtils.simpleDialog("Diagram Verification", "Please open a diagram before running this command.");
			return;
		}

		ProgressDialog loading = new ProgressDialog();
		ApplicationManager.instance().getViewManager().showDialog(loading);

		ExecutorService executor = Executors.newFixedThreadPool(10);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					ViewUtils.clearLog(ViewUtils.SCOPE_PLUGIN);
					final String response = OntoUMLServerUtils.requestModelVerification(ModelElement.generateModel(true));

					if (response != null) {
						ViewUtils.logDiagramVerificationResponse(response);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

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
			progressPanel = new ProgressPanel();
			return progressPanel;
		}

		@Override
		public void prepare(IDialog dialog) {
			mainDialog = dialog;
			mainDialog.setTitle("Verification Service");
			mainDialog.setModal(false);
			mainDialog.setResizable(false);
			dialog.setSize(progressPanel.getWidth(), progressPanel.getHeight() + 20);
			progressPanel.setContainerDialog(dialog);
		}

		@Override
		public void shown() {
		}

		@Override
		public boolean canClosed() {
			return true;
		}

	}

}