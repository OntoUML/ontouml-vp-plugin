package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.OntoUMLServerUtils;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ServerRequest;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import it.unibz.inf.ontouml.vp.views.ProgressPanel;

/**
 * Implementation of toolbar button Export to gUFO.
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public class ExportToGUFOAction implements VPActionController {

	private ProgressPanel progressPanel;
	private ProgressDialog loading;
	private IDialog mainDialog;
	ExportToGUFORequest request;

	@Override
	public void performAction(VPAction action) {

		request = new ExportToGUFORequest();

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

	private void saveFile(BufferedReader buffer) throws IOException {
		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
		final FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(), "Choose destination", FileDialog.SAVE);

		String suggestedFolderPath = projectConfigurations.getExportGUFOFolderPath();
		String suggestedFileName = projectConfigurations.getExportGUFOFilename();

		if (suggestedFileName.isEmpty()) {
			String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
			suggestedFileName = projectName + ".ttl";
		}

		fd.setDirectory(suggestedFolderPath);
		fd.setFile(suggestedFileName);
		fd.setVisible(true);

		if (fd.getDirectory() != null && fd.getFile() != null) {
			final String fileDirectory = fd.getDirectory();
			final String fileName = !fd.getFile().endsWith(".ttl") ? fd.getFile() + ".ttl" : fd.getFile();
			final String output = buffer.lines().collect(Collectors.joining("\n"));

			Files.write(Paths.get(fileDirectory, fileName), output.getBytes());
			projectConfigurations.setExportGUFOFolderPath(fileDirectory);
			projectConfigurations.setExportGUFOFilename(fileName);
			configs.save();
		}
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
			mainDialog.setTitle("Export to GUFO");
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

	public class ExportToGUFORequest extends ServerRequest {

		@Override
		public void run() {
			while (keepRunning()) {
				try {
					final BufferedReader gufo = OntoUMLServerUtils.transformToGUFO(ModelElement.generateModel(true), "http://api.ontouml.org/", "turtle", "name");

					if (keepRunning()) {
						if (gufo != null) {
							mainDialog.close();
							saveFile(gufo);
							ViewUtils.cleanAndShowMessage("Model exported successfuly.");
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