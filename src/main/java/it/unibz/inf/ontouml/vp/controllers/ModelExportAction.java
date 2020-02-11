package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * Implementation toolbar button action responsible for exporting OntoUML model
 * in JSON (according to OntoUML Schema).
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
 *
 */
public class ModelExportAction implements VPActionController {

	/**
	 * 
	 * Performs model export in JSON format.
	 * 
	 * @param action
	 * 
	 */
	@SuppressWarnings("resource")
	@Override
	public void performAction(VPAction action) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();

		@SuppressWarnings("serial")
		JFileChooser fileChooser = new JFileChooser() {
			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				if (f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this, "File already exists.\nDo you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					}
				}
				super.approveSelection();
			}
		};

		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON File", "json"));
		fileChooser.setAcceptAllFileFilterUsed(true);

		fileChooser.setCurrentDirectory(new File(configurations.getExportFolderPath()));
		fileChooser.setSelectedFile(new File(ApplicationManager.instance().getProjectManager().getProject().getName() + ".json"));

		int result = fileChooser.showSaveDialog(ApplicationManager.instance().getViewManager().getRootFrame());

		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				FileWriter fw = new FileWriter(fileChooser.getSelectedFile().getAbsolutePath() + ".json");
				final String jsonModel = ModelElement.generateModel(true);
				fw.write(jsonModel);
				configurations.setExportFolderPath(fileChooser.getCurrentDirectory().toString());
			} catch (IOException e) {
				ViewUtils.log("Export Failed. Please submit your Visual Paradigm's log and the time of the error our developers.", ViewUtils.SCOPE_PLUGIN);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the menu containing the button is accessed allowing for
	 * action manipulation, such as enable/disable or selecting the button.
	 * 
	 * OBS: DOES NOT apply to this class.
	 */
	@Override
	public void update(VPAction action) {
	}

}