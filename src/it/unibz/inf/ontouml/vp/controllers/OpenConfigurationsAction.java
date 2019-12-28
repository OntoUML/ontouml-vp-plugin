package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.OntoUMLPluginForVP;
import it.unibz.inf.ontouml.vp.views.ConfigurationsMenuView;

public class ConfigurationsMenu implements VPActionController {
	
	private ConfigurationsMenuView _configurationsMenuView;
	private IDialog _dialog;

	@Override
	public void performAction(VPAction arg0) {
		ApplicationManager.instance().getViewManager().showDialog(new ConfigurationsDialog());
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}
	
	protected class ConfigurationsDialog implements IDialogHandler {	
		
		@Override
		public Component getComponent() {
			_configurationsMenuView = new ConfigurationsMenuView(OntoUMLPluginForVP.getCurrentPorjectConfigurations());
			return _configurationsMenuView;
		}
		
		@Override
		public void prepare(IDialog dialog) {
			_dialog = dialog;
			_dialog.setTitle(OntoUMLPluginForVP.PLUGIN_NAME + " Configurations");
			_dialog.setModal(false);
			_dialog.setResizable(true);
			_dialog.setSize(_configurationsMenuView.getWidth(),_configurationsMenuView.getHeight()+20);
			_configurationsMenuView.setContainerDialog(_dialog);
		}
		
		@Override
		public void shown() {}
		
		@Override
		public boolean canClosed() {
			return true;
		}
		
	}

	
}
