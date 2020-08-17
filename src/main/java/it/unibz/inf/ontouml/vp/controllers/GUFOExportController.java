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

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.ServerRequest;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import it.unibz.inf.ontouml.vp.views.GUFOExportView;
import it.unibz.inf.ontouml.vp.views.ProgressPanel;

/**
 * Implementation of toolbar button Export to gUFO.
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public class GUFOExportController implements VPActionController {

	private ProgressPanel progressPanel;
	private ProgressDialog loading;
	private IDialog mainDialog;

	private ExportDialog menu;
	private GUFOExportView _exportMenuView;
	private IDialog _dialog;
	MenuExport requestMenu;

	@Override
	public void performAction(VPAction action) {

		requestMenu = new MenuExport();
		menu = new ExportDialog();

		if (OntoUMLPlugin.getExportToGUFOWindowOpen() == true)
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

	private void saveFile(BufferedReader buffer, String exportFormat) throws IOException {
		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
		final FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
				"Choose destination", FileDialog.SAVE);

		String suggestedFolderPath = projectConfigurations.getExportGUFOFolderPath();
		String suggestedFileName = projectConfigurations.getExportGUFOFilename();
		String exportFormatExtension = exportFormat != "Turtle" ? ".nt" : ".ttl";

		if (suggestedFileName.isEmpty()) {
			String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
			suggestedFileName = projectName + exportFormatExtension;
		}

		fd.setDirectory(suggestedFolderPath);
		fd.setFile(suggestedFileName);
		fd.setVisible(true);

		if (fd.getDirectory() != null && fd.getFile() != null) {
			final String fileDirectory = fd.getDirectory();
			final String fileName = !fd.getFile().endsWith(exportFormatExtension) ? fd.getFile() + exportFormatExtension : fd.getFile();
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
			_exportMenuView = new GUFOExportView(Configurations.getInstance().getProjectConfigurations(),
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
			ViewManagerUtils.cleanAndShowMessage("Request cancelled by the user.");
			return true;
		}

	}

	public class MenuExport extends ServerRequest {

		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();

		@Override
		public void run() {
			while (keepRunning()) {
				try {

					if (keepRunning()) {
						if (!_exportMenuView.getIsOpen()) {

							if (_exportMenuView.getIsToExport()) {

								loading = new ProgressDialog();
								ApplicationManager.instance().getViewManager().showDialog(loading);

								final BufferedReader gufo = OntoUMLServerAccessController.transformToGUFO(
										ModelElement.generateModel(_exportMenuView.getSavedElements(), true),
										projectConfigurations.getExportGUFOIRI(),
										projectConfigurations.getExportGUFOFormat(),
										projectConfigurations.getExportGUFOURIFormat(),
										projectConfigurations.getExportGUFOInverseBox(),
										projectConfigurations.getExportGUFOObjectBox(),
										projectConfigurations.getExportGUFOAnalysisBox(),
										projectConfigurations.getExportGUFOPackagesBox(),
										projectConfigurations.getExportGUFOElementMapping(),
										projectConfigurations.getExportGUFOPackageMapping(), loading);

								if (gufo != null) {
									saveFile(gufo,projectConfigurations.getExportGUFOFormat());
									ViewManagerUtils.cleanAndShowMessage("Model exported successfully.");
									requestMenu.doStop();
								} else {
									menu.canClosed();
									requestMenu.doStop();
									ViewManagerUtils.cleanAndShowMessage(
											"Unable to transform to GUFO. Please check your model.");
								}
							} else {
								menu.canClosed();
								requestMenu.doStop();
								ViewManagerUtils.cleanAndShowMessage("Request cancelled by the user.");
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