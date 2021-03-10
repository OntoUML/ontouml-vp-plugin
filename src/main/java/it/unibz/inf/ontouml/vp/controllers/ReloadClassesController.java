package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;

public class ReloadClassesController implements VPActionController {

  private static int time = 5000;

  @Override
  public void performAction(VPAction action) {
    reloadPlugin();
    doTheThing();
  }

  @Override
  public void update(VPAction action) {}

  private void reloadPlugin() {
    System.out.println("----------------------------------------");
    System.out.println("Reloading OntoUML Plugin...");
    ApplicationManager app = ApplicationManager.instance();
    app.reloadPluginClasses(OntoUMLPlugin.PLUGIN_ID);
    System.out.println("Plugin reloaded!");
  }

  private void doTheThing() {
    ProgressDialogHandler pdh = new ProgressDialogHandler();
    pdh.showDialog();

    //    new SwingWorker<List<Integer>, Integer>() {
    //      protected List<Integer> doInBackground() {
    //        try {
    //          Thread.sleep(ReloadClassesController.time);
    //        } catch (InterruptedException e) {
    //          e.printStackTrace();
    //        }
    //        pdh.closeDialog();
    //        return null;
    //      }
    //    }.execute();
  }
}
