package it.unibz.inf.ontouml.vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.listeners.ProjectListener;
import javax.swing.ToolTipManager;

/**
 * Implementation of VPPlugin responsible for configuring OntoUML Plugin's behaviour when loading
 * and unload.
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public class OntoUMLPlugin implements VPPlugin {

  public static final String PLUGIN_VERSION_RELEASE = "0.6";
  public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
  public static final String PLUGIN_NAME = "OntoUML Plugin";
  public static final String PLUGIN_REPO = "https://github.com/OntoUML/ontouml-vp-plugin/";
  public static final String PLUGIN_REPO_OWNER = "OntoUML";
  public static final String PLUGIN_REPO_NAME = "ontouml-vp-plugin";

  private static boolean isExportToGUFOWindowOpen;
  private static boolean isConfigWindowOpen;
  private static ProjectListener projectListener;

  /** OntoUMLPlugin constructor. Declared to make explicit Open API requirements. */
  public OntoUMLPlugin() {
    // The constructor of a VPPlugin MUST NOT have parameters.
    isExportToGUFOWindowOpen = false;
    isConfigWindowOpen = false;
    System.out.println("OntoUML Plugin (v" + PLUGIN_VERSION_RELEASE + ") loaded successfully.");
  }

  /**
   * Called by Visual Paradigm when the plugin is loaded.
   *
   * @param pluginInfo
   */
  @Override
  public void loaded(VPPluginInfo pluginInfo) {
    // Visual Paradigm's own window is not a plain Swing top-level; every dialog this plugin opens
    // (Configurations, Suggest Metaclass, Document...) is a Swing panel hosted inside it. In that
    // arrangement Swing's normal tooltip — a lightweight popup drawn on the window's own layered
    // pane — can end up not shown at all, on any component, however long the mouse sits still,
    // because there is no such pane to draw it on. A real (heavyweight) popup window is drawn by
    // the OS instead, and does not depend on that.
    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

    final ProjectManager pm = ApplicationManager.instance().getProjectManager();
    final IProject p = pm.getProject();

    projectListener = new ProjectListener();
    p.addProjectListener(projectListener);
  }

  /**
   * Called by Visual Paradigm when the plugin is unloaded (i.e., Visual Paradigm will be exited).
   * This method is not called when the plugin is reloaded.
   */
  @Override
  public void unloaded() {}

  public static void setExportToGUFOWindowOpen(boolean open) {
    isExportToGUFOWindowOpen = open;
  }

  public static boolean getExportToGUFOWindowOpen() {
    return isExportToGUFOWindowOpen;
  }

  public static void setConfigWindowOpen(boolean open) {
    isConfigWindowOpen = open;
  }

  public static boolean getConfigWindowOpen() {
    return isConfigWindowOpen;
  }
}
