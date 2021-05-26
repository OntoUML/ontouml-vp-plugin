package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.views.DbMappingView;
import java.awt.Component;

public class DbMappingDialogHandler implements IDialogHandler {

  private IDialog dialog;
  private final DbMappingView view;
  private final ViewManager viewManager;
  private boolean wasShown = false;
  private boolean wasClosed = false;
  private boolean wasCancelled = false;

  public DbMappingDialogHandler(ProjectConfigurations projectConfigurations) {
    view = new DbMappingView(projectConfigurations);
    viewManager = ApplicationManager.instance().getViewManager();

    view.onExport(
        e -> {
          if (view.checkValidParamters()) {
            view.updateConfigurationsValues(projectConfigurations);
            Configurations.getInstance().save();
            closeDialog();
          }
        });
    view.onCancel(
        e -> {
          wasCancelled = true;
          closeDialog();
        });
  }

  /**
   * Called once before the dialog is shown. Developer should return the content of the dialog
   * (similar to the content pane).
   */
  @Override
  public Component getComponent() {
    return view;
  }

  /**
   * Called after the getComponent(). A dialog is created on Visual Paradigm internally (it still
   * not shown out). Developer can set the outlook of the dialog on prepare().
   */
  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;
    dialog.setTitle(OntoUMLPlugin.PLUGIN_NAME);
    dialog.setModal(true);
    dialog.setResizable(false); // true
    dialog.setSize(view.getWidth(), view.getHeight() + 20);
    dialog.pack();
  }

  /** Called when the dialog is shown. */
  @Override
  public void shown() {}

  /** Called when the dialog is closed by the user clicking on the close button of the frame. */
  @Override
  public boolean canClosed() {
    wasCancelled = true;
    wasClosed = true;
    return true;
  }

  public void showDialog() {
    if (!wasClosed) {
      wasShown = true;
      viewManager.showDialog(this);
    }
  }

  public void closeDialog() {
    if (wasClosed) {
      System.out.println("Mapping to ER dialog was already closed.");
    } else if (!wasShown) {
      System.out.println("Mapping to ER dialog was never shown. Setting wasClosed to \"true\"");
    } else {
      System.out.println("Closing mapping do ER dialog.");
      dialog.close();
    }
    wasClosed = true;
  }

  public boolean wasCancelled() {
    return wasCancelled;
  }
}
