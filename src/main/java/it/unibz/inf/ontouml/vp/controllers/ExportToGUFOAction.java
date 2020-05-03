package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.OntoUMLServerUtils;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ServerRequest;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import it.unibz.inf.ontouml.vp.views.ExportToGUFOView;
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

	private ExportDialog menu;
	private ExportToGUFOView _exportMenuView;
	private IDialog _dialog;
	MenuExport requestMenu;

	@Override
	public void performAction(VPAction action) {

		requestMenu = new MenuExport();
		menu = new ExportDialog();
		
		if(OntoUMLPlugin.getExportToGUFOWindowOpen() == true)
			return;
		else
			OntoUMLPlugin.setExportToGUFOWindowOpen(true);
		
		ApplicationManager.instance().getViewManager().showDialog(menu);

	}

	/**
	 * Called when the menu containing the button is accessed allowing for action
	 * manipulation, such as enable/disable or selecting the button.
	 * 
	 * OBS: DOES NOT apply to this class.
	 */
	@Override
	public void update(VPAction action) {
	}

	private void saveFile(BufferedReader buffer) throws IOException {
		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
		final FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
				"Choose destination", FileDialog.SAVE);

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

	public class ProgressDialog implements IDialogHandler {

		@Override
		public Component getComponent() {
			progressPanel = new ProgressPanel(requestMenu);
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
			mainDialog.close();
			return true;
		}
	}

	protected class ExportDialog implements IDialogHandler {

		/**
		 * 
		 * Called once before the dialog is shown. Developer should return the content
		 * of the dialog (similar to the content pane).
		 * 
		 */
		@Override
		public Component getComponent() {
			_exportMenuView = new ExportToGUFOView(Configurations.getInstance().getProjectConfigurations(),
					requestMenu);
			return _exportMenuView;
		}

		/**
		 * 
		 * Called after the getComponent(). A dialog is created on Visual Paradigm
		 * internally (it still not shown out). Developer can set the outlook of the
		 * dialog on prepare().
		 * 
		 */
		@Override
		public void prepare(IDialog dialog) {
			_dialog = dialog;
			_dialog.setTitle(OntoUMLPlugin.PLUGIN_NAME + " Configurations");
			_dialog.setModal(false);
			_dialog.setResizable(false);
			_dialog.setSize(_exportMenuView.getWidth(), _exportMenuView.getHeight() + 20);
			_exportMenuView.setContainerDialog(_dialog);
		}

		/**
		 * 
		 * Called when the dialog is shown.
		 * 
		 */
		@Override
		public void shown() {
		}

		/**
		 * 
		 * Called when the dialog is closed by the user clicking on the close button of
		 * the frame.
		 * 
		 */
		@Override
		public boolean canClosed() {
			requestMenu.doStop();
			OntoUMLPlugin.setExportToGUFOWindowOpen(false);
			ViewUtils.cleanAndShowMessage("Request cancelled by the user.");
			return true;
		}

	}

	public class MenuExport extends ServerRequest {

		@Override
		public void run() {
			while (keepRunning()) {
				try {

					if (keepRunning()) {
						if (!_exportMenuView.getIsOpen()) {

							if (_exportMenuView.getIsToExport()) {
								
								loading = new ProgressDialog();
								ApplicationManager.instance().getViewManager().showDialog(loading);

								final BufferedReader gufo = OntoUMLServerUtils.transformToGUFO(ModelElement.generateModel(_exportMenuView.getSavedElements(), true),
										"http://api.ontouml.org/", "turtle", "name", loading);
								
								if (gufo != null) {
									saveFile(gufo);
									ViewUtils.cleanAndShowMessage("Model exported successfully.");
									requestMenu.doStop();
								} else {
									menu.canClosed();
									requestMenu.doStop();
									ViewUtils.cleanAndShowMessage("Unable to transform to GUFO. Please check your model.");
								}
							} else {
								menu.canClosed();
								requestMenu.doStop();
								ViewUtils.cleanAndShowMessage("Request cancelled by the user.");
							}
							
							OntoUMLPlugin.setExportToGUFOWindowOpen(false);
						}
					} else {
						OntoUMLPlugin.setExportToGUFOWindowOpen(false);
						menu.canClosed();
						requestMenu.doStop();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}