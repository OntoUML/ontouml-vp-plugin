/**
 * Class responsible for displaying the graphical interface for exporting the OntoUML model to the
 * relational schema.
 *
 * <p>Author: Gustavo Ludovico Guidoni
 */
package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.Component;

public class DBExportHandler implements IDialogHandler {

  private DBExportView view;

  /** Called when the dialog is closed by the user clicking on the close button of the frame. */
  @Override
  public boolean canClosed() {
    OntoUMLPlugin.setDBExportWindowOpen(false);
    ViewManagerUtils.cleanAndShowMessage("Request cancelled by the user.");
    return true;
  }

  /**
   * Called once before the dialog is shown. Developer should return the content of the dialog
   * (similar to the content pane).
   */
  @Override
  public Component getComponent() {
    view = new DBExportView(Configurations.getInstance().getProjectConfigurations());
    return view;
  }

  /**
   * Called after the getComponent(). A dialog is created on Visual Paradigm internally (it still
   * not shown out). Developer can set the outlook of the dialog on prepare().
   */
  @Override
  public void prepare(IDialog dialog) {
    dialog.setTitle(OntoUMLPlugin.PLUGIN_NAME + " Generate Schema");
    dialog.setModal(false);
    dialog.setResizable(false);
    dialog.setSize(view.getWidth(), view.getHeight());
    view.setContainerDialog(dialog);
  }

  /** Called when the dialog is shown. */
  @Override
  public void shown() {
    // TODO Auto-generated method stub
  }
}
