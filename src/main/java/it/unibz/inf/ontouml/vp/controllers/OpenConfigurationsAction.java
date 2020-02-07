package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.views.ConfigurationsView;

import java.awt.*;

/**
 * 
 * Controller responsible for opening the OntoUML Plugin configurations menu.
 * <code>OpenConfigurationsAction</code> contains an implementation of 
 * <code>IDialogHandler</code> necessary for presenting Java components in 
 * Visual Paradigm.
 * 
 * @author Claudenir Fonseca
 *
 */
public class OpenConfigurationsAction implements VPActionController {
	
	private ConfigurationsView _configurationsMenuView;
	private IDialog _dialog;
	
	/**
	 * 
	 * Opens OntoUML Plugin configuration menu. Called when the button is clicked,
	 * providing access to the action information.
	 * 
	 * @param action
	 * 
	 */
	@Override
	public void performAction(VPAction action) {
		ApplicationManager.instance().getViewManager().showDialog(new ConfigurationsDialog());
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
	
	/**
	 * 
	 * Implementation of <code>IDialogHandler</code> necessary for presenting 
	 * Java components in Visual Paradigm.
	 * 
	 * More information available at
	 *  {@link https://images.visual-paradigm.com/docs/plugin_user_guide/Plug-in_Users_Guide.pdf} .
	 * 
	 * @author Claudenir Fonseca
	 *
	 */
	protected class ConfigurationsDialog implements IDialogHandler {	
		
		/**
		 * 
		 * Called once before the dialog is shown. Developer should return the
		 * content of the dialog (similar to the content pane).
		 *  
		 */
		@Override
		public Component getComponent() {
			_configurationsMenuView = new ConfigurationsView(
					Configurations.getInstance().getProjectConfigurations());
			return _configurationsMenuView;
		}
		
		/**
		 * 
		 *  Called after the getComponent(). A dialog is created on Visual 
		 *  Paradigm internally (it still not shown out). Developer can set the 
		 *  outlook of the dialog on prepare().
		 *  
		 */
		@Override
		public void prepare(IDialog dialog) {
			_dialog = dialog;
			_dialog.setTitle(OntoUMLPlugin.PLUGIN_NAME + " Configurations");
			_dialog.setModal(false);
			_dialog.setResizable(true);
			_dialog.setSize(_configurationsMenuView.getWidth(), _configurationsMenuView.getHeight() + 20);
			_configurationsMenuView.setContainerDialog(_dialog);
		}
		
		/**
		 * 
		 *  Called when the dialog is shown.
		 *  
		 */
		@Override
		public void shown() {}
		
		/**
		 * 
		 *  Called when the dialog is closed by the user clicking on the 
		 *  close button of the frame.
		 * 
		 */
		@Override
		public boolean canClosed() {
			return true;
		}
		
	}

	
}
