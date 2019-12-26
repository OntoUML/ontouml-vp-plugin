package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.OntoUMLPluginForVP;
import it.unibz.inf.ontouml.vp.views.ConfigurationsMenuView;

public class ConfigurationsMenu implements VPActionController, IDialogHandler {
	
	private ConfigurationsMenuView _configurationsMenuView;
	private IDialog _dialog;

	@Override
	public void performAction(VPAction arg0) {
		// TODO Auto-generated method stub
//		JFileChooser jfc = ApplicationManager.instance().getViewManager().createJFileChooser();
//		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		int ret = jfc.showOpenDialog(null);
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Component getComponent() {
		this._configurationsMenuView = new ConfigurationsMenuView();
		return _configurationsMenuView;
	}

	@Override
	public void prepare(IDialog dialog) {
		this._dialog = dialog;
		this._dialog.setTitle(OntoUMLPluginForVP.PLUGIN_NAME + " Configurations");
		this._dialog.setModal(false);
		this._dialog.setResizable(false);
	}

	@Override
	public void shown() {}

	@Override
	public boolean canClosed() {
		return true;
	}
	
}
